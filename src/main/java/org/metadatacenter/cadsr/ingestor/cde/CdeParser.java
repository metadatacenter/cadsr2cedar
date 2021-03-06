package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.handler.*;
import org.metadatacenter.cadsr.ingestor.exception.UnknownSeparatorException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.cadsr.ingestor.util.CdeUtil;
import org.metadatacenter.cadsr.ingestor.util.CedarFieldUtil;
import org.metadatacenter.cadsr.ingestor.util.Constants;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class CdeParser {

  private static final Logger logger = LoggerFactory.getLogger(CdeParser.class);
  private static Map<String, Map<String, Object>> cdeCache;
  private static int cacheHitsCount = 0;
  private static int cacheMissesCount = 0;

  static {
    cdeCache = new HashMap<>(); // Cache to avoid parsing the data element multiple times
  }

  public static void parseDataElement(DataElement dataElement, final Map<String, Object> fieldMap) throws
      UnsupportedDataElementException, UnknownSeparatorException {

    String cdeId = CdeUtil.generateCdeUniqueId(dataElement);
    if (cdeCache.containsKey(cdeId)) {
      cacheHitsCount++;
      fieldMap.putAll(cdeCache.get(cdeId));
    }
    else {
      cacheMissesCount++;
      createEmptyField(fieldMap);
      setFieldIdentifier(fieldMap, dataElement.getPUBLICID().getContent());
      setFieldName(fieldMap, dataElement.getLONGNAME().getContent(), dataElement.getPUBLICID().getContent());
      setFieldDescription(fieldMap, dataElement.getPREFERREDDEFINITION().getContent());
      setFieldQuestions(fieldMap, dataElement, new UserQuestionsHandler());
      setInputType(fieldMap, dataElement, new InputTypeHandler());
      setVersion(fieldMap, dataElement, new VersionHandler());
      setValueConstraints(fieldMap, dataElement, new ValueConstraintsHandler());
      setUI(fieldMap, dataElement, new UIHandler());
      setProperties(fieldMap, dataElement, new PropertiesHandler());
      setPermissibleValues(fieldMap, dataElement, new PermissibleValuesHandler());
      setCategories(fieldMap, dataElement, new CategoriesHandler());
      // Add to cache
      cdeCache.put(cdeId, fieldMap);
    }
    //logger.info("CDE cache hits: " + cacheHitsCount + " CDE cache misses: " + cacheMissesCount);
  }

  private static void setFieldIdentifier(final Map<String, Object> fieldMap, String content) {
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_IDENTIFIER, content);
  }

  private static void setFieldName(final Map<String, Object> fieldMap, String nameContent, String idContent) {
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_NAME, asJsonSchemaName(nameContent, idContent));
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_TITLE, CedarFieldUtil.asJsonSchemaTitle(nameContent));
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, CedarFieldUtil.asJsonSchemaDescription(nameContent));
  }

  private static Object asJsonSchemaName(String nameContent, String idContent) {
    return String.format("%s (%s)", nameContent, idContent);
  }

  private static void setFieldDescription(final Map<String, Object> fieldMap, String content) {
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, content);
  }

  private static void setFieldQuestions(final Map<String, Object> fieldMap, DataElement dataElement,
                                        UserQuestionsHandler
                                            userQuestionsHandler) throws UnsupportedDataElementException {
    userQuestionsHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setInputType(final Map<String, Object> fieldMap, DataElement dataElement, InputTypeHandler
      inputTypeHandler) throws UnsupportedDataElementException {
    inputTypeHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setProperties(Map<String, Object> fieldMap, DataElement dataElement, PropertiesHandler
      propertiesHandler) throws UnsupportedDataElementException {
    propertiesHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setPermissibleValues(Map<String, Object> fieldMap, DataElement dataElement,
                                           PermissibleValuesHandler
                                               permissibleValuesHandler) throws UnsupportedDataElementException,
      UnknownSeparatorException {
    permissibleValuesHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setValueConstraints(Map<String, Object> fieldMap, DataElement dataElement, ValueConstraintsHandler
      valueConstraintsHandler) throws UnsupportedDataElementException {
    valueConstraintsHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setUI(Map<String, Object> fieldMap, DataElement dataElement, UIHandler uiHandler) throws UnsupportedDataElementException {
    uiHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setVersion(Map<String, Object> fieldMap, DataElement dataElement, VersionHandler
      versionHandler) throws UnsupportedDataElementException {
    versionHandler.handle(dataElement).apply(fieldMap);
  }

  private static void setCategories(Map<String, Object> fieldMap, DataElement dataElement, CategoriesHandler
      categoriesHandler) {
    categoriesHandler.handle(dataElement).apply(fieldMap);
  }


  private static void createEmptyField(final Map<String, Object> fieldMap) {
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, ModelNodeValues.JSON_SCHEMA_IRI);
    fieldMap.put(ModelNodeNames.JSON_LD_ID, null);
    fieldMap.put(ModelNodeNames.JSON_LD_TYPE, "https://schema.metadatacenter.org/core/TemplateField");
    fieldMap.put(ModelNodeNames.JSON_LD_CONTEXT, setDefaultContext());
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.OBJECT);
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_TITLE, "");
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, "");
    fieldMap.put(ModelNodeNames.UI, setDefaultUi());
    fieldMap.put(ModelNodeNames.VALUE_CONSTRAINTS, setDefaultValueConstraints());
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, null);
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_NAME, "");
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, "");
    fieldMap.put(ModelNodeNames.PAV_CREATED_ON, null);
    fieldMap.put(ModelNodeNames.PAV_CREATED_BY, null);
    fieldMap.put(ModelNodeNames.PAV_LAST_UPDATED_ON, null);
    fieldMap.put(ModelNodeNames.OSLC_MODIFIED_BY, null);
    fieldMap.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, Constants.CEDAR_SCHEMA_VERSION);
    fieldMap.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, ModelNodeValues.FALSE);
  }

  private static Map<String, Object> setDefaultContext() {
    Map<String, Object> context = Maps.newHashMap();
    context.put(ModelNodeNames.XSD, ModelNodeValues.XSD_IRI);
    context.put(ModelNodeNames.PAV, ModelNodeValues.PAV_IRI);
    context.put(ModelNodeNames.OSLC, ModelNodeValues.OSLC_IRI);
    context.put(ModelNodeNames.SCHEMA, ModelNodeValues.SCHEMA_IRI);
    context.put(ModelNodeNames.BIBO, ModelNodeValues.BIBO_IRI);
    context.put(ModelNodeNames.SKOS, ModelNodeValues.SKOS_IRI);
    context.put(ModelNodeNames.SCHEMA_ORG_NAME, setAtTypeString());
    context.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, setAtTypeString());
    context.put(ModelNodeNames.SKOS_PREFLABEL, setAtTypeString());
    context.put(ModelNodeNames.SKOS_ALTLABEL, setAtTypeString());
    context.put(ModelNodeNames.PAV_CREATED_ON, setAtTypeDateTime());
    context.put(ModelNodeNames.PAV_CREATED_BY, setAtTypeId());
    context.put(ModelNodeNames.PAV_LAST_UPDATED_ON, setAtTypeDateTime());
    context.put(ModelNodeNames.OSLC_MODIFIED_BY, setAtTypeId());
    return context;
  }

  private static Map<String, Object> setAtTypeId() {
    Map<String, Object> typeId = Maps.newHashMap();
    typeId.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.LD_ID);
    return typeId;
  }

  private static Map<String, Object> setAtTypeString() {
    Map<String, Object> typeString = Maps.newHashMap();
    typeString.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.XSD_STRING);
    return typeString;
  }

  private static Map<String, Object> setAtTypeDateTime() {
    Map<String, Object> typeDateTime = Maps.newHashMap();
    typeDateTime.put(ModelNodeNames.JSON_LD_TYPE, ModelNodeValues.XSD_DATETIME);
    return typeDateTime;
  }

  private static Object setDefaultUi() {
    Map<String, Object> inputType = Maps.newHashMap();
    inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, null);
    return inputType;
  }

  private static Map<String, Object> setDefaultValueConstraints() {
    Map<String, Object> valueConstraints = Maps.newHashMap();
    valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_REQUIRED_VALUE, ModelNodeValues.FALSE);
    valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_MULTIPLE_CHOICE, ModelNodeValues.FALSE);
    return valueConstraints;
  }
}
