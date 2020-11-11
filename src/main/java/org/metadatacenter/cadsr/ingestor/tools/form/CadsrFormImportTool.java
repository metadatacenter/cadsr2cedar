package org.metadatacenter.cadsr.ingestor.tools.form;

import com.google.common.base.Stopwatch;
import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.ingestor.form.FormParser;
import org.metadatacenter.cadsr.ingestor.form.FormUtil;
import org.metadatacenter.cadsr.ingestor.tools.form.config.ConfigSettings;
import org.metadatacenter.cadsr.ingestor.tools.form.config.ConfigSettingsParser;
import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.GeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CadsrFormImportTool {

  private static final Logger logger = LoggerFactory.getLogger(CadsrFormImportTool.class);

  public static void main(String[] args) {

    final Stopwatch stopwatch = Stopwatch.createStarted();
    final LocalDateTime startTime = LocalDateTime.now();
    ConfigSettings settings = ConfigSettingsParser.parse(args);

    try {
      logger.info("#####################################################################");
      logger.info("#              CEDAR - caDSR Form Ingestion Tool                    #");
      logger.info("#####################################################################");
      logger.info("# Execution started at " + startTime);
      logger.info("# Execution settings:");
      logger.info("#   - Input form: " + settings.getFormFilePath());
      logger.info("#####################################################################\n");

      logger.info("Reading input form: " + settings.getFormFilePath());

      Form form = FormUtil.getForm(new FileInputStream(settings.getFormFilePath()));

      Map templateMap = FormUtil.getTemplateMapFromForm(form);

      System.out.println(GeneralUtil.convertMapToJson(templateMap));

      CedarServices.createTemplate(templateMap, )


      printSummary(stopwatch, startTime);

    } catch (IOException | JAXBException e) {
      logger.error("Error: " + e.getMessage());
      e.printStackTrace();
    } finally {

    }
  }

  private static void printSummary(Stopwatch stopwatch, LocalDateTime startTime) {
    long elapsedTimeInSeconds = stopwatch.elapsed(TimeUnit.SECONDS);
    long hours = elapsedTimeInSeconds / 3600;
    long minutes = (elapsedTimeInSeconds % 3600) / 60;
    long seconds = (elapsedTimeInSeconds % 60);
    logger.info("#####################################################################");
    logger.info("#                        EXECUTION SUMMARY                          #");
    logger.info("#####################################################################");
    logger.info("#  - Total time: " + String.format("%02d:%02d:%02d", hours, minutes, seconds));
    logger.info("#  - Started at: " + startTime);
    logger.info("#  - Finished at: " + LocalDateTime.now());
    logger.info("#####################################################################");
  }

}
