package org.metadatacenter.cadsr.ingestor.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonPropertyOrder({"uniqueId", "id", "name","description", "children"})
public class CategoryTreeNode {

  private String uniqueId; // Unique identifier. Path from the root of the tree to the category.
  @JsonProperty("id")
  private String localId; // Format: PublicId (or name if not available) + "-V" + Version
  private String name;
  private String description;
  private List<CategoryTreeNode> children;
  private String parentUniqueId;
  private String version;

  public CategoryTreeNode() {
  }

  public CategoryTreeNode(String uniqueId, String localId, String name, String description, List<CategoryTreeNode> children, String parentUniqueId, String version) {
    this.uniqueId = uniqueId;
    this.localId = localId;
    this.name = name;
    this.description = description;
    this.children = children;
    this.parentUniqueId = parentUniqueId;
    this.version = version;
  }

  public String getUniqueId() {
    return uniqueId;
  }

  public String getLocalId() {
    return localId;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public List<CategoryTreeNode> getChildren() {
    return children;
  }

  public String getParentUniqueId() {
    return parentUniqueId;
  }

  public String getVersion() {
    return version;
  }

  // Setters


  public void setParentUniqueId(String parentUniqueId) {
    this.parentUniqueId = parentUniqueId;
  }

  public void setChildren(List<CategoryTreeNode> children) {
    this.children = children;
  }

  @Override
  public String toString() {
    return "CategoryTreeNode{" +
        "uniqueId='" + uniqueId + '\'' +
        ", localId='" + localId + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", children=" + children +
        ", parentUniqueId='" + parentUniqueId + '\'' +
        ", version='" + version + '\'' +
        '}';
  }
}
