package org.metadatacenter.cadsr.ingestor.cde.action;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;

import java.io.IOException;

public interface CdeAction {
  String execute(CedarEnvironment environment, String apiKey) throws IOException;
}
