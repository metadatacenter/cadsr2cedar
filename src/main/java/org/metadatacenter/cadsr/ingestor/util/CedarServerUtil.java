package org.metadatacenter.cadsr.ingestor.util;

import org.metadatacenter.model.CedarResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.metadatacenter.cadsr.ingestor.util.Constants.*;
import static org.metadatacenter.constant.CedarQueryParameters.QP_RESOURCE_TYPES;

public class CedarServerUtil {

  private static final Logger logger = LoggerFactory.getLogger(CedarServerUtil.class);

  public static CedarEnvironment toCedarEnvironment(String targetEnvironment) {
    if (targetEnvironment.compareToIgnoreCase(CedarEnvironment.LOCAL.name()) == 0) {
      return CedarEnvironment.LOCAL;
    }
    else if (targetEnvironment.compareToIgnoreCase(CedarEnvironment.STAGING.name()) == 0) {
      return CedarEnvironment.STAGING;
    }
    else if (targetEnvironment.compareToIgnoreCase(CedarEnvironment.PRODUCTION.name()) == 0) {
      return CedarEnvironment.PRODUCTION;
    }
    else {
      throw new IllegalArgumentException("Invalid target environment: " +
          targetEnvironment + ". Allowed values: local, staging, production");
    }
  }

  public static String getResourceServerUrl(CedarEnvironment targetServer) {
    if (Constants.CedarEnvironment.LOCAL.equals(targetServer)) {
      return LOCAL_RESOURCE_SERVER_URL;
    } else if (Constants.CedarEnvironment.STAGING.equals(targetServer)) {
      return STAGING_RESOURCE_SERVER_URL;
    } else if (Constants.CedarEnvironment.PRODUCTION.equals(targetServer)) {
      return PRODUCTION_RESOURCE_SERVER_URL;
    }
    throw new RuntimeException("Invalid target environment");
  }

  public static String getRepoServerUrl(CedarEnvironment targetServer) {
    if (Constants.CedarEnvironment.LOCAL.equals(targetServer)) {
      return LOCAL_REPO_SERVER_URL;
    } else if (Constants.CedarEnvironment.STAGING.equals(targetServer)) {
      return STAGING_REPO_SERVER_URL;
    } else if (Constants.CedarEnvironment.PRODUCTION.equals(targetServer)) {
      return PRODUCTION_REPO_SERVER_URL;
    }
    throw new RuntimeException("Invalid target environment");
  }

  public static String getCategoriesRestEndpoint(CedarEnvironment targetEnvironment) {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/categories";
  }

  public static String getCategoryRestEndpoint(String categoryId, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    categoryId = GeneralUtil.encodeIfNeeded(categoryId);
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/categories/" + categoryId;
  }

  public static String getRootCategoryRestEndpoint(CedarEnvironment targetEnvironment) {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/categories/root";
  }

  public static String getCategoryTreeEndpoint(CedarEnvironment targetEnvironment) {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/categories/tree";
  }

  public static String getAttachCategoryEndpoint(CedarEnvironment targetEnvironment) {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/command/attach-category";
  }

  public static String getAttachCategoriesEndpoint(CedarEnvironment targetEnvironment) {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    return serverUrl + "/command/attach-categories";
  }

  public static String getTemplatesEndpoint(String folderId, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(targetEnvironment);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(targetEnvironment);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else { // Short id
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/templates?folder_id=" + folderId;
  }

  public static String getTemplateFieldsEndpoint(String folderId, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(targetEnvironment);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(targetEnvironment);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else { // Short id
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/template-fields?folder_id=" + folderId;
  }

  public static String getTemplateFieldEndPoint(String fieldId, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    String serverUrl = getResourceServerUrl(targetEnvironment);
    fieldId = GeneralUtil.encodeIfNeeded(fieldId);
    return serverUrl + "/template-fields/" + fieldId;
  }

  public static String getFolderContentsEndPoint(String folderId, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(targetEnvironment);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(targetEnvironment);
    if (GeneralUtil.isURL(folderId)) {
      folderId = GeneralUtil.encodeIfNeeded(folderId);
    }
    else {
      folderId = GeneralUtil.encodeIfNeeded(repoServerUrl + "/folders/" + folderId);
    }
    return resourceServerUrl + "/folders/" + folderId + "/contents";
  }

//  public static String getSearchEndPoint(String q, List<CedarResourceType> resourceTypes, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
//    String url = getResourceServerUrl(targetEnvironment) + "?";
//    if (q != null && q.length() > 0) {
//      url += "q=" + q;
//    }
//    if (resourceTypes != null && resourceTypes.size() > 0) {
//
//
//      url += QP_RESOURCE_TYPES + "="
//    }
//    return serverUrl + "/search?q" + fieldId;
//  }

  public static String getCdesInFolderExtractEndPoint(String cedarFolderId, List<String> fieldNames,
                                                      boolean includeCategoryIds, CedarEnvironment targetEnvironment) throws UnsupportedEncodingException {
    String resourceServerUrl = CedarServerUtil.getResourceServerUrl(targetEnvironment);
    String repoServerUrl = CedarServerUtil.getRepoServerUrl(targetEnvironment);
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
