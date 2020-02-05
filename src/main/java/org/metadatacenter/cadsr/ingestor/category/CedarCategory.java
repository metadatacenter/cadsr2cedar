package org.metadatacenter.cadsr.ingestor.category;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"schema:identifier", "schema:name", "schema:description", "parentCategoryId"})
public class CedarCategory {

  @JsonProperty("schema:identifier")
  private String id;
  @JsonProperty("schema:name")
  private String name;
  @JsonProperty("schema:description")
  private String description;
  private String parentCategoryId;

  public CedarCategory() {
  }

  public CedarCategory(String id, String name, String description, String parentCategoryId) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.parentCategoryId = parentCategoryId;
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getParentCategoryId() {
    return parentCategoryId;
  }

  @Override
  public String toString() {
    return "CedarCategory{" +
        "id='" + id + '\'' +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", parentCategoryId='" + parentCategoryId + '\'' +
        '}';
  }

}
