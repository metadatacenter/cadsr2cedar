package org.metadatacenter.cadsr.ingestor;

import com.google.common.collect.Maps;
import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CdeParser;
import org.metadatacenter.cadsr.ingestor.cde.handler.PermissibleValuesHandler;
import org.metadatacenter.cadsr.ingestor.cde.handler.UIHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnknownSeparatorException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class UIHandlerTest {

  private UIHandler handler;

  @Before
  public void init() {
    handler = new UIHandler();
  }

  @Test
  public void shouldDefineTemporalGranularityDate_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    String temporalGranularity = handler.handle(dataElement).getTemporalGranularity();
    assertThat(temporalGranularity, is(anyOf(
        is(ModelNodeValues.TEMPORAL_GRANULARITY_YEAR),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_MONTH),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_DAY))));
  }

  @Test
  public void shouldNotDefineDisplayTimeFormat_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    String displayTimeFormat = handler.handle(dataElement).getDisplayTimeFormat();
    assertThat(displayTimeFormat, isEmptyOrNullString());
  }

  @Test
  public void shouldNotDefineTimezoneEnabled_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    Boolean timezoneEnabled = handler.handle(dataElement).getTimezoneEnabled();
    assertThat(timezoneEnabled, is(nullValue()));
  }

  @Test
  public void shouldDefineTemporalGranularityTime_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3631725.xml");
    String temporalGranularity = handler.handle(dataElement).getTemporalGranularity();
    assertThat(temporalGranularity, is(anyOf(
        is(ModelNodeValues.TEMPORAL_GRANULARITY_HOUR),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_MINUTE),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_SECOND),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_DECIMALSECOND))));
  }

  @Test
  public void shouldDefineTemporalGranularityTime_DATETIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-6422999.xml");
    String temporalGranularity = handler.handle(dataElement).getTemporalGranularity();
    assertThat(temporalGranularity, is(anyOf(
        is(ModelNodeValues.TEMPORAL_GRANULARITY_HOUR),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_MINUTE),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_SECOND),
        is(ModelNodeValues.TEMPORAL_GRANULARITY_DECIMALSECOND))));
  }

  @Test
  public void shouldDefineDisplayTimeFormat_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3631725.xml");
    String displayTimeFormat = handler.handle(dataElement).getDisplayTimeFormat();
    assertThat(displayTimeFormat, is(anyOf(is(ModelNodeValues.TIME_FORMAT_24H), is(ModelNodeValues.TIME_FORMAT_12H))));
  }

  @Test
  public void shouldDefineDisplayTimeFormatTime_DATETIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-6422999.xml");
    String displayTimeFormat = handler.handle(dataElement).getDisplayTimeFormat();
    assertThat(displayTimeFormat, is(anyOf(is(ModelNodeValues.TIME_FORMAT_24H), is(ModelNodeValues.TIME_FORMAT_12H))));
  }

  @Test
  public void shouldDefineTimezoneEnabled_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3631725.xml");
    Boolean timezoneEnabled = handler.handle(dataElement).getTimezoneEnabled();
    assertThat(timezoneEnabled, anyOf(equalTo(true), equalTo(false)));
  }

  @Test
  public void shouldDefineTimezoneEnabled_DATETIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-6422999.xml");
    Boolean timezoneEnabled = handler.handle(dataElement).getTimezoneEnabled();
    assertThat(timezoneEnabled, anyOf(equalTo(true), equalTo(false)));
  }

  @Test
  public void shouldNotDefineTemporalGranularityDate_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    String temporalGranularity = handler.handle(dataElement).getTemporalGranularity();
    assertThat(temporalGranularity, isEmptyOrNullString());
  }

  @Test
  public void shouldNotDefineDisplayTimeFormat_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    String displayTimeFormat = handler.handle(dataElement).getDisplayTimeFormat();
    assertThat(displayTimeFormat, isEmptyOrNullString());
  }

  @Test
  public void shouldNotDefineTimezoneEnabled_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    Boolean timezoneEnabled = handler.handle(dataElement).getTimezoneEnabled();
    assertThat(timezoneEnabled, is(nullValue()));
  }

  @Test
  public void shouldNotDefineTimezoneEnabled_CHARACTER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001831.xml");
    Boolean timezoneEnabled = handler.handle(dataElement).getTimezoneEnabled();
    assertThat(timezoneEnabled, is(nullValue()));
  }
  
}
