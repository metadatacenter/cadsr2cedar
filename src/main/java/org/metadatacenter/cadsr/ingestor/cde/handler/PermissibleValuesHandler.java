package org.metadatacenter.cadsr.ingestor.cde.handler;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.cde.schema.PermissibleValues;
import org.metadatacenter.cadsr.cde.schema.PermissibleValuesITEM;
import org.metadatacenter.cadsr.ingestor.cde.CadsrConceptOrigins;
import org.metadatacenter.cadsr.ingestor.cde.Value;
import org.metadatacenter.cadsr.ingestor.cde.ValueSetsOntologyManager;
import org.metadatacenter.cadsr.ingestor.util.ValueSetUtil;
import org.metadatacenter.cadsr.ingestor.exception.DuplicatedAxiomException;
import org.metadatacenter.cadsr.ingestor.exception.InvalidIdentifierException;
import org.metadatacenter.cadsr.ingestor.exception.UnknownSeparatorException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.metadatacenter.cadsr.ingestor.cde.CadsrConstants.ENUMERATED;
import static org.metadatacenter.cadsr.ingestor.cde.CadsrConstants.NON_ENUMERATED;
import static org.metadatacenter.cadsr.ingestor.util.Constants.*;

public class PermissibleValuesHandler implements ModelHandler {

  private static final Logger logger = LoggerFactory.getLogger(PermissibleValuesHandler.class);

  private List<Map<String, Object>> ontologies;
  private List<Map<String, Object>> valueSets;
  private List<Map<String, Object>> classes;
  private List<Map<String, Object>> branches;

  public PermissibleValuesHandler handle(DataElement dataElement) throws UnsupportedDataElementException,
      UnknownSeparatorException {
    String valueDomainType = dataElement.getVALUEDOMAIN().getValueDomainType().getContent();
    if (ENUMERATED.equals(valueDomainType)) {
      handleEnumeratedType(dataElement);
    } else if (NON_ENUMERATED.equals(valueDomainType)) {
      handleNonEnumeratedType(dataElement);
    } else {
      String reason = String.format("Value domain is neither enumerated nor non-enumerated = %s (Unknown)",
          valueDomainType);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    return this;
  }

  private void handleEnumeratedType(DataElement dataElement) throws UnknownSeparatorException {

    ontologies = Lists.newArrayList();
    valueSets = Lists.newArrayList();
    classes = Lists.newArrayList();
    branches = Lists.newArrayList();

    PermissibleValues permissibleValues = dataElement.getVALUEDOMAIN().getPermissibleValues();
    Set<Value> values = getValues(permissibleValues);

    if (values.size() <= MAX_ENUMERATED_TERMS) {
      String valueDomainId = dataElement.getVALUEDOMAIN().getPublicId().getContent();
      String valueDomainVersion = dataElement.getVALUEDOMAIN().getVersion().getContent();
      String valueSetId = null;
      try {
        valueSetId = ValueSetUtil.generateValueSetId(valueDomainId, valueDomainVersion);
      } catch (InvalidIdentifierException e) {
        logger.error(e.getMessage());
      }
      setListOfClasses(valueSetId, values);
    } else {
      try {
        ValueSetsOntologyManager.addValueSetToOntology(dataElement, values);
      } catch (DuplicatedAxiomException e) {
        logger.error(e.getMessage());
      }
      setValueSet(dataElement, values.size());
    }
  }

  private Set<Value> getValues(PermissibleValues permissibleValues) throws UnknownSeparatorException {
    Set<Value> termSet = Sets.newHashSet();
    for (PermissibleValuesITEM permissibleItem : permissibleValues.getPermissibleValuesITEM()) {
      String termIri = constructTermIri(permissibleItem);
      String termSource = getTermSource(permissibleItem);
      termSet.add(new Value(
          permissibleItem.getVMPUBLICID().getContent(),
          permissibleItem.getVMVERSION().getContent(),
          permissibleItem.getVALIDVALUE().getContent(),
          permissibleItem.getVALUEMEANING().getContent(),
          termIri,
          termSource,
          permissibleItem.getMEANINGDESCRIPTION().getContent(),
          permissibleItem.getPVBEGINDATE().getContent(),
          permissibleItem.getPVENDDATE().getContent()
      ));
    }
    return termSet;
  }

  private String constructTermIri(PermissibleValuesITEM permissibleItem) throws UnknownSeparatorException {
    String conceptUri;
    if (isNullConcept(permissibleItem)) {
      // use “VM” plus the Value Meaning PublicID and Version
      String publicId = permissibleItem.getVMPUBLICID().getContent();
      String version = permissibleItem.getVMVERSION().getContent();
      conceptUri = CDE_VALUESETS_ONTOLOGY_IRI + "/VM" + publicId + "v" + version;
    } else {
      String conceptOrigin = permissibleItem.getMEANINGCONCEPTORIGIN().getContent();
      String ontologyIri = CadsrConceptOrigins.ONTOLOGY_IRI_MAP.get(conceptOrigin);
      if (isComplexConcept(permissibleItem)) {
        // Use the rightmost concept (last) in the list of concepts
        conceptUri = ontologyIri + extractLastConcept(permissibleItem.getMEANINGCONCEPTS().getContent());
      } else { // regular concept
        conceptUri = ontologyIri + permissibleItem.getMEANINGCONCEPTS().getContent();
      }
    }
    return conceptUri;
  }

  private String getTermSource(PermissibleValuesITEM permissibleItem) {
    String conceptOrigin = permissibleItem.getMEANINGCONCEPTORIGIN().getContent();
    String termSource = CadsrConceptOrigins.ONTOLOGY_LABEL_MAP.get(conceptOrigin);
    return termSource;
  }

  private void setListOfClasses(String valueSetId, Set<Value> values) {
    for (Value value : values) {
      Map<String, Object> cls = Maps.newHashMap();
      cls.put(ModelNodeNames.VALUE_CONSTRAINTS_URI, ValueSetUtil.generateValueId(valueSetId, value));
      cls.put(ModelNodeNames.VALUE_CONSTRAINTS_LABEL, value.getDisplayLabel());
      cls.put(ModelNodeNames.VALUE_CONSTRAINTS_PREFLABEL, value.getDbLabel());
      cls.put(ModelNodeNames.VALUE_CONSTRAINTS_TYPE, ModelNodeValues.ONTOLOGY_CLASS);
      cls.put(ModelNodeNames.VALUE_CONSTRAINTS_SOURCE, value.getTermSource());
      classes.add(cls);
    }
  }

  private void setValueSet(DataElement dataElement, int size) {
    Map<String, Object> valueSet = Maps.newHashMap();
    valueSet.put(ModelNodeNames.VALUE_CONSTRAINTS_NAME, dataElement.getVALUEDOMAIN().getLongName().getContent());
    valueSet.put(ModelNodeNames.VALUE_CONSTRAINTS_VS_COLLECTION, CDE_VALUESETS_ONTOLOGY_ID);
    valueSet.put(ModelNodeNames.VALUE_CONSTRAINTS_URI, ValueSetUtil.generateValueSetIRI(dataElement));
    valueSet.put(ModelNodeNames.VALUE_CONSTRAINTS_NUM_TERMS, size);
    valueSets.add(valueSet);
  }

  private static boolean isNullConcept(PermissibleValuesITEM permissibleItem) {
    String conceptId = permissibleItem.getMEANINGCONCEPTS().getContent();
    return Strings.isNullOrEmpty(conceptId);
  }

  private static boolean isComplexConcept(PermissibleValuesITEM permissibleItem) {
    String conceptId = permissibleItem.getMEANINGCONCEPTS().getContent();
    return (conceptId.contains(",") || conceptId.contains(":"));
  }

  private static String extractLastConcept(String complexConcept) throws UnknownSeparatorException {
    String lastConcept = null;
    int indexColon = complexConcept.lastIndexOf(":");
    int indexComma = complexConcept.lastIndexOf(",");
    if (indexColon > indexComma) {
      lastConcept = complexConcept.substring(indexColon + 1);
    } else if (indexColon < indexComma) {
      lastConcept = complexConcept.substring(indexComma + 1);
    } else {
      throw new UnknownSeparatorException("Found a complex concept with an unknown separator: " + complexConcept);
    }
    return lastConcept;
  }

  private void handleNonEnumeratedType(DataElement dataElement) {
    // Does nothing
  }

  public List<Map<String, Object>> getOntologies() {
    return (ontologies != null ? Collections.unmodifiableList(ontologies) : null);
  }

  public List<Map<String, Object>> getValueSets() {
    return (valueSets != null ? Collections.unmodifiableList(valueSets) : null);
  }

  public List<Map<String, Object>> getClasses() {
    return (classes != null ? Collections.unmodifiableList(classes) : null);
  }

  public List<Map<String, Object>> getBranches() {
    return (branches != null ? Collections.unmodifiableList(branches) : null);
  }

  @Override
  public void apply(Map<String, Object> fieldObject) {
    Map<String, Object> valueConstraints = (Map<String, Object>) fieldObject.get(ModelNodeNames.VALUE_CONSTRAINTS);
    if (ontologies != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_ONTOLOGIES, getOntologies());
    }
    if (valueSets != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_VALUE_SETS, getValueSets());
    }
    if (classes != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_CLASSES, getClasses());
    }
    if (branches != null) {
      valueConstraints.put(ModelNodeNames.VALUE_CONSTRAINTS_BRANCHES, getBranches());
    }
  }
}
