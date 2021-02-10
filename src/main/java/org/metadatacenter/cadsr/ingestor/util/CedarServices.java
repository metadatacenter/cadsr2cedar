package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.cadsr.ingestor.category.CategoryStats;
import org.metadatacenter.cadsr.ingestor.category.CategoryTreeNode;
import org.metadatacenter.cadsr.ingestor.category.CedarCategory;
import org.metadatacenter.cadsr.ingestor.cde.CdeStats;
import org.metadatacenter.cadsr.ingestor.cde.CdeSummary;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.metadatacenter.model.CedarResourceType;
import org.metadatacenter.server.neo4j.cypher.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.security.InvalidParameterException;
import java.util.*;

import static org.metadatacenter.cadsr.ingestor.util.Constants.*;
import static org.metadatacenter.model.ModelNodeNames.*;
import static org.metadatacenter.util.json.JsonMapper.MAPPER;

public class CedarServices {

  private static final Logger logger = LoggerFactory.getLogger(CedarServices.class);
  private static ObjectMapper objectMapper = new ObjectMapper();

  /*** Field services ***/

  public static void deleteAllFieldsInFolder(String folderId, CedarServer server, String apiKey) throws IOException {
    List<String> fieldIds = findFieldsInFolder(folderId, server, apiKey);
    for (String fieldId : fieldIds) {
      deleteField(fieldId, server, apiKey);
    }
  }

  // Returns the @ids of all the CEDAR fields in the given folder
  public static List<String> findFieldsInFolder(String folderId, CedarServer server, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getFolderContentsEndPoint(folderId, server);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", endpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      List<JsonNode> resources = JsonUtil.extractJsonFieldAsList(response, "resources");
      List<String> fieldIds = new ArrayList<>();
      for (JsonNode resource : resources) {
        if (resource.get("resourceType").asText().equals("field")) {
          fieldIds.add(resource.get(JSON_LD_ID).asText());
        }
      }
      return fieldIds;
    } else {
      logger.error("Error retrieving fields in folder");
      throw new InternalError("Error retrieving fields in folder");
    }
  }

  public static List<CdeSummary> findCdeSummariesInFolder(String cedarFolderId, List<String> fieldNames,
                                                          boolean includeCategoryIds, CedarServer server, String apiKey) throws IOException {

    List<CdeSummary> cdeSummaries = new ArrayList<>();

    boolean finished = false;
    int offset = 0;
    int limit = 100;
    while (!finished) {
      String endpoint = CedarServerUtil.getCdesInFolderExtractEndPoint(cedarFolderId, fieldNames, includeCategoryIds,  server);
      endpoint = endpoint + "&offset=" + offset + "&limit=" + limit;
      HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", endpoint, apiKey);
      int responseCode = connection.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
        connection.disconnect();
        List<JsonNode> resources = JsonUtil.extractJsonFieldAsList(response, "resources");
        for (JsonNode resource : resources) {
          cdeSummaries.add(objectMapper.treeToValue(resource, CdeSummary.class));
        }
        int totalCount =  JsonUtil.extractJsonFieldValueAsInt(response, "totalCount");
        if (cdeSummaries.size() >= totalCount) {
          finished = true;
        }
        else {
          offset += limit;
        }
        logger.info(cdeSummaries.size() + "/" + totalCount + " CDEs retrieved.");
      } else {
        String message = "Error retrieving CDE summaries: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
        logger.error(message);
        throw new InternalError(message);
      }
    }
    return cdeSummaries;
  }

  public static Map<String, Object> getFieldById(String fieldId, CedarServer server, String apiKey) throws IOException {
    String fieldEndpoint = CedarServerUtil.getTemplateFieldEndPoint(fieldId, server);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", fieldEndpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      return JsonUtil.readJsonAsMap(response);
    } else {
      String message = "Error retrieving field: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
      throw new InternalError(message);
    }
  }

  public static Map<String, Object> getFieldReport(String fieldId, CedarServer server, String apiKey) throws IOException {
    String fieldReportEndpoint = CedarServerUtil.getTemplateFieldReportEndPoint(fieldId, server);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", fieldReportEndpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      return JsonUtil.readJsonAsMap(response);
    } else {
      String message = "Error retrieving field report: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
      throw new InternalError(message);
    }
  }

  public static boolean isFieldInPath(String fieldId, String folderPath, CedarServer cedarServer, String apiKey) throws IOException {
    Map<String, Object> fieldReport = getFieldReport(fieldId, cedarServer, apiKey);
    if (fieldReport.containsKey("parentPath")) {
      if (fieldReport.get("parentPath").equals(folderPath)) {
        return true;
      }
      else {
        return false;
      }
    }
    throw new IllegalArgumentException("Couldn't find parent path");
  }

  /**
   * Search CDE by public identifier (e.g., 6421467) and version (e.g. 1.0.0). This method assumes that the CDEs are
   * stored in the folder Constants.CEDAR_CDES_FOLDER_PATH. Given that we still don't have a way to search by
   * schema:identifier, the method makes, first, a general search call to find all the fields that contain the publicId
   * in their title, because that's the convention that we are using to name CDEs (e.g., Discontinue Participation Date
   * (7514951)). Then, it checks that the version of the fields found matches the version passed as a parameter.
   * Finally, makes an additional call to the '/report' endpoint to check that the field is stored into the
   * Constants.CEDAR_CDES_FOLDER_PATH folder. If all the previous conditions are met, we consider that the CDE is the
   * right one, make a final call to retrieve its content in JSON-LD, and return it as a Map.
   */
  public static Optional<Map<String, Object>> searchCdeByPublicIdAndVersion(String publicId, String version,
                                                                            CedarServer cedarServer, String apiKey) throws IOException {
    String reformattedVersion = CdeUtil.reformatVersioningNumber(version);
    List<CedarResourceType> resourceTypes = Arrays.asList(new CedarResourceType[]{CedarResourceType.FIELD});
    String searchEndpoint = CedarServerUtil.getSearchEndPoint(publicId, resourceTypes, cedarServer);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", searchEndpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      int totalCount = JsonUtil.extractJsonFieldValueAsInt(response, "totalCount");
      if (totalCount > 0) {
        List<JsonNode> resources = JsonUtil.extractJsonFieldAsList(response, "resources");
        for (JsonNode resource : resources) {
          String publicIdFound = resource.get(SCHEMA_ORG_IDENTIFIER).asText();
          String versionFound = resource.get(PAV_VERSION).asText();
          if (publicIdFound.equals(publicId) && versionFound.equals(reformattedVersion)) {
            String fieldId = resource.get(JSON_LD_ID).asText();
            // Check if the field is in the CDE folder
            if (isFieldInPath(fieldId, CEDAR_CDES_FOLDER_PATH, cedarServer, apiKey)) {
              // Retrieve the field's content
              return Optional.of(getFieldById(fieldId, cedarServer, apiKey));
            }
          }
        }
      }
      return Optional.empty();
    } else {
      String message = "Error retrieving CDE: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
      throw new InternalError(message);
    }
  }

  public static void deleteField(String fieldId, CedarServer server, String apiKey) throws IOException {
    String fieldEndpoint = CedarServerUtil.getTemplateFieldEndPoint(fieldId, server);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("DELETE", fieldEndpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
      String message = "Error deleting field: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
      logger.error(message);
      throw new InternalError(message);
    }
    connection.disconnect();
  }

  public static String createCde(Map<String, Object> cdeFieldMap, String cdeHashCode, String cedarFolderId,
                                 Optional<List<String>> cedarCategoryIds, CedarServer cedarEnvironment,
                                 String apiKey) {

    HttpURLConnection conn = null;
    String cedarCdeId = null;
    try {

      String templateFieldsEndpoint = CedarServerUtil.getTemplateFieldsEndpoint(cedarFolderId, cedarEnvironment);

      // Extract the categories from the map if they are still there. They are not part of the CEDAR model so we
      // don't want to post them
      if (cdeFieldMap.containsKey(CDE_CATEGORY_IDS_FIELD)) {
        cdeFieldMap.remove(CDE_CATEGORY_IDS_FIELD);
      }

      String payload = objectMapper.writeValueAsString(cdeFieldMap);
      conn = ConnectionUtil.createAndOpenConnection("POST", templateFieldsEndpoint, apiKey);

      // Set hash code
      conn.setRequestProperty("CEDAR-Source-Hash", cdeHashCode);

      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        ConnectionUtil.logErrorMessageAndThrowException("Error creating CDE", conn);
      } else {
        // Read the CDE @id
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        cedarCdeId = JsonUtil.extractJsonFieldValueAsText(response, JSON_LD_ID);

        logger.info("CDE created successfully: " + CdeUtil.generateCdeUniqueId(cdeFieldMap) + "; CEDAR Id: " + cedarCdeId);
        CdeStats.getInstance().numberOfCdesCreated++;
        // Optionally, attach CDE to categories
        if (cedarCategoryIds.isPresent()) {
          String attachCategoriesEndpoint = CedarServerUtil.getAttachCategoriesEndpoint(cedarEnvironment);
          CedarServices.attachCdeToCategories(cedarCdeId, cedarCategoryIds.get(), false, attachCategoriesEndpoint, apiKey);
        }
      }
    } catch (Exception e) {
      logger.error(e.toString());
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return cedarCdeId;
  }

  /*** Category Services ***/

  public static String getRootCategoryId(CedarServer targetEnvironment, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getRootCategoryRestEndpoint(targetEnvironment);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", endpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      return JsonUtil.extractJsonFieldValueAsText(response, "@id");
    } else {
      logger.error("Error retrieving root category id");
      throw new InternalError("Error retrieving root category id");
    }
  }

  public static void deleteCategory(String categoryCedarId, CedarServer targetEnvironment, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getCategoryRestEndpoint(categoryCedarId, targetEnvironment);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("DELETE", endpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
      String message = "Error deleting category (" + categoryCedarId + "): " +
          ConnectionUtil.readResponseMessage(connection.getInputStream());
      logger.error(message);
      throw new InternalError(message);
    }
    else {
      logger.info("Category deleted successfully. CEDAR @id: " + categoryCedarId);
      CategoryStats.getInstance().numberOfCategoriesDeleted++;
    }
    connection.disconnect();
  }

  /**
   * Uploads a category to CEDAR, including its children
   */
  public static String createCategory(CategoryTreeNode category, String cedarParentCategoryId,
                                    CedarServer server, String apiKey) {

    Map<String, String> categoryFieldsMap = new HashMap<>();
    categoryFieldsMap.put(SCHEMA_ORG_IDENTIFIER, category.getUniqueId());
    categoryFieldsMap.put(SCHEMA_ORG_NAME, category.getName());
    categoryFieldsMap.put(SCHEMA_ORG_DESCRIPTION, category.getDescription());
    categoryFieldsMap.put(NodeProperty.PARENT_CATEGORY_ID.getValue(), cedarParentCategoryId);

    String cedarCategoryId = null;
    HttpURLConnection conn = null;
    try {
      Thread.sleep(50);
      logger.info("Creating category with unique id: " + category.getUniqueId());
      String payload = objectMapper.writeValueAsString(categoryFieldsMap);
      String url = CedarServerUtil.getCategoriesRestEndpoint(server);
      conn = ConnectionUtil.createAndOpenConnection("POST", url, apiKey);
      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
        logger.error("Error creating category: " + category.getUniqueId());
        GeneralUtil.logErrorMessage(conn);
      } else {
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        cedarCategoryId = JsonUtil.extractJsonFieldValueAsText(response, "@id");
        logger.info("Category created successfully: " + category.getUniqueId() + "; CEDAR Category Id: " + cedarCategoryId);
        CategoryStats.getInstance().numberOfCategoriesCreated++;
        for (CategoryTreeNode categoryTreeNode : category.getChildren()) {
          createCategory(categoryTreeNode, cedarCategoryId, server, apiKey);
        }
      }
    } catch (JsonProcessingException e) {
      logger.error(e.toString());
    } catch (IOException e) {
      logger.error(e.toString());
    } catch (InterruptedException e) {
      logger.error(e.getMessage());
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return cedarCategoryId;
  }

  public static Map<String, String> getCategoryUniqueIdsToCedarCategoryIdsMap(Constants.CedarServer targetEnvironment, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getCategoryTreeEndpoint(targetEnvironment);
    Map<String, String> categoryUniqueIdsMap = null;
    HttpURLConnection conn = null;
    try {
      conn = ConnectionUtil.createAndOpenConnection("GET", endpoint, apiKey);
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        GeneralUtil.logErrorMessage(conn);
      } else {
        // Read the CDE @id
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        JsonNode categoryTree = objectMapper.readTree(response);
        categoryUniqueIdsMap = CategoryUtil.getCategoryUniqueIdsFromCategoryTree(categoryTree);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return categoryUniqueIdsMap;
  }

  public static CedarCategory getCedarCategoryTree(Constants.CedarServer targetEnvironment, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getCategoryTreeEndpoint(targetEnvironment);
    HttpURLConnection conn = null;
    CedarCategory categoryTree = null;
    try {
      conn = ConnectionUtil.createAndOpenConnection("GET", endpoint, apiKey);
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        GeneralUtil.logErrorMessage(conn);
      } else {
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        categoryTree = objectMapper.readValue(response, CedarCategory.class);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return categoryTree;
  }

  public static void updateCategory(String categoryCedarId, CategoryTreeNode category, CedarServer server,
                                    String apiKey) {

    Map<String, String> categoryFieldsMap = new HashMap<>();
    categoryFieldsMap.put(SCHEMA_ORG_IDENTIFIER, category.getUniqueId());
    categoryFieldsMap.put(SCHEMA_ORG_NAME, category.getName());
    categoryFieldsMap.put(SCHEMA_ORG_DESCRIPTION, category.getDescription());

    HttpURLConnection conn = null;
    try {
      String payload = objectMapper.writeValueAsString(categoryFieldsMap);
      String url = CedarServerUtil.getCategoryRestEndpoint(categoryCedarId, server);
      conn = ConnectionUtil.createAndOpenConnection("PUT", url, apiKey);
      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK) {
        logger.error("Error updating category: " + category.getUniqueId());
        GeneralUtil.logErrorMessage(conn);
      }
      else {
        logger.info("Category updated successfully. Id: " + category.getUniqueId() + "; CEDAR @id: " + categoryCedarId);
        CategoryStats.getInstance().numberOfCategoriesUpdated++;
      }
    } catch (JsonProcessingException e) {
      logger.error(e.toString());
    } catch (IOException e) {
      logger.error(e.toString());
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
  }

  /*** CDE-Category Services ***/

  /**
   * Attach a CDE to multiple categories (making a single REST call)
   */
  public static void attachCdeToCategories(String cedarCdeId, List<String> cedarCategoryIds, boolean reviewMode,
                                           String endpoint,
                                           String apiKey) {

    if (cedarCategoryIds.size() > 0) {
      logger.info("Attaching CDE to the following categories: ");
      for (int i=0; i<cedarCategoryIds.size(); i++) {
        logger.info(" - " + cedarCategoryIds.get(i));
      }
    }

    if (!cedarCategoryIds.isEmpty()) {

      ObjectNode node = objectMapper.createObjectNode();
      node.put(CEDAR_CATEGORY_ATTACH_ARTIFACT_ID, cedarCdeId);
      ArrayNode array = objectMapper.valueToTree(cedarCategoryIds);
      node.putArray(CEDAR_CATEGORY_ATTACH_CATEGORY_IDS).addAll(array);

      HttpURLConnection conn = null;
      try {
        String payload = objectMapper.writeValueAsString(node);
        conn = ConnectionUtil.createAndOpenConnection("POST", endpoint, apiKey);
        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes());
        os.flush();
        int responseCode = conn.getResponseCode();
        if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
          GeneralUtil.logErrorMessage(conn);
        } else {
          logger.info("CDE attached successfully to categories");
          if (reviewMode) {
            CdeStats.getInstance().numberOfCdeToCategoryRelationsCreatedAfterReview += cedarCategoryIds.size();
          }
          else {
            CdeStats.getInstance().numberOfCdeToCategoryRelationsCreatedWhenCreatingCdes += cedarCategoryIds.size();
          }

        }
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        logger.error(e.toString());
      } finally {
        if (conn != null) {
          conn.disconnect();
        }
      }
    } else {
      String message = "No category ids provided";
      logger.error(message);
      throw new IllegalArgumentException(message);
    }
  }

  /*** Template Services ***/

  public static String createTemplate(Map<String, Object> templateMap, String cedarFolderId,
                                      CedarServer cedarServer, String apiKey) throws RuntimeException, IOException {

    HttpURLConnection conn = null;
    String cedarTemplateId = null;
    try {

      String templatesEndpoint = CedarServerUtil.getTemplatesEndpoint(cedarFolderId, cedarServer);
      String payload = objectMapper.writeValueAsString(templateMap);
      conn = ConnectionUtil.createAndOpenConnection("POST", templatesEndpoint, apiKey);
      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        ConnectionUtil.logErrorMessageAndThrowException("Error creating template", conn);
      } else {
        // Read the template @id
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        cedarTemplateId = JsonUtil.extractJsonFieldValueAsText(response, JSON_LD_ID);
        logger.info("Template created successfully. CEDAR Id: " + cedarTemplateId);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return cedarTemplateId;
  }

  /*** Terminology Services ***/
  public static Map<String, Object> integratedSearch(Map<String, Object> valueConstraints, Integer page, Integer pageSize,
                                                     CedarServer cedarEnvironment, String apiKey) throws IOException {
    HttpURLConnection conn = null;
    Map<String, Object> resultsMap = new HashMap<>();
    try {
      String integratedSearchEndpoint = CedarServerUtil.getIntegratedSearchEndpoint(cedarEnvironment);
      Map<String, Object> vcMap = new HashMap<>();
      vcMap.put("valueConstraints", valueConstraints);
      Map<String, Object> payloadMap = new HashMap<>();
      payloadMap.put("parameterObject", vcMap);
      payloadMap.put("page", page);
      payloadMap.put("pageSize", pageSize);
      String payload = objectMapper.writeValueAsString(payloadMap);
      conn = ConnectionUtil.createAndOpenConnection("POST", integratedSearchEndpoint, apiKey);
      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        ConnectionUtil.logErrorMessageAndThrowException("Error creating template", conn);
      } else {
        String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
        resultsMap = objectMapper.readValue(response, HashMap.class);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return resultsMap;
  }

}
