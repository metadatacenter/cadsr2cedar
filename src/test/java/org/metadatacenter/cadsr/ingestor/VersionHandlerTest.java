package org.metadatacenter.cadsr.ingestor;

import org.junit.Before;
import org.junit.Test;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.handler.VersionHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class VersionHandlerTest {

  private VersionHandler handler;

  @Before
  public void init() {
    handler = new VersionHandler();
  }

  @Test
  public void shouldProduceVersioning_RELEASED() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2001826.xml");
    VersionHandler newHandler = handler.handle(dataElement);
    String status = newHandler.getStatus();
    String version = newHandler.getVersion();
    // Assert
    assertThat(status, is("bibo:published"));
    assertThat(version, is("3.0.0"));
  }

  @Test(expected = UnsupportedDataElementException.class)
  public void shouldNotProduceVersioning_APPRVD_FOR_TRIAL_USE() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2626966.xml");
    try {
      VersionHandler newHandler = handler.handle(dataElement);
    } catch (UnsupportedDataElementException e) {
      String message = e.getMessage();
      assertThat(message, is("Skipping 'Fluorescence in situ Hybridization Cytogenetic Analysis Cell " +
          "Assessment Percentage Value' (ID: 2626966) - Reason: The data element status of 'APPRVD FOR TRIAL USE' is " +
          "not supported (Unsupported)"));
      throw e;
    }
  }

  @Test(expected = UnsupportedDataElementException.class)
  public void shouldNotProduceVersioning_DRAFT_MOD() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-2953241.xml");
    try {
      VersionHandler newHandler = handler.handle(dataElement);
    } catch (UnsupportedDataElementException e) {
      String message = e.getMessage();
      assertThat(message, is("Skipping 'Hematopoietic Cell Transplantation Recipient Research Data Shared " +
          "Consent Date' (ID: 2953241) - Reason: The data element status of 'DRAFT MOD' is not supported " +
          "(Unsupported)"));
      throw e;
    }
  }

  @Test(expected = UnsupportedDataElementException.class)
  public void shouldNotProduceVersioning_DRAFT_NEW() throws Exception {
    DataElement dataElement = FileUtils.readDataElementResource("cde-sample-5286461.xml");
    try {
      VersionHandler newHandler = handler.handle(dataElement);
    } catch (UnsupportedDataElementException e) {
      String message = e.getMessage();
      assertThat(message, is("Skipping 'Medical Research Council Breathlessness Score Have to Stop for " +
          "Breath After Walking About 100 Yards on the Level Indicator' (ID: 5286461) - Reason: The data element " +
          "status of 'DRAFT NEW' is not supported (Unsupported)"));
      throw e;
    }
  }
}
