package org.metadatacenter.cadsr.ingestor.tools.form;

import com.google.common.base.Stopwatch;
import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.ingestor.tools.form.config.ConfigSettings;
import org.metadatacenter.cadsr.ingestor.tools.form.config.ConfigSettingsParser;
import org.metadatacenter.cadsr.ingestor.util.FormUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

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

      System.out.println(form.getCreatedBy());

    } catch (IOException | JAXBException e) {
      logger.error("Error: " + e.getMessage());
      e.printStackTrace();
    } finally {

    }
  }

}
