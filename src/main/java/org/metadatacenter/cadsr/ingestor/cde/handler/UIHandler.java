package org.metadatacenter.cadsr.ingestor.cde.handler;

import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.cde.schema.VALUEDOMAIN;
import org.metadatacenter.cadsr.ingestor.cde.CadsrConstants;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import java.util.HashMap;
import java.util.Map;

import static org.metadatacenter.cadsr.ingestor.cde.CadsrConstants.*;

public class UIHandler implements ModelHandler {

  private String temporalGranularity;
  private String displayTimeFormat;
  private Boolean timezoneEnabled;

  public UIHandler handle(DataElement dataElement) throws UnsupportedDataElementException {
    final VALUEDOMAIN valueDomain = dataElement.getVALUEDOMAIN();
    String valueDomainType = valueDomain.getValueDomainType().getContent();
    if (ENUMERATED.equals(valueDomainType) || NON_ENUMERATED.equals(valueDomainType)) {
      handleValueDomain(valueDomain);
    } else {
      String reason = String.format("Value domain is neither enumerated nor non-enumerated = %s (Unknown)",
          valueDomainType);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    return this;
  }

  private void handleValueDomain(VALUEDOMAIN valueDomain) {
    String datatype = valueDomain.getDatatype().getContent();
    String displayFormat = valueDomain.getDisplayFormat().getContent();
    if (CadsrConstants.DATE_LIST.contains(datatype)) {
      handleDateUI(displayFormat);
    } else if (CadsrConstants.ALL_NUMERIC_LIST.contains(datatype)) {
      handleTimeUI(displayFormat);
    } else if (CadsrConstants.ALL_TEMPORAL_LIST.contains(datatype)) {
      handleDateTimeUI(displayFormat);
    }
  }

  private void handleDateUI(String displayFormat) {
    if (displayFormat != null) {
      if (DATE_GRANULARITY_YEAR_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_YEAR;
      } else if (DATE_GRANULARITY_MONTH_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_MONTH;
      } else if (DATE_GRANULARITY_DAY_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_DAY;
      } else { // default granularity
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_DAY;
      }
    }
  }

  private void handleTimeUI(String displayFormat) {
    if (displayFormat != null) {
      if (TIME_GRANULARITY_HOUR_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_HOUR;
      } else if (TIME_GRANULARITY_MINUTE_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_MINUTE;
      } else if (TIME_GRANULARITY_SECOND_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_SECOND;
      } else if (TIME_GRANULARITY_DECIMALSECOND_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_DECIMALSECOND;
      } else { // default granularity
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_SECOND;
      }

      if (DISPLAY_TIME_FORMAT_24H_FORMATS.contains(displayFormat)) {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_24H;
      } else if (DISPLAY_TIME_FORMAT_AMPM_FORMATS.contains(displayFormat)) {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_AMPM;
      } else {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_24H; // default value
      }

      timezoneEnabled = false; // default value
    }
  }

  private void handleDateTimeUI(String displayFormat) {
    if (displayFormat != null) {
      if (DATETIME_GRANULARITY_HOUR_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_HOUR;
      } else if (DATETIME_GRANULARITY_MINUTE_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_MINUTE;
      } else if (DATETIME_GRANULARITY_SECOND_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_SECOND;
      } else if (DATETIME_GRANULARITY_DECIMALSECOND_FORMATS.contains(displayFormat)) {
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_DECIMALSECOND;
      } else { // default granularity
        temporalGranularity = ModelNodeValues.TEMPORAL_GRANULARITY_SECOND;
      }

      if (DISPLAY_TIME_FORMAT_24H_FORMATS.contains(displayFormat)) {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_24H;
      } else if (DISPLAY_TIME_FORMAT_AMPM_FORMATS.contains(displayFormat)) {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_AMPM;
      } else {
        displayTimeFormat = ModelNodeValues.TIME_FORMAT_24H; // default value
      }

      timezoneEnabled = false; // default value
    }
  }

  @Override
  public void apply(Map<String, Object> fieldObject) {
    Map<String, Object> ui = new HashMap((Map<String, Object>) fieldObject.get(ModelNodeNames.UI));
    if (temporalGranularity != null) {
      ui.put(ModelNodeNames.UI_TEMPORAL_GRANULARITY, temporalGranularity);
    }
    if (displayTimeFormat != null) {
      ui.put(ModelNodeNames.UI_DISPLAY_TIME_FORMAT, displayTimeFormat);
    }
    if (timezoneEnabled != null) {
      ui.put(ModelNodeNames.UI_TIMEZONE_ENABLED, timezoneEnabled);
    }
    fieldObject.put(ModelNodeNames.UI, ui);
  }
}
