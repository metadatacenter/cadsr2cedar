package org.metadatacenter.cadsr.ingestor.tools.cde.config;

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
    options.addRequiredOption("k", "apikey", true, "[REQUIRED] API key of CEDAR's caDSR Admin user");
    options.addRequiredOption( "f", "folder", true, "[REQUIRED] Identifier of the CEDAR folder where the CDEs will be stored");
    options.addRequiredOption( "s", "server", true, "[REQUIRED] Target CEDAR server. Possible values: local, staging, production");
    options.addOption( "x", "cadsr-exec-folder", true, "Path to a local folder with temporal files used during execution. The folder will be removed after execution");
    options.addOption( "t", "update-categories", false, "Update CEDAR categories");
    options.addOption( "c", "update-cdes", false, "Update CEDAR CDEs and attach them to the corresponding CEDAR categories");
    options.addOption( "d", "delete-categories", false, "Delete existing CEDAR caDSR categories (excluding its root)");
    options.addOption( "o", "ontology-folder", true, "Path to the folder the CADSR-VS ontology will be saved in");

    options.addOption( "h", "ftp-host", true, "caDSR FTP host");
    options.addOption( "u", "ftp-user", true, "caDSR FTP user name");
    options.addOption( "p", "ftp-password", true, "caDSR FTP password");

    // Either a categories XML file or FTP configuration settings are required
    OptionGroup categoriesGroup = new OptionGroup();
    categoriesGroup.addOption(new Option( "g", "ftp-categories-folder", true, "caDSR FTP categories working directory"));
    categoriesGroup.addOption(new Option("G", "categories-file", true, "caDSR XML Categories .zip file path"));
    categoriesGroup.setRequired(false); // Only required when updating categories
    options.addOptionGroup(categoriesGroup);

    // Either a CDEs XML file or FTP configuration settings are required
    OptionGroup cdesGroup = new OptionGroup();
    cdesGroup.addOption(new Option( "e", "ftp-cdes-folder", true, "caDSR FTP CDEs working directory"));
    cdesGroup.addOption(new Option( "E", "cdes-file", true, "caDSR XML CDEs .zip file path"));
    cdesGroup.setRequired(false); // Only required when updating CDEs
    options.addOptionGroup(cdesGroup);

    try {
      CommandLine cmd = parser.parse(options, args);

      if (cmd.hasOption("x")) {
        settings.setExecutionFolder(cmd.getOptionValue("x"));
      } else { // Use default execution folder
        settings.setExecutionFolder(Constants.EXECUTION_FOLDER);
      }

      if (cmd.hasOption("t")) {
        settings.setUpdateCategories(true);
        if (!cmd.hasOption("g") && (!cmd.hasOption("G"))) {
          throw new ParseException("Missing required option when updating categories: [-g, -G]");
        }
      } else {
        settings.setUpdateCategories(false);
      }

      if (cmd.hasOption("c")) {
        settings.setUpdateCdes(true);
        if (!cmd.hasOption("e") && (!cmd.hasOption("E"))) {
          throw new ParseException("Missing required option when updating CDEs: [-e, -E]");
        }
      } else {
        settings.setUpdateCdes(false);
      }

      if (cmd.hasOption("d")) {
        settings.setDeleteCategories(true);
      } else {
        settings.setDeleteCategories(false);
      }

      if (cmd.hasOption("k")) {
        settings.setCadsrAdminApikey(cmd.getOptionValue("k"));
      }

      if (cmd.hasOption("f")) {
        settings.setCedarCdeFolderId(cmd.getOptionValue("f"));
      }

      if (cmd.hasOption("s")) {
        String cedarEnvironmentStr = cmd.getOptionValue("s");
        try {
          settings.setCedarServer(CedarServerUtil.toCedarServerFromServerName(cedarEnvironmentStr));
        } catch(IllegalArgumentException e) {
          throw new ParseException(e.getMessage());
        }
      }

      // Check that the full FTP configuration has been provided
      if (cmd.hasOption("g") || (cmd.hasOption("e"))) {
        if (cmd.hasOption("g")) {
          settings.setFtpCategoriesFolder(cmd.getOptionValue("g"));
        }
        if (cmd.hasOption("e")) {
          settings.setFtpCdesFolder(cmd.getOptionValue("e"));
        }
        if (!cmd.hasOption("h") || !cmd.hasOption("u") || !cmd.hasOption("p")) {
          throw new ParseException("Missing any of the required options for FTP download: host (-h), user name (-u), password (-p)");
        }
        else {
          settings.setFtpHost(cmd.getOptionValue("h"));
          settings.setFtpUser(cmd.getOptionValue("u"));
          settings.setFtpPassword(cmd.getOptionValue("p"));
        }
      }

      if (cmd.hasOption("G")) {
        settings.setCategoriesFilePath(cmd.getOptionValue("G"));
      }

      if (cmd.hasOption("E")) {
        settings.setCdesFilePath(cmd.getOptionValue("E"));
      }

      if (cmd.hasOption("o")) {
        settings.setOntologyOutputFolderPath(cmd.getOptionValue("o"));
      }
      else {
        settings.setOntologyOutputFolderPath(Constants.ONTOLOGY_FOLDER);
      }


    } catch (ParseException e) {
      logger.error("Error parsing command-line arguments: " + e.getMessage());
//      logger.error("Arguments entered: ");
//      for (String argGroup : GeneralUtil.commandLineArgumentsGrouped(args, "-p", "--ftp-password", "-k", "apikey")) {
//        logger.info("  " + argGroup);
//      }
      logger.info("Please, follow the instructions below:");
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "mvn exec:java@cedar-cadsr-updater -Dexec.args=\"[options]\"\nOptions:", options);
      System.exit(1);
    }

    return settings;

  }

}
