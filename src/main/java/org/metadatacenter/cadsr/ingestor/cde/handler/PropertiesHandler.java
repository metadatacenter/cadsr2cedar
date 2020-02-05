package org.metadatacenter.cadsr.ingestor.cde.handler;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CadsrDatatypes;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PropertiesHandler implements ModelHandler {

  private static final String ENUMERATED = "Enumerated";
  private static final String NON_ENUMERATED = "NonEnumerated";

  private final Map<String, Object> typeProperty = Maps.newHashMap();
  private final Map<String, Object> valueProperty = Maps.newHashMap();
  private final Map<String, Object> idProperty = Maps.newHashMap();
  private final Map<String, Object> rdfsLabelProperty = Maps.newHashMap();
  private final Map<String, Object> skosNotationProperty = Maps.newHashMap();

  public PropertiesHandler handle(DataElement dataElement) throws UnsupportedDataElementException {
    String valueDomainType = dataElement.getVALUEDOMAIN().getValueDomainType().getContent();
    if (ENUMERATED.equals(valueDomainType)) {
      handleEnumeratedType();
    } else if (NON_ENUMERATED.equals(valueDomainType)) {
      handleNonEnumeratedType(dataElement);
    } else {
      String reason = String.format("Value domain is not either enumerated or non-enumerated = %s (Unknown)",
          valueDomainType);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    return this;
  }

  private void handleEnumeratedType() {
    typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setOneOfStringOrArray());
    idProperty.put(ModelNodeNames.JSON_LD_ID, setUriString());
    rdfsLabelProperty.put(ModelNodeNames.RDFS_LABEL, setTypeStringOrNull());
    skosNotationProperty.put(ModelNodeNames.SKOS_NOTATION, setTypeStringOrNull());
  }

  private void handleNonEnumeratedType(DataElement dataElement) throws UnsupportedDataElementException {
    String datatype = dataElement.getVALUEDOMAIN().getDatatype().getContent();
    if (CadsrDatatypes.ALL_STRING_LIST.contains(datatype)) {
      typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setOneOfStringOrArray());
      valueProperty.put(ModelNodeNames.JSON_LD_VALUE, setTypeStringOrNull());
    } else if (CadsrDatatypes.ALL_NUMERIC_LIST.contains(datatype)) {
      typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setUriString());
      valueProperty.put(ModelNodeNames.JSON_LD_VALUE, setTypeStringOrNull());
    } else if (CadsrDatatypes.ALL_DATE_LIST.contains(datatype)) {
      typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setUriString());
      valueProperty.put(ModelNodeNames.JSON_LD_VALUE, setTypeStringOrNull());
    } else if (CadsrDatatypes.ALL_BOOLEAN_LIST.contains(datatype)) {
      typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setUriString());
      valueProperty.put(ModelNodeNames.JSON_LD_VALUE, setTypeStringOrNull());
    } else if (CadsrDatatypes.ALL_URI_LIST.contains(datatype)) {
      typeProperty.put(ModelNodeNames.JSON_LD_TYPE, setOneOfStringOrArray());
      idProperty.put(ModelNodeNames.JSON_LD_ID, setUriString());
    } else {
      String reason = String.format("A non-enumerated %s is not supported (Unsupported)", datatype);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
  }

  private static Map<String, Object> setOneOfStringOrArray() {
    Map<String, Object> oneOf = Maps.newHashMap();
    List<Map<String, Object>> listOfStringAndArray = Lists.newArrayList();
    listOfStringAndArray.add(setUriString());
    listOfStringAndArray.add(setUriStringArray());
    oneOf.put(ModelNodeNames.JSON_SCHEMA_ONE_OF, listOfStringAndArray);
    return oneOf;
  }

  private static Map<String, Object> setUriString() {
    Map<String, Object> uriString = Maps.newHashMap();
    uriString.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.STRING);
    uriString.put(ModelNodeNames.JSON_SCHEMA_FORMAT, ModelNodeValues.URI);
    return uriString;
  }

  private static Map<String, Object> setUriStringArray() {
    Map<String, Object> uriStringArray = Maps.newHashMap();
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.ARRAY);
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_MIN_ITEMS, 1);
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_ITEMS, setUriString());
    uriStringArray.put(ModelNodeNames.JSON_SCHEMA_UNIQUE_ITEMS, ModelNodeValues.TRUE);
    return uriStringArray;
  }

  private Object setTypeStringOrNull() {
    Map<String, Object> typeStringOrNull = Maps.newHashMap();
    typeStringOrNull.put(ModelNodeNames.JSON_SCHEMA_TYPE,
        Lists.newArrayList(ModelNodeValues.STRING, ModelNodeValues.NULL));
    return typeStringOrNull;
  }

  @Nullable
  public Map<String, Object> getTypeProperty() {
    Map<String, Object> toReturn = null;
    if (!typeProperty.isEmpty()) {
      toReturn = Collections.unmodifiableMap(typeProperty);
    }
    return toReturn;
  }

  @Nullable
  public Map<String, Object> getValueProperty() {
    Map<String, Object> toReturn = null;
    if (!valueProperty.isEmpty()) {
      toReturn = Collections.unmodifiableMap(valueProperty);
    }
    return toReturn;
  }

  @Nullable
  public Map<String, Object> getIdProperty() {
    Map<String, Object> toReturn = null;
    if (!idProperty.isEmpty()) {
      toReturn = Collections.unmodifiableMap(idProperty);
    }
    return toReturn;
  }

  @Nullable
  public Map<String, Object> getRdfsLabelProperty() {
    Map<String, Object> toReturn = null;
    if (!rdfsLabelProperty.isEmpty()) {
      toReturn = Collections.unmodifiableMap(rdfsLabelProperty);
    }
    return toReturn;
  }

  @Nullable
  public Map<String, Object> getSkosNotationProperty() {
    Map<String, Object> toReturn = null;
    if (!skosNotationProperty.isEmpty()) {
      toReturn = Collections.unmodifiableMap(skosNotationProperty);
    }
    return toReturn;
  }

  @Override
  public void apply(final Map<String, Object> fieldObject) {
    fieldObject.put(ModelNodeNames.JSON_SCHEMA_PROPERTIES, getPropertiesObject());
    if (!getRequiredList().isEmpty()) {
      fieldObject.put(ModelNodeNames.JSON_SCHEMA_REQUIRED, getRequiredList());
    }
  }

  private Map<String, Object> getPropertiesObject() {
    Map<String, Object> properties = Maps.newHashMap();
    putIfNotNull(properties, getTypeProperty());
    putIfNotNull(properties, getValueProperty());
    putIfNotNull(properties, getIdProperty());
    putIfNotNull(properties, getRdfsLabelProperty());
    putIfNotNull(properties, getSkosNotationProperty());
    return properties;
  }

  private static void putIfNotNull(final Map<String, Object> properties, Object property) {
    if (property != null) {
      properties.putAll((Map<String, Object>) property);
    }
  }

  private List<String> getRequiredList() {
    List<String> requiredList = Lists.newArrayList();
    addIfNotNull(requiredList, ModelNodeNames.JSON_LD_VALUE, getValueProperty());
    return requiredList;
  }

  private static void addIfNotNull(final List<String> list, String item, Object value) {
    if (value != null) {
      list.add(item);
    }
  }
}
