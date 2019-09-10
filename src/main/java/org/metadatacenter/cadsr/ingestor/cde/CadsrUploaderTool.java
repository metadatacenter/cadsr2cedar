package org.metadatacenter.cadsr.ingestor.cde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import com.google.common.io.Files;
import org.metadatacenter.cadsr.ingestor.ConnectionUtils;
import org.metadatacenter.cadsr.ingestor.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.metadatacenter.cadsr.ingestor.Constants.*;

public class CadsrUploaderTool {

  private static final Logger logger = LoggerFactory.getLogger(CadsrUploaderTool.class);

  private static final DecimalFormat countFormat = new DecimalFormat("#,###,###,###");

  private static ObjectMapper objectMapper = new ObjectMapper();

  private static Map<String, List<String>> cedarCdeIdsToCategoryIds;
  private static Map<String, List<String>> cedarCdeIdsToCedarCategoryIds;

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

    final Stopwatch stopwatch = Stopwatch.createStarted();

    int totalCdes = 0;
    boolean success = false;
    try {
      File inputSource = new File(inputSourceLocation);
      String endpoint = getRestEndpoint(targetServer, folderId);
      if (inputSource.isDirectory()) {
        totalCdes = uploadCdeFromDirectory(inputSource, endpoint, apiKey);
      } else {
        totalCdes = uploadCdeFromFile(inputSource, endpoint, apiKey);
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

  private static String getRestEndpoint(String targetServer, String folderId) {
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

  private static int uploadCdeFromDirectory(File inputDir, String endpoint, String apiKey) throws IOException {
    int totalCdes = 0;
    for (final File inputFile : inputDir.listFiles()) {
      totalCdes += uploadCdeFromFile(inputFile, endpoint, apiKey);
    }
    return totalCdes;
  }

  public static int uploadCdeFromFile(File inputFile, String endpoint, String apiKey) throws IOException {
    logger.info("Processing input file at " + inputFile.getAbsolutePath());
    Collection<Map<String, Object>> fieldMaps = CadsrUtils.getFieldMapsFromInputStream(new FileInputStream(inputFile));
    int totalFields = fieldMaps.size();
    if (totalFields > 0) {
      int counter = 0;
      for (Map<String, Object> fieldMap : fieldMaps) {
        HttpURLConnection conn = null;
        try {
          String payload = new ObjectMapper().writeValueAsString(fieldMap);
          conn = createAndOpenConnection(endpoint, apiKey);
          OutputStream os = conn.getOutputStream();
          os.write(payload.getBytes());
          os.flush();
          int responseCode = conn.getResponseCode();
          if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            logErrorMessage(conn);
          } else {
            logResponseMessage(conn);

            // Read the CDE @id
            String response = ConnectionUtils.readResponseMessage(conn.getInputStream());
            String cedarCdeId = JsonUtils.extractJsonFieldValue(response, "@id");

            List<String> categoryIds = (List) fieldMap.get(CDE_CATEGORY_IDS_FIELD);
            cedarCdeIdsToCategoryIds.put(cedarCdeId, categoryIds);


          }
          if (multiplesOfAHundred(counter)) {
            logger.info(String.format("Uploading CDEs (%d/%d)", counter, totalFields));
          }
          counter++;
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
      logger.info(String.format("Uploading CDEs (%d/%d)", counter, totalFields));
    }
    return totalFields;
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

  private static HttpURLConnection createAndOpenConnection(String endpoint, String apiKey) throws IOException {
    ignoreSSLCheckingByAcceptingAnyCertificates();
    try {
      URL url = new URL(endpoint);
      HttpURLConnection conn = (HttpURLConnection) url.openConnection();
      conn.setDoOutput(true);
      conn.setRequestMethod("POST");
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