package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import org.metadatacenter.constant.CedarConstants;
import org.metadatacenter.model.ModelNodeNames;
import org.metadatacenter.model.ModelNodeValues;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.*;

public class CedarFieldUtil {

  private static final Logger logger = LoggerFactory.getLogger(CedarFieldUtil.class);

  public static Map<String, Object> generateDefaultSectionBreak(String sectionName, String description, Constants.CedarServer cedarServer) {
    Map<String, Object> defaultSectionBreak = new HashMap<>();
    Instant now = Instant.now();
    defaultSectionBreak.put(ModelNodeNames.JSON_SCHEMA_SCHEMA, ModelNodeValues.JSON_SCHEMA_IRI);
    defaultSectionBreak.put(ModelNodeNames.JSON_LD_ID, generateFieldId(cedarServer));
    defaultSectionBreak.put(ModelNodeNames.JSON_LD_TYPE, "https://schema.metadatacenter.org/core/StaticTemplateField");
    defaultSectionBreak.put(ModelNodeNames.JSON_LD_CONTEXT, generateSectionBreakDefaultContext());
    defaultSectionBreak.put(ModelNodeNames.JSON_SCHEMA_TYPE, ModelNodeValues.OBJECT);
    defaultSectionBreak.put(ModelNodeNames.JSON_SCHEMA_TITLE, asJsonSchemaTitle(sectionName));
    defaultSectionBreak.put(ModelNodeNames.JSON_SCHEMA_DESCRIPTION, asJsonSchemaDescription(sectionName));
    defaultSectionBreak.put(ModelNodeNames.UI, generateSectionBreakDefaultUi());
    defaultSectionBreak.put(ModelNodeNames.SCHEMA_ORG_NAME, sectionName);
    defaultSectionBreak.put(ModelNodeNames.SCHEMA_ORG_DESCRIPTION, description);
    defaultSectionBreak.put(ModelNodeNames.PAV_CREATED_ON, null); // Will be automatically generated when posting it
    defaultSectionBreak.put(ModelNodeNames.PAV_CREATED_BY, null); // Will be automatically generated when posting it
    defaultSectionBreak.put(ModelNodeNames.PAV_LAST_UPDATED_ON, null); // Will be automatically generated when posting it
    defaultSectionBreak.put(ModelNodeNames.OSLC_MODIFIED_BY, null); // Will be automatically generated when posting it
    defaultSectionBreak.put(ModelNodeNames.SCHEMA_ORG_SCHEMA_VERSION, Constants.CEDAR_SCHEMA_VERSION);
    defaultSectionBreak.put(ModelNodeNames.JSON_SCHEMA_ADDITIONAL_PROPERTIES, ModelNodeValues.FALSE);
    return defaultSectionBreak;
  }

  private static Map<String, String> generateSectionBreakDefaultContext() {
    Map<String, String> context = Maps.newHashMap();
    context.put(ModelNodeNames.SCHEMA, ModelNodeValues.SCHEMA_IRI);
    context.put(ModelNodeNames.PAV, ModelNodeValues.PAV_IRI);
    context.put(ModelNodeNames.BIBO, ModelNodeValues.BIBO_IRI);
    context.put(ModelNodeNames.OSLC, ModelNodeValues.OSLC_IRI);
    return context;
  }

  private static Map<String, String> generateSectionBreakDefaultUi() {
    Map<String, String> context = Maps.newHashMap();
    context.put(ModelNodeNames.UI_FIELD_INPUT_TYPE, ModelNodeNames.FIELD_INPUT_TYPE_SECTION_BREAK);
    context.put(ModelNodeNames.UI_CONTENT, null);
    return context;
  }

  public static String generateFieldId(Constants.CedarServer cedarServer) {
    return CedarServerUtil.getRepoServerUrl(cedarServer) + "/template-fields/" + UUID.randomUUID();
  }

  public static Object asJsonSchemaTitle(String content) {
    return String.format("'%s' field schema", content);
  }

  public static Object asJsonSchemaDescription(String content) {
    return String.format("'%s' field schema generated by the CEDAR Template Editor", content);
  }



}