package org.metadatacenter.cadsr.ingestor.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import org.metadatacenter.cadsr.form.schema.Form;
import org.metadatacenter.cadsr.ingestor.util.Constants;
import org.metadatacenter.cadsr.ingestor.util.GeneralUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

public class FormUtil {

  private static final Logger logger = LoggerFactory.getLogger(FormUtil.class);
  private static ObjectMapper objectMapper = new ObjectMapper();

  public static Form getForm(InputStream is) throws JAXBException, IOException {
    JAXBContext jaxbContext = JAXBContext.newInstance(Form.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    InputStream cleanIs = GeneralUtil.processInvalidXMLCharacters(is);
    return (Form) jaxbUnmarshaller.unmarshal(new InputStreamReader(cleanIs, Constants.CHARSET));
  }

  public static FormParseResult getTemplateMapFromForm(Form form, String reportId) throws IOException {
    Map<String, Object> templateMap = Maps.newHashMap();
    FormParser.parseForm(form, templateMap, reportId);
    List<String> messages = FormParseReporter.getInstance().getMessages(reportId);
    // Once the messages have been retrieved we don't need them anymore in the map
    FormParseReporter.getInstance().remove(reportId);
    return new FormParseResult(templateMap, messages);
  }

}
