package org.metadatacenter.cadsr.ingestor;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.handler.InputTypeHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class InputTypeHandlerTest {

  private InputTypeHandler handler;

  @Before
  public void init() {
    handler = new InputTypeHandler();
  }

  @Test
  public void shouldDefineTemporalField_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL)));
  }

  @Test
  public void shouldDefineTemporalField_JAVA_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513819.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL)));
  }

  @Test
  public void shouldDefineTemporalField_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3631725.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL)));
  }

  @Test
  public void shouldDefineTemporalField_DATETIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-6422999.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL)));
  }

  @Test
  public void shouldDefineTemporalField_DATE_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-5254714.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEMPORAL)));
  }

  @Test
  public void shouldDefineTextField_CHARACTER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001831.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD)));
  }

  @Test
  public void shouldDefineTextField_JAVA_STRING() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608127.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD)));
  }

  @Test
  public void shouldDefineTextField_ALPHANUMERIC() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3245384.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD)));
  }

  @Test
  public void shouldDefineTextField_ISO21090CD() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3177059.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEXTFIELD)));
  }

  @Test
  public void shouldDefineNumericField_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC)));
  }

  @Test
  public void shouldDefineNumericField_JAVA_LONG() throws Exception, UnsupportedDataElementException {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608393.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC)));
  }

  @Test
  public void shouldDefineNumericField_JAVA_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513896.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC)));
  }

  @Test
  public void shouldDefineNumericField_JAVA_DOUBLE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513777.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_NUMERIC)));
  }

  @Ignore
  @Test
  public void shouldDefineTextArea_CHARACTER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2182451.xml");
    Map<String, Object> inputType = handler.handle(dataElement).getInputType();
    // Assert
    assertThat(inputType.get(ModelNodeNames.UI_FIELD_INPUT_TYPE).toString(), is(equalTo(ModelNodeNames.FIELD_INPUT_TYPE_TEXTAREA)));
  }
}
