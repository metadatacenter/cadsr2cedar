package org.metadatacenter.cadsr.ingestor.category;

public class CategorySummary {

  private String cedarId;
  private String parentCedarId;
  private String hashCode;

  public CategorySummary() {
  }

  public CategorySummary(String cedarId, String parentCedarId, String hashCode) {
    this.cedarId = cedarId;
    this.parentCedarId = parentCedarId;
    this.hashCode = hashCode;
  }

  public String getCedarId() {
    return cedarId;
  }

  public String getParentCedarId() {
    return parentCedarId;
  }

  public String getHashCode() {
    return hashCode;
  }
}
