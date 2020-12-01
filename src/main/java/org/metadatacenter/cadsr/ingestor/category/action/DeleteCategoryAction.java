package org.metadatacenter.cadsr.ingestor.category.action;

import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DeleteCategoryAction implements CategoryAction {

  private static final Logger logger = LoggerFactory.getLogger(DeleteCategoryAction.class);

  private String categoryCedarId;

  public DeleteCategoryAction(String categoryCedarId) {
    this.categoryCedarId = categoryCedarId;
  }

  public String getCategoryCedarId() {
    return categoryCedarId;
  }

  @Override
  public void execute(CedarServer cedarEnvironment, String apiKey) throws IOException {
    CedarServices.deleteCategory(categoryCedarId, cedarEnvironment, apiKey);
  }
}
