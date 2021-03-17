package org.metadatacenter.cadsr.ingestor.category.action;

import org.metadatacenter.cadsr.ingestor.category.CategoryTreeNode;
import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateCategoryAction implements CategoryAction {

  private static final Logger logger = LoggerFactory.getLogger(CreateCategoryAction.class);

  private CategoryTreeNode category;
  private String parentCategoryCedarId; // It makes it possible to connect the new category to the CEDAR category tree

  public CreateCategoryAction(CategoryTreeNode category, String parentCategoryCedarId) {
    this.category = category;
    this.parentCategoryCedarId = parentCategoryCedarId;
  }

  public CategoryTreeNode getCategory() {
    return category;
  }

  public String getParentCategoryCedarId() {
    return parentCategoryCedarId;
  }

  @Override
  public void execute(CedarServer cedarEnvironment, String apiKey) {
    logger.info("Creating category: " + category.getUniqueId());
    CedarServices.createCategory(category, parentCategoryCedarId, cedarEnvironment, apiKey);
  }
}
