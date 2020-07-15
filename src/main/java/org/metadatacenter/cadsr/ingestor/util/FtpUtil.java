package org.metadatacenter.cadsr.ingestor.util;

import org.apache.commons.io.output.CountingOutputStream;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FtpUtil {

  private static final Logger logger = LoggerFactory.getLogger(FtpUtil.class);

  public static File downloadMostRecentFile(String server, String user, String password,
                                            String workingDirectory, String destinationFolder) throws IOException {


    FTPClient ftpClient = new FTPClient();
    try {

      ftpClient.connect(server);
      ftpClient.login(user, password);
      ftpClient.enterLocalPassiveMode();
      ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

      ftpClient.changeWorkingDirectory(workingDirectory);

      FTPFile[] files = ftpClient.listFiles();

      if (files.length > 0) {
        List<FTPFile> filesList = Arrays.asList(files);
        filesList.sort(Comparator.comparing(FTPFile::getTimestamp).reversed());
        FTPFile mostRecentFile = filesList.get(0);

        // Create the path to the destination file if it does not exist
        File directory = new File(destinationFolder);
        if (!directory.exists()){
          directory.mkdirs();
        }

        File destinationFile = new File(destinationFolder + "/" + mostRecentFile.getName());
        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile));
        logger.info("Downloading file: " + mostRecentFile.getName() + " (size: " + mostRecentFile.getSize() / 1024 + " KB)");
        logger.info("Destination path: " + destinationFile.getAbsolutePath());
        CountingOutputStream cos = new CountingOutputStream(outputStream){
          double lastProgressShown = -1;
          protected void beforeWrite(int n){
            super.beforeWrite(n);
            double progressPercentage = Math.round(((double) getCount() * 100) / (double) mostRecentFile.getSize());
            if ( progressPercentage != lastProgressShown && progressPercentage % 10 == 0) {
              logger.info("Download progress: " + (int) progressPercentage + "%");
              lastProgressShown = progressPercentage;
            }
          }
        };
        boolean success = ftpClient.retrieveFile(mostRecentFile.getName(), cos);
        outputStream.close();

        if (success) {
          logger.info("The file has been downloaded successfully");
          return destinationFile;
        }
        else {
          throw new InternalError("Error downloading file: " + destinationFile.getAbsolutePath());
        }
      } else {
        throw new InternalError("The FTP folder is empty: " + workingDirectory);
      }
    } finally {
      try {
        if (ftpClient.isConnected()) {
          ftpClient.logout();
          ftpClient.disconnect();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }
    }
  }
}
