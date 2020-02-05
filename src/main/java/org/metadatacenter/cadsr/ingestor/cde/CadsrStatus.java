package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.collect.Lists;

import java.util.List;

public class CadsrStatus {

  public final static String APPROVED_FOR_TRIAL_USE = "APPRVD FOR TRIAL USE";
  public final static String DRAFT_MOD = "DRAFT MOD";
  public final static String DRAFT_NEW = "DRAFT NEW";
  public final static String RELEASED = "RELEASED";

  public static final List<String> STATUS_LIST = Lists.newArrayList(
      RELEASED
  );
}
