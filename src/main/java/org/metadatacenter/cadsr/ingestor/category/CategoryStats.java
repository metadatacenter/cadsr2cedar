package org.metadatacenter.cadsr.ingestor.category;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a singleton that stores execution statistics that are used to generate a report for the user
 */
public class CategoryStats {

  public int numberOfInputCategories;
  public int numberOfExistingCategories;
  public int numberOfCategoriesToBeCreated;
  public int numberOfCategoriesCreated;
  public int numberOfCategoriesToBeUpdated;
  public int numberOfCategoriesUpdated;
  public int numberOfCategoriesToBeDeleted;
  public int numberOfCategoriesDeleted;
  public Set<String> idsOfCategoriesNotFoundInSource;

  private static CategoryStats singleInstance = null;

  private CategoryStats() {
    numberOfInputCategories = 0;
    numberOfExistingCategories = 0;
    numberOfCategoriesToBeCreated = 0;
    numberOfCategoriesCreated = 0;
    numberOfCategoriesToBeUpdated = 0;
    numberOfCategoriesUpdated = 0;
    numberOfCategoriesToBeDeleted = 0;
    numberOfCategoriesDeleted = 0;
    idsOfCategoriesNotFoundInSource = new HashSet<>();
  }

  public static void resetStats() {
    singleInstance = new CategoryStats();
  }

  public static CategoryStats getInstance() {
    if (singleInstance == null) {
      singleInstance = new CategoryStats();
    }
    return singleInstance;
  }
}
