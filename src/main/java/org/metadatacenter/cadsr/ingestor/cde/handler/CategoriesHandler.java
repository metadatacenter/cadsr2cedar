package org.metadatacenter.cadsr.ingestor.cde.handler;

import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.util.CategoryUtil;
import org.metadatacenter.cadsr.ingestor.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CategoriesHandler implements ModelHandler {

  private List<String> categoryIds = new ArrayList<>();

  public CategoriesHandler handle(DataElement dataElement) {
    categoryIds = CategoryUtil.extractCategoryIdsFromCdeField(dataElement);
    return this;
  }

  @Override
  public void apply(Map<String, Object> fieldObject) {
    fieldObject.put(Constants.CDE_CATEGORY_IDS_FIELD, categoryIds);
  }
}

