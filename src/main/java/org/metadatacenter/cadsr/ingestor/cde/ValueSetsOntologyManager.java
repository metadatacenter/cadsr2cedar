package org.metadatacenter.cadsr.ingestor.cde;

import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.util.GeneralUtil;
import org.metadatacenter.cadsr.ingestor.util.ValueSetUtil;
import org.metadatacenter.cadsr.ingestor.exception.DuplicatedAxiomException;
import org.metadatacenter.cadsr.ingestor.exception.InvalidIdentifierException;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;
import org.semanticweb.owlapi.vocab.SKOSVocabulary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Set;

import static org.metadatacenter.cadsr.ingestor.util.Constants.*;

public class ValueSetsOntologyManager {

  private static OWLOntologyManager manager;
  private static OWLOntology ontology;
  private static OWLDataFactory owlDataFactory;

  private static int valueSetsCount = 0;
  private static int valuesCount = 0;

  private static final Logger logger = LoggerFactory.getLogger(ValueSetsOntologyManager.class);

  static {
    manager = OWLManager.createOWLOntologyManager();
    owlDataFactory = manager.getOWLDataFactory();
    // Create the ontology that will contain the value sets. The root classes will represent value sets. All
    // subclasses of a rootclass will represent the values of the value set.
    try {
      ontology = manager.createOntology(IRI.create(CDE_VALUESETS_ONTOLOGY_IRI));
    } catch (OWLOntologyCreationException e) {
      logger.error("Error while creating ontology: " + e);
    }
  }

  public static void addValueSetToOntology(DataElement dataElement, Set<Value> values) throws DuplicatedAxiomException {

    String valueDomainId = GeneralUtil.getValueOrNull(dataElement.getVALUEDOMAIN().getPublicId().getContent());
    String valueDomainVersion = GeneralUtil.getValueOrNull(dataElement.getVALUEDOMAIN().getVersion().getContent());
    String valueDomainPrefName =
        GeneralUtil.getValueOrNull(dataElement.getVALUEDOMAIN().getPreferredName().getContent());
    String valueDomainPrefDefinition = GeneralUtil.getValueOrNull(dataElement.getVALUEDOMAIN().getPreferredDefinition()
        .getContent());

    // Create the class that will represent the value set
    OWLClass valueSetClass = dataElementToOWLClass(valueDomainId, valueDomainVersion);

    // Check if the ontology already contains that class
    if (!ontology.containsClassInSignature(valueSetClass.getIRI())) {

      // Add annotation properties to the value set
      String valueSetId = null;
      try {
        valueSetId = ValueSetUtil.generateValueSetId(valueDomainId, valueDomainVersion);
      } catch (InvalidIdentifierException e) {
        logger.error(e.getMessage());
      }

      logger.info("Adding value set to the ontology. ValueSetId = " + valueSetId + ". Number of values: " + values.size());

      ontology = addAnnotationAxiomToClass(IRI.create(DUBLINCORE_IDENTIFIER_IRI), valueDomainId, valueSetClass);
      if (valueDomainVersion != null) {
        ontology = addAnnotationAxiomToClass(IRI.create(DUBLINCORE_VERSION_IRI), valueDomainVersion, valueSetClass);
      }
      // This is the label that will be shown in BP's ontology hierarchy
      ontology = addAnnotationAxiomToClass(OWLRDFVocabulary.RDFS_LABEL.getIRI(), valueSetId, valueSetClass);
      if (valueDomainPrefName != null) {
        ontology = addAnnotationAxiomToClass(SKOSVocabulary.ALTLABEL.getIRI(), valueDomainPrefName, valueSetClass);
      }
      if (valueDomainPrefDefinition != null) {
        ontology = addAnnotationAxiomToClass(OWLRDFVocabulary.RDFS_COMMENT.getIRI(), valueDomainPrefDefinition,
            valueSetClass);
      }

      // Values of the value set
      for (Value value : values) {
        ontology = addPermissibleValuesToOntology(valueSetId, value, valueSetClass);
        valuesCount++;
      }
      valueSetsCount++;
    } else {
      logger.warn("The value set has not been added to the ontology because it's already there. Class IRI: " + valueSetClass.getIRI());
    }
  }

  public static OWLClass dataElementToOWLClass(String valueSetId, String valueSetVersion) {
    return owlDataFactory.getOWLClass(IRI.create(ValueSetUtil.generateValueSetIRI(valueSetId, valueSetVersion)));
  }

  public static OWLOntology addPermissibleValuesToOntology(String valueSetId, Value value, OWLClass valueSetClass)
      throws DuplicatedAxiomException {
    // Create the class that represents the value
    OWLClass valueClass = owlDataFactory.getOWLClass(ValueSetUtil.generateValueIRI(valueSetId, value));
    // Create subclass axiom
    OWLAxiom axiom = owlDataFactory.getOWLSubClassOfAxiom(valueClass, valueSetClass);
    // Add subclass axiom to the ontology
    manager.addAxiom(ontology, axiom);
    // Add annotation properties to the value
    if (value.getId() != null) {
      ontology = addAnnotationAxiomToClass(IRI.create(DUBLINCORE_IDENTIFIER_IRI), value.getId(), valueClass);
    }
    if (value.getVersion() != null) {
      ontology = addAnnotationAxiomToClass(IRI.create(DUBLINCORE_VERSION_IRI), value.getVersion(), valueClass);
    }
    if (value.getDbLabel() != null) {
      ontology = addAnnotationAxiomToClass(IRI.create(SKOS_NOTATION_IRI), value.getDbLabel(), valueClass);
      //ontology = addAnnotationAxiomToClass(SKOSVocabulary.HIDDENLABEL.getIRI(), value.getDbLabel(), valueClass);
    }
    if (value.getDisplayLabel() != null) {
      ontology = addAnnotationAxiomToClass(OWLRDFVocabulary.RDFS_LABEL.getIRI(), value.getDisplayLabel(), valueClass);
    }
    if (value.getTermIri() != null) {
      ontology = addAnnotationAxiomToClass(SKOSVocabulary.RELATEDMATCH.getIRI(), value.getTermIri(), valueClass);
    }
    if (value.getDescription() != null) {
      ontology = addAnnotationAxiomToClass(OWLRDFVocabulary.RDFS_COMMENT.getIRI(), value.getDescription(), valueClass);
    }
    if (value.getBeginDate() != null) {
      ontology = addAnnotationAxiomToClass(IRI.create(SCHEMAORG_STARTTIME_IRI), value.getBeginDate(), valueClass);
    }
    if (value.getEndDate() != null) {
      ontology = addAnnotationAxiomToClass(IRI.create(SCHEMAORG_ENDTIME_IRI), value.getEndDate(), valueClass);
    }
    return ontology;
  }

  public static OWLOntology addAnnotationAxiomToClass(IRI propertyIRI, String annotationText, OWLClass c) throws
      DuplicatedAxiomException {
    OWLAnnotationProperty p = owlDataFactory.getOWLAnnotationProperty(propertyIRI);
    OWLAnnotationValue v = owlDataFactory.getOWLLiteral(annotationText);
    OWLAnnotation valueMeaningAnnotation = owlDataFactory.getOWLAnnotation(p, v);
    OWLAnnotationAssertionAxiom ax = owlDataFactory.getOWLAnnotationAssertionAxiom(c.getIRI(), valueMeaningAnnotation);
    if (ontology.containsAxiom(ax)) {
      String message = "Duplicated annotation axiom: " + c.getIRI() + " - " + p.getIRI() + " - " + v.toString();
      throw new DuplicatedAxiomException(message);
    }
    manager.addAxiom(ontology, ax);
    return ontology;
  }

  public static void saveOntology(File ontologyFile) {
    try {
      manager.saveOntology(ontology, IRI.create(ontologyFile.toURI()));
    } catch (OWLOntologyStorageException e) {
      logger.error("Error while saving the ontology: " + e);
    }
  }

  public static int getValueSetsCount() {
    return valueSetsCount;
  }

  public static int getValuesCount() {
    return valuesCount;
  }
}
