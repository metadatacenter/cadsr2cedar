package org.metadatacenter.cadsr.ingestor;

public final class Constants {

  public static final String CHARSET = "UTF-8";

  // Version of generated CEDAR CDEs
  public static final String CEDAR_SCHEMA_VERSION = "1.5.0";

  // Schema.org URIs
  public static final String SCHEMAORG_IRI = "https://schema.org/";
  public static final String SCHEMAORG_STARTTIME_IRI = SCHEMAORG_IRI + "startTime";
  public static final String SCHEMAORG_ENDTIME_IRI = SCHEMAORG_IRI + "endTime";
  // Dublic Core URIs
  public static final String DUBLINCORE_IRI = "http://purl.org/dc/terms/";
  public static final String DUBLINCORE_IDENTIFIER_IRI = DUBLINCORE_IRI + "identifier";
  public static final String DUBLINCORE_VERSION_IRI = DUBLINCORE_IRI + "hasVersion";
  // SKOS URIs
  public static final String SKOS_IRI = "http://www.w3.org/2004/02/skos/core#";
  public static final String SKOS_NOTATION_IRI = SKOS_IRI + "notation";

  // Maximum number of enumerated values, or minimum number of values for the value set to be defined as a BioPortal
  // value set. A value of 0 means that all value sets will be created as BioPortal value sets.
  public static final int MAX_ENUMERATED_TERMS = 0;

  public static final String CDE_VALUESETS_ONTOLOGY_ID = "CADSR-VS";
  public static final String CDE_VALUESETS_ONTOLOGY_IRI = "https://cadsr.nci.nih.gov/metadata/" + CDE_VALUESETS_ONTOLOGY_ID;
  public static final String CDE_VALUESETS_ONTOLOGY_NAME = "cadsr-vs.owl";

  // Categories
  public static final String ATTACH_CATEGORIES_OPTION = "-a";
  public static final String ROOT_CATEGORY_KEY = "CDE_ROOT";
  public static final String CDE_CATEGORY_IDS_FIELD = "categoryIds"; // This field is not part of the CEDAR model
  public static final String CATEGORIES_FOLDER_NAME = "categories";
  public static final String CATEGORIES_FILE_NAME_SUFFIX = "_categories";
  public static final String CEDAR_CATEGORY_CHILDREN_FIELD_NAME = "children";
  public static final String CEDAR_CATEGORY_ATTACH_ARTIFACT_ID = "artifactId";
  public static final String CEDAR_CATEGORY_ATTACH_CATEGORY_ID = "categoryId";
  public static final String CEDAR_CATEGORY_ATTACH_CATEGORY_IDS = "categoryIds";


}
