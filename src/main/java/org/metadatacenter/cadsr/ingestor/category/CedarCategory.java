package org.metadatacenter.cadsr.ingestor.category;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"@id", "schema:identifier", "schema:name", "schema:description", "parentCategoryId", "children"})
public class CedarCategory {

  @JsonProperty("@id")
  private String cedarId; // CEDAR's @id
  @JsonProperty("schema:identifier")
  private String id; // Unique identifier (see details in Category.java)
  @JsonProperty("schema:name")
  private String name;
  @JsonProperty("schema:description")
  private String description;
  @JsonProperty("parentCategoryId")
  private String parentCategoryCedarId;
  private List<CedarCategory> children;

  public CedarCategory() {
  }

  public CedarCategory(String cedarId, String id, String name, String description, String parentCategoryCedarId, List<CedarCategory> children) {
    this.cedarId = cedarId;
    this.id = id;
    this.name = name;
    this.description = description;
    this.parentCategoryCedarId = parentCategoryCedarId;
    this.children = children;
  }

  public String getCedarId() {
    return cedarId;
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

  public String getParentCategoryCedarId() {
    return parentCategoryCedarId;
  }

  public List<CedarCategory> getChildren() {
    return children;
  }


}
