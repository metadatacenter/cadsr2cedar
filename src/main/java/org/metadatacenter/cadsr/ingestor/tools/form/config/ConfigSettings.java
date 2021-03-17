package org.metadatacenter.cadsr.ingestor.tools.form.config;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSettings {

  private static final Logger logger = LoggerFactory.getLogger(ConfigSettings.class);

  private String formFilePath;

  public ConfigSettings() {
  }

  public static Logger getLogger() {
    return logger;
  }

  public String getFormFilePath() {
    return formFilePath;
  }

  public void setFormFilePath(String formFilePath) {
    this.formFilePath = formFilePath;
  }
}
