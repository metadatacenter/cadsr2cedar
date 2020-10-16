package org.metadatacenter.cadsr.ingestor.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.metadatacenter.cadsr.category.schema.CSI;
import org.metadatacenter.cadsr.category.schema.ClassificationScheme;
import org.metadatacenter.cadsr.category.schema.Classifications;
import org.metadatacenter.cadsr.category.schema.Context;
import org.metadatacenter.cadsr.cde.schema.CLASSIFICATIONSLIST;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.category.*;
import org.metadatacenter.cadsr.ingestor.cde.CdeStats;
import org.metadatacenter.cadsr.ingestor.cde.CdeSummary;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.util.*;

import static org.metadatacenter.cadsr.ingestor.util.Constants.CADSR_CATEGORY_SCHEMA_ORG_ID;
import static org.metadatacenter.cadsr.ingestor.util.Constants.CDE_CATEGORY_IDS_FIELD;
import static org.metadatacenter.model.ModelNodeNames.JSON_LD_ID;

public class CategoryUtil {

  private static final Logger logger = LoggerFactory.getLogger(CategoryUtil.class);

  private static ObjectMapper objectMapper = new ObjectMapper();

  /**
   * @param inputFile
   * @param outputDir
   * @return Category Tree file
   * @throws IOException
   */
  public static File convertCdeCategoriesFromFile(File inputFile, File outputDir) throws IOException {
    logger.info("Processing input file at " + inputFile.getAbsolutePath());
    List<CategoryTreeNode> categoryTree;
    File categoryTreeFile = null;
    try {
      Classifications classifications = CategoryUtil.getClassifications(new FileInputStream(inputFile));
      categoryTree = CategoryUtil.classificationsToCdeCategoryTree(classifications);
      logger.info("Generating categories file...");
      String categoriesFileName = inputFile.getName().substring(0, inputFile.getName().lastIndexOf('.')) + ".json";
      categoryTreeFile = new File(outputDir, categoriesFileName);
      new ObjectMapper().writeValue(categoryTreeFile, categoryTree);
    } catch (JAXBException e) {
      e.printStackTrace();
    }
    return categoryTreeFile;
  }

  public static void convertCdeCategoriesFromDirectory(File inputDir, File outputDir) throws IOException {
    for (final File inputFile : inputDir.listFiles()) {
      convertCdeCategoriesFromFile(inputFile, outputDir);
    }
  }

  public static void uploadCategoriesFromDirectory(File inputDir, String cedarRootCategoryId,
                                                   CedarEnvironment environment,
                                                   String apiKey) throws IOException {
    for (final File inputFile : inputDir.listFiles()) {
      uploadCategoriesFromFile(inputFile, cedarRootCategoryId, environment, apiKey);
    }
  }

  public static void uploadCategoriesFromFile(File inputFile, String cedarRootCategoryId, CedarEnvironment environment,
                                              String apiKey) throws IOException {

    List<CategoryTreeNode> categoryTreeNodes = readCategoriesFromFile(inputFile);
    for (CategoryTreeNode categoryTreeNode : categoryTreeNodes) {
      CedarServices.createCategory(categoryTreeNode, cedarRootCategoryId, environment, apiKey);
    }
  }

  private static List<CategoryTreeNode> readCategoriesFromFile(File inputFile) throws IOException {
    logger.info("Processing input file at " + inputFile.getAbsolutePath());
    List<CategoryTreeNode> categories =
        objectMapper.readValue(inputFile, objectMapper.getTypeFactory().constructCollectionType(List.class,
            CategoryTreeNode.class));
    return categories;
  }

  public static Classifications getClassifications(InputStream is) throws JAXBException,
      IOException {
    // Note that CLASSIFICATIONSLISTCATEGORIES is a class name that we created to diferentiate the categories extracted
    // from the categories file that the NCI sent us from the CLASSIFICATIONSLIST elements extracted from the original
    // caDSR CDEs XML file
    JAXBContext jaxbContext = JAXBContext.newInstance(Classifications.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    InputStream cleanIs = GeneralUtil.processInvalidXMLCharacters(is);
    return (Classifications) jaxbUnmarshaller.unmarshal(new InputStreamReader(cleanIs, Constants.CHARSET));
  }

  public static List<CategoryTreeNode> classificationsToCdeCategoryTree(Classifications classifications) {
    List<Category> categories = classificationsToCategoriesList(classifications);
    List<CategoryTreeNode> categoryTreeNodes = cdeCategoriesListToTree(categories);
    return categoryTreeNodes;
  }

  public static List<Category> classificationsToCategoriesList(Classifications classifications) {

    List<Category> categories = new ArrayList<>();

    for (Context context : classifications.getContext()) {

      // Level 1 (root categories)
      Category ctxCategory = generateCategory(context.getPreferredName(), Optional.empty(), Optional.empty(),
          Optional.empty(),
          Optional.empty(), Optional.empty(), context.getVersion().toString(), CADSR_CATEGORY_SCHEMA_ORG_ID);
      addCategoryToList(ctxCategory, categories);

      // Level 2
      for (ClassificationScheme cs : context.getClassificationScheme()) {
        String version = cs.getVersion() != null ? cs.getVersion().toString() : "1";
        Category csCategory = generateCategory(cs.getPreferredName(), Optional.of(ctxCategory.getLocalId()),
            Optional.empty(),
            Optional.of(cs.getPublicId().toString()), Optional.ofNullable(cs.getLongName()), Optional.empty(),
            version, ctxCategory.getUniqueId());
        addCategoryToList(csCategory, categories);

        // Levels 3 and beyond
        for (CSI csi : cs.getCSI()) {
          categories.addAll(classificationSchemeItemToCategories(csi, Optional.of(ctxCategory.getLocalId()),
              Optional.of(csCategory.getLocalId()), csCategory.getUniqueId(), new ArrayList<>()));
        }
      }
    }
    CategoryStats.getInstance().numberOfInputCategories = categories.size();
    return categories;
  }

  public static List<Category> classificationSchemeItemToCategories(CSI csi, Optional<String> ctxLocalId,
                                                                    Optional<String> csLocalId, String parentUniqueId
      , List<Category> categories) {

    Category category = generateCategory(csi.getClassificationSchemeItemName(), ctxLocalId, csLocalId,
        Optional.of(csi.getPublicId().toString()), Optional.empty(), Optional.of(csi.getClassificationSchemeItemType()),
        csi.getVersion().toString(), parentUniqueId);

    //categories.add(category);
    addCategoryToList(category, categories);

    for (CSI csiChildren : csi.getCSI()) {
      classificationSchemeItemToCategories(csiChildren, ctxLocalId, csLocalId, category.getUniqueId(), categories);
    }

    return categories;
  }

  /**
   * Category names should be unique at the same level. In order to ensure that, we add a suffix for categories that
   * have the same name at the same level
   *
   * @param newCategory
   * @param categories
   * @return
   */
  public static List<Category> addCategoryToList(Category newCategory, List<Category> categories) {

    List<Integer> positionOfCategoriesWithSameName = new ArrayList<>();

    for (int i = 0; i < categories.size(); i++) {
      Category category = categories.get(i);
      if (newCategory.getParentUniqueId().equals(category.getParentUniqueId()) && newCategory.getName().equals(category.getName())) {
        logger.warn("Found two categories with the same name at the same level! Name: " + category.getName() + ". Category Ids: " + category.getUniqueId() + "; " + newCategory.getUniqueId());
        positionOfCategoriesWithSameName.add(i);
      }
    }

    // Update names of categories with the same name than newCategory
    for (int position : positionOfCategoriesWithSameName) {
      Category existingCategory = categories.get(position);
      categories.remove(position);
      String existingCategoryNewName = existingCategory.getName() + " " + existingCategory.getVersion();
      logger.info("Updating category name to avoid confusion: " + existingCategory.getName() + " (" + existingCategory.getUniqueId() + ")" + " -> " + existingCategoryNewName);
      existingCategory.setName(existingCategoryNewName);
      categories.add(existingCategory);
    }

    // Update name of newCategory
    if (!positionOfCategoriesWithSameName.isEmpty()) {
      String newName = newCategory.getName() + " " + newCategory.getVersion();
      logger.info("Updating category name to avoid confusion: " + newCategory.getName() + " (" + newCategory.getUniqueId() + ")" + " -> " + newName);
      newCategory.setName(newName);
    }

    categories.add(newCategory);

    return categories;
  }


  private static Category generateCategory(String name, Optional<String> ctxLocalId, Optional<String> csLocalId,
                                           Optional<String> publicId,
                                           Optional<String> longName, Optional<String> type, String version,
                                           String parentUniqueId) {

    String categoryPublicId = publicId.isPresent() ? publicId.get() : null;
    String localId = generateCategoryLocalId(name, publicId, version);
    String cadsrId = generateCadsrCategoryId(localId, ctxLocalId, csLocalId);
    String uniqueId = generateCdeCategoryUniqueId(name, publicId, version, parentUniqueId);
    String categoryName = longName.isPresent() ? longName.get().trim() : name.trim();
    String description = longName.isPresent() ? longName.get().trim() : name.trim();
    String categoryType = type.isPresent() ? type.get() : null;

    return new Category(categoryPublicId, version, localId, cadsrId, uniqueId, categoryName, description,
        categoryType, parentUniqueId);
  }

  public static List<CategoryTreeNode> cdeCategoriesListToTree(List<Category> categories) {
    return getChildrenNodes(CADSR_CATEGORY_SCHEMA_ORG_ID, categories);
  }

  public static List<CategoryTreeNode> getChildrenNodes(String parentUniqueId, List<Category> categories) {
    List<CategoryTreeNode> childrenNodes = new ArrayList<>();
    for (Category category : categories) {
      if (category.getParentUniqueId().equals(parentUniqueId)) {
        CategoryTreeNode node = new CategoryTreeNode(category.getUniqueId(), category.getPublicId(),
            category.getName(), category.getDescription(),
            getChildrenNodes(category.getUniqueId(), categories), category.getParentUniqueId(), category.getVersion());
        childrenNodes.add(node);
      }
    }
    return childrenNodes;
  }

  public static String generateCategoryLocalId(String name, Optional<String> publicId, String version) {
    final String separator = "_";
    String categoryId = (publicId.isPresent() ? publicId.get() : name) + separator + "V" + version;
    return categoryId.replaceAll("\\s", "").replaceAll("/", ""); // Remove spaces and slashes
  }

  public static String generateCadsrCategoryId(String categoryLocalId, Optional<String> ctxLocalId,
                                               Optional<String> csLocalId) {
    String categoryId = CADSR_CATEGORY_SCHEMA_ORG_ID;
    if (ctxLocalId.isPresent()) {
      if (csLocalId.isPresent()) {
        return categoryId + "/" + ctxLocalId.get() + "/" + csLocalId.get() + "/" + categoryLocalId;
      } else {
        return categoryId + "/" + ctxLocalId.get() + "/" + categoryLocalId;
      }
    } else {
      return categoryId + "/" + categoryLocalId;
    }
  }

  public static String generateCdeCategoryUniqueId(String name, Optional<String> publicId,
                                                   String version, String parentCategoryUniqueId) {

    String categoryId = generateCategoryLocalId(name, publicId, version);
    return parentCategoryUniqueId + "/" + categoryId;

  }

  public static Map<String, String> getCategoryUniqueIdsFromCategoryTree(JsonNode cedarCategoryTree) {
    return getCategoryUniqueIds(cedarCategoryTree, new HashMap<>());
  }

  // Generates a map of categoryId to cedarCategoryId
  private static Map<String, String> getCategoryUniqueIds(JsonNode category, Map<String, String> categoryUniqueIds) {

    if (category.hasNonNull(JSON_LD_ID) && category.hasNonNull(ModelNodeNames.SCHEMA_ORG_IDENTIFIER)) {
      categoryUniqueIds.put(category.get(ModelNodeNames.SCHEMA_ORG_IDENTIFIER).asText(),
          category.get(JSON_LD_ID).asText());
    }

    if (category.hasNonNull(Constants.CEDAR_CATEGORY_CHILDREN_FIELD_NAME)) {
      JsonNode children = category.get(Constants.CEDAR_CATEGORY_CHILDREN_FIELD_NAME);
      if (children.isArray()) {
        for (JsonNode childCategory : children) {
          getCategoryUniqueIds(childCategory, categoryUniqueIds);
        }
      }
    }

    return categoryUniqueIds;
  }

  /**
   * Generates a hash code based on the attributes of a category. This hash code is used to identify changes in the
   * category and decide if it should be updated in CEDAR. Given that the goal is to keep categories updated in
   * CEDAR, this function needs to focus only on the attributes that CEDAR stores for categories.
   */
  public static String generateCategoryHashCode(Category category) {
    return generateCategoryHashCode(category.toCedarCategory());
  }

  public static String generateCategoryHashCode(CedarCategory cedarCategory) {
    return GeneralUtil.getSha1(cedarCategory.getId() + cedarCategory.getName() + cedarCategory.getDescription());
  }

  public static Map<String, CategorySummary> generateExistingCategoriesMap(CedarEnvironment environment,
                                                                           String apiKey) throws IOException {
    Map<String, CategorySummary> existingCategoriesMap = new HashMap<>();
    CedarCategory currentCedarCategoryTree = CedarServices.getCedarCategoryTree(environment, apiKey);
    return addCdeCategoriesToMap(currentCedarCategoryTree.getChildren(), existingCategoriesMap);
  }

  public static Map<String, CategorySummary> extractCdeCategoriesMap(Map<String, CategorySummary> allCategories) {
    Map<String, CategorySummary> cdeCategories = new HashMap<>();
    for (String categoryUniqueId : allCategories.keySet()) {
      // Ignore the root NCI caDSR category
      if (categoryUniqueId.startsWith(CADSR_CATEGORY_SCHEMA_ORG_ID) &&
          !categoryUniqueId.equals(CADSR_CATEGORY_SCHEMA_ORG_ID)) {
        cdeCategories.put(categoryUniqueId, allCategories.get(categoryUniqueId));
      }
    }
    return cdeCategories;
  }

  /**
   * Reads a list of CedarCategory objects and stores them into a map (key: category unique id; value: CategorySummary)
   * @param categories
   * @param categoriesMap
   * @return Categories map
   */
  private static Map<String, CategorySummary> addCdeCategoriesToMap(List<CedarCategory> categories, Map<String,
      CategorySummary> categoriesMap) {
    for (CedarCategory category : categories) {
      if (!categoriesMap.containsKey(category.getId())) {
        String categoryHash = CategoryUtil.generateCategoryHashCode(category);
        categoriesMap.put(category.getId(), new CategorySummary(category.getCedarId(),
            category.getParentCategoryCedarId(), categoryHash));
        addCdeCategoriesToMap(category.getChildren(), categoriesMap);
      } else {
        throw new InternalError("Category already in map: " + category.getId());
      }
    }
    return categoriesMap;
  }

  /**
   * Extracts the category ids from a dataElement without performing a full parsing of the data element. As explained
   * in Category.java, the CDEs XML file does not provide more than three levels, so this field will make it possible
   * to link CDES to categories. Some categories have the same cadsrId but different uniqueId because the
   * classification scheme items are at different levels (e.g., 3 vs 4).
   *
   * @param dataElement
   * @return
   */
  public static List<String> extractCategoryIdsFromCdeField(DataElement dataElement) {
    List<String> categoryIds = new ArrayList<>();
    final CLASSIFICATIONSLIST classificationsList = dataElement.getCLASSIFICATIONSLIST();

    if (classificationsList != null) {
      classificationsList.getCLASSIFICATIONSLISTITEM().stream().forEach(item -> {

        String ctxName = item.getClassificationScheme().getContextName().getContent();
        String ctxVersion = item.getClassificationScheme().getContextVersion().getContent();
        String ctxLocalId = CategoryUtil.generateCategoryLocalId(ctxName, Optional.empty(), ctxVersion);

        String csName = item.getClassificationScheme().getPreferredName().getContent();
        String csPublicId = item.getClassificationScheme().getPublicId().getContent();
        String csVersion = item.getClassificationScheme().getVersion().getContent();
        String csLocalId = CategoryUtil.generateCategoryLocalId(csName, Optional.of(csPublicId), csVersion);

        String csiName = item.getClassificationSchemeItemName().getContent();
        String csiPublicId = item.getCsiPublicId().getContent();
        String csiVersion = item.getCsiVersion().getContent();
        String categoryId = CategoryUtil.generateCategoryLocalId(csiName,
            Optional.of(csiPublicId), csiVersion);

        String cadsrCategoryId = CategoryUtil.generateCadsrCategoryId(categoryId, Optional.of(ctxLocalId),
            Optional.of(csLocalId));

        if (!categoryIds.contains(cadsrCategoryId)) {
          categoryIds.add(cadsrCategoryId);
        }
      });
    }
    return categoryIds;
  }

  /**
   * Extracts the category's caDSR identifiers from a CDE field. Note that
   * @param cdeFieldMap
   * @return
   */
  public static List<String> extractCategoryCadsrIdsFromCdeField(Map<String, Object> cdeFieldMap) {
    if (cdeFieldMap.containsKey(CDE_CATEGORY_IDS_FIELD)) {
      return (List) cdeFieldMap.get(CDE_CATEGORY_IDS_FIELD);
    } else {
      logger.warn("No categories found for CDE: " + cdeFieldMap.get(JSON_LD_ID));
      return new ArrayList<>();
    }
  }

  public static List<String> extractCategoryCedarIdsFromCdeField(DataElement cde,
                                                                 Map<String, Set<String>> categoryCadsrIdsToCategoryCedarIds) {
    Map<String, Object> cdeFieldMap = CdeUtil.getFieldMapFromDataElement(cde);
    if (cdeFieldMap != null) {
      return extractCategoryCedarIdsFromCdeField(cdeFieldMap, categoryCadsrIdsToCategoryCedarIds);
    }
    else {
      logger.warn("Couldn't extract cdeFieldMap from the CDE because the CDE was skipped.");
      return new ArrayList<>();
    }
  }

  public static List<String> extractCategoryCedarIdsFromCdeField(Map<String, Object> cdeFieldMap,
                                                                 Map<String, Set<String>> categoryCadsrIdsToCategoryCedarIds) {
    List<String> categoryCadsrIds = new ArrayList<>();
    if (cdeFieldMap.containsKey(CDE_CATEGORY_IDS_FIELD)) {
      categoryCadsrIds = (List) cdeFieldMap.get(CDE_CATEGORY_IDS_FIELD);
    } else {
      logger.warn("No categories found for CDE: " + cdeFieldMap.get(JSON_LD_ID));
    }
    List<String> categoryCedarIds = new ArrayList<>();
    for (String categoryCadsrId : categoryCadsrIds) {
      if (categoryCadsrIdsToCategoryCedarIds.containsKey(categoryCadsrId)) {
        categoryCedarIds.addAll(categoryCadsrIdsToCategoryCedarIds.get(categoryCadsrId));
      } else {
        logger.error("Error: Category referenced from the CDE is not in the categories XML: " + categoryCadsrId);
        CategoryStats.getInstance().idsOfCategoriesNotFoundInSource.add(categoryCadsrId);
      }
    }
    return categoryCedarIds;
  }

  /**
   * Translates a map based on category unique Ids to a map that uses category caDSR ids as keys (see Category.java
   * to understand the difference between category caDSR ids and category unique ids)
   *
   * @param categoryUniqueIdsToCedarCategoryIdsMap
   * @return
   */
  public static Map<String, Set<String>> generateCategoryCadsrIdsToCedarCategoryIdsMap(Map<String, String> categoryUniqueIdsToCedarCategoryIdsMap) {
    Map<String, Set<String>> categoryCadsrIdsToCedarCategoryIdsMap = new HashMap<>();
    for (String categoryUniqueId : categoryUniqueIdsToCedarCategoryIdsMap.keySet()) {
      String categoryCadsrId = categoryUniqueIdToCadsrId(categoryUniqueId);
      if (categoryCadsrIdsToCedarCategoryIdsMap.containsKey(categoryCadsrId)) {
        Set<String> cedarCategoryIds = categoryCadsrIdsToCedarCategoryIdsMap.get(categoryCadsrId);
        cedarCategoryIds.add(categoryUniqueIdsToCedarCategoryIdsMap.get(categoryUniqueId));
      }
      else {
        Set<String> cedarCategoryIds = new HashSet<>();
        cedarCategoryIds.add(categoryUniqueIdsToCedarCategoryIdsMap.get(categoryUniqueId));
        categoryCadsrIdsToCedarCategoryIdsMap.put(categoryCadsrId, cedarCategoryIds);
      }
    }
    return categoryCadsrIdsToCedarCategoryIdsMap;
  }

  public static String categoryUniqueIdToCadsrId(String categoryUniqueId) {
    String[] categoryIdsFromRoot = categoryUniqueId.split("/");
    if (categoryIdsFromRoot.length <= 4) { // 4 because NCI CaDSR root + Context + CS + CSI
      return categoryUniqueId;
    } else {
      return categoryIdsFromRoot[0] + "/" + categoryIdsFromRoot[1] + "/" + categoryIdsFromRoot[2] + "/" + categoryIdsFromRoot[categoryIdsFromRoot.length - 1];
    }
  }

  /**
   * Deletes all NCI caDSR categories. It does not remove neither the root category nor the NCI Cadsr root category.
   */
  public static void deleteAllNciCadsrCategories(CedarEnvironment cedarEnvironment, String apiKey) throws IOException {
    Map<String, CategorySummary> allCategoriesMap =
        Collections.unmodifiableMap(CategoryUtil.generateExistingCategoriesMap(cedarEnvironment, apiKey));
    // Keep only the CDE categories (ignoring the top-level CDE category)
    Map<String, CategorySummary> existingCdeCategoriesMap = CategoryUtil.extractCdeCategoriesMap(allCategoriesMap);
    for (CategorySummary categorySummary : existingCdeCategoriesMap.values()) {
      CedarServices.deleteCategory(categorySummary.getCedarId(), cedarEnvironment, apiKey);
    }
  }

  /**
   * Creates any missing CDE-Category relations
   */
  public static void reviewCdeCategoryRelations(List<DataElement> newDataElements,
                                                 Map<String, CdeSummary> existingCdesMap,
                                                 CedarEnvironment cedarEnvironment, String apiKey) throws IOException {

    if (newDataElements.size() > 0 && existingCdesMap.size() > 0) {

      // Read the categoryIds from CEDAR to be able to link CDEs to them
      Map<String, String> categoryUniqueIdsToCedarCategoryIds =
          CedarServices.getCategoryUniqueIdsToCedarCategoryIdsMap(cedarEnvironment, apiKey);
      Map<String, Set<String>> categoryCadsrIdsToCedarCategoryIds =
          CategoryUtil.generateCategoryCadsrIdsToCedarCategoryIdsMap(categoryUniqueIdsToCedarCategoryIds);

      for (DataElement cde : newDataElements) {
        String cdeId = CdeUtil.generateCdeUniqueId(cde);
        List<String> expectedCategoryCedarIds = CategoryUtil.extractCategoryCedarIdsFromCdeField(cde,
            categoryCadsrIdsToCedarCategoryIds);
        if (existingCdesMap.containsKey(cdeId)) {
          CdeSummary existingCdeSummary = existingCdesMap.get(cdeId);
          List<String> existingCategoryCedarIds = existingCdeSummary.getCategoryCedarIds();
          List<String> missingCdeToCategoryAttachments = new ArrayList<>();
          for (String expectedCategoryCedarId : expectedCategoryCedarIds) {
            if (!existingCategoryCedarIds.contains(expectedCategoryCedarId)) {
              logger.info("Found missing CDE-Category relation. CDE Id: " + cdeId + "; Category CEDAR Id: " + expectedCategoryCedarId);
              missingCdeToCategoryAttachments.add(expectedCategoryCedarId);
            }
          }
          if (missingCdeToCategoryAttachments.size() > 0) {
            CdeStats.getInstance().numberOfMissingCdeToCategoryRelations += missingCdeToCategoryAttachments.size();
            CedarServices.attachCdeToCategories(existingCdeSummary.getCedarId(), missingCdeToCategoryAttachments, true,
                CedarServerUtil.getAttachCategoriesEndpoint(cedarEnvironment), apiKey);
          }
        }
      }
    }
  }

}
