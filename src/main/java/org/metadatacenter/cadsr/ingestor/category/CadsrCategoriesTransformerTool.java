package org.metadatacenter.cadsr.ingestor.category;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Stopwatch;
import org.apache.http.util.ExceptionUtils;
import org.metadatacenter.cadsr.category.schema.Classifications;
import org.metadatacenter.cadsr.ingestor.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * This class reads a caDSR XML file with a <Classifications> root element and generates a JSON file with a tree of
 * categories.
 */
public class CadsrCategoriesTransformerTool {

  private static final Logger logger = LoggerFactory.getLogger(CadsrCategoriesTransformerTool.class);

  public static void main(String[] args) {

    String inputSourceLocation = args[0];
    String outputTargetLocation = args[1];

    final Stopwatch stopwatch = Stopwatch.createStarted();

    int totalCategories = 0;
    boolean success = false;
    try {
      File inputSource = new File(inputSourceLocation);
      File outputDir = Util.checkDirectoryExists(outputTargetLocation);
      if (inputSource.isDirectory()) {
        totalCategories = convertCdeCategoriesFromDirectory(inputSource, outputDir);
      } else {
        totalCategories = convertCdeCategoriesFromFile(inputSource, outputDir);
      }
      success = true;
    } catch (Exception e) {
      logger.error(Util.getStackTrace(e));
      success = false;
    } finally {
      printSummary(stopwatch, totalCategories, success);
    }
  }

  private static int convertCdeCategoriesFromDirectory(File inputDir, File outputDir) throws IOException {
    int totalCategories = 0;
    for (final File inputFile : inputDir.listFiles()) {
      totalCategories += convertCdeCategoriesFromFile(inputFile, outputDir);
    }
    return totalCategories;
  }

  public static int convertCdeCategoriesFromFile(File inputFile, File outputDir) throws IOException {
    logger.info("Processing input file at " + inputFile.getAbsolutePath());
    File outputSubDir = Util.createDirectoryBasedOnInputFileName(inputFile, outputDir);
    List<CategoryTreeNode> categoryTree = null;

    try {
      Classifications classifications = CadsrCategoriesUtils.getClassifications(new FileInputStream(inputFile));
      categoryTree = CadsrCategoriesUtils.classificationsToCategoryTree(classifications);
      logger.info("Generating categories file...");
      String categoriesFileName = inputFile.getName().substring(0,inputFile.getName().lastIndexOf('.')) + ".json";
      new ObjectMapper().writeValue(new File(outputSubDir, categoriesFileName), categoryTree);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    // FIX
    return categoryTree.size();
  }

  private static void printSummary(Stopwatch stopwatch, int totalCdes, boolean success) {
    final DecimalFormat countFormat = new DecimalFormat("#,###,###,###");
    logger.info("----------------------------------------------------------");
    if (success) {
      logger.info("TRANSFORM-CATEGORIES SUCCESS");
    } else {
      logger.info("TRANSFORM-CATEGORIES FAILED (see error.log for details)");
    }
    logger.info("----------------------------------------------------------");
    logger.info("Total number of generated categories: " + countFormat.format(totalCdes));
    long elapsedTimeInSeconds = stopwatch.elapsed(TimeUnit.SECONDS);
    long hours = elapsedTimeInSeconds / 3600;
    long minutes = (elapsedTimeInSeconds % 3600) / 60;
    long seconds = (elapsedTimeInSeconds % 60);
    logger.info("Total time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    logger.info("Finished at: " + LocalDateTime.now());
    logger.info("----------------------------------------------------------");
  }

}

