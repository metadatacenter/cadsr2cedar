package org.metadatacenter.cadsr.ingestor.form.handler;

import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.form.schema.Module;
import org.metadatacenter.cadsr.form.schema.Question;
import org.metadatacenter.cadsr.ingestor.util.CedarFieldUtil;
import org.metadatacenter.cadsr.ingestor.util.CedarServices;
import org.metadatacenter.cadsr.ingestor.util.Constants.CedarServer;
import org.metadatacenter.model.ModelNodeNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static org.metadatacenter.model.ModelNodeNames.*;

public class TemplateHeaderAndFooterHandler implements ModelHandler {

  private static final Logger logger = LoggerFactory.getLogger(TemplateHeaderAndFooterHandler.class);


  private String header = null;
  private String footer = null;

  public TemplateHeaderAndFooterHandler handle(Form form) {
    setTemplateHeader(form);
    setTemplateFooter(form);
    return this;
  }

  private void setTemplateHeader(Form form) {
    if (form.getHeaderInstruction() != null) {
      header = form.getHeaderInstruction().getText();
    }
  }

  private void setTemplateFooter(Form form) {
    if (form.getFooterInstruction() != null) {
      footer = form.getFooterInstruction().getText();
    }
  }

  @Nullable
  public String getHeader() {
    return header;
  }

  @Nullable
  public String getFooter() {
    return footer;
  }

  @Override
  public void apply(Map<String, Object> fieldMap) {
    if (getHeader() != null) {
      ((Map<String, Object>)fieldMap.get(UI)).put(UI_HEADER, getHeader());
    }
    if (getFooter() != null) {
      ((Map<String, Object>)fieldMap.get(UI)).put(UI_FOOTER, getFooter());
    }
  }


}
