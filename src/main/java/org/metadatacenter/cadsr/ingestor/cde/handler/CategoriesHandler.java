package org.metadatacenter.cadsr.ingestor.cde.handler;

import org.metadatacenter.cadsr.cde.schema.CLASSIFICATIONSLIST;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.Constants;
import org.metadatacenter.cadsr.ingestor.category.CadsrCategoriesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class CategoriesHandler implements ModelHandler {

  private List<String> categoryIds = new ArrayList<>();

  public CategoriesHandler handle(DataElement dataElement) {

    final CLASSIFICATIONSLIST classificationsList = dataElement.getCLASSIFICATIONSLIST();
    if (classificationsList != null) {
      classificationsList.getCLASSIFICATIONSLISTITEM().stream().forEach(item -> {

        String ctxName =  item.getClassificationScheme().getContextName().getContent();
        String ctxVersion = item.getClassificationScheme().getContextVersion().getContent();
        String ctxId = CadsrCategoriesUtils.generateCategoryId(ctxName, Optional.empty(), Optional.empty(), ctxVersion);

        String csName = item.getClassificationScheme().getPreferredName().getContent();
        String csPublicId = item.getClassificationScheme().getPublicId().getContent();
        String csVersion = item.getClassificationScheme().getVersion().getContent();
        String csId = CadsrCategoriesUtils.generateCategoryId(csName, Optional.empty(), Optional.of(csPublicId), csVersion);

        String csiName = item.getClassificationSchemeItemName().getContent();
        String csiType = item.getClassificationSchemeItemType().getContent();
        String csiPublicId = item.getCsiPublicId().getContent();
        String csiVersion= item.getCsiVersion().getContent();
        String csiId = CadsrCategoriesUtils.generateCategoryId(csiName, Optional.of(csiType),
            Optional.of(csiPublicId), csiVersion);

        String categoryId = CadsrCategoriesUtils.generateCadsrCategoryId(csiId, Optional.of(ctxId), Optional.of(csId));

        if (!categoryIds.contains(categoryId)) {
          categoryIds.add(categoryId);
        }

      });
    }
    return this;
  }

  @Override
  public void apply(Map<String, Object> fieldObject) {
    fieldObject.put(Constants.CDE_CATEGORY_IDS_FIELD, categoryIds);
  }
}

