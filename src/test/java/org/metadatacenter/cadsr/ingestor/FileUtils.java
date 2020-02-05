package org.metadatacenter.cadsr.ingestor;

import com.google.common.io.Resources;
import org.metadatacenter.cadsr.cde.schema.DataElement;
import org.metadatacenter.cadsr.ingestor.cde.CadsrUtils;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

  public static DataElement readDataElementResource(String filename) throws JAXBException, IOException {
    InputStream is = Resources.getResource(filename).openStream();
    return CadsrUtils.getDataElement(is);
  }
}
