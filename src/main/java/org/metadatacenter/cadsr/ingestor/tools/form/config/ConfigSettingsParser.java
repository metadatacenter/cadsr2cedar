package org.metadatacenter.cadsr.ingestor.tools.form.config;

import org.apache.commons.cli.*;
import org.metadatacenter.cadsr.ingestor.util.CedarServerUtil;
import org.metadatacenter.cadsr.ingestor.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigSettingsParser {

  private static final Logger logger = LoggerFactory.getLogger(ConfigSettingsParser.class);

  public static ConfigSettings parse(String[] args) {

    ConfigSettings settings = new ConfigSettings();

    // Create command line parser
    CommandLineParser parser = new DefaultParser();

    // Create the Options
    Options options = new Options();
    options.addRequiredOption("f", "form", true, "[REQUIRED] Path of the .xml file with the caDSR Form to be imported into CEDAR");

    try {
      CommandLine cmd = parser.parse(options, args);

      if (cmd.hasOption("f")) {
        settings.setFormFilePath(cmd.getOptionValue("f"));
      }

    } catch (ParseException e) {
      logger.error("Error parsing command-line arguments: " + e.getMessage());
      logger.info("Please, follow the instructions below:");
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "mvn exec:java@cedar-cadsr-form-import -Dexec.args=\"[options]\"\nOptions:", options);
      System.exit(1);
    }

    return settings;

  }

}
