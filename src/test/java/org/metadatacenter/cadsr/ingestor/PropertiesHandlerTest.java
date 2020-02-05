package org.metadatacenter.cadsr.ingestor;

import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.handler.PropertiesHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

public class PropertiesHandlerTest {

  private PropertiesHandler handler;

  @Before
  public void init() {
    handler = new PropertiesHandler();
  }

  @Test
  public void shouldProducePropertiesForDAteValue_NON_ENUMERATED_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForDateValue_NON_ENUMERATED_JAVA_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513819.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForIdValue_ENUMERATED_CHARACTER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001831.xml");
    assertEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForStringValue_NON_ENUMERATED_JAVA_STRING() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608127.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForIdValue_ENUMERATED_ALPHANUMERIC() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3245384.xml");
    assertEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForStringValue_NON_ENUMERATED_ISO21090CD() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3177059.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForNumericValue_NON_ENUMERATED_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForNumericValue_NON_ENUMERATED_JAVA_LONG() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608393.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForNumericValue_NON_ENUMERATED_JAVA_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513896.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProducePropertiesForNumericValue_NON_ENUMERATED_JAVA_DOUBLE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513777.xml");
    assertNonEnumeratedType(dataElement);
  }

  private void assertNonEnumeratedType(DataElement dataElement) throws UnsupportedDataElementException {
    PropertiesHandler newHandler = handler.handle(dataElement);
    Map<String, Object> typeProperty = newHandler.getTypeProperty();
    Map<String, Object> valueProperty = newHandler.getValueProperty();
    Map<String, Object> idProperty = newHandler.getIdProperty();
    Map<String, Object> rdfsLabelProperty = newHandler.getRdfsLabelProperty();
    // Assert
    assertThat(typeProperty, is(notNullValue()));
    assertThat(valueProperty, is(notNullValue()));
    assertThat(idProperty, is(nullValue()));
    assertThat(rdfsLabelProperty, is(nullValue()));
  }

  private void assertEnumeratedType(DataElement dataElement) throws UnsupportedDataElementException {
    PropertiesHandler newHandler = handler.handle(dataElement);
    Map<String, Object> typeProperty = newHandler.getTypeProperty();
    Map<String, Object> valueProperty = newHandler.getValueProperty();
    Map<String, Object> idProperty = newHandler.getIdProperty();
    Map<String, Object> rdfsLabelProperty = newHandler.getRdfsLabelProperty();
    // Assert
    assertThat(typeProperty, is(notNullValue()));
    assertThat(valueProperty, is(nullValue()));
    assertThat(idProperty, is(notNullValue()));
    assertThat(rdfsLabelProperty, is(notNullValue()));
  }
}
