package org.metadatacenter.cadsr.ingestor.category;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CadsrCategoriesUploaderTool {

  private static final Logger logger = LoggerFactory.getLogger(CadsrCategoriesUploaderTool.class);

  private static final DecimalFormat countFormat = new DecimalFormat("#,###,###,###");

  private static ObjectMapper objectMapper = new ObjectMapper();

  //@formatter:off
  private static TrustManager[] trustAllCerts = new TrustManager[1];

  static {
    trustAllCerts[0] = new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return null;
      }

      public void checkClientTrusted(X509Certificate[] certs, String authType) {
      }

      public void checkServerTrusted(X509Certificate[] certs, String authType) {
      }
    };
  }
  //@formatter:on

  public static void main(String[] args) {

    String inputSourceLocation = args[0];
    String cedarRootCategoryId = args[1];
    String targetServer = args[2];
    String apiKey = args[3];

    final Stopwatch stopwatch = Stopwatch.createStarted();

    int totalCategories = 0;
    boolean success = false;
    try {
      File inputSource = new File(inputSourceLocation);
      String endpoint = getRestEndpoint(targetServer);

      if (inputSource.isDirectory()) {
        totalCategories = uploadCategoriesFromDirectory(inputSource, cedarRootCategoryId, endpoint, apiKey);
      } else {
        totalCategories = uploadCategoriesFromFile(inputSource, cedarRootCategoryId, endpoint, apiKey);
      }
      success = true;
    } catch (Exception e) {
      logger.error(e.toString());
      success = false;
    } finally {
      printSummary(stopwatch, totalCategories, success);
    }
  }

  private static String getRestEndpoint(String targetServer) {
    String serverUrl = getServerUrl(targetServer);
    return serverUrl + "/categories";
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

  private static int uploadCategoriesFromDirectory(File inputDir, String cedarRootCategoryId, String endpoint,
                                                   String apiKey) throws IOException {
    int totalCategories = 0;
    for (final File inputFile : inputDir.listFiles()) {
      totalCategories += uploadCategoriesFromFile(inputFile, cedarRootCategoryId, endpoint, apiKey);
    }
    return totalCategories;
  }

  public static int uploadCategoriesFromFile(File inputFile, String cedarRootCategoryId, String endpoint,
                                             String apiKey) throws IOException {

    List<CategoryTreeNode> categoryTreeNodes = readCategoriesFromFile(inputFile);
    int counter = 0;
    for (CategoryTreeNode categoryTreeNode : categoryTreeNodes) {
      uploadCategory(categoryTreeNode, cedarRootCategoryId, endpoint, apiKey, counter);
    }
    return counter;
  }

  private static void uploadCategory(CategoryTreeNode category, String cedarParentCategoryId,
                                     String endpoint, String apiKey, int counter) {

    CedarCategory cedarCategory =
        new CedarCategory(category.getCadsrId(), category.getName(), category.getDescription(), cedarParentCategoryId);

    HttpURLConnection conn = null;
    try {
      Thread.sleep(50);
      logger.info("Trying to upload: " + category.getCadsrId());
      String payload = objectMapper.writeValueAsString(cedarCategory);
      conn = createAndOpenConnection(endpoint, apiKey);
      OutputStream os = conn.getOutputStream();
      os.write(payload.getBytes());
      os.flush();
      int responseCode = conn.getResponseCode();
      if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
        logger.error("Error creating category: " + category.getCadsrId());
        logErrorMessage(conn);
      } else {
        logger.info("Category created: " + payload);
        String response = ConnectionUtils.readResponseMessage(conn.getInputStream());
        String cedarCategoryId = JsonUtils.extractJsonFieldValue(response, "@id");
        counter++;
        //logger.info(String.format("Uploading categories (%d/%d)", counter, allCategories.size()));
        for (CategoryTreeNode categoryTreeNode : category.getChildren()) {
          uploadCategory(categoryTreeNode, cedarCategoryId, endpoint, apiKey, counter);
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
  }

  private static List<CategoryTreeNode> readCategoriesFromFile(File inputFile) throws IOException {
    logger.info("Processing input file at " + inputFile.getAbsolutePath());
    List<CategoryTreeNode> categories =
        objectMapper.readValue(inputFile, objectMapper.getTypeFactory().constructCollectionType(List.class,
            CategoryTreeNode.class));
    return categories;
  }

  private static void logErrorMessage(final HttpURLConnection conn) {
    String response = ConnectionUtils.readResponseMessage(conn.getErrorStream());
    logger.error(response);
    throw new RuntimeException("Unable to upload Category. Reason:\n" + response);
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

  private static void printSummary(Stopwatch stopwatch, int totalCdes, boolean success) {
    logger.info("----------------------------------------------------------");
    if (success) {
      logger.info("UPLOAD-CATEGORIES SUCCESS");
    } else {
      logger.info("UPLOAD-CATEGORIES FAILED (see error.log for details)");
    }
    logger.info("----------------------------------------------------------");
    logger.info("Total number categories uploaded: " + countFormat.format(totalCdes));
    long elapsedTimeInSeconds = stopwatch.elapsed(TimeUnit.SECONDS);
    long hours = elapsedTimeInSeconds / 3600;
    long minutes = (elapsedTimeInSeconds % 3600) / 60;
    long seconds = (elapsedTimeInSeconds % 60);
    logger.info("Total time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    logger.info("Finished at: " + LocalDateTime.now());
    logger.info("----------------------------------------------------------");
  }

}
