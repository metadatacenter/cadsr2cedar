package org.metadatacenter.cadsr.ingestor.category;

public class Category {

  /**
   * Note about id generation: We assume that a category is unique inside a context and classification scheme.
   * Therefore, we don't need to keep track of nesting between classification scheme items and use the following
   * pattern to identify categories: ContextId/ClassificationSchemeId/ClassificationSchemeItemId
   */

  private String id; // local id
  private String cadsrId; // contextId + classificationSchemeId + classificationSchemeItemId
  private String uniqueId; // Unique identifier. It will be used to build the tree
  private String name;
  private String description;
  private String type;
  private String parentId;
  private String version;

  public Category(String id, String cadsrId, String uniqueId, String name, String description, String type,
                  String parentId, String version) {
    this.id = id;
    this.cadsrId = cadsrId;
    this.uniqueId = uniqueId;
    this.name = name;
    this.description = description;
    this.type = type;
    this.parentId = parentId;
    this.version = version;
  }

  public String getId() {
    return id;
  }

  public String getCadsrId() {
    return cadsrId;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getType() {
    return type;
  }

  public String getParentId() {
    return parentId;
  }

  public String getVersion() {
    return version;
  }

  public void setName(String name) {
    this.name = name;
  }
}
