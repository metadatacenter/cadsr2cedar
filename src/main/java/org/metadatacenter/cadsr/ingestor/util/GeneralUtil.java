package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Files;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GeneralUtil {

  private static final Logger logger = LoggerFactory.getLogger(CdeUtil.class);

  public static String getSha1(String input) {
    MessageDigest mDigest = null;
    try {
      mDigest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      e.printStackTrace();
    }
    byte[] result = mDigest.digest(input.getBytes());
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < result.length; i++) {
      sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
    }
    return sb.toString();
  }

  public static String getValueOrNull(String input) {
    if (input != null & input.trim().length() > 0) {
      return input;
    } else {
      return null;
    }
  }

  public static File checkDirectoryExists(String directory) {
    File outputDir = new File(directory);
    if (!outputDir.exists()) {
      outputDir.mkdir();
    }
    return outputDir;
  }

  public static File createDirectoryBasedOnInputFileName(File sourceFile, File outputDir) {
    String outputLocation = outputDir.getAbsolutePath() + "/" + Files.getNameWithoutExtension(sourceFile.getName());
    return checkDirectoryExists(outputLocation);
  }

  public static boolean multiplesOfAHundred(int counter) {
    return counter != 0 && counter % 100 == 0;
  }

  public static InputStream processInvalidXMLCharacters(InputStream in) throws IOException {
    StringBuffer out = new StringBuffer(); // Used to hold the output.
    Reader r = new InputStreamReader(in, Constants.CHARSET);
    int intChar;
    while ((intChar = r.read()) != -1) {
      char inputChar = (char) intChar;
      if (isValidXMLCharacter(inputChar)) {
        out.append(inputChar);
      }
      else {
        out.append(" ");
      }
    }
    return new ByteArrayInputStream(out.toString().getBytes());
  }

  /**
   * Check if the XML character is valid as specified by the XML 1.0 standard. For reference, please see
   * <a href="http://www.w3.org/TR/2000/REC-xml-20001006#NT-Char">the standard</a>.
   */
  private static boolean isValidXMLCharacter(char character) {
    if ((character == 0x9) ||
        (character == 0xA) ||
        (character == 0xD) ||
        ((character >= 0x20) && (character <= 0xD7FF)) ||
        ((character >= 0xE000) && (character <= 0xFFFD)) ||
        ((character >= 0x10000) && (character <= 0x10FFFF))) {
      return true;
    } else {
      return false;
    }
  }

  public static String getStackTrace(Exception e) {
    StringWriter writer = new StringWriter();
    PrintWriter printWriter = new PrintWriter(writer);
    e.printStackTrace(printWriter);
    return writer.toString();
  }

  public static String encodeIfNeeded(String uri) throws UnsupportedEncodingException {
    String decodedUri = URLDecoder.decode(uri, "UTF-8");
    // It is necessary to encode it
    if (uri.compareTo(decodedUri) == 0) {
      return URLEncoder.encode(uri, "UTF-8");
    }
    // If was already encoded
    else {
      return uri;
    }
  }

  public static void logErrorMessage(final HttpURLConnection conn) {
    String response = ConnectionUtil.readResponseMessage(conn.getErrorStream());
    logger.error(response);
    throw new RuntimeException(response);
  }

  public static void logResponseMessage(final HttpURLConnection conn) throws IOException {
    String response = ConnectionUtil.readResponseMessage(conn.getInputStream());
    logger.debug(response);
  }

  public static String calculatePercentage(int amount, int total) {
    final DecimalFormat percentFormat = new DecimalFormat("###.##%");
    return percentFormat.format(((float) amount / (float) total));
  }

  public static List<String> commandLineArgumentsGrouped(String[] args, String passwordShortOption, String passwordLongOption, String apikeyShortOption, String apikeyLongOption) {
    List<String> argGroups = new ArrayList<>();
    String group = "";
    for (int i=0; i<args.length; i++) {
      String arg = args[i];
      if (arg.equals(passwordShortOption) || arg.equals(passwordLongOption) || arg.equals(apikeyShortOption) || arg.equals(apikeyLongOption)) {
        group = group.concat(arg + " ********"); // hide password or apikey
        i++;
      }
      else {
        if (arg.startsWith("-")) {
          if (group.length() > 0) {
            argGroups.add(group);
            group = "";
          }
          group = group.concat(arg);
        }
        else {
          group = group.concat(" " + arg);
          argGroups.add(group);
          group = "";
        }
      }
    }
    return argGroups;
  }

  public static boolean isURL(String url) {
    try {
      new URL(url);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public static String convertMapToJson(Map map) {
    ObjectMapper objectMapper = new ObjectMapper();
    String json = null;
    try {
      json = objectMapper.writeValueAsString(map);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return json;
  }

}
