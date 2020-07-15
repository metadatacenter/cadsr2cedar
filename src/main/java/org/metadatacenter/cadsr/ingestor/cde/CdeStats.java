package org.metadatacenter.cadsr.ingestor.cde;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is a singleton that stores execution statistics that are used to generate a report for the user
 */
public class CdeStats {

  public int numberOfInputCdes;
  public int numberOfExistingCdes;
  public int numberOfCdesProcessedOk;
  public int numberOfCdesToBeCreated;
  public int numberOfCdesCreated;
  public int numberOfCdesToBeUpdatedOrDeleted;
  public int numberOfCdesUpdatedOrDeleted;
  public int numberOfCdesChangedButVersionNotUpdated;
  public int numberOfMissingCdeToCategoryRelations;
  public int numberOfCdeToCategoryRelationsCreatedWhenCreatingCdes;
  public int numberOfCdeToCategoryRelationsCreatedAfterReview;
  public List<String> skippedIds;
  public List<String> failedIds;
  private Map<String, Integer> skippedReasons; // stores the reason and the count of CDEs skipped for that reason
  private Map<String, Integer> failedReasons;

  private static CdeStats singleInstance = null;

  private CdeStats() {
    numberOfInputCdes = 0;
    numberOfExistingCdes = 0;
    numberOfCdesProcessedOk = 0;
    numberOfCdesToBeCreated = 0;
    numberOfCdesCreated = 0;
    numberOfCdesToBeUpdatedOrDeleted = 0;
    numberOfCdesChangedButVersionNotUpdated = 0;
    numberOfCdesUpdatedOrDeleted = 0;
    skippedIds = new ArrayList<>();
    failedIds = new ArrayList<>();
    numberOfMissingCdeToCategoryRelations = 0;
    numberOfCdeToCategoryRelationsCreatedWhenCreatingCdes = 0;
    numberOfCdeToCategoryRelationsCreatedAfterReview = 0;
    skippedReasons = new HashMap<>();
    failedReasons = new HashMap<>();
  }

  public static CdeStats getInstance() {
    if (singleInstance == null) {
      singleInstance = new CdeStats();
    }
    return singleInstance;
  }

  public void addSkipped(String cdeUniqueId, String reason) {
    if (!skippedIds.contains(cdeUniqueId)) {
      skippedIds.add(cdeUniqueId);
      if (skippedReasons.containsKey(reason)) {
        skippedReasons.replace(reason, skippedReasons.get(reason) + 1);
      }
      else {
        skippedReasons.put(reason, 1);
      }
    }
  }

  public void addFailed(String cdeUniqueId, String reason) {
    if (!failedIds.contains(cdeUniqueId)) {
      failedIds.add(cdeUniqueId);
      if (failedReasons.containsKey(reason)) {
        failedReasons.replace(reason, failedReasons.get(reason) + 1);
      }
      else {
        failedReasons.put(reason, 1);
      }
    }
  }

  public Map<String, Integer> getSkippedReasons() {
    return skippedReasons;
  }

  public Map<String, Integer> getFailedReasons() {
    return failedReasons;
  }
}
