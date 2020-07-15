package org.metadatacenter.cadsr.ingestor.category.action;

import org.metadatacenter.cadsr.ingestor.category.CategoryTreeNode;
import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UpdateCategoryAction implements CategoryAction {

  private static final Logger logger = LoggerFactory.getLogger(UpdateCategoryAction.class);

  private String categoryCedarId;
  private CategoryTreeNode category;

  public UpdateCategoryAction(String categoryCedarId, CategoryTreeNode category) {
    this.categoryCedarId = categoryCedarId;
    this.category = category;
  }

  public String getCategoryCedarId() {
    return categoryCedarId;
  }

  public CategoryTreeNode getCategory() {
    return category;
  }

  @Override
  public void execute(CedarEnvironment cedarEnvironment, String apiKey) {
    CedarServices.updateCategory(categoryCedarId, category, cedarEnvironment, apiKey);
  }
}
