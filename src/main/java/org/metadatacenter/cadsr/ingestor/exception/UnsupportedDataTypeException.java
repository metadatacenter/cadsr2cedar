package org.metadatacenter.cadsr.ingestor.exception;

import static com.google.common.base.Preconditions.checkNotNull;

public class UnsupportedDataTypeException extends Exception {

  private final String datatype;

  public UnsupportedDataTypeException(String datatype) {
    this.datatype = checkNotNull(datatype);
  }

  public String getDatatype() {
    return datatype;
  }

  @Override
  public String getMessage() {
    return ("Cannot handle datatype: " + datatype);
  }
}
