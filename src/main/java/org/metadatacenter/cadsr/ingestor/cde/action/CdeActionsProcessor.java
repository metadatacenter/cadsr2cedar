package org.metadatacenter.cadsr.ingestor.cde.action;

import org.metadatacenter.cadsr.ingestor.cde.CdeStats;
import org.metadatacenter.cadsr.ingestor.cde.CdeSummary;
import org.metadatacenter.cadsr.ingestor.util.CdeUtil;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CdeActionsProcessor {

  private static final Logger logger = LoggerFactory.getLogger(CdeActionsProcessor.class);

  private List<CreateCdeAction> createCdeActions;
  private List<UpdateOrDeleteCdeAction> updateOrDeleteCdeActions;
  private Map<String, CdeSummary> cdesMap; // To store the final CDEs so that they can saved to a file
  private CedarEnvironment cedarEnvironment;
  private String apiKey;

  public CdeActionsProcessor(List<CreateCdeAction> createCdeActions, List<UpdateOrDeleteCdeAction> updateOrDeleteCdeActions,
                             Map<String, CdeSummary> cdesMap, CedarEnvironment cedarEnvironment, String apiKey) {
    this.createCdeActions = createCdeActions;
    this.updateOrDeleteCdeActions = updateOrDeleteCdeActions;
    this.cdesMap = cdesMap;
    this.cedarEnvironment = cedarEnvironment;
    this.apiKey = apiKey;
    // Save stats
    CdeStats.getInstance().numberOfCdesToBeCreated = createCdeActions.size();
    CdeStats.getInstance().numberOfCdesToBeUpdatedOrDeleted = updateOrDeleteCdeActions.size();
  }

  public Map<String, CdeSummary> getCdesMap() {
    return cdesMap;
  }

  public void executeCdeActions() throws IOException {
    logger.info("Applying CDE actions: ");
    if (createCdeActions.size() > 0) {
      executeCreateActions();
    }
    logger.info("Applying CDE actions: ");
    if (createCdeActions.size() > 0) {
      executeUpdateOrDeleteActions();
    }
    logger.info("Finished applying CDE actions.");
  }

  public void logActionsSummary() {
    logger.info("CDE actions: ");
    logger.info(" - " + createCdeActions.size() + " Create actions");
  }

  /*** Private methods to execute actions ***/

  private void executeCreateActions() {
    logger.info("Executing CDE Create actions");
    int count = 0;
    for (CreateCdeAction createCdeAction : createCdeActions) {
      logger.info("Progress: " + count++ + "/" + createCdeActions.size());
      String createdCdeCedarId = createCdeAction.execute(cedarEnvironment, apiKey);
      String createdCdeUniqueId = CdeUtil.generateCdeUniqueId(createCdeAction.getCdeFieldMap());
      CdeSummary cdeSummary = new CdeSummary(createdCdeCedarId, null, null, createCdeAction.getHashCode(),
          new ArrayList<>());
      cdesMap.put(createdCdeUniqueId, cdeSummary);
    }
  }

  private void executeUpdateOrDeleteActions() {
    logger.info("Executing CDE Update/Delete actions");
    for (UpdateOrDeleteCdeAction updateOrDeleteCdeAction : updateOrDeleteCdeActions) {
      updateOrDeleteCdeAction.execute(cedarEnvironment, apiKey);
    }
  }
}
