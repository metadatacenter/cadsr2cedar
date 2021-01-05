package org.metadatacenter.cadsr.ingestor.form.handler;

import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.form.schema.Module;
import org.metadatacenter.cadsr.form.schema.Question;
import org.metadatacenter.cadsr.ingestor.util.CedarFieldUtil;
import org.metadatacenter.cadsr.ingestor.util.CedarServerUtil;
import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.metadatacenter.cadsr.ingestor.util.GeneralUtil;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

import static org.metadatacenter.model.ModelNodeNames.*;

public class TemplateFieldsHandler implements ModelHandler {

  private static final Logger logger = LoggerFactory.getLogger(TemplateFieldsHandler.class);
  private CedarServer cedarServer;
  private String apiKey;
  private List<Map<String, Object>> fields = new ArrayList<>();

  public TemplateFieldsHandler(CedarServer cedarServer, String apiKey) {
    this.cedarServer = cedarServer;
    this.apiKey = apiKey;
  }

  public TemplateFieldsHandler handle(Form form) throws IOException {

    List<Module> modules = form.getModule();

    for (Module module : modules) {
      handleModule(module);
    }

    return this;
  }

  private void handleModule(Module module) throws IOException {
    handleModuleInfo(module);
    for (Question question : module.getQuestion()) {
      handleQuestion(question);
    }
  }

  private void handleModuleInfo(Module module) {
    if (!GeneralUtil.isNullOrEmpty(module.getLongName())) {
      Map<String, Object> sectionBreak = CedarFieldUtil.generateDefaultSectionBreak(module.getLongName(), "", cedarServer);
      fields.add(sectionBreak);
    }
  }

  private void handleQuestion(Question question) throws IOException {
    if (question.getDataElement() != null) {
      Optional<Map<String, Object>> result =
          CedarServices.searchCdeByPublicIdAndVersion(question.getDataElement().getPublicID(),
              question.getDataElement().getVersion(), cedarServer, apiKey);
      if(result.isPresent()) {
        Map<String, Object> cde = result.get();
        cde = customizeCde(cde, question);
        fields.add(cde);
      }
      else {
        logger.warn("CDE not found: PublicId: " + question.getDataElement().getPublicID() + " ; Version: " + question.getDataElement().getVersion());
      }
    }
    else {
      logger.info("The question does not have an associated data element. Question publicId: " + question.getPublicID());
    }
  }

  private Map<String, Object> customizeCde(Map<String, Object> cde, Question question) {
    cde = customizeCdePrefLabel(cde, question.getQuestionText());
    // TODO: Complete CDE customization
    cde = customizeCdeValues(cde);
    // values, etc.
    return cde;
  }

  private Map<String, Object> customizeCdePrefLabel(Map<String, Object> cde, String prefLabel) {
    String originalPrefLabel = (String) cde.get(SKOS_PREFLABEL);
    if (!GeneralUtil.isNullOrEmpty(prefLabel) && !prefLabel.equals(originalPrefLabel)) {
      logger.info("Replacing prefLabel: " + originalPrefLabel + " -> " + prefLabel);
      cde.replace(SKOS_PREFLABEL, prefLabel);
    }
    return cde;
  }

  private Map<String, Object> customizeCdeValues(Map<String, Object> cde) {
    // 1. Retrieve CDE values from BioPortal
    if (cde.containsKey("_valueConstraints")
        && ((Map<String, Object>)cde.get("_valueConstraints")).containsKey("valueSets")
        && ((List)((Map<String, Object>)cde.get("_valueConstraints")).get("valueSets")).size() > 0) {
      CedarServices.integratedSearch((Map<String, Object>)cde.get("_valueConstraints"), cedarServer, apiKey);
    }
    return cde;
  }


  private Map<String, Object> getUpdatedUi(String fieldName, String fieldDescription, Map<String, Object> templateMap) {
    Map<String, Object> ui = (Map<String, Object>) templateMap.get(ModelNodeNames.UI);
    // Update order
    ((List<String>) ui.get(ModelNodeNames.UI_ORDER)).add(fieldName);
    // Update property labels
    ((Map<String, String>) ui.get(ModelNodeNames.UI_PROPERTY_LABELS)).put(fieldName, fieldName);
    // Update property descriptions
    ((Map<String, String>) ui.get(ModelNodeNames.UI_PROPERTY_DESCRIPTIONS)).put(fieldName, fieldDescription);
    return ui;
  }

  private Map<String, Object> getUpdatedPropertiesContextProperties(String fieldName, Map<String, Object> templateMap) {
    Map<String, Object> properties = (Map<String, Object>) templateMap.get(JSON_SCHEMA_PROPERTIES);
    Map<String, Object> propertiesContext = (Map<String, Object>) properties.get(JSON_LD_CONTEXT);
    Map<String, Object> propertiesContextProperties = (Map<String, Object>) propertiesContext.get(JSON_SCHEMA_PROPERTIES);
    propertiesContextProperties.put(fieldName, new HashMap<String, List<String>>(){{
      put(JSON_SCHEMA_ENUM, Arrays.asList(new String[]{"https://schema.metadatacenter.org/properties/" + UUID.randomUUID()}));
    }});
    return propertiesContextProperties;
  }

  private List<String> getUpdatedPropertiesContextRequired(String fieldName, Map<String, Object> templateMap) {
    Map<String, Object> properties = (Map<String, Object>) templateMap.get(JSON_SCHEMA_PROPERTIES);
    Map<String, Object> propertiesContext = (Map<String, Object>) properties.get(JSON_LD_CONTEXT);
    List<String> propertiesContextRequired = (List<String>) propertiesContext.get(JSON_SCHEMA_REQUIRED);
    if (!propertiesContextRequired.contains(fieldName)) {
      propertiesContextRequired.add(fieldName);
    }
    return propertiesContextRequired;
  }

  private List<String> getUpdatedRequired(String fieldName, Map<String, Object> templateMap) {
    List<String> required = new ArrayList<>((List<String>) templateMap.get(JSON_SCHEMA_REQUIRED));
    if (!required.contains(fieldName)) {
      required.add(fieldName);
    }
    return required;
  }

  @Override
  public void apply(Map<String, Object> templateMap) { // Add all fields to the template
    for (Map<String, Object> field : fields) {
      String fieldName = (String) field.get(ModelNodeNames.SCHEMA_ORG_NAME);
      String fieldDescription = (String) field.get(ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
      // Update _ui
      templateMap.replace(ModelNodeNames.UI, getUpdatedUi(fieldName, fieldDescription, templateMap));
      // Update properties.@context.properties
      ((Map<String, Object>)((Map<String, Object>) templateMap.get(JSON_SCHEMA_PROPERTIES)).get(JSON_LD_CONTEXT)).
          replace(JSON_SCHEMA_PROPERTIES, getUpdatedPropertiesContextProperties(fieldName, templateMap));
      // Update properties.@context.required
      ((Map<String, Object>)((Map<String, Object>) templateMap.get(JSON_SCHEMA_PROPERTIES)).get(JSON_LD_CONTEXT)).
          replace(JSON_SCHEMA_REQUIRED, getUpdatedPropertiesContextRequired(fieldName, templateMap));
      // Update properties
      ((Map<String, Object>) templateMap.get(JSON_SCHEMA_PROPERTIES)).put(fieldName, field);
      // Update required
      templateMap.replace(JSON_SCHEMA_REQUIRED, getUpdatedRequired(fieldName, templateMap));
    }
  }



}