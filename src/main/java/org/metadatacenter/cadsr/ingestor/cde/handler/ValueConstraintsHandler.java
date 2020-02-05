package org.metadatacenter.cadsr.ingestor.cde.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.cde.schema.VALUEDOMAIN;
import org.metadatacenter.cadsr.ingestor.cde.CadsrDatatypes;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataTypeException;
import org.metadatacenter.model.ModelNodeNames;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ValueConstraintsHandler implements ModelHandler {

  private static final String ENUMERATED = "Enumerated";
  private static final String NON_ENUMERATED = "NonEnumerated";

  private static final String XSD_DECIMAL = "xsd:decimal";
  private static final String XSD_LONG = "xsd:long";
  private static final String XSD_INT = "xsd:int";
  private static final String XSD_DOUBLE = "xsd:double";
  private static final String XSD_BYTE = "xsd:byte";
  private static final String XSD_SHORT = "xsd:short";
  private static final String XSD_FLOAT = "xsd:float";

  private Integer minLength;
  private Integer maxLength;
  private Integer decimalPlace;
  private Number minValue;
  private Number maxValue;
  private String unitOfMeasure;
  private String numberType;
  private List<Map<String, String>> literals;

  public ValueConstraintsHandler handle(DataElement dataElement) throws UnsupportedDataElementException {
    final VALUEDOMAIN valueDomain = dataElement.getVALUEDOMAIN();
    String valueDomainType = valueDomain.getValueDomainType().getContent();
    if (ENUMERATED.equals(valueDomainType) || NON_ENUMERATED.equals(valueDomainType)) {
      try {
        handleValueDomain(valueDomain);
      } catch (UnsupportedDataTypeException e) {
        throw new UnsupportedDataElementException(dataElement, e.getMessage());
      }
    } else {
      String reason = String.format("Value domain is not either enumerated or non-enumerated = %s (Unknown)",
          valueDomainType);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    return this;
  }

  private void handleValueDomain(VALUEDOMAIN valueDomain) throws UnsupportedDataTypeException {
    String datatype = valueDomain.getDatatype().getContent();
    if (CadsrDatatypes.ALL_STRING_LIST.contains(datatype)) {
      handleStringValueConstraints(datatype, valueDomain);
    } else if (CadsrDatatypes.ALL_NUMERIC_LIST.contains(datatype)) {
      handleNumericValueConstraints(datatype, valueDomain);
    } else if (CadsrDatatypes.ALL_DATE_LIST.contains(datatype)) {
      // Do nothing. There is no need to set value constraints for date fields
    } else if (CadsrDatatypes.ALL_BOOLEAN_LIST.contains(datatype)) {
      handleBooleanValueConstraints();
    } else if (CadsrDatatypes.ALL_URI_LIST.contains(datatype)) {
      // Do nothing. There is no need to set value constraints for URI fields
    } else {
      throw new UnsupportedDataTypeException(datatype);
    }
  }

  private void handleStringValueConstraints(String datatype, VALUEDOMAIN valueDomain) throws UnsupportedDataTypeException {
    if (CadsrDatatypes.STRING_LIST.contains(datatype)) {
      minLength = getMinimumLength(valueDomain);
      maxLength = getMaximumLength(valueDomain);
    } else if (CadsrDatatypes.STRING_MAX_LENGTH_1_LIST.contains(datatype)) {
      maxLength = 1;
    } else {
      throw new UnsupportedDataTypeException(datatype);
    }
  }

  private void handleNumericValueConstraints(String datatype, VALUEDOMAIN valueDomain) throws UnsupportedDataTypeException {
    numberType = getNumberType(datatype);
    minValue = getMinimumValue(valueDomain.getMinimumValue().getContent(), datatype, numberType);
    maxValue = getMaximumValue(valueDomain.getMaximumValue().getContent(), datatype, numberType);
    decimalPlace = getDecimalPlace(valueDomain);
    unitOfMeasure = getUnitOfMeasure(valueDomain);
  }

  private void handleBooleanValueConstraints() {
    Map<String, String> yesOption = Maps.newHashMap();
    yesOption.put("label", "True");
    Map<String, String> noOption = Maps.newHashMap();
    noOption.put("label", "False");
    literals = Arrays.asList(yesOption, noOption);
  }

  private static Integer getMinimumLength(VALUEDOMAIN valueDomain) {
    String value = valueDomain.getMinimumLength().getContent();
    if (!Strings.isNullOrEmpty(value)) {
      return Integer.parseInt(value);
    } else {
      return null;
    }
  }

  private static Integer getMaximumLength(VALUEDOMAIN valueDomain) {
    String value = valueDomain.getMaximumLength().getContent();
    if (!Strings.isNullOrEmpty(value)) {
      return Integer.parseInt(value);
    } else {
      return null;
    }
  }

  @Nullable
  private static String getNumberType(String numericDataType) throws UnsupportedDataTypeException {
    if (!Strings.isNullOrEmpty(numericDataType)) {
      if (CadsrDatatypes.NUMERIC_ANY_LIST.contains(numericDataType)) {
        return XSD_DECIMAL;
      } else if (CadsrDatatypes.NUMERIC_INTEGER_LIST.contains(numericDataType)) {
        return XSD_INT;
      } else if (CadsrDatatypes.NUMERIC_POSITIVE_INTEGER_LIST.contains(numericDataType)) {
        return XSD_INT;
      } else if (CadsrDatatypes.NUMERIC_BYTE_LIST.contains(numericDataType)) {
        return XSD_BYTE;
      } else if (CadsrDatatypes.NUMERIC_OCTET_LIST.contains(numericDataType)) {
        return XSD_INT;
      } else if (CadsrDatatypes.NUMERIC_SHORT_INTEGER_LIST.contains(numericDataType)) {
        return XSD_SHORT;
      } else if (CadsrDatatypes.NUMERIC_LONG_INTEGER_LIST.contains(numericDataType)) {
        return XSD_LONG;
      } else if (CadsrDatatypes.NUMERIC_FLOAT_LIST.contains(numericDataType)) {
        return XSD_FLOAT;
      } else if (CadsrDatatypes.NUMERIC_DOUBLE_LIST.contains(numericDataType)) {
        return XSD_DOUBLE;
      } else {
        throw new UnsupportedDataTypeException(numericDataType);
      }
    } else {
      return null;
    }
  }

  @Nullable
  private static Number getMinimumValue(String minValue, String numericDataType, String numberType)
      throws UnsupportedDataTypeException {
    if (Strings.isNullOrEmpty(minValue)) { // If the XML does not specify a minimum value
      if (CadsrDatatypes.NUMERIC_POSITIVE_INTEGER_LIST.contains(numericDataType)) {
        minValue = "0";
      } else if (CadsrDatatypes.NUMERIC_BYTE_LIST.contains(numericDataType)) {
        minValue = "-128";
      } else if (CadsrDatatypes.NUMERIC_OCTET_LIST.contains(numericDataType)) {
        minValue = "0";
      }
    }
    return getNumber(minValue, numberType);
  }

  @Nullable
  private static Number getMaximumValue(String maxValue, String numericDataType, String numberType)
      throws UnsupportedDataTypeException {
    if (Strings.isNullOrEmpty(maxValue)) { // If the XML does not specify a maximum value
      if (CadsrDatatypes.NUMERIC_BYTE_LIST.contains(numericDataType)) {
        maxValue = "127";
      } else if (CadsrDatatypes.NUMERIC_OCTET_LIST.contains(numericDataType)) {
        maxValue = "255";
      }
    }
    return getNumber(maxValue, numberType);
  }

  @Nullable
  private static Number getNumber(String numberValue, String numberType) throws UnsupportedDataTypeException {
    if (!Strings.isNullOrEmpty(numberValue)) {
      if (XSD_DECIMAL.equals(numberType)) {
        return new BigDecimal(numberValue);
      } else if (XSD_INT.equals(numberType)) {
        return Integer.valueOf(numberValue);
      } else if (XSD_BYTE.equals(numberType)) {
        return Byte.valueOf(numberValue);
      } else if (XSD_SHORT.equals(numberType)) {
        return Short.valueOf(numberValue);
      } else if (XSD_LONG.equals(numberType)) {
        return Long.valueOf(numberValue);
      } else if (XSD_FLOAT.equals(numberType)) {
        return Float.valueOf(numberValue);
      } else if (XSD_DOUBLE.equals(numberType)) {
        return Double.valueOf(numberValue);
      } else {
        throw new UnsupportedDataTypeException(numberType);
      }
    } else {
      return null;
    }
  }

  private static Integer getDecimalPlace(VALUEDOMAIN valueDomain) {
    String decimalPlace = valueDomain.getDecimalPlace().getContent();
    if (!Strings.isNullOrEmpty(decimalPlace)) {
      return Integer.parseInt(decimalPlace);
    } else {
      return null;
    }
  }

  @Nullable
  private static String getUnitOfMeasure(VALUEDOMAIN valueDomain) {
    String unitOfMeasure = valueDomain.getUnitOfMeasure().getContent();
    if (!Strings.isNullOrEmpty(unitOfMeasure)) {
      return unitOfMeasure;
    } else {
      return null;
    }
  }

  @Override
  public void apply(Map<String, Object> fieldObject) {
    Map<String, Object> valueConstraints = (Map<String, Object>) fieldObject.get(ModelNodeNames.VALUE_CONSTRAINTS);
    if (minLength != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_MIN_STRING_LENGTH, minLength);
    }
    if (maxLength != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_MAX_STRING_LENGTH, maxLength);
    }
    if (decimalPlace != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_DECIMAL_PLACE, decimalPlace);
    }
    if (minValue != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_MIN_NUMBER_VALUE, minValue);
    }
    if (maxValue != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_MAX_NUMBER_VALUE, maxValue);
    }
    if (numberType != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_NUMBER_TYPE, numberType);
    }
    if (unitOfMeasure != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_UNIT_OF_MEASURE, unitOfMeasure);
    }
    if (literals != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_LITERALS, literals);
    }

  }
}
