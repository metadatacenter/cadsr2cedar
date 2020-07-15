package org.metadatacenter.cadsr.ingestor.category.action;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;

import java.io.IOException;

public interface CategoryAction {
  void execute(CedarEnvironment environment, String apiKey) throws IOException;
}
