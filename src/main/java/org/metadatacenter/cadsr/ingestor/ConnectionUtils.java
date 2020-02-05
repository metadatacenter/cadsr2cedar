package org.metadatacenter.cadsr.ingestor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ConnectionUtils {

  private static final Logger logger = LoggerFactory.getLogger(ConnectionUtils.class);

  public static String readResponseMessage(InputStream is) {
    StringBuffer sb = new StringBuffer();
    try (BufferedReader br = new BufferedReader(new InputStreamReader(is))) {
      String messageLine;
      while ((messageLine = br.readLine()) != null) {
        sb.append(messageLine);
      }
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    return sb.toString();
  }

}
