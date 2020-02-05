package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;

import java.util.Objects;

public class Value {
  private final String id; // VMPUBLICID
  private final String version; // VMVERSION
  private final String dbLabel; // VALIDVALUE
  private final String displayLabel; // VALUEMEANING
  private final String termIri; // MEANINGCONCEPTS
  private final String termSource; // MEANINGCONCEPTORIGIN
  private final String description; // MEANINGDESCRIPTION
  private final String beginDate; // PVBEGINDATE
  private final String endDate; // PVENDDATE


  public Value(String id, String version, String dbLabel, String displayLabel, String termIri,
               String termSource, String description, String beginDate, String endDate) {
    this.id = Strings.isNullOrEmpty(id) ? null : id;
    this.version = Strings.isNullOrEmpty(version) ? null : version;
    this.dbLabel = Strings.isNullOrEmpty(dbLabel) ? null : dbLabel;
    this.termIri = Strings.isNullOrEmpty(termIri) ? null : termIri;
    this.termSource = Strings.isNullOrEmpty(termSource) ? null : termSource;
    this.displayLabel = Strings.isNullOrEmpty(displayLabel) ? null : displayLabel;
    this.description = Strings.isNullOrEmpty(description) ? null : description;
    this.beginDate = Strings.isNullOrEmpty(beginDate) ? null : beginDate;
    this.endDate = Strings.isNullOrEmpty(endDate) ? null : endDate;
  }

  public String getId() {
    return id;
  }

  public String getVersion() {
    return version;
  }

  public String getDbLabel() {
    return dbLabel;
  }

  public String getDisplayLabel() {
    return displayLabel;
  }

  public String getTermIri() {
    return termIri;
  }

  public String getTermSource() {
    return termSource;
  }

  public String getDescription() {
    return description;
  }

  public String getBeginDate() {
    return beginDate;
  }

  public String getEndDate() {
    return endDate;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", getId())
        .add("version", getVersion())
        .add("dbLabel", getDbLabel())
        .add("displayLabel", getDisplayLabel())
        .add("termIri", getTermIri())
        .add("termSource", getTermSource())
        .add("description", getDescription())
        .add("beginDate", getBeginDate())
        .add("endDate", getEndDate())
        .toString();
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, version, dbLabel, displayLabel, termIri, termSource, description, beginDate, endDate);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof Value)) {
      return false;
    }
    Value other = (Value) obj;
    return Objects.equals(id, other.id)
        && Objects.equals(version, other.version)
        && Objects.equals(dbLabel, other.dbLabel)
        && Objects.equals(displayLabel, other.displayLabel)
        && Objects.equals(termIri, other.termIri)
        && Objects.equals(termSource, other.termSource)
        && Objects.equals(description, other.description)
        && Objects.equals(beginDate, other.beginDate)
        && Objects.equals(endDate, other.endDate);
  }
}