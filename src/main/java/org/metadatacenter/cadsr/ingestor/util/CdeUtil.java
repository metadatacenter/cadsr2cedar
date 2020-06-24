package org.metadatacenter.cadsr.ingestor.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.cde.schema.DataElementsList;
import org.metadatacenter.cadsr.cde.schema.PermissibleValuesITEM;
import org.metadatacenter.cadsr.ingestor.cde.CdeParser;
import org.metadatacenter.cadsr.ingestor.cde.CdeStats;
import org.metadatacenter.cadsr.ingestor.cde.CdeSummary;
import org.metadatacenter.cadsr.ingestor.cde.handler.VersionHandler;
import org.metadatacenter.cadsr.ingestor.exception.UnknownSeparatorException;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

public class CdeUtil {

  private static final Logger logger = LoggerFactory.getLogger(CdeUtil.class);

  public static Collection<Map<String, Object>> getFieldMapsFromDataElements(DataElementsList del) {
    final List<Map<String, Object>> fieldMaps = Lists.newArrayList();
    for (DataElement dataElement : del.getDataElement()) {
      Map<String, Object> fieldMap = getFieldMapFromDataElement(dataElement);
      if (fieldMap != null) {
        fieldMaps.add(fieldMap);
      }
    }
    return fieldMaps;
  }

  public static Map<String, Object> getFieldMapFromDataElement(DataElement de) {
    final Map<String, Object> fieldMap = Maps.newHashMap();
    try {
      CdeParser.parseDataElement(de, fieldMap);
      return fieldMap;
    } catch (UnsupportedDataElementException e) {
      CdeStats.getInstance().addSkipped(e.getReason());
      logger.warn(e.getMessage());
    } catch (UnknownSeparatorException e) {
      CdeStats.getInstance().addFailed(e.getMessage());
      logger.error(e.getMessage());
    }
    return null;
  }

  public static Collection<Map<String, Object>> getFieldMapsFromInputStream(InputStream is) {
    final List<Map<String, Object>> fieldMaps = Lists.newArrayList();
    try {
      DataElementsList del = getDataElementLists(is);
      fieldMaps.addAll(getFieldMapsFromDataElements(del));
    } catch (ClassCastException e) {
      logger.error("Source document is not a list of data elements: " + e);
    } catch (UnsupportedEncodingException e) {
      logger.error("Unsupported encoding: " + e);
    } catch (JAXBException | IOException e) {
      logger.error("Error while parsing source document: " + e);
    }
    return fieldMaps;
  }

  public static DataElementsList getDataElementLists(InputStream is) throws JAXBException, IOException {
    JAXBContext jaxbContext = JAXBContext.newInstance(DataElementsList.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    InputStream cleanIs = GeneralUtil.processInvalidXMLCharacters(is);
    return (DataElementsList) jaxbUnmarshaller.unmarshal(new InputStreamReader(cleanIs, Constants.CHARSET));
  }

  public static DataElement getDataElement(InputStream is) throws JAXBException, UnsupportedEncodingException {
    JAXBContext jaxbContext = JAXBContext.newInstance(DataElement.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    return (DataElement) jaxbUnmarshaller.unmarshal(new InputStreamReader(is, Constants.CHARSET));
  }

  public static String generateCdeUniqueId(DataElement cde) {
    return generateCdeUniqueId(cde.getPUBLICID().getContent(),
        VersionHandler.reformatVersioningNumber(cde.getVERSION().getContent()));
  }

  public static String generateCdeUniqueId(Map<String, Object> cdeMap) {
    return generateCdeUniqueId((String) cdeMap.get(ModelNodeNames.SCHEMA_ORG_IDENTIFIER),
        (String) cdeMap.get(ModelNodeNames.PAV_VERSION));
  }

  public static String generateCdeUniqueId(String publicId, String version) {
    return publicId + "V" + version;
  }

  /**
   * Returns a hash code to determine if the data element has changed.
   *
   * @param dataElement
   * @return Hash code
   */
  public static String generateCdeHashCode(DataElement dataElement) {

    String publicId = dataElement.getPUBLICID().getContent();
    String version = dataElement.getVERSION().getContent();

    String dataElementDateModified = dataElement.getDateModified().getContent();

    return GeneralUtil.getSha1(publicId + version + dataElementDateModified);
  }

  public static Map<String, CdeSummary> getExistingCedarCdeSummaries(String cedarFolderId, Constants.CedarEnvironment cedarEnvironment, String apiKey) throws IOException {
    // Retrieve existing CDEs from CEDAR
    logger.info("Retrieving current CDEs from CEDAR (folder id: " + cedarFolderId + ").");
    List fieldNamesToInclude = new ArrayList(Arrays.asList(new String[]{"schema:identifier", "pav:version",
        "sourceHash"}));
    List<CdeSummary> cdeSummaries = CedarServices.findCdeSummariesInFolder(cedarFolderId,
        fieldNamesToInclude, true, cedarEnvironment, apiKey);
    logger.info("Number of CDEs retrieved from CEDAR: " + cdeSummaries.size() + ".");
    CdeStats.getInstance().numberOfExistingCdes = cdeSummaries.size();

    // Create CDE Map (key: cdeId (PublicId + "V" + Version); Value: CdeSummary)
    Map<String, CdeSummary> existingCdesMap = new HashMap<>();
    for (CdeSummary cdeSummary : cdeSummaries) {
      String cdeMapKey = CdeUtil.generateCdeUniqueId(cdeSummary.getId(), cdeSummary.getVersion());
      existingCdesMap.put(cdeMapKey, cdeSummary);
    }
    return existingCdesMap;
  }

}
