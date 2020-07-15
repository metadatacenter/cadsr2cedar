package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtil {

  private static ObjectMapper objectMapper = new ObjectMapper();

  public static String extractJsonFieldValueAsText(String json, String fieldName) throws IOException {
    JsonNode node = objectMapper.readTree(json);
    if (node.has(fieldName)) {
      return node.get(fieldName).asText();
    } else {
      throw new RuntimeException("Json field not found in object: " + fieldName);
    }
  }

  public static Map<String, Object> readJsonAsMap(String json) throws IOException {
    TypeReference<HashMap<String, Object>> typeRef
        = new TypeReference<HashMap<String, Object>>() {};
    return objectMapper.readValue(json, typeRef);
  }

  public static JsonNode extractJsonFieldValueAsNode(String json, String fieldName) throws IOException {
    JsonNode node = objectMapper.readTree(json);
    if (node.has(fieldName)) {
      return node.get(fieldName);
    } else {
      throw new RuntimeException("Json field not found in object: " + fieldName);
    }
  }

  public static int extractJsonFieldValueAsInt(String json, String fieldName) throws IOException {
    JsonNode node = objectMapper.readTree(json);
    if (node.has(fieldName)) {
      return node.get(fieldName).asInt();
    } else {
      throw new RuntimeException("Json field not found in object: " + fieldName);
    }
  }

  public static List<JsonNode> extractJsonFieldAsList(String json, String fieldName) throws IOException {
    JsonNode node = objectMapper.readTree(json);
    List items = new ArrayList();
    if (node.has(fieldName) && node.get(fieldName).isArray()) {
      for (final JsonNode itemNode : node.get(fieldName)) {
        items.add(itemNode);
      }
    } else {
      throw new RuntimeException("Json field not found or is not an array: " + fieldName);
    }
    return items;
  }

}
