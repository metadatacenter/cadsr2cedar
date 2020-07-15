package org.metadatacenter.cadsr.ingestor.cde.handler;

import com.google.common.base.Strings;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CadsrStatus;
import org.metadatacenter.cadsr.ingestor.exception.UnsupportedDataElementException;
import org.metadatacenter.model.BiboStatus;
import org.metadatacenter.model.ModelNodeNames;

import javax.annotation.Nullable;
import java.util.Map;

public class VersionHandler implements ModelHandler {

  private String status = null;
  private String version = null;

  public VersionHandler handle(DataElement dataElement) throws UnsupportedDataElementException {
    setDataElementStatus(dataElement);
    setDataElementVersion(dataElement);
    return this;
  }

  private void setDataElementStatus(DataElement dataElement) throws UnsupportedDataElementException {
    String status = dataElement.getWORKFLOWSTATUS().getContent();
    if (Strings.isNullOrEmpty(status)) {
      String reason = String.format("Data element status is empty (Unknown)");
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    if (!CadsrStatus.STATUS_LIST.contains(status)) {
      String reason = String.format("The data element status of '%s' is not supported (Unsupported)", status);
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    if (CadsrStatus.RELEASED.equals(status)) {
      this.status = BiboStatus.PUBLISHED.getValue();
    }
  }

  private void setDataElementVersion(DataElement dataElement) throws UnsupportedDataElementException {
    String version = dataElement.getVERSION().getContent();
    if (Strings.isNullOrEmpty(version)) {
      String reason = String.format("Data element version is empty (Unknown)");
      throw new UnsupportedDataElementException(dataElement, reason);
    }
    this.version = reformatVersioningNumber(version);
  }

  public static String reformatVersioningNumber(String version) {
    String[] originalVersion = version.split("\\.");
    String[] baseVersion = {"0", "0", "0"};
    for (int i = 0; i < originalVersion.length; i++) {
      baseVersion[i] = originalVersion[i];
    }
    return String.join(".", baseVersion);
  }

  @Nullable
  public String getStatus() {
    return status;
  }

  @Nullable
  public String getVersion() {
    return version;
  }

  @Override
  public void apply(Map<String, Object> fieldMap) {
    if (getVersion() != null) {
      fieldMap.put(ModelNodeNames.PAV_VERSION, getVersion());
    }
    if (getStatus() != null) {
      fieldMap.put(ModelNodeNames.BIBO_STATUS, getStatus());
    }
  }
}
