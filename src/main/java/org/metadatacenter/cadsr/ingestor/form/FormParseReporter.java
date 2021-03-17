package org.metadatacenter.cadsr.ingestor.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a singleton that stores a report that will be returned to the client using the form parser
 */
public class FormParseReporter {

  private Map<String, List<String>> messagesMap; // (uploadId + fileName) -> list of messages

  private static FormParseReporter singleInstance = null;

  private FormParseReporter() {
    messagesMap = new HashMap<>();
  }

  public static FormParseReporter getInstance() {
    if (singleInstance == null) {
      singleInstance = new FormParseReporter();
    }
    return singleInstance;
  }

  public void addMessage(String reportId, String message) {
    if (!messagesMap.containsKey(reportId)) {
      messagesMap.put(reportId, new ArrayList<>());
    }
    List<String> messages = messagesMap.get(reportId);
    messages.add(message);
    messagesMap.replace(reportId, messages);
  }

  public List<String> getMessages(String reportId) {
    return messagesMap.containsKey(reportId) ? messagesMap.get(reportId) : new ArrayList<>();
  }

  public void remove(String reportId) {
    if (messagesMap.containsKey(reportId)) {
      messagesMap.remove(reportId);
    }
  }

}
