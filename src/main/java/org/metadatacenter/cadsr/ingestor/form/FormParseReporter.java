package org.metadatacenter.cadsr.ingestor.form;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a singleton that stores a report that will be returned to the client using the form parser
 */
public class FormParseReporter {

  private List<String> messages;

  private static FormParseReporter singleInstance = null;

  private FormParseReporter() {
    messages =  new ArrayList<>();
  }

  public static FormParseReporter getInstance() {
    if (singleInstance == null) {
      singleInstance = new FormParseReporter();
    }
    return singleInstance;
  }

  public void addMessage(String message) {
    messages.add(message);
  }

  public List<String> getMessages() {
    return messages;
  }

}
