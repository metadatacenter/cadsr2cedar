package org.metadatacenter.cadsr.ingestor.cde.action;

import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;

import java.io.IOException;

public interface CdeAction {
  String execute(CedarServer server, String apiKey) throws IOException;
}
