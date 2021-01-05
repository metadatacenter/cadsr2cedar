package org.metadatacenter.cadsr.ingestor.util;

import org.metadatacenter.model.CedarResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.metadatacenter.cadsr.ingestor.util.Constants.*;
import static org.metadatacenter.constant.CedarQueryParameters.QP_RESOURCE_TYPES;

public class CedarServerUtil {

  private static final Logger logger = LoggerFactory.getLogger(CedarServerUtil.class);

  public static CedarServer toCedarServerFromServerName(String targetServer) {
    if (targetServer.compareToIgnoreCase(CedarServer.LOCAL.name()) == 0) {
      return CedarServer.LOCAL;
    }
    else if (targetServer.compareToIgnoreCase(CedarServer.STAGING.name()) == 0) {
      return CedarServer.STAGING;
    }
    else if (targetServer.compareToIgnoreCase(CedarServer.PRODUCTION.name()) == 0) {
      return CedarServer.PRODUCTION;
    }
    else {
      throw new IllegalArgumentException("Invalid target environment: " +
          targetServer + ". Allowed values: local, staging, production");
    }
  }

  public static CedarServer toCedarServerFromHostName(String cedarHost) {
    if (cedarHost.compareToIgnoreCase(LOCAL_CEDAR_HOST) == 0) {
      return CedarServer.LOCAL;
    }
    else if (cedarHost.compareToIgnoreCase(STAGING_CEDAR_HOST) == 0) {
      return CedarServer.STAGING;
    }
    else if (cedarHost.compareToIgnoreCase(PRODUCTION_CEDAR_HOST) == 0) {
      return CedarServer.PRODUCTION;
    }
    else {
      throw new IllegalArgumentException("Invalid cedar host: " + cedarHost);
    }
  }

  public static String getResourceServerUrl(CedarServer cedarServer) {
    if (Constants.CedarServer.LOCAL.equals(cedarServer)) {
      return LOCAL_RESOURCE_SERVER_URL;
    } else if (Constants.CedarServer.STAGING.equals(cedarServer)) {
      return STAGING_RESOURCE_SERVER_URL;
    } else if (Constants.CedarServer.PRODUCTION.equals(cedarServer)) {
      return PRODUCTION_RESOURCE_SERVER_URL;
    }
    throw new RuntimeException("Invalid target environment");
  }

  public static String getRepoServerUrl(CedarServer cedarServer) {
    if (Constants.CedarServer.LOCAL.equals(cedarServer)) {
      return LOCAL_REPO_SERVER_URL;
    } else if (Constants.CedarServer.STAGING.equals(cedarServer)) {
      return STAGING_REPO_SERVER_URL;
    } else if (Constants.CedarServer.PRODUCTION.equals(cedarServer)) {
      return PRODUCTION_REPO_SERVER_URL;
    }
    throw new RuntimeException("Invalid target environment");
  }

  public static String getTerminologyServerUrl(CedarServer cedarServer) {
    if (Constants.CedarServer.LOCAL.equals(cedarServer)) {
      return LOCAL_TERMINOLOGY_SERVER_URL;
    } else if (Constants.CedarServer.STAGING.equals(cedarServer)) {
      return STAGING_TERMINOLOGY_SERVER_URL;
    } else if (Constants.CedarServer.PRODUCTION.equals(cedarServer)) {
      return PRODUCTION_TERMINOLOGY_SERVER_URL;
    }
    throw new RuntimeException("Invalid target environment");
  }

  public static String getCategoriesRestEndpoint(CedarServer cedarServer) {
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/categories";
  }

  public static String getCategoryRestEndpoint(String categoryId, CedarServer cedarServer) throws UnsupportedEncodingException {
    categoryId = GeneralUtil.encodeIfNeeded(categoryId);
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/categories/" + categoryId;
  }

  public static String getRootCategoryRestEndpoint(CedarServer cedarServer) {
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/categories/root";
  }

  public static String getCategoryTreeEndpoint(CedarServer cedarServer) {
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/categories/tree";
  }

  public static String getAttachCategoryEndpoint(CedarServer cedarServer) {
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/command/attach-category";
  }

  public static String getAttachCategoriesEndpoint(CedarServer cedarServer) {
    String serverUrl = getResourceServerUrl(cedarServer);
    return serverUrl + "/command/attach-categories";
  }

  public static String getIntegratedSearchEndpoint(CedarServer cedarServer) {
    String serverUrl = getTerminologyServerUrl(cedarServer);
    return serverUrl + "/bioportal/integrated-search";
  }

  public static String getTemplatesEndpoint(String folderId, CedarServer cedarServer) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(cedarServer);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(cedarServer);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else { // Short id
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/templates?folder_id=" + folderId;
  }

  public static String getTemplateFieldsEndpoint(String folderId, CedarServer cedarServer) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(cedarServer);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(cedarServer);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else { // Short id
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/template-fields?folder_id=" + folderId;
  }

  public static String getTemplateFieldEndPoint(String fieldId, CedarServer cedarServer) throws UnsupportedEncodingException {
    String serverUrl = getResourceServerUrl(cedarServer);
    fieldId = GeneralUtil.encodeIfNeeded(fieldId);
    return serverUrl + "/template-fields/" + fieldId;
  }

  public static String getTemplateFieldReportEndPoint(String fieldId, CedarServer cedarServer) throws UnsupportedEncodingException {
    return getTemplateFieldEndPoint(fieldId, cedarServer) + "/report";
  }

  public static String getFolderContentsEndPoint(String folderId, CedarServer cedarServer) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(cedarServer);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(cedarServer);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else {
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/folders/" + folderId + "/contents";
  }

  public static String getSearchEndPoint(String q, List<CedarResourceType> resourceTypes, CedarServer cedarServer) {
    String url = getResourceServerUrl(cedarServer) + "/search?";
    if (q != null && q.length() > 0) {
      url += "q=" + q + "&";
    }
    if (resourceTypes != null && resourceTypes.size() > 0) {
      String types = resourceTypes.stream().map(CedarResourceType::getValue).collect(Collectors.joining(","));
      url += QP_RESOURCE_TYPES + "=" + types;
    }
    return url;
  }

  public static String getCdesInFolderExtractEndPoint(String cedarFolderId, List<String> fieldNames,
                                                      boolean includeCategoryIds, CedarServer cedarServer) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(cedarServer);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(cedarServer);
    String folderId;
    if (GeneralUtil.isURL(cedarFolderId)) {
      folderId = GeneralUtil.encodeIfNeeded(cedarFolderId);
    }
    else { // Short id
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + cedarFolderId);
    }
    String url = resourceServerUrl + "/folders/" + folderId + "/contents-extract?resource_types=field";
    List<String> updatedFieldNames = new ArrayList<>(fieldNames); // Create a copy because we may update it to include 'categories'
    if (includeCategoryIds) {
      updatedFieldNames.add("categories");
    }
    if (updatedFieldNames.size() > 0) {
      url = url + "&field_names=" + String.join(",", updatedFieldNames);
    }
    return url;
  }

}
