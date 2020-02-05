package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.collect.Maps;

import java.util.Map;

public class CadsrConceptOrigins {

  public static final String NCIT_IRI = "http://ncicb.nci.nih.gov/xml/owl/EVS/Thesaurus.owl#";
  public static final String NCIT_ONTOLOGY_LABEL = "NCIT";
  public static final String CTCAE_IRI = "http://ncicb.nci.nih.gov/xml/owl/EVS/ctcae5.owl#";
  public static final String CTCAE_ONTOLOGY_LABEL = "CTCAE";

  public static final Map<String, String> ONTOLOGY_IRI_MAP = Maps.newHashMap();

  static {
    ONTOLOGY_IRI_MAP.put("NCI Thesaurus", NCIT_IRI);
    ONTOLOGY_IRI_MAP.put("NCI Thesaurus,NCI Thesaurus", NCIT_IRI);
    ONTOLOGY_IRI_MAP.put("NCI Thesaurus,NCI Thesaurus,NCI Thesaurus", NCIT_IRI);
    ONTOLOGY_IRI_MAP.put("NCI Thesaurus,NCI Thesaurus,NCI Thesaurus,NCI Thesaurus", NCIT_IRI);
    ONTOLOGY_IRI_MAP.put("CTCAE", CTCAE_IRI);
  }

  public static final Map<String, String> ONTOLOGY_LABEL_MAP = Maps.newHashMap();

  static {
    ONTOLOGY_LABEL_MAP.put("NCI Thesaurus", NCIT_ONTOLOGY_LABEL);
    ONTOLOGY_LABEL_MAP.put("NCI Thesaurus,NCI Thesaurus", NCIT_ONTOLOGY_LABEL);
    ONTOLOGY_LABEL_MAP.put("NCI Thesaurus,NCI Thesaurus,NCI Thesaurus", NCIT_ONTOLOGY_LABEL);
    ONTOLOGY_LABEL_MAP.put("NCI Thesaurus,NCI Thesaurus,NCI Thesaurus,NCI Thesaurus", NCIT_ONTOLOGY_LABEL);
    ONTOLOGY_LABEL_MAP.put("CTCAE", CTCAE_ONTOLOGY_LABEL);
  }
}
