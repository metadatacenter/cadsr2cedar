package org.metadatacenter.cadsr.ingestor.cde;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CdeSummary {

  @JsonProperty("@id")
  private String cedarId;
  @JsonProperty("schema:identifier")
  private String id; // Public Id
  @JsonProperty("pav:version")
  private String version;
  @JsonProperty("sourceHash")
  private String hashCode;
  @JsonProperty("categories")
  private List<String> categoryCedarIds;

  public CdeSummary() {
  }

  public CdeSummary(String cedarId, String id, String version, String hashCode, List<String> categoryCedarIds) {
    this.cedarId = cedarId;
    this.id = id;
    this.version = version;
    this.hashCode = hashCode;
    this.categoryCedarIds = categoryCedarIds;
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

  public List<String> getCategoryCedarIds() {
    return categoryCedarIds;
  }
}
