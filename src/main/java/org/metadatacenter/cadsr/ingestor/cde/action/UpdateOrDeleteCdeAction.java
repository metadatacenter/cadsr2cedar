package org.metadatacenter.cadsr.ingestor.cde.action;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateOrDeleteCdeAction implements CdeAction {

  private static final Logger logger = LoggerFactory.getLogger(UpdateOrDeleteCdeAction.class);

  private String cedarId;
  private String id; // Public Id
  private String version;
  private String hashCode;

  public UpdateOrDeleteCdeAction(String cedarId, String id, String version, String hashCode) {
    this.cedarId = cedarId;
    this.id = id;
    this.version = version;
    this.hashCode = hashCode;
  }

  public static Logger getLogger() {
    return logger;
  }

  public String getCedarId() {
    return cedarId;
  }

  public String getId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public String getHashCode() {
    return hashCode;
  }

  @Override
  public String execute(CedarServer cedarEnvironment, String apiKey) {
    logger.warn("No execution defined for update/delete action. Doing nothing.");
    return cedarId;
  }
}
