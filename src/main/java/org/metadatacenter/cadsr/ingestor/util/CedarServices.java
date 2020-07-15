package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.metadatacenter.cadsr.ingestor.category.CategoryStats;
import org.metadatacenter.cadsr.ingestor.category.CategoryTreeNode;
import org.metadatacenter.cadsr.ingestor.category.CedarCategory;
import org.metadatacenter.cadsr.ingestor.cde.CdeStats;
import org.metadatacenter.cadsr.ingestor.cde.CdeSummary;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;
import org.metadatacenter.server.neo4j.cypher.NodeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.*;

import static org.metadatacenter.cadsr.ingestor.util.Constants.*;
import static org.metadatacenter.model.ModelNodeNames.*;

public class CedarServices {

  private static final Logger logger = LoggerFactory.getLogger(CdeUploadUtil.class);
  private static ObjectMapper objectMapper = new ObjectMapper();

  /*** Field services ***/

  public static void deleteAllFieldsInFolder(String folderId, CedarEnvironment environment, String apiKey) throws IOException {
    List<String> fieldIds = findFieldsInFolder(folderId, environment, apiKey);
    for (String fieldId : fieldIds) {
      deleteField(fieldId, environment, apiKey);
    }
  }

  // Returns the @ids of all the CEDAR fields in the given folder
  public static List<String> findFieldsInFolder(String folderId, CedarEnvironment environment, String apiKey) throws IOException {
    String endpoint = CedarServerUtil.getFolderContentsEndPoint(folderId, environment);
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
                                                          boolean includeCategoryIds, CedarEnvironment environment, String apiKey) throws IOException {

    List<CdeSummary> cdeSummaries = new ArrayList<>();

    boolean finished = false;
    int offset = 0;
    int limit = 100;
    while (!finished) {
      String endpoint = CedarServerUtil.getCdesInFolderExtractEndPoint(cedarFolderId, fieldNames, includeCategoryIds,  environment);
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

  public static Map<String, Object> getCdeById(String fieldId, CedarEnvironment environment, String apiKey) throws IOException {
    String fieldEndpoint = CedarServerUtil.getTemplateFieldEndPoint(fieldId, environment);
    HttpURLConnection connection = ConnectionUtil.createAndOpenConnection("GET", fieldEndpoint, apiKey);
    int responseCode = connection.getResponseCode();
    if (responseCode == HttpURLConnection.HTTP_OK) {
      String response = ConnectionUtil.readResponseMessage(connection.getInputStream());
      connection.disconnect();
      return JsonUtil.readJsonAsMap(response);
    } else {
      String message = "Error retrieving CDE: " + ConnectionUtil.readResponseMessage(connection.getInputStream());
      throw new InternalError(message);
    }
  }

  public static void deleteField(String fieldId, CedarEnvironment environment, String apiKey) throws IOException {
    String fieldEndpoint = CedarServerUtil.getTemplateFieldEndPoint(fieldId, environment);
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
                                 Optional<List<String>> cedarCategoryIds, CedarEnvironment cedarEnvironment,
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

  public static String getRootCategoryId(CedarEnvironment targetEnvironment, String apiKey) throws IOException {
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

  public static void deleteCategory(String categoryCedarId, CedarEnvironment targetEnvironment, String apiKey) throws IOException {
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
                                    CedarEnvironment environment, String apiKey) {

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
      String url = CedarServerUtil.getCategoriesRestEndpoint(environment);
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
          createCategory(categoryTreeNode, cedarCategoryId, environment, apiKey);
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

  public static Map<String, String> getCategoryUniqueIdsToCedarCategoryIdsMap(Constants.CedarEnvironment targetEnvironment, String apiKey) throws IOException {
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

  public static CedarCategory getCedarCategoryTree(Constants.CedarEnvironment targetEnvironment, String apiKey) throws IOException {
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

  public static void updateCategory(String categoryCedarId, CategoryTreeNode category, CedarEnvironment environment,
                                    String apiKey) {

    Map<String, String> categoryFieldsMap = new HashMap<>();
    categoryFieldsMap.put(SCHEMA_ORG_IDENTIFIER, category.getUniqueId());
    categoryFieldsMap.put(SCHEMA_ORG_NAME, category.getName());
    categoryFieldsMap.put(SCHEMA_ORG_DESCRIPTION, category.getDescription());

    HttpURLConnection conn = null;
    try {
      String payload = objectMapper.writeValueAsString(categoryFieldsMap);
      String url = CedarServerUtil.getCategoryRestEndpoint(categoryCedarId, environment);
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

}
