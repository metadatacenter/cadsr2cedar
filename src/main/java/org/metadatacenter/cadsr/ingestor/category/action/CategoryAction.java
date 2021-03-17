package org.metadatacenter.cadsr.ingestor.category.action;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;

import java.io.IOException;

public interface CategoryAction {
  void execute(CedarServer server, String apiKey) throws IOException;
}
