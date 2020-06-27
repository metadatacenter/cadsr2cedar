package org.metadatacenter.cadsr.ingestor.cde.handler;

import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CadsrConstants;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import java.util.Collections;
import java.util.Map;

import static org.metadatacenter.cadsr.ingestor.cde.CadsrConstants.ENUMERATED;
import static org.metadatacenter.cadsr.ingestor.cde.CadsrConstants.NON_ENUMERATED;

public class InputTypeHandler implements ModelHandler {

  private final Map<String, Object> inputType = Maps.newHashMap();

  public InputTypeHandler handle(DataElement dataElement) throws UnsupportedDataElementException {
    String valueDomainType = dataElement.getVALUEDOMAIN().getValueDomainType().getContent();
    if (ENUMERATED.equals(valueDomainType)) {
      handleEnumeratedType();
    } else if (NON_ENUMERATED.equals(valueDomainType)) {
      handleNonEnumeratedType(dataElement);
    } else {
      String reason = String.format("Value domain is neither enumerated or non-enumerated = %s (Unknown)",
          valueDomainType);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    return this;
  }

  private void handleEnumeratedType() {
    inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeValues.TEXT_FIELD);
  }

  private void handleNonEnumeratedType(DataElement dataElement) throws UnsupportedDataElementException {
    String datatype = dataElement.getVALUEDOMAIN().getDatatype().getContent();
    if (CadsrConstants.ALL_STRING_LIST.contains(datatype)) {
      inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeValues.TEXT_FIELD);
    } else if (CadsrConstants.ALL_NUMERIC_LIST.contains(datatype)) {
      inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeValues.NUMERIC);
    } else if (CadsrConstants.ALL_TEMPORAL_LIST.contains(datatype)) {
      inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL);
    } else if (CadsrConstants.ALL_BOOLEAN_LIST.contains(datatype)) {
      inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeValues.RADIO);
    } else if (CadsrConstants.ALL_URI_LIST.contains(datatype)) {
      inputType.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeValues.LINK);
    } else {
      String reason = String.format("A non-enumerated %s is not supported (Unsupported)", datatype);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
  }

  public Map<String, Object> getInputType() {
    return Collections.unmodifiableMap(inputType);
  }

  @Override
  public void apply(final Map<String, Object> fieldObject) {
    fieldObject.put(ModelNodeNames.UI, getInputType());
  }
}
