package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public class CadsrConstants {

  // The reasons behind the following caDSR-to-CEDAR datatype mappings are available at https://stanfordmedicine.app
  // .box.com/file/571026362915

  // caDSR datatypes mapped in the 1st iteration
  public static final String ALPHANUMERIC = "ALPHANUMERIC";
  public static final String CHARACTER = "CHARACTER";
  public static final String DATE = "DATE";
  public static final String ISO21090CD = "ISO21090CDv1.0";
  public static final String JAVA_DATE = "java.util.Date";
  public static final String JAVA_DOUBLE = "java.lang.Double";
  public static final String JAVA_INTEGER = "java.lang.Integer";
  public static final String JAVA_LONG = "java.lang.Long";
  public static final String JAVA_STRING = "java.lang.String";
  public static final String NUMBER = "NUMBER";
  // caDSR datatypes mapped on December 2019 as part of the 2nd iteration (Phase II)
  public static final String ALPHA_DVG = "Alpha DVG";
  public static final String BOOLEAN = "BOOLEAN";
  public static final String CLOB = "CLOB";
  public static final String DATE_ALPHA_DVG = "Date Alpha DVG";
  public static final String HL7CDV3 = "HL7CDv3";
  public static final String HL7EDV3 = "HL7EDv3";
  public static final String HL7INTV3 = "HL7INTv3";
  public static final String HL7PNV3 = "HL7PNv3";
  public static final String HL7REALV3 = "HL7REALv3";
  public static final String HL7STV3 = "HL7STv3";
  public static final String HL7TELV3 = "HL7TELv3";
  public static final String INTEGER = "Integer";
  public static final String ISO21090ADPARTV1 = "ISO21090ADPartv1.0";
  public static final String ISO21090ADV1 = "ISO21090ADv1.0";
  public static final String ISO21090ADXPALV1 = "ISO21090ADXPALv1.0";
  public static final String ISO21090ADXPCNTV1 = "ISO21090ADXPCNTv1.0";
  public static final String ISO21090ADXPCTYV1 = "ISO21090ADXPCTYv1.0";
  public static final String ISO21090ADXPDALV1 = "ISO21090ADXPDALv1.0";
  public static final String ISO21090ADXPSTAV1 = "ISO21090ADXPSTAv1.0";
  public static final String ISO21090ADXPV1 = "ISO21090ADXPv1.0";
  public static final String ISO21090ADXPZIPV1 = "ISO21090ADXPZIPv1.0";
  public static final String ISO21090ANYV1 = "ISO21090ANYv1.0";
  public static final String ISO21090BAGV1 = "ISO21090BAGv1.0";
  public static final String ISO21090BLV1 = "ISO21090BLv1.0";
  public static final String ISO21090DSETV1 = "ISO21090DSETv1.0";
  public static final String ISO21090EDTEXTV1 = "ISO21090EDTEXTv1.0";
  public static final String ISO21090EDV1 = "ISO21090EDv1.0";
  public static final String ISO21090ENONV1 = "ISO21090ENONv1.0";
  public static final String ISO21090ENPNV1 = "ISO21090ENPNv1.0";
  public static final String ISO21090ENTNV1 = "ISO21090ENTNv1.0";
  public static final String ISO21090ENXPV1 = "ISO21090ENXPv1.0";
  public static final String ISO21090IIV1 = "ISO21090IIv1.0";
  public static final String ISO21090INTNTNEGV1 = "ISO21090INTNTNEGv1.0";
  public static final String ISO21090INTPOSV1 = "ISO21090INTPOSv1.0";
  public static final String ISO21090INTV1 = "ISO21090INTv1.0";
  public static final String ISO21090IVLV1 = "ISO21090IVLv1.0";
  public static final String ISO21090PQV1 = "ISO21090PQv1.0";
  public static final String ISO21090QTYV1 = "ISO21090QTYv1.0";
  public static final String ISO21090REALV1 = "ISO21090REALv1.0";
  public static final String ISO21090RTOV1 = "ISO21090RTOv1.0";
  public static final String ISO21090STSIMV1 = "ISO21090STSIMv1.0";
  public static final String ISO21090STV1 = "ISO21090STv1.0";
  public static final String ISO21090TELURLV1 = "ISO21090TELURLv1.0";
  public static final String ISO21090TELV1 = "ISO21090TELv1.0";
  public static final String ISO21090TV1 = "ISO21090Tv1.0";
  public static final String ISO21090URGV1 = "ISO21090URGv1.0";
  public static final String JAVA_BOOLEAN = "java.lang.Boolean";
  public static final String JAVA_BYTE = "java.lang.Byte";
  public static final String JAVA_CHARACTER = "java.lang.Character";
  public static final String JAVA_FLOAT = "java.lang.Float";
  public static final String JAVA_INTEGER_ARRAY = "java.lang.Integer[]";
  public static final String JAVA_OBJECT = "java.lang.Object";
  public static final String JAVA_SHORT = "java.lang.Short";
  public static final String JAVA_STRING_ARRAY = "java.lang.String[]";
  public static final String JAVA_COLLECTION = "java.util.Collection";
  public static final String JAVA_MAP = "java.util.Map";
  public static final String NUMERIC_ALPHA_DVG = "Numeric Alpha DVG";
  public static final String OBJECT = "OBJECT";
  public static final String SAS_DATE = "SAS Date";
  public static final String SAS_TIME = "SAS Time";
  public static final String UMLCODEV1 = "UMLCodev1.0";
  public static final String UMLOCTETV1 = "UMLOctetv1.0";
  public static final String UMLUIDV1 = "UMLUidv1.0";
  public static final String UMLURIV1 = "UMLUriv1.0";
  public static final String UMLXMLV1 = "UMLXMLv1.0";
  public static final String VARCHAR = "varchar";
  public static final String XSD_BOOLEAN = "xsd:boolean";
  public static final String XSD_STRING = "xsd:string";
  // Mappings implemented on June 2020 as part of Phase IIb
  public static final String DATE_TIME = "DATE/TIME";
  public static final String DATETIME = "DATETIME";
  public static final String HL7TSV3 = "HL7TSv3";
  public static final String ISO21090PQTIMEV1 = "ISO21090PQTIMEv1.0";
  public static final String ISO21090TSDATFLV1 = "ISO21090TSDATFLv1.0";
  public static final String ISO21090TSDTTIV1 = "ISO21090TSDTTIv1.0";
  public static final String ISO21090TSV1 = "ISO21090TSv1.0";
  public static final String JAVA_TIMESTAMP = "java.sql.Timestamp";
  public static final String TIME = "TIME";
  public static final String XSD_DATETIME = "xsd:dateTime";
  // Data types that we agreed to skip
  public static final String ANY_CLASS = "anyClass";
  public static final String BINARY = "binary";
  public static final String DERIVED = "Derived";
  public static final String UMLBINARYV1 = "UMLBinaryv1.0";

  /* String */
  public static final List<String> STRING_LIST = Lists.newArrayList(
      CHARACTER, JAVA_STRING, ALPHANUMERIC, ISO21090CD, ALPHA_DVG, CLOB, DATE_ALPHA_DVG,
      HL7EDV3, HL7PNV3, HL7STV3, HL7TELV3, ISO21090ADPARTV1, ISO21090ADV1, ISO21090ADXPALV1, ISO21090ADXPCNTV1,
      ISO21090ADXPCTYV1, ISO21090ADXPDALV1, ISO21090ADXPSTAV1, ISO21090ADXPV1, ISO21090ADXPZIPV1, ISO21090ANYV1,
      ISO21090BAGV1, ISO21090DSETV1, ISO21090EDTEXTV1, ISO21090EDV1, ISO21090ENONV1, ISO21090ENPNV1,
      ISO21090ENTNV1, ISO21090ENXPV1, ISO21090IIV1, ISO21090STSIMV1, ISO21090STV1, ISO21090TELURLV1, ISO21090TELV1,
      JAVA_OBJECT, JAVA_COLLECTION, JAVA_MAP, NUMERIC_ALPHA_DVG, OBJECT, UMLCODEV1, UMLUIDV1,
      UMLXMLV1, VARCHAR, XSD_STRING, HL7CDV3, ISO21090IVLV1, JAVA_INTEGER_ARRAY, JAVA_STRING_ARRAY
  );
  public static final List<String> STRING_MAX_LENGTH_1_LIST = Lists.newArrayList(JAVA_CHARACTER);

  /* Numeric */
  public static final List<String> NUMERIC_ANY_LIST = Lists.newArrayList();
  public static final List<String> NUMERIC_INTEGER_LIST = Lists.newArrayList(
      HL7INTV3, INTEGER, ISO21090TV1, JAVA_INTEGER, SAS_DATE, SAS_TIME);
  public static final List<String> NUMERIC_POSITIVE_INTEGER_LIST = Lists.newArrayList(
      ISO21090INTNTNEGV1, ISO21090INTPOSV1, ISO21090INTV1);
  public static final List<String> NUMERIC_BYTE_LIST = Lists.newArrayList(JAVA_BYTE);
  public static final List<String> NUMERIC_OCTET_LIST = Lists.newArrayList(UMLOCTETV1);
  public static final List<String> NUMERIC_SHORT_INTEGER_LIST = Lists.newArrayList(JAVA_SHORT);
  public static final List<String> NUMERIC_LONG_INTEGER_LIST = Lists.newArrayList(JAVA_LONG);
  public static final List<String> NUMERIC_FLOAT_LIST = Lists.newArrayList(
      JAVA_FLOAT, HL7REALV3, ISO21090PQV1, ISO21090QTYV1, ISO21090REALV1, ISO21090RTOV1, ISO21090URGV1, NUMBER);
  public static final List<String> NUMERIC_DOUBLE_LIST = Lists.newArrayList(JAVA_DOUBLE);

  /* Date */
  public static final List<String> DATE_LIST = Lists.newArrayList(DATE, JAVA_DATE);

  /* Time */
  public static final List<String> TIME_LIST = Lists.newArrayList(HL7TSV3, ISO21090PQTIMEV1, ISO21090TSV1, TIME);

  /* Datetime */
  public static final List<String> DATETIME_LIST = Lists.newArrayList(DATE_TIME, DATETIME, ISO21090TSDATFLV1,
      ISO21090TSDTTIV1, JAVA_TIMESTAMP, XSD_DATETIME);


  /* Boolean */
  public static final List<String> BOOLEAN_LIST = Lists.newArrayList(BOOLEAN, JAVA_BOOLEAN, ISO21090BLV1, XSD_BOOLEAN);

  /* URI */
  public static final List<String> URI_LIST = Lists.newArrayList(UMLURIV1);

  /* Groups */
  public static final List<String> ALL_STRING_LIST = Lists.newArrayList();
  public static final List<String> ALL_NUMERIC_LIST = Lists.newArrayList();
  public static final List<String> ALL_TEMPORAL_LIST = Lists.newArrayList();
  public static final List<String> ALL_BOOLEAN_LIST = Lists.newArrayList();
  public static final List<String> ALL_URI_LIST = Lists.newArrayList();

  // Populate groups
  static {
    // String
    ALL_STRING_LIST.addAll(STRING_LIST);
    ALL_STRING_LIST.addAll(STRING_MAX_LENGTH_1_LIST);
    // Numeric
    ALL_NUMERIC_LIST.addAll(NUMERIC_ANY_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_INTEGER_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_POSITIVE_INTEGER_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_BYTE_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_OCTET_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_SHORT_INTEGER_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_LONG_INTEGER_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_FLOAT_LIST);
    ALL_NUMERIC_LIST.addAll(NUMERIC_DOUBLE_LIST);
    // Date, Time, and Datetime
    ALL_TEMPORAL_LIST.addAll(DATE_LIST);
    ALL_TEMPORAL_LIST.addAll(TIME_LIST);
    ALL_TEMPORAL_LIST.addAll(DATETIME_LIST);
    // Boolean
    ALL_BOOLEAN_LIST.addAll(BOOLEAN_LIST);
    // Uri
    ALL_URI_LIST.addAll(URI_LIST);
  }

  /* ValueDomainType values */
  public static final String ENUMERATED = "Enumerated";
  public static final String NON_ENUMERATED = "NonEnumerated";

  /* Temporal granularity according to caDSR display formats */
  // Date
  public static final List<String> DATE_GRANULARITY_YEAR_FORMATS = Lists.newArrayList("YYYY");
  public static final List<String> DATE_GRANULARITY_MONTH_FORMATS = Lists.newArrayList("MM/YYYY", "MMYYYY",
      "YYYYMM", "mm/dd/yy", "mm/dd/yyyy");
  public static final List<String> DATE_GRANULARITY_DAY_FORMATS = Lists.newArrayList("DD-MON-YYYY", "DY/MTH/YR",
      "MM/DD/YYYY", "MMDDYYYY", "MON/DD/YYYY", "YYYY-MM-DD");
  // Time
  public static final List<String> TIME_GRANULARITY_HOUR_FORMATS = Lists.newArrayList("hh");
  public static final List<String> TIME_GRANULARITY_MINUTE_FORMATS = Lists.newArrayList("TIME (HR(24):MN)",
      "TIME_HH:MM", "TIME_MIN", "hh:mm", "hhmm");
  public static final List<String> TIME_GRANULARITY_SECOND_FORMATS = Lists.newArrayList("hh:mm:rr", "hh:mm:ss",
      "hh:mm:ss:rr", "hhmmss");
  public static final List<String> TIME_GRANULARITY_DECIMALSECOND_FORMATS = Lists.newArrayList();

  // Date time
  public static final List<String> DATETIME_GRANULARITY_HOUR_FORMATS = Lists.newArrayList();
  public static final List<String> DATETIME_GRANULARITY_MINUTE_FORMATS = Lists.newArrayList();
  public static final List<String> DATETIME_GRANULARITY_SECOND_FORMATS = Lists.newArrayList();
  public static final List<String> DATETIME_GRANULARITY_DECIMALSECOND_FORMATS = Lists.newArrayList();

  /* Display time format according to caDSR display formats */
  public static final List<String> DISPLAY_TIME_FORMAT_24H_FORMATS = Lists.newArrayList("TIME (HR(24):MN)");
  public static final List<String> DISPLAY_TIME_FORMAT_AMPM_FORMATS = Lists.newArrayList();

}
















