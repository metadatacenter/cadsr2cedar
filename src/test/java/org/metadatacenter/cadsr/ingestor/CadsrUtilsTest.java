package org.metadatacenter.cadsr.ingestor;

import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CadsrUtils;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;


public class CadsrUtilsTest {

  private final String TEMPLATE_FIELD_TYPE = "https://schema.metadatacenter.org/core/TemplateField";

  @Test
  public void shouldProduceFieldMap_STRING_ENUMERATED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-5873923.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.TEXT_FIELD));
    assertThat(getValueSetsConstraintMap(fieldMap).size(), is(1));
    assertThat(getValueSetName(fieldMap, 0), is("Yes No Indicator"));
    assertThat(getValueSetNumTerms(fieldMap, 0), is(2));
    assertThat(getValueSetVsCollection(fieldMap, 0), is("CADSR-VS"));
    assertThat(getValueSetUri(fieldMap, 0), is("https://cadsr.nci.nih.gov/metadata/CADSR-VS/VD3506068v1"));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_TYPE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.SKOS_NOTATION), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(nullValue()));
    assertThat(fieldMap.containsKey(ModelNodeNames.JSON_SCHEMA_REQUIRED), is(false));
  }

  @Test
  public void shouldProduceFieldMap_STRING_NON_ENUMERATED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2182451.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assertions
    shouldProduceStringNonEnumerated(fieldMap);
  }

  @Test
  public void shouldProduceFieldMap_STRING_MAX_LENGTH_1() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2752716.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceStringNonEnumerated(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH), is(1));
  }

  @Ignore // Ignored because there are no CDEs that use this data type
  @Test
  public void shouldProduceFieldMap_NUMERIC_ANY() throws Exception { }

  @Test
  public void shouldProduceFieldMap_NUMERIC_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3017383.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:int"));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_POSITIVE_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3176123.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:int"));
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE), is(0));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_BYTE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2903311.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:byte"));
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE), is(Byte.valueOf("-128")));
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE), is(Byte.valueOf("127")));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_OCTET() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2930238.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:int"));
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE), is(0));
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE), is(255));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_SHORT_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2771274.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:short"));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_LONG_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2544997.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:long"));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_FLOAT() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:float"));
  }

  @Test
  public void shouldProduceFieldMap_NUMERIC_DOUBLE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2711953.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    shouldProduceBasicNumeric(fieldMap);
    assertThat(getValueConstraints(fieldMap).get(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE), is("xsd:double"));
  }

  @Test
  public void shouldProduceFieldMap_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.DATE));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_TYPE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(nullValue()));
    assertThat(getRequiredList(fieldMap).size(), is(1));
    assertThat(getRequired(fieldMap, 0), is(ModelNodeNames.JSON_LD_VALUE));
  }

  @Test
  public void shouldProduceFieldMap_BOOLEAN_ENUMERATED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2968037.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.TEXT_FIELD));
    assertThat(getValueSetsConstraintMap(fieldMap).size(), is(1));
    assertThat(getValueSetName(fieldMap, 0), is("True False Boolean Value Code"));
    assertThat(getValueSetNumTerms(fieldMap, 0), is(2));
    assertThat(getValueSetVsCollection(fieldMap, 0), is("CADSR-VS"));
    assertThat(getValueSetUri(fieldMap, 0), is("https://cadsr.nci.nih.gov/metadata/CADSR-VS/VD2321242v1.1"));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_TYPE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.SKOS_NOTATION), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(nullValue()));
    assertThat(fieldMap.containsKey(ModelNodeNames.JSON_SCHEMA_REQUIRED), is(false));
  }

  @Test
  public void shouldProduceFieldMap_BOOLEAN_NON_ENUMERATED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2611972.xml");
    Map<String, Object> fieldMap = CadsrUtils.getFieldMapFromDataElement(dataElement);
    // Assert
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.RADIO));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_TYPE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(nullValue()));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(getRequiredList(fieldMap).size(), is(1));
    assertThat(getRequired(fieldMap, 0), is(ModelNodeNames.JSON_LD_VALUE));
    assertThat(getValueConstraints(fieldMap).size(), is(3)); // requiredValue, multipleChoice, and literals
    assertThat(getLiteralsConstraintMap(fieldMap).size(), is(2));
    assertThat(getLiteralsConstraintMap(fieldMap).get(0).get(ModelNodeNames.VALUE_CONSTRAINTS_LABEL), is("True"));
    assertThat(getLiteralsConstraintMap(fieldMap).get(1).get(ModelNodeNames.VALUE_CONSTRAINTS_LABEL), is("False"));
  }

  @Ignore // Ignored because there are no CDEs that use this type
  @Test
  public void shouldProduceFieldMap_URI() { }

  /* Helpers */

  public void shouldProduceStringNonEnumerated(Map<String, Object> fieldMap) {
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.TEXT_FIELD));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_TYPE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(nullValue()));
    assertThat(getRequiredList(fieldMap).size(), is(1));
    assertThat(getRequired(fieldMap, 0), is(ModelNodeNames.JSON_LD_VALUE));
  }

  // Basic numeric definition. It does not require the @type field
  public void shouldProduceBasicNumeric(Map<String, Object> fieldMap) {
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_SCHEMA), is(ModelNodeValues.JSON_SCHEMA_IRI));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(fieldMap.get(ModelNodeNames.JSON_LD_TYPE), is(TEMPLATE_FIELD_TYPE));
    assertThat(fieldMap.get(ModelNodeNames.JSON_SCHEMA_TYPE), is(ModelNodeValues.OBJECT));
    assertThat(getInputType(fieldMap), is(ModelNodeValues.NUMERIC));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_ID), is(nullValue()));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.JSON_LD_VALUE), is(not(nullValue())));
    assertThat(getProperties(fieldMap).get(ModelNodeNames.RDFS_LABEL), is(nullValue()));
    assertThat(getRequiredList(fieldMap).size(), is(1));
    assertThat(getRequired(fieldMap, 0), is(ModelNodeNames.JSON_LD_VALUE));
  }

  private static String getInputType(Map<String, Object> fieldMap) {
    Map<String, Object> uiMap = (Map<String, Object>) fieldMap.get(ModelNodeNames.UI);
    return (String) uiMap.get(ModelNodeNames.UI_FIELD_INPUT_TYPE);
  }

  private Map<String, Object> getValueConstraints(Map<String, Object> fieldMap) {
    return (Map<String, Object>) fieldMap.get(ModelNodeNames.VALUE_CONSTRAINTS);
  }

  private List<Map<String, Object>> getValueSetsConstraintMap(Map<String, Object> fieldMap) {
    Map<String, Object> valueConstraints = getValueConstraints(fieldMap);
    return (List<Map<String, Object>>) valueConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS);
  }

  private String getValueSetName(Map<String, Object> fieldMap, int position) {
    Map<String, Object> vsConstraints = getValueSetsConstraintMap(fieldMap).get(position);
    return (String) vsConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_NAME);
  }

  private int getValueSetNumTerms(Map<String, Object> fieldMap, int position) {
    Map<String, Object> vsConstraints = getValueSetsConstraintMap(fieldMap).get(position);
    return (Integer) vsConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS);
  }

  private String getValueSetVsCollection(Map<String, Object> fieldMap, int position) {
    Map<String, Object> vsConstraints = getValueSetsConstraintMap(fieldMap).get(position);
    return (String) vsConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION);
  }

  private String getValueSetUri(Map<String, Object> fieldMap, int position) {
    Map<String, Object> vsConstraints = getValueSetsConstraintMap(fieldMap).get(position);
    return (String) vsConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_URI);
  }

  private List<Map<String, Object>> getClassesConstraintMap(Map<String, Object> fieldMap) {
    Map<String, Object> valueConstraints = (Map<String, Object>) fieldMap.get(ModelNodeNames.VALUE_CONSTRAINTS);
    return (List<Map<String, Object>>) valueConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_CLASSES);
  }

  private List<Map<String, String>> getLiteralsConstraintMap(Map<String, Object> fieldMap) {
    Map<String, Object> valueConstraints = getValueConstraints(fieldMap);
    return (List<Map<String, String>>) valueConstraints.get(ModelNodeNames.VALUE_CONSTRAINTS_LITERALS);
  }

  private Map<String, Object> getProperties(Map<String, Object> fieldMap) {
    return (Map<String, Object>) fieldMap.get(ModelNodeNames.JSON_SCHEMA_PROPERTIES);
  }

  private List<String> getRequiredList(Map<String, Object> fieldMap) {
    return (List<String>) fieldMap.get(ModelNodeNames.JSON_SCHEMA_REQUIRED);
  }

  private String getRequired(Map<String, Object> fieldMap, int i) {
    return getRequiredList(fieldMap).get(i);
  }
}
