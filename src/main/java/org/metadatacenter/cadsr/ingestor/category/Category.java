package org.metadatacenter.cadsr.ingestor.category;

import java.util.ArrayList;

public class Category {

  private String publicId;
  private String version;
  private String localId;
  private String cadsrId;
  private String uniqueId;
  private String name;
  private String description;
  private String type;
  private String parentUniqueId;

  /**
   * @param publicId       caDSR classification PublicId (e.g., 4280706)
   * @param version        caDSR classification Version (e.g. 1)
   * @param localId        Format: PublicId (or name if not available) + "-V" + Version
   * @param cadsrId        Format: ContextId/ClassificationSchemeId/ClassificationSchemeItemId(local id). The CDEs
   *                       XML file does not provide more than three levels, so this field will make it possible to
   *                       link CDES to categories. Some categories have the same cadsrId but different uniqueIds
   *                       because the classification scheme items are at different levels (e.g., 3 vs 4).
   * @param uniqueId       Unique identifier. Path from the root of the tree to the category. In some cases (when the
   *                       classificationSchemeItem is at the 3rd level, the uniqueId will be equal to cadsrId. This
   *                       identifier will be used to build the tree.
   * @param name
   * @param description
   * @param type
   * @param parentUniqueId
   */
  public Category(String publicId, String version, String localId, String cadsrId, String uniqueId, String name, String description,
                  String type, String parentUniqueId) {
    this.publicId = publicId;
    this.version = version;
    this.localId = localId;
    this.cadsrId = cadsrId;
    this.uniqueId = uniqueId;
    this.name = name;
    this.type =  type;
    this.description = description;
    this.parentUniqueId = parentUniqueId;
  }

  public String getPublicId() {
    return publicId;
  }

  public String getVersion() {
    return version;
  }

  public String getLocalId() {
    return localId;
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

  public String getParentUniqueId() {
    return parentUniqueId;
  }

  public void setName(String name) {
    this.name = name;
  }

  public CedarCategory toCedarCategory(String ldId, String parentCategoryLdId) {
    return new CedarCategory(ldId, uniqueId, name, description, parentCategoryLdId, new ArrayList<>());
  }

  public CedarCategory toCedarCategory() {
    return new CedarCategory(null, uniqueId, name, description, null, new ArrayList<>());
  }

  public CategoryTreeNode toCategoryTreeNode() {
    return new CategoryTreeNode(uniqueId, localId, name, description, new ArrayList<>(), parentUniqueId, version);
  }

}
