package org.metadatacenter.cadsr.ingestor.cde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import com.google.inject.internal.cglib.proxy.$FixedValue;
import org.metadatacenter.cadsr.ingestor.ConnectionUtils;
import org.metadatacenter.cadsr.ingestor.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.metadatacenter.cadsr.ingestor.Constants.*;

public class CadsrUploaderTool {

  private static final Logger logger = LoggerFactory.getLogger(CadsrUploaderTool.class);

  private static final DecimalFormat countFormat = new DecimalFormat("#,###,###,###");

  private static ObjectMapper objectMapper = new ObjectMapper();

  private static Map<String, String> categoryIdsToCedarCategoryIds = new HashMap<>();

  //@formatter:off
  private static TrustManager[] trustAllCerts = new TrustManager[1];
  static {
    trustAllCerts[0] = new X509TrustManager() {
      public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
      public void checkClientTrusted(X509Certificate[] certs, String authType) {}
      public void checkServerTrusted(X509Certificate[] certs, String authType) {}
    };
  }
  //@formatter:on

  public static void main(String[] args) {

    String inputSourceLocation = args[0];
    String targetServer = args[1];
    String folderId = args[2];
    String apiKey = args[3];
    boolean attachCategories = false;
    if ((args.length > 4) && (args[4] != null) && args[4].equals(ATTACH_CATEGORIES_OPTION)) {
      attachCategories = true;
    }

    // Read the categoryIds from CEDAR to be able to link CDEs to them
    if (attachCategories) {
      String categoryTreeEndpoint = getCategoryTreeEndpoint(targetServer);
      try {
        categoryIdsToCedarCategoryIds = getCedarCategoryIds(categoryTreeEndpoint, apiKey);
      } catch (IOException e) {
        logger.error(e.getMessage());
      }
    }

    final Stopwatch stopwatch = Stopwatch.createStarted();

    int totalCdes = 0;
    boolean success = false;
    try {
      File inputSource = new File(inputSourceLocation);
      String templateFieldsEndpoint = getTemplateFieldsEndpoint(targetServer, folderId);
      String attachCategoriesEndpoint = getAttachCategoriesEndpoint(targetServer);
      if (inputSource.isDirectory()) {
        totalCdes = uploadCdeFromDirectory(inputSource, attachCategories, templateFieldsEndpoint, attachCategoriesEndpoint, apiKey);
      } else {
        totalCdes = uploadCdeFromFile(inputSource, attachCategories, templateFieldsEndpoint, attachCategoriesEndpoint, apiKey);
      }
      success = true;
    } catch (Exception e) {
      logger.error(e.toString());
      success = false;
    } finally {
      storeOntologyInTempDir();
      printSummary(stopwatch, totalCdes, success);
    }
  }

  private static String getTemplateFieldsEndpoint(String targetServer, String folderId) {
    String serverUrl = getServerUrl(targetServer);
    String folderUrl = getFolderUrl(targetServer);
    return serverUrl + "/template-fields?folder_id=" + folderUrl + "%2F" + folderId;
  }

  private static String getServerUrl(String targetServer) {
    if ("local".equals(targetServer)) {
      return "https://resource.metadatacenter.orgx";
    } else if ("staging".equals(targetServer)) {
      return "https://resource.staging.metadatacenter.org";
    } else if ("production".equals(targetServer)) {
      return "https://resource.metadatacenter.org";
    }
    throw new RuntimeException("Invalid target server, possible values are 'local', 'staging', 'production'");
  }

  private static String getFolderUrl(String targetServer) {
    if ("local".equals(targetServer)) {
      return "https:%2F%2Frepo.metadatacenter.orgx%2Ffolders";
    } else if ("staging".equals(targetServer)) {
      return "https:%2F%2Frepo.staging.metadatacenter.org%2Ffolders";
    } else if ("production".equals(targetServer)) {
      return "https:%2F%2Frepo.metadatacenter.org%2Ffolders";
    }
    throw new RuntimeException("Invalid target server, possible values are 'local', 'staging', 'production'");
  }

  private static String getCategoryTreeEndpoint(String targetServer) {
    String serverUrl = getServerUrl(targetServer);
    return serverUrl + "/categories/tree";
  }

  private static String getAttachCategoryEndpoint(String targetServer) {
    String serverUrl = getServerUrl(targetServer);
    return serverUrl + "/command/attach-category";
  }

  private static String getAttachCategoriesEndpoint(String targetServer) {
    String serverUrl = getServerUrl(targetServer);
    return serverUrl + "/command/attach-categories";
  }

  private static int uploadCdeFromDirectory(File inputDir, boolean attachCategories, String templateFieldsEndpoint,
                                            String attachCategoryEndpoint, String apiKey) throws IOException {
    int totalCdes = 0;
    for (final File inputFile : inputDir.listFiles()) {
      totalCdes += uploadCdeFromFile(inputFile, attachCategories, templateFieldsEndpoint, attachCategoryEndpoint, apiKey);
    }
    return totalCdes;
  }

  public static int uploadCdeFromFile(File inputFile, boolean attachCategories, String templateFieldsEndpoint,
                                      String attachCategoryEndpoint, String apiKey) throws IOException {

      /**********/
//      templateFieldsEndpoint = "http://www.mocky.io/v2/5d77e1e83200005847924066";
//      attachCategoryEndpoint = "http://www.mocky.io/v2/5d77e1e83200005847924066";
      /**********/

      logger.info("Processing input file at " + inputFile.getAbsolutePath());
      Collection<Map<String, Object>> fieldMaps =
          CadsrUtils.getFieldMapsFromInputStream(new FileInputStream(inputFile));

      int totalFields = fieldMaps.size();
      if (totalFields > 0) {
        int counter = 0;
        for (Map<String, Object> fieldMap : fieldMaps) {
          HttpURLConnection conn = null;
          try {

            // Extract the categories from the map. They are not part of the CEDAR model so we don't want to post them
            List<String> categoryIds = (List) fieldMap.get(CDE_CATEGORY_IDS_FIELD);
            fieldMap.remove(CDE_CATEGORY_IDS_FIELD);

            String payload = objectMapper.writeValueAsString(fieldMap);
            conn = createAndOpenConnection("POST", templateFieldsEndpoint, apiKey);
            OutputStream os = conn.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            int responseCode = conn.getResponseCode();
            if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
              logErrorMessage(conn);
            } else {
              //logger.info("CDE uploaded: " + payload);
              if (attachCategories) {
                // Read the CDE @id
                String response = ConnectionUtils.readResponseMessage(conn.getInputStream());
                String cedarCdeId = JsonUtils.extractJsonFieldValue(response, "@id");
                attachCdeToCategories(cedarCdeId, categoryIds, attachCategoryEndpoint, apiKey);
              }
            }
            if (multiplesOfAHundred(counter)) {
              logger.info(String.format("Uploading CDEs (%d/%d)", counter, totalFields));
            }
            counter++;
          } catch (Exception e) {
            logger.error(e.toString());
          } finally {
            if (conn != null) {
              conn.disconnect();
            }
          }
        }
        logger.info(String.format("Uploading CDEs (%d/%d)", counter, totalFields));
      }
    return totalFields;
  }

  private static Map<String, String> getCedarCategoryIds(String endpoint, String apiKey) throws IOException {

    /**********/
    //endpoint = "http://www.mocky.io/v2/5d77fe6b3200006f7f9240ee";
    /***********/

    Map<String, String> categoryIdsMap = null;
    HttpURLConnection conn = null;
    try {
      conn = createAndOpenConnection("GET", endpoint, apiKey);
      int responseCode = conn.getResponseCode();
      if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
        logErrorMessage(conn);
      } else {
        // Read the CDE @id
        String response = ConnectionUtils.readResponseMessage(conn.getInputStream());
        JsonNode categoryTree = objectMapper.readTree(response);
        categoryIdsMap = CadsrUtils.getCategoryIdsFromCategoryTree(categoryTree);
      }
    } finally {
      if (conn != null) {
        conn.disconnect();
      }
    }
    return categoryIdsMap;
  }

  /**
   *
   * Attach a CDE to multiple categories (making multiple REST calls)
   *
   * @param cedarCdeId: CEDAR CDE id
   * @param categoryIds: list of cadsr category Ids (not CEDAR  ids)
   */
  private static void attachCdeToCategoriesMultipleCalls(String cedarCdeId, List<String> categoryIds, String endpoint,
                                                         String apiKey) {

    for (String categoryId : categoryIds) {

      if (categoryIdsToCedarCategoryIds.containsKey(categoryId)) {

        String cedarCategoryId = categoryIdsToCedarCategoryIds.get(categoryId);

        ObjectNode node = objectMapper.createObjectNode();
        node.put(CEDAR_CATEGORY_ATTACH_ARTIFACT_ID, cedarCdeId);
        node.put(CEDAR_CATEGORY_ATTACH_CATEGORY_ID, cedarCategoryId);

        HttpURLConnection conn = null;
        try {
          String payload = objectMapper.writeValueAsString(node);
          logger.info("Attaching CDE to category: " + payload);
          conn = createAndOpenConnection("POST", endpoint, apiKey);
          OutputStream os = conn.getOutputStream();
          os.write(payload.getBytes());
          os.flush();
          int responseCode = conn.getResponseCode();
          if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            logErrorMessage(conn);
          } else {
            logResponseMessage(conn);
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
      }
      else {
        logger.error("Could not find CEDAR Id in map for category id: " + categoryId);
      }
    }
  }

  /**
   * Attach a CDE to multiple categories (making a single REST call)
   *
   * @param cedarCdeId:  CEDAR CDE id
   * @param categoryIds: list of cadsr category Ids (not CEDAR ids)
   */
  private static void attachCdeToCategories(String cedarCdeId, List<String> categoryIds, String endpoint,
                                            String apiKey) {

    List<String> cedarCategoryIds = new ArrayList<>();
    for (String categoryId : categoryIds) {
      if (categoryIdsToCedarCategoryIds.containsKey(categoryId)) {
        cedarCategoryIds.add(categoryIdsToCedarCategoryIds.get(categoryId));
      }
      else {
        logger.error("Could not find CEDAR Id in map for category id: " + categoryId);
      }
    }

    logger.info("Attaching CDE to the following categories: " + String.join(", ", cedarCategoryIds));

    if (!cedarCategoryIds.isEmpty()) {

      ObjectNode node = objectMapper.createObjectNode();
      node.put(CEDAR_CATEGORY_ATTACH_ARTIFACT_ID, cedarCdeId);
      ArrayNode array = objectMapper.valueToTree(cedarCategoryIds);
      node.putArray(CEDAR_CATEGORY_ATTACH_CATEGORY_IDS).addAll(array);

      HttpURLConnection conn = null;
      try {
        String payload = objectMapper.writeValueAsString(node);
        logger.info("Attaching CDE to category: " + payload);
        conn = createAndOpenConnection("POST", endpoint, apiKey);
        OutputStream os = conn.getOutputStream();
        os.write(payload.getBytes());
        os.flush();
        int responseCode = conn.getResponseCode();
        if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
          logErrorMessage(conn);
        } else {
          logResponseMessage(conn);
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

    }
  }


  private static void logErrorMessage(final HttpURLConnection conn) {
    String response = ConnectionUtils.readResponseMessage(conn.getErrorStream());
    logger.error(response);
    throw new RuntimeException("Unable to upload CDE. Reason:\n" + response);
  }

  private static void logResponseMessage(final HttpURLConnection conn) throws IOException {
    String response = ConnectionUtils.readResponseMessage(conn.getInputStream());
    String message = createMessageBasedOnFieldNameAndId(response);
    logger.debug("POST 200 OK: " + message);
  }

  private static String createMessageBasedOnFieldNameAndId(String response) throws IOException {
    JsonNode responseNode = objectMapper.readTree(response);
    String fieldName = responseNode.get("schema:name").asText();
    String fieldId = responseNode.get("@id").asText();
    return String.format("%s (ID: %s)", fieldName, fieldId);
  }

  private static HttpURLConnection createAndOpenConnection(String requestMethod, String endpoint, String apiKey) throws IOException {
    ignoreSSLCheckingByAcceptingAnyCertificates();
    try {
      URL url = new URL(endpoint);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestProperty("Content-Type", "application/json");
      conn.setRequestProperty("Authorization", apiKey);
      return conn;
    } catch (MalformedURLException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  private static void ignoreSSLCheckingByAcceptingAnyCertificates() {
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
      HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> 1 == 1);
    } catch (KeyManagementException | NoSuchAlgorithmException e) {
      logger.error(e.getMessage());
      throw new RuntimeException(e.getMessage());
    }
  }

  private static boolean multiplesOfAHundred(int counter) {
    return counter != 0 && counter % 100 == 0;
  }

  private static void storeOntologyInTempDir() {
    File outputTempDir = Files.createTempDir();
    File outputOntologyFile = new File(outputTempDir, CDE_VALUESETS_ONTOLOGY_NAME);
    logger.info("Storing the generated value set ontology at " + outputOntologyFile);
    ValueSetsOntologyManager.saveOntology(outputOntologyFile);
  }

  private static void printSummary(Stopwatch stopwatch, int totalCdes, boolean success) {
    logger.info("----------------------------------------------------------");
    if (success) {
      logger.info("UPLOAD SUCCESS");
    } else {
      logger.info("UPLOAD FAILED (see error.log for details)");
    }
    logger.info("----------------------------------------------------------");
    logger.info("Total number of generated CDEs: " + countFormat.format(totalCdes));
    long elapsedTimeInSeconds = stopwatch.elapsed(TimeUnit.SECONDS);
    long hours = elapsedTimeInSeconds / 3600;
    long minutes = (elapsedTimeInSeconds % 3600) / 60;
    long seconds = (elapsedTimeInSeconds % 60);
    logger.info("Total time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    logger.info("Finished at: " + LocalDateTime.now());
    logger.info("----------------------------------------------------------");
  }
}
