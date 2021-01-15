package org.metadatacenter.cadsr.ingestor.form;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.metadatacenter.bridge.CedarDataServices;
import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.ingestor.form.handler.TemplateFieldsHandler;
import org.metadatacenter.cadsr.ingestor.form.handler.TemplateHeaderAndFooterHandler;
import org.metadatacenter.cadsr.ingestor.util.CdeUtil;
import org.metadatacenter.cadsr.ingestor.util.CedarServerUtil;
import org.metadatacenter.cadsr.ingestor.util.Constants;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.metadatacenter.cadsr.ingestor.util.GeneralUtil;
import org.metadatacenter.config.CedarConfig;
import org.metadatacenter.config.environment.CedarEnvironmentVariableProvider;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;
import org.metadatacenter.model.SystemComponent;
import org.metadatacenter.server.logging.AppLogger;
import org.metadatacenter.server.logging.AppLoggerQueueService;
import org.metadatacenter.server.security.model.user.CedarUser;
import org.metadatacenter.server.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.metadatacenter.cadsr.ingestor.util.Constants.DEFAULT_TEMPLATE_VERSION;
import static org.metadatacenter.cadsr.ingestor.util.Constants.TEMPLATE_TYPE;

public class FormParser {

  private static final Logger logger = LoggerFactory.getLogger(FormParser.class);
  private static CedarConfig cedarConfig;
  private static CedarServer cedarServer;
  private static String apiKey;

  static {
    SystemComponent systemComponent = SystemComponent.CADSR_TOOL;
    Map<String, String> environment = CedarEnvironmentVariableProvider.getFor(systemComponent);
    cedarConfig = CedarConfig.getInstance(environment);
    cedarServer = CedarServerUtil.toCedarServerFromHostName(cedarConfig.getHost());
    // An alternative to using the apiKey of the caDSR user, which has more privileges than needed for template
    // ingestion, would be to read the user's api from the request and use a constructor new FormParser(String apiKey).
    apiKey = cedarConfig.getCaDSRAdminUserConfig().getApiKey();
  }

  public static void parseForm(Form form, final Map<String, Object> templateMap) throws IOException {

    createEmptyTemplate(templateMap);
    setTemplateIdentifier(templateMap, form.getPublicID());
    setTemplateName(templateMap, form.getLongName(), form.getPublicID());
    setTemplateDescription(templateMap, form.getPreferredDefinition());
    setTemplateHeaderAndFooter(templateMap, form, new TemplateHeaderAndFooterHandler());
    setTemplateFields(templateMap, form, new TemplateFieldsHandler(cedarServer, apiKey));
//  setFieldQuestions(fieldMap, dataElement, new UserQuestionsHandler());
    setTemplateVersion(templateMap, form.getVersion());
//  setProperties(fieldMap, dataElement, new PropertiesHandler());
//  setCategories(fieldMap, dataElement, new CategoriesHandler());
  }

  private static void createEmptyTemplate(final Map<String, Object> templateMap) {
    templateMap.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, ModelNodeValues.JSON_SCHEMA_IRI);
    templateMap.put(ModelNodeNames.JSON_LD_ID, null);
    templateMap.put(ModelNodeNames.JSON_LD_TYPE, TEMPLATE_TYPE);
    templateMap.put(ModelNodeNames.JSON_LD_CONTEXT, setDefaultContext());
    templateMap.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.OBJECT);
    templateMap.put(ModelNodeNames.JSON_SCHEMA_TITLE, "");
    templateMap.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, "");
    templateMap.put(ModelNodeNames.UI, setDefaultUi());
    templateMap.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, setDefaultProperties());
    templateMap.put(ModelNodeNames.SCHEMA_ORG_NAME, "");
    templateMap.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, "");
    templateMap.put(ModelNodeNames.PAV_CREATED_ON, null);
    templateMap.put(ModelNodeNames.PAV_CREATED_BY, null);
    templateMap.put(ModelNodeNames.PAV_LAST_UPDATED_ON, null);
    templateMap.put(ModelNodeNames.OSLC_MODIFIED_BY, null);
    templateMap.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, Constants.CEDAR_SCHEMA_VERSION);
    templateMap.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, ModelNodeValues.FALSE);
    templateMap.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, setRequired());
    templateMap.put(ModelNodeNames.PAV_VERSION, DEFAULT_TEMPLATE_VERSION);
    templateMap.put(ModelNodeNames.BIBO_STATUS, "bibo:draft"); // TODO
  }

  private static void setTemplateIdentifier(final Map<String, Object> templateMap, String content) {
    templateMap.put(ModelNodeNames.SCHEMA_ORG_IDENTIFIER, GeneralUtil.getOptionalValue(content));
  }

  private static void setTemplateName(final Map<String, Object> templateMap, String nameContent, String idContent) {
    templateMap.put(ModelNodeNames.SCHEMA_ORG_NAME, asJsonSchemaName(nameContent, idContent));
    templateMap.put(ModelNodeNames.JSON_SCHEMA_TITLE, asJsonSchemaTitle(nameContent));
    templateMap.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, asJsonSchemaDescription(nameContent));
  }

  private static void setTemplateDescription(final Map<String, Object> templateMap, String content) {
    templateMap.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, GeneralUtil.getOptionalValue(content));
  }

  private static void setTemplateHeaderAndFooter(final Map<String, Object> templateMap, Form form, TemplateHeaderAndFooterHandler templateHeaderAndFooterHandler) {
    templateHeaderAndFooterHandler.handle(form).apply(templateMap);
  }

  private static void setTemplateFields(Map<String, Object> templateMap, Form form, TemplateFieldsHandler templateFieldsHandler) throws IOException {
    templateFieldsHandler.handle(form).apply(templateMap);
  }

  private static void setTemplateVersion(final Map<String, Object> templateMap, String content) {
    if (content != null) {
      templateMap.put(ModelNodeNames.PAV_VERSION, CdeUtil.reformatVersioningNumber(content));
    }
  }

  /**
   * Utility methods
   */

  private static Object asJsonSchemaName(String nameContent, String idContent) {
    String idContentValue = GeneralUtil.getOptionalValue(idContent);
    return idContentValue.length() > 0 ? nameContent + " (" + idContentValue + ")" : nameContent;
  }

  private static Object asJsonSchemaTitle(String content) {
    return String.format("'%s' template schema", content);
  }

  private static Object asJsonSchemaDescription(String content) {
    return String.format("'%s' template schema auto-generated by CEDAR", content);
  }

  // Target: @context
  private static Map<String, Object> setDefaultContext() {
    Map<String, Object> context = Maps.newHashMap();
    context.put(ModelNodeNames.XSD, ModelNodeValues.XSD_IRI);
    context.put(ModelNodeNames.PAV, ModelNodeValues.PAV_IRI);
    context.put(ModelNodeNames.OSLC, ModelNodeValues.OSLC_IRI);
    context.put(ModelNodeNames.SCHEMA, ModelNodeValues.SCHEMA_IRI);
    context.put(ModelNodeNames.BIBO, ModelNodeValues.BIBO_IRI);
    context.put(ModelNodeNames.SCHEMA_ORG_NAME, setAtTypeString());
    context.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, setAtTypeString());
    context.put(ModelNodeNames.PAV_CREATED_ON, setAtTypeDateTime());
    context.put(ModelNodeNames.PAV_CREATED_BY, setAtTypeId());
    context.put(ModelNodeNames.PAV_LAST_UPDATED_ON, setAtTypeDateTime());
    context.put(ModelNodeNames.OSLC_MODIFIED_BY, setAtTypeId());
    return context;
  }

  // Targets: pav:createdBy, oslc:modifiedBy
  private static Map<String, Object> setAtTypeId() {
    Map<String, Object> typeId = Maps.newHashMap();
    typeId.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.LD_ID);
    return typeId;
  }

  // Targets: schema:name, schema:description
  private static Map<String, Object> setAtTypeString() {
    Map<String, Object> typeString = Maps.newHashMap();
    typeString.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.XSD_STRING);
    return typeString;
  }

  // Targets: pav:createdOn, pav:lasUpdatedOn
  private static Map<String, Object> setAtTypeDateTime() {
    Map<String, Object> typeDateTime = Maps.newHashMap();
    typeDateTime.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.XSD_DATETIME);
    return typeDateTime;
  }

  // Target: _ui
  private static Object setDefaultUi() {
    Map<String, Object> ui = Maps.newHashMap();
    ui.put(ModelNodeNames.UI_PAGES, new ArrayList());
    ui.put(ModelNodeNames.UI_ORDER, new ArrayList());
    ui.put(ModelNodeNames.UI_PROPERTY_LABELS, Maps.newHashMap());
    ui.put(ModelNodeNames.UI_PROPERTY_DESCRIPTIONS, Maps.newHashMap());
    return ui;
  }

  // Target: required
  private static Object setRequired() {
    return Arrays.asList(new String[]{
        ModelNodeNames.JSON_LD_CONTEXT,
        ModelNodeNames.JSON_LD_ID,
        ModelNodeNames.SCHEMA_IS_BASED_ON,
        ModelNodeNames.SCHEMA_ORG_NAME,
        ModelNodeNames.SCHEMA_ORG_DESCRIPTION,
        ModelNodeNames.PAV_CREATED_ON,
        ModelNodeNames.PAV_CREATED_BY,
        ModelNodeNames.PAV_LAST_UPDATED_ON,
        ModelNodeNames.OSLC_MODIFIED_BY,
    });
  }

  // Target: properties
  private static Object setDefaultProperties() {
    Map<String, Object> properties = Maps.newHashMap();
    properties.put(ModelNodeNames.JSON_LD_CONTEXT, setDefaultPropertiesContext());
    properties.put(ModelNodeNames.JSON_LD_ID, setUriStringOrNull()); // We allow null because @id is set by the server
    properties.put(ModelNodeNames.JSON_LD_TYPE, setOneOfStringOrArray());
    properties.put(ModelNodeNames.SCHEMA_IS_BASED_ON, setUriString());
    properties.put(ModelNodeNames.SCHEMA_ORG_NAME, setStringMinLength());
    properties.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, setString());
    properties.put(ModelNodeNames.PAV_DERIVED_FROM, setUriString());
    properties.put(ModelNodeNames.PAV_CREATED_ON, setDateTimeStringOrNull());
    properties.put(ModelNodeNames.PAV_CREATED_BY, setUriStringOrNull());
    properties.put(ModelNodeNames.PAV_LAST_UPDATED_ON, setDateTimeStringOrNull());
    properties.put(ModelNodeNames.OSLC_MODIFIED_BY, setUriStringOrNull());
    return properties;
  }

  // Target: properties.@context
  private static Object setDefaultPropertiesContext() {
    Map<String, Object> contextProperty = Maps.newHashMap();
    contextProperty.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.OBJECT);
    contextProperty.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, setDefaultPropertiesContextProperties());
    contextProperty.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, setDefaultPropertiesContextRequired());
    contextProperty.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, false);
    return contextProperty;
  }

  // Target: properties.@context.properties
  private static Object setDefaultPropertiesContextProperties() {
    Map<String, Object> contextProperties = Maps.newHashMap();
    contextProperties.put(ModelNodeNames.RDFS, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.RDFS_IRI));
    contextProperties.put(ModelNodeNames.XSD, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.XSD_IRI));
    contextProperties.put(ModelNodeNames.PAV, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.PAV_IRI));
    contextProperties.put(ModelNodeNames.SCHEMA, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.SCHEMA_IRI));
    contextProperties.put(ModelNodeNames.OSLC, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.OSLC_IRI));
    contextProperties.put(ModelNodeNames.SKOS, setDefaultPropertiesContextPropertiesUri(ModelNodeValues.SKOS_IRI));
    contextProperties.put(ModelNodeNames.RDFS_LABEL, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_STRING));
    contextProperties.put(ModelNodeNames.SCHEMA_IS_BASED_ON, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.LD_ID));
    contextProperties.put(ModelNodeNames.SCHEMA_ORG_NAME, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_STRING));
    contextProperties.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_STRING));
    contextProperties.put(ModelNodeNames.PAV_DERIVED_FROM, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.LD_ID));
    contextProperties.put(ModelNodeNames.PAV_CREATED_ON, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_DATETIME));
    contextProperties.put(ModelNodeNames.PAV_CREATED_BY, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.LD_ID));
    contextProperties.put(ModelNodeNames.PAV_LAST_UPDATED_ON, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_DATETIME));
    contextProperties.put(ModelNodeNames.OSLC_MODIFIED_BY, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.LD_ID));
    contextProperties.put(ModelNodeNames.SKOS_NOTATION, setDefaultPropertiesContextPropertiesAtTypeObject(ModelNodeValues.XSD_STRING));
    return contextProperties;
  }

  // Target: properties.@context.required
  private static List setDefaultPropertiesContextRequired() {
    List propertyContextRequired = new ArrayList<String>();
    propertyContextRequired.add(ModelNodeNames.XSD);
    propertyContextRequired.add(ModelNodeNames.PAV);
    propertyContextRequired.add(ModelNodeNames.SCHEMA);
    propertyContextRequired.add(ModelNodeNames.OSLC);
    propertyContextRequired.add(ModelNodeNames.SCHEMA_IS_BASED_ON);
    propertyContextRequired.add(ModelNodeNames.SCHEMA_ORG_NAME);
    propertyContextRequired.add(ModelNodeNames.SCHEMA_ORG_DESCRIPTION);
    propertyContextRequired.add(ModelNodeNames.PAV_CREATED_ON);
    propertyContextRequired.add(ModelNodeNames.PAV_CREATED_BY);
    propertyContextRequired.add(ModelNodeNames.PAV_LAST_UPDATED_ON);
    propertyContextRequired.add(ModelNodeNames.OSLC_MODIFIED_BY);
    return propertyContextRequired;
  }

  // Targets:
  // - properties.@context.properties.rdfs
  // - properties.@context.properties.xsd
  // - properties.@context.properties.pav
  // - properties.@context.properties.schema
  // - properties.@context.properties.oslc
  // - properties.@context.properties.skos
  private static Object setDefaultPropertiesContextPropertiesUri(String uri) {
    Map<String, Object> content = Maps.newHashMap();
    content.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    content.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeValues.URI);
    content.put(ModelNodeNames.JSON_SCHEMA_ENUM, Arrays.asList(new String[]{uri}));
    return content;
  }

  // Targets:
  // - properties.@context.properties.rdfs:label
  // - properties.@context.properties.schema:isBasedOn
  // - properties.@context.properties.schema:name
  // - properties.@context.properties.schema:description
  // - properties.@context.properties.pav:derivedFrom
  // - properties.@context.properties.pav:createdOn
  // - properties.@context.properties.pav:createdBy
  // - properties.@context.properties.pav:lastUpdatedOn
  // - properties.@context.properties.oslc:modifiedBy
  // - properties.@context.properties.skos:notation
  private static Object setDefaultPropertiesContextPropertiesAtTypeObject(String type) {
    // Target: X.properties.@type
    Map<String, Object> contentPropertiesAtType = Maps.newHashMap();
    contentPropertiesAtType.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    contentPropertiesAtType.put(ModelNodeNames.JSON_SCHEMA_ENUM, Arrays.asList(new String[]{type}));

    // Target: X.properties
    Map<String, Object> contentProperties = Maps.newHashMap();
    contentProperties.put(ModelNodeNames.JSON_LD_TYPE, contentPropertiesAtType);

    // Target: X
    Map<String, Object> content = Maps.newHashMap();
    content.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.OBJECT);
    content.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, contentProperties);

    return content;
  }

  private static Map<String, Object> setString() {
    Map<String, Object> str = Maps.newHashMap();
    str.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    return str;
  }

  private static Map<String, Object> setUriString() {
    Map<String, Object> uriString = Maps.newHashMap();
    uriString.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    uriString.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeValues.URI);
    return uriString;
  }

  private static Map<String, Object> setStringMinLength() {
    Map<String, Object> stringMinLength = Maps.newHashMap();
    stringMinLength.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    stringMinLength.put(ModelNodeNames.JSON_SCHEMA_MIN_LENGTH, 1);
    return stringMinLength;
  }

  private static Map<String, Object> setUriStringOrNull() {
    Map<String, Object> uriString = Maps.newHashMap();
    uriString.put(ModelNodeNames.JSON_SCHEMA_TYPE, Arrays.asList(new String[]{ModelNodeValues.STRING, ModelNodeValues.NULL}));
    uriString.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeValues.URI);
    return uriString;
  }

  private static Map<String, Object> setOneOfStringOrArray() {
    Map<String, Object> oneOf = Maps.newHashMap();
    List<Map<String, Object>> listOfStringAndArray = Lists.newArrayList();
    listOfStringAndArray.add(setUriString());
    listOfStringAndArray.add(setUriStringArray());
    oneOf.put(ModelNodeNames.JSON_SCHEMA_ONE_OF, listOfStringAndArray);
    return oneOf;
  }

  private static Map<String, Object> setUriStringArray() {
    Map<String, Object> uriStringArray = Maps.newHashMap();
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.ARRAY);
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_MIN_ITEMS, 1);
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_ITEMS, setUriString());
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_UNIQUE_ITEMS, ModelNodeValues.TRUE);
    return uriStringArray;
  }

  private static Map<String, Object> setDateTimeStringOrNull() { // We allow null because the value is set by the server
    Map<String, Object> uriString = Maps.newHashMap();
    uriString.put(ModelNodeNames.JSON_SCHEMA_TYPE, Arrays.asList(new String[]{ModelNodeValues.STRING, ModelNodeValues.NULL}));
    uriString.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeValues.DATE_TIME);
    return uriString;
  }

}
