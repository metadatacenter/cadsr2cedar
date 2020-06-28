package org.metadatacenter.cadsr.ingestor;

import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.handler.PermissibleValuesHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnknownSeparatorException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class ValueConstraintsHandlerTest {

  private PermissibleValuesHandler handler;

  @Before
  public void init() {
    handler = new PermissibleValuesHandler();
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_JAVA_DATE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513819.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3631725.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_DATETIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-6422999.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_DATE_TIME() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-5254714.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProduceVCs_ENUMERATED_CHARACTER() throws Exception, UnknownSeparatorException {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-5873923.xml");
    PermissibleValuesHandler newHandler = handler.handle(dataElement);
    List<Map<String, Object>> ontologies = newHandler.getOntologies();
    List<Map<String, Object>> valueSets = newHandler.getValueSets();
    List<Map<String, Object>> branches = newHandler.getBranches();
    List<Map<String, Object>> classes = newHandler.getClasses();
    // Assert
    assertThat(ontologies.isEmpty(), is(true));
    assertThat(valueSets.isEmpty(), is(false));
    assertThat(branches.isEmpty(), is(true));
    assertThat(classes.isEmpty(), is(true));
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_JAVA_STRING() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608127.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldProduceVCs_ENUMERATED_ALPHANUMERIC_PARTIALLY_ANNOTATED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3245384.xml");
    try {
      PermissibleValuesHandler newHandler = handler.handle(dataElement);
      List<Map<String, Object>> ontologies = newHandler.getOntologies();
      List<Map<String, Object>> valueSets = newHandler.getValueSets();
      List<Map<String, Object>> branches = newHandler.getBranches();
      List<Map<String, Object>> classes = newHandler.getClasses();
      // Assert
      assertThat(ontologies.isEmpty(), is(true));
      assertThat(valueSets.isEmpty(), is(false));
      assertThat(branches.isEmpty(), is(true));
      assertThat(classes.isEmpty(), is(true));
    } catch (UnsupportedDataElementException e) {
      String message = e.getMessage();
      assertThat(message, is("Skipping 'Orthodontist Model Use Use Frequency' (ID: 3245384) - Reason: " +
          "Controlled term for value 'OCCASIONALLY' is null (NullValue)"));
      throw e;
    }
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_ISO21090CD() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-3177059.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_NUMBER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2002061.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_JAVA_LONG() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2608393.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_JAVA_INTEGER() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513896.xml");
    assertNonEnumeratedType(dataElement);
  }

  @Test
  public void shouldNotProduceVCs_NON_ENUMERATED_JAVA_DOUBLE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2513777.xml");
    assertNonEnumeratedType(dataElement);
  }

  private void assertNonEnumeratedType(DataElement dataElement) throws Exception {
    PermissibleValuesHandler newHandler = null;
    newHandler = handler.handle(dataElement);

    List<Map<String, Object>> ontologies = newHandler.getOntologies();
    List<Map<String, Object>> valueSets = newHandler.getValueSets();
    List<Map<String, Object>> branches = newHandler.getBranches();
    List<Map<String, Object>> classes = newHandler.getClasses();
    // Assert
    assertThat(ontologies, is(nullValue()));
    assertThat(valueSets, is(nullValue()));
    assertThat(branches, is(nullValue()));
    assertThat(classes, is(nullValue()));
  }
}
