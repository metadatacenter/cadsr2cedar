package org.metadatacenter.cadsr.ingestor.form;

import java.util.List;
import java.util.Map;

public class FormParseResult {

  private Map<String, Object> templateMap;
  private List<String> reportMessages;

  public FormParseResult(Map<String, Object> templateMap, List<String> reportMessages) {
    this.templateMap = templateMap;
    this.reportMessages = reportMessages;
  }

  public Map<String, Object> getTemplateMap() {
    return templateMap;
  }

  public void setTemplateMap(Map<String, Object> templateMap) {
    this.templateMap = templateMap;
  }

  public List<String> getReportMessages() {
    return reportMessages;
  }

  public void setReportMessages(List<String> reportMessages) {
    this.reportMessages = reportMessages;
  }
}
