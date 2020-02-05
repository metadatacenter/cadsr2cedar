package org.metadatacenter.cadsr.ingestor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class JsonUtils {

  public static String extractJsonFieldValue(String json, String fieldName) throws IOException {
    JsonNode node = new ObjectMapper().readTree(json);
    if (node.has(fieldName)) {
      return node.get(fieldName).asText();
    } else {
      throw new RuntimeException("Json field not found in object: " + fieldName);
    }
  }

}
