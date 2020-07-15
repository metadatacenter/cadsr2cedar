package org.metadatacenter.cadsr.ingestor.util;

import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.Value;
import org.metadatacenter.cadsr.ingestor.exception.InvalidIdentifierException;

import static org.metadatacenter.cadsr.ingestor.util.Constants.CDE_VALUESETS_ONTOLOGY_IRI;

public class ValueSetUtil {

  public static String generateValueSetIRI(DataElement dataElement) {
    String valueSetId = dataElement.getVALUEDOMAIN().getPublicId().getContent();
    String valueSetVersion = dataElement.getVALUEDOMAIN().getVersion().getContent();
    return generateValueSetIRI(valueSetId, valueSetVersion);
  }

  public static String generateValueSetIRI(String valueSetId, String valueSetVersion) {
    String valueSetIRI = null;
    try {
      valueSetIRI = CDE_VALUESETS_ONTOLOGY_IRI + "/" + generateValueSetId(valueSetId, valueSetVersion);
    } catch (InvalidIdentifierException e) {
      e.printStackTrace();
    }
    return valueSetIRI;
  }

  public static String generateValueSetId(String valueDomainId, String valueDomainVersion) throws
      InvalidIdentifierException {
    if (valueDomainId != null && valueDomainId.length() > 0) {
      String valueSetId = "VD" + valueDomainId;
      if (valueDomainVersion != null && valueDomainVersion.length() > 0) {
        valueSetId = valueSetId + "v" + valueDomainVersion;
      }
      return valueSetId;
    } else {
      throw new InvalidIdentifierException("The value domain identifier is null or empty");
    }
  }

  public static String generateValueIRI(String valueSetId, Value value) {
    return CDE_VALUESETS_ONTOLOGY_IRI + "/" + generateValueId(valueSetId, value);
  }

  public static String generateValueId(String valueSetId, Value value) {
    return GeneralUtil.getSha1(valueSetId + value.toString());
  }

}
