package org.metadatacenter.cadsr.ingestor.util;

public final class Constants {

  public static final int MAX_CDES_TO_PROCESS = Integer.MAX_VALUE; // It can be used to limit the number of CDEs to be processed when debugging or testing

  public enum CedarEnvironment {LOCAL, STAGING, PRODUCTION}

  public static final String LOCAL_RESOURCE_SERVER_URL = "https://resource.metadatacenter.orgx";
  public static final String STAGING_RESOURCE_SERVER_URL = "https://resource.staging.metadatacenter.org";
  public static final String PRODUCTION_RESOURCE_SERVER_URL = "https://resource.metadatacenter.org";

  public static final String LOCAL_REPO_SERVER_URL = "https://repo.metadatacenter.orgx";
  public static final String STAGING_REPO_SERVER_URL = "https://repo.staging.metadatacenter.org";
  public static final String PRODUCTION_REPO_SERVER_URL = "https://repo.metadatacenter.org";

  // Version of generated CEDAR CDEs
  public static final String CEDAR_SCHEMA_VERSION = "1.6.0";

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

  public static final String CHARSET = "UTF-8";

  // Maximum number of enumerated values, or minimum number of values for the value set to be defined as a BioPortal
  // value set. A value of 0 means that all value sets will be created as BioPortal value sets.
  public static final int MAX_ENUMERATED_TERMS = 0;

  public static final String CDE_VALUESETS_ONTOLOGY_ID = "CADSR-VS";
  public static final String CDE_VALUESETS_ONTOLOGY_IRI = "https://cadsr.nci.nih.gov/metadata/" + CDE_VALUESETS_ONTOLOGY_ID;

  // Categories
  public static final String CADSR_CATEGORY_SCHEMA_ORG_ID = "CADSR-CATEGORY-ID";
  public static final String CDE_CATEGORY_IDS_FIELD = "categoryIds"; // This field is not part of the CEDAR model
  public static final String CEDAR_CATEGORY_CHILDREN_FIELD_NAME = "children";
  public static final String CEDAR_CATEGORY_ATTACH_ARTIFACT_ID = "artifactId";
  public static final String CEDAR_CATEGORY_ATTACH_CATEGORY_IDS = "categoryIds";

  // Folder and file paths
  public static final String EXECUTION_FOLDER = "cadsr-exec-tmp"; // Temporal folder with files used during execution
  public static final String CATEGORIES_FOLDER = "categories";
  public static final String CDES_FOLDER = "cdes";
  public static final String UNZIPPED_FOLDER = "unzipped";
  public static final String ONTOLOGY_FOLDER = "ontology";
  public static final String ONTOLOGY_FILE = CDE_VALUESETS_ONTOLOGY_ID + ".owl";

}
