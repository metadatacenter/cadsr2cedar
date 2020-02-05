package org.metadatacenter.cadsr.ingestor.cde;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is a singleton that stores execution statistics that are used to generate a report for the user
 */
public class CadsrTransformationStats {

  public int numberOfInputCdes;
  public int numberOfCdesProcessedOk;
  public int numberOfCdesSkipped;
  public int numberOfCdesFailed;
  private Map<String, Integer> skippedReasons; // stores the reason and the count of CDEs skipped for that reason
  private Map<String, Integer> failedReasons;

  private static CadsrTransformationStats singleInstance = null;

  private CadsrTransformationStats() {
    numberOfInputCdes = 0;
    numberOfCdesProcessedOk = 0;
    numberOfCdesSkipped = 0;
    numberOfCdesFailed = 0;
    skippedReasons = new HashMap<>();
    failedReasons = new HashMap<>();
  }

  public static CadsrTransformationStats getInstance() {
    if (singleInstance == null) {
      singleInstance = new CadsrTransformationStats();
    }
    return singleInstance;
  }

  public void addSkipped(String reason) {
    if (skippedReasons.containsKey(reason)) {
      skippedReasons.replace(reason, skippedReasons.get(reason) + 1);
    }
    else {
      skippedReasons.put(reason, 1);
    }
    numberOfCdesSkipped++;
  }

  public void addFailed(String reason) {
    if (failedReasons.containsKey(reason)) {
      failedReasons.replace(reason, failedReasons.get(reason) + 1);
    }
    else {
      failedReasons.put(reason, 1);
    }
    numberOfCdesFailed++;
  }

  public Map<String, Integer> getSkippedReasons() {
    return skippedReasons;
  }

  public Map<String, Integer> getFailedReasons() {
    return failedReasons;
  }
}
