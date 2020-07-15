package org.metadatacenter.cadsr.ingestor.cde;

import com.google.common.collect.Lists;

import java.util.List;

public class CadsrConstants {

  // The reasoning behind these mappings are documented at https://stanfordmedicine.app.box.com/file/571026362915

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
  // Data types added on July 2020
  public static final String BINARY_OBJECT = "BINARY OBJECT";
  public static final String BRIDG_SET_V21 = "BRIDG_SET_v2.1";
  public static final String HL7ADV3 = "HL7ADv3";
  public static final String HL7ANYV3 = "HL7ANYv3";
  public static final String HL7BAGADV3 = "HL7BAGADv3";
  public static final String HL7BAGTELV3 = "HL7BAGTELv3";
  public static final String HL7BLV3 = "HL7BLv3";
  public static final String HL7DSETCDV3 = "HL7DSETCDv3";
  public static final String HL7DSETCRV3 = "HL7DSETCRv3";
  public static final String HL7DSETENV3 = "HL7DSETENv3";
  public static final String HL7DSETIDV3 = "HL7DSETIDv3";
  public static final String HL7DSETOIDV3 = "HL7DSETOIDv3";
  public static final String HL7DSETONV3 = "HL7DSETONv3";
  public static final String HL7DSETSCV3 = "HL7DSETSCv3";
  public static final String HL7DSETSTV3 = "HL7DSETSTv3";
  public static final String HL7DSETTELURLV3 = "HL7DSETTELURLv3";
  public static final String HL7EXPRPQV3 = "HL7EXPRPQv3";
  public static final String HL7IDV3 = "HL7IDv3";
  public static final String HL7IIV3 = "HL7IIv3";
  public static final String HL7INTNONNEGV3 = "HL7INTNONNEGv3";
  public static final String HL7INTPOSV3 = "HL7INTPOSv3";
  public static final String HL7IVLEXPRTSDATETIME = "HL7IVLEXPRTSDATETIME";
  public static final String HL7IVLINTV3 = "HL7IVLINTv3";
  public static final String HL7IVLPQV3 = "HL7IVLPQv3";
  public static final String HL7IVLTSDATEFULLV3 = "HL7IVLTSDATEFULLv3";
  public static final String HL7IVLTSDATETIMEV3 = "HL7IVLTSDATETIMEv3";
  public static final String HL7IVLTSDATEV3 = "HL7IVLTSDATEv3";
  public static final String HL7OIDV3 = "HL7OIDv3";
  public static final String HL7PPDV30 = "HL7PPDv3.0";
  public static final String HL7PQTIMEV3 = "HL7PQTIMEv3";
  public static final String HL7PQV3 = "HL7PQv3";
  public static final String HL7RTOINTNONNEGINTPO = "HL7RTOINTNONNEGINTPO";
  public static final String HL7RTOINTNONNEGPQTIM = "HL7RTOINTNONNEGPQTIM";
  public static final String HL7RTOPQPQTIMEV3 = "HL7RTOPQPQTIMEv3";
  public static final String HL7RTOPQPQV3 = "HL7RTOPQPQv3";
  public static final String HL7SCV3 = "HL7SCv3";
  public static final String HL7STSIMPLEV3 = "HL7STSIMPLEv3";
  public static final String HL7TELURLV3 = "HL7TELURLv3";
  public static final String HL7TNV3 = "HL7TNv3";
  public static final String HL7TSDATEFULLV3 = "HL7TSDATEFULLv3";
  public static final String HL7TSDATETIMEV3 = "HL7TSDATETIMEv3";
  public static final String HL7URGINTNONNEGvV3 = "HL7URGINTNONNEGv3";
  public static final String HL7URGINTPOSV3 = "HL7URGINTPOSv3";
  public static final String HL7URGPQTIMEV3 = "HL7URGPQTIMEv3";
  public static final String HL7URGPQV3 = "HL7URGPQv3";
  public static final String ISO21090ADXPBNNV1 = "ISO21090ADXPBNNv1.0";
  public static final String ISO21090ADXPBNRV1 = "ISO21090ADXPBNRv1.0";
  public static final String ISO21090ADXPBNSV1 = "ISO21090ADXPBNSv1.0";
  public static final String ISO21090ADXPBRV1 = "ISO21090ADXPBRv1.0";
  public static final String ISO21090ADXPCARV1 = "ISO21090ADXPCARv1.0";
  public static final String ISO21090ADXPCENV1 = "ISO21090ADXPCENv1.0";
  public static final String ISO21090ADXPCPAV1 = "ISO21090ADXPCPAv1.0";
  public static final String ISO21090ADXPDIAAV1 = "ISO21090ADXPDIAAv1.0";
  public static final String ISO21090ADXPDIQV1 = "ISO21090ADXPDIQv1.0";
  public static final String ISO21090ADXPDIRV1 = "ISO21090ADXPDIRv1.0";
  public static final String ISO21090ADXPDISTV1 = "ISO21090ADXPDISTv1.0";
  public static final String ISO21090ADXPDMDV1 = "ISO21090ADXPDMDv1.0";
  public static final String ISO21090ADXPDMIDV1 = "ISO21090ADXPDMIDv1.0";
  public static final String ISO21090ADXPINTV1 = "ISO21090ADXPINTv1.0";
  public static final String ISO21090ADXPPOBV1 = "ISO21090ADXPPOBv1.0";
  public static final String ISO21090ADXPPREV1 = "ISO21090ADXPPREv1.0";
  public static final String ISO21090ADXPSALV1 = "ISO21090ADXPSALv1.0";
  public static final String ISO21090ADXPSTBV1 = "ISO21090ADXPSTBv1.0";
  public static final String ISO21090ADXPSTRV1 = "ISO21090ADXPSTRv1.0";
  public static final String ISO21090ADXPSTTPV1 = "ISO21090ADXPSTTPv1.0";
  public static final String ISO21090ADXPUNIDV1 = "ISO21090ADXPUNIDv1.0";
  public static final String ISO21090ADXPUNTV1 = "ISO21090ADXPUNTv1.0";
  public static final String ISO21090BLNTNULV1 = "ISO21090BLNTNULv1.0";
  public static final String ISO21090CDBASEV1 = "ISO21090CDBasev1.0";
  public static final String ISO21090CDCVV1 = "ISO21090CDCVv1.0";
  public static final String ISO21090CDV1 = "ISO21090CDv1.0";
  public static final String ISO21090COLLV1 = "ISO21090COLLv1.0";
  public static final String ISO21090COV1 = "ISO21090COv1.0";
  public static final String ISO21090CSV1 = "ISO21090CSv1.0";
  public static final String ISO21090EDDCINLINV1 = "ISO21090EDDCINLINv1.";
  public static final String ISO21090EDDCREFV1 = "ISO21090EDDCREFv1.0";
  public static final String ISO21090EDIMAGEV1 = "ISO21090EDIMAGEv1.0";
  public static final String ISO21090EDSIGNAT1 = "ISO21090EDSIGNAT1.0";
  public static final String ISO21090EDSTRTITV1 = "ISO21090EDSTRTITv1.0";
  public static final String ISO21090EDSTRTXV1 = "ISO21090EDSTRTXv1.0";
  public static final String ISO21090EIVLV1 = "ISO21090EIVLv1.0";
  public static final String ISO21090ENV1 = "ISO21090ENV1.0";
  public static final String ISO21090GLISTV1 = "ISO21090GLISTv1.0";
  public static final String ISO21090GTSBPIVLV1 = "ISO21090GTSBPIVLv1.0";
  public static final String ISO21090HISTV1 = "ISO21090HISTv1.0";
  public static final String ISO21090HXITV1 = "ISO21090HXITv1.0";
  public static final String ISO21090IVLHIGHV1 = "ISO21090IVLHIGHv1.0";
  public static final String ISO21090IVLLOWV1 = "ISO21090IVLLOWv1.0";
  public static final String ISO21090IVLWIDV1 = "ISO21090IVLWIDv1.0";
  public static final String ISO21090LISTV1 = "ISO21090LISTv1.0";
  public static final String ISO21090MOV1 = "ISO21090MOv1.0";
  public static final String ISO21090NPPDV1 = "ISO21090NPPDv1.0";
  public static final String ISO21090PIVLV1 = "ISO21090PIVLv1.0";
  public static final String ISO21090PQRV1 = "ISO21090PQRv1.0";
  public static final String ISO21090QSCV1 = "ISO21090QSCv1.0";
  public static final String ISO21090QSDV1 = "ISO21090QSDv1.0";
  public static final String ISO21090QSETV1 = "ISO21090QSETv1.0";
  public static final String ISO21090QSIV1 = "ISO21090QSIv1.0";
  public static final String ISO21090QSPV1 = "ISO21090QSPv1.0";
  public static final String ISO21090QSSV1 = "ISO21090QSSv1.0";
  public static final String ISO21090QSUV1 = "ISO21090QSUv1.0";
  public static final String ISO21090SCNTV1 = "ISO21090SCNTv1.0";
  public static final String ISO21090SCV1 = "ISO21090SCv1.0";
  public static final String ISO21090SLISTV1 = "ISO21090SLISTv1.0";
  public static final String ISO21090STNTV1 = "ISO21090STNTv1.0";
  public static final String ISO21090STRDCCMCTV1 = "ISO21090StrDcCMCtv1.";
  public static final String ISO21090STRDCCMFTNTV = "ISO21090StrDcCMFtntv";
  public static final String ISO21090STRDCCMGV1 = "ISO21090StrDcCMGv1.0";
  public static final String ISO21090STRDCCMINLV1 = "ISO21090StrDcCMInlv1";
  public static final String ISO21090STRDCCMTITV1 = "ISO21090StrDcCMTitv1";
  public static final String ISO21090STRDCCLGPV1 = "ISO21090StrDcClGpv1.";
  public static final String ISO21090STRDCCONTV1 = "ISO21090StrDcContv1.";
  public static final String ISO21090STRDCFTNRFV1 = "ISO21090StrDcFtnRfv1";
  public static final String ISO21090STRDCFTNV1 = "ISO21090StrDcFtnv1.0";
  public static final String ISO21090STRDCITEMV1 = "ISO21090StrDcItemv1.";
  public static final String ISO21090STRDCLHTMLV1 = "ISO21090StrDcLHtmlv1";
  public static final String ISO21090STRDCLENV1 = "ISO21090StrDcLenv1.0";
  public static final String ISO21090STRDCLISTV1 = "ISO21090StrDcListv1.";
  public static final String ISO21090STRDCPARGV1 = "ISO21090StrDcPargv1.";
  public static final String ISO21090STRDCRDMMV1 = "ISO21090StrDcRdMMv1.";
  public static final String ISO21090STRDCSUBV1 = "ISO21090StrDcSubv1.0";
  public static final String ISO21090STRDCTCELV1 = "ISO21090StrDcTCelv1.";
  public static final String ISO21090STRDCTRGRPV1 = "ISO21090StrDcTRGrpv1";
  public static final String ISO21090STRDCTRPTV1 = "ISO21090StrDcTRPtv1.";
  public static final String ISO21090STRDCTRV1 = "ISO21090StrDcTRv1.0";
  public static final String ISO21090STRDCTBLEIT1 = "ISO21090StrDcTbleIt1";
  public static final String ISO21090STRDCTBLEV1 = "ISO21090StrDcTblev1.";
  public static final String ISO21090STRDCTCELLV1 = "ISO21090StrDcTcellv1";
  public static final String ISO21090STRDCTITFTV1 = "ISO21090StrDcTitFtv1";
  public static final String ISO21090STRDOCBAV1 = "ISO21090StrDocBav1.0";
  public static final String ISO21090STRDOCBRV1 = "ISO21090StrDocBrv1.0";
  public static final String ISO21090STRDOCCAPV1 = "ISO21090StrDocCapv1.";
  public static final String ISO21090STRDOCCLITV1 = "ISO21090StrDocClItv1";
  public static final String ISO21090STRDOCCOLV1 = "ISO21090StrDocColv1.";
  public static final String ISO21090STRDOCSUPV1 = "ISO21090StrDocSupv1.";
  public static final String ISO21090STRDOCTEXTV1 = "ISO21090StrDocTextv1";
  public static final String ISO21090STRDOCTITV1 = "ISO21090StrDocTitv1.";
  public static final String ISO21090TELEMAILV1 = "ISO21090TELEMAILv1.0";
  public static final String ISO21090TELPERSV1 = "ISO21090TELPERSv1.0";
  public static final String ISO21090TELPHONV1 = "ISO21090TELPHONv1.0";
  public static final String ISO21090TSBIRTHV1 = "ISO21090TSBIRTHv1.0";
  public static final String ISO21090TSDATEV1 = "ISO21090TSDATEv1.0";
  public static final String ISO21090TSDTTIFLV1 = "ISO21090TSDTTIFLv1.0";
  public static final String ISO21090URGHIGHV1 = "ISO21090URGHIGHv1.0";
  public static final String ISO21090URGLOWV1 = "ISO21090URGLOWv1.0";
  public static final String ISO21090UVPV1 = "ISO21090UVPv1.0";
  public static final String MUTABLETREENOTE = "MutableTreeNote";
  public static final String SVG = "SVG";
  public static final String STRING_ARRAY = "String Array";
  public static final String TABLE = "TABLE";
  public static final String EVS_DOMAIN_PROPERTY = "evs.domain.Property";
  public static final String EVS_DOMAIN_ROLE = "evs.domain.Role";
  public static final String EVS_DOMAIN_SOURCE = "evs.domain.Source";
  public static final String EVS_DOMAIN_TREENODE = "evs.domain.TreeNode";
  public static final String JAVA_LANG_STRING = "java.lang.String[][]";
  public static final String JAVA_LANG_VOID = "java.lang.Void";
  public static final String JAVA_MATH_BIGDECIMAL = "java.math.BigDecimal";
  public static final String JAVA_SQL_BLOB = "java.sql.Blob";
  public static final String JAVA_SQL_CLOB = "java.sql.Clob";
  public static final String JAVA_UTIL_ARRAYLIST = "java.util.ArrayList";
  public static final String JAVA_UTIL_HASHSET = "java.util.HashSet";
  public static final String JAVA_UTIL_HASHTABLE = "java.util.Hashtable";
  public static final String JAVA_UTIL_SET = "java.util.Set";
  public static final String JAVA_UTIL_VECTOR = "java.util.Vector";
  public static final String XSD_ID_THEN_XSD_ID = "xsd:ID' THEN 'xsd:ID'";
  public static final String XSD_IDREF_THEN_XSD_IDREF = "xsd:IDREF' THEN 'xsd:IDREF'";
  public static final String XSD_BASE64BINARY = "xsd:base64Binary";
  public static final String XSD_BOOLEAN_THEN_XSD_BOOLEAN = "xsd:boolean' THEN 'xsd:boolean'";
  public static final String XSD_BYTE_THEN_XSD_BYTE = "xsd:byte' THEN 'xsd:byte'";
  public static final String XSD_DATE_THEN_XSD_DATE = "xsd:date' THEN 'xsd:date'";
  public static final String XSD_DATETIME_THEN_XSD_DATETIME = "xsd:dateTime' THEN 'xsd:dateTime'";
  public static final String XSD_DECIMAL_THEN_XSD_DECIMAL = "xsd:decimal' THEN 'xsd:decimal'";
  public static final String XSD_DOUBLE_THEN_XSD_DOUBLE = "xsd:double' THEN 'xsd:double'";
  public static final String XSD_FLOAT_THEN_XSD_FLOAT = "xsd:float' THEN 'xsd:float'";
  public static final String XSD_INT_THEN_XSD_INT = "xsd:int' THEN 'xsd:int'";
  public static final String XSD_INTEGER_THEN_XSD_INTEGER = "xsd:integer' THEN 'xsd:integer'";
  public static final String XSD_LONG_THEN_XSD_LONG = "xsd:long' THEN 'xsd:long'";
  public static final String XSD_SHORT_THEN_XSD_SHORT = "xsd:short' THEN 'xsd:short'";
  public static final String XSD_STRING_THEN_XSD_STRING = "xsd:string' THEN 'xsd:string'";
  public static final String XSD_TIME_THEN_XSD_TIMEPUBLIC_STATIC_FINAL_STRING_XXX = "xsd:time' THEN 'xsd:time'public " +
      "static final String XXX = \"";

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

  // Datatypes added on July 2020. NCI suggested to temporarily map them directly to String
  public static final List<String> ADDITIONAL_STRING_LIST = Lists.newArrayList(
      BINARY_OBJECT, BRIDG_SET_V21, HL7ADV3, HL7ANYV3, HL7BAGADV3, HL7BAGTELV3, HL7BLV3, HL7DSETCDV3, HL7DSETCRV3,
      HL7DSETENV3, HL7DSETIDV3, HL7DSETOIDV3, HL7DSETONV3, HL7DSETSCV3, HL7DSETSTV3, HL7DSETTELURLV3, HL7EXPRPQV3,
      HL7IDV3, HL7IIV3, HL7INTNONNEGV3, HL7INTPOSV3, HL7IVLEXPRTSDATETIME, HL7IVLINTV3, HL7IVLPQV3,
      HL7IVLTSDATEFULLV3, HL7IVLTSDATETIMEV3, HL7IVLTSDATEV3, HL7OIDV3, HL7PPDV30, HL7PQTIMEV3, HL7PQV3,
      HL7RTOINTNONNEGINTPO, HL7RTOINTNONNEGPQTIM, HL7RTOPQPQTIMEV3, HL7RTOPQPQV3, HL7SCV3, HL7STSIMPLEV3, HL7TELURLV3
      , HL7TNV3, HL7TSDATEFULLV3, HL7TSDATETIMEV3, HL7URGINTNONNEGvV3, HL7URGINTPOSV3, HL7URGPQTIMEV3, HL7URGPQV3,
      ISO21090ADXPBNNV1, ISO21090ADXPBNRV1, ISO21090ADXPBNSV1, ISO21090ADXPBRV1, ISO21090ADXPCARV1, ISO21090ADXPCENV1
      , ISO21090ADXPCPAV1, ISO21090ADXPDIAAV1, ISO21090ADXPDIQV1, ISO21090ADXPDIRV1, ISO21090ADXPDISTV1,
      ISO21090ADXPDMDV1, ISO21090ADXPDMIDV1, ISO21090ADXPINTV1, ISO21090ADXPPOBV1, ISO21090ADXPPREV1,
      ISO21090ADXPSALV1, ISO21090ADXPSTBV1, ISO21090ADXPSTRV1, ISO21090ADXPSTTPV1, ISO21090ADXPUNIDV1,
      ISO21090ADXPUNTV1, ISO21090BLNTNULV1, ISO21090CDBASEV1, ISO21090CDCVV1, ISO21090CDV1, ISO21090COLLV1,
      ISO21090COV1, ISO21090CSV1, ISO21090EDDCINLINV1, ISO21090EDDCREFV1, ISO21090EDIMAGEV1, ISO21090EDSIGNAT1,
      ISO21090EDSTRTITV1, ISO21090EDSTRTXV1, ISO21090EIVLV1, ISO21090ENV1, ISO21090GLISTV1, ISO21090GTSBPIVLV1,
      ISO21090HISTV1, ISO21090HXITV1, ISO21090IVLHIGHV1, ISO21090IVLLOWV1, ISO21090IVLWIDV1, ISO21090LISTV1,
      ISO21090MOV1, ISO21090NPPDV1, ISO21090PIVLV1, ISO21090PQRV1, ISO21090QSCV1, ISO21090QSDV1, ISO21090QSETV1,
      ISO21090QSIV1, ISO21090QSPV1, ISO21090QSSV1, ISO21090QSUV1, ISO21090SCNTV1, ISO21090SCV1, ISO21090SLISTV1,
      ISO21090STNTV1, ISO21090STRDCCMCTV1, ISO21090STRDCCMFTNTV, ISO21090STRDCCMGV1, ISO21090STRDCCMINLV1,
      ISO21090STRDCCMTITV1, ISO21090STRDCCLGPV1, ISO21090STRDCCONTV1, ISO21090STRDCFTNRFV1, ISO21090STRDCFTNV1,
      ISO21090STRDCITEMV1, ISO21090STRDCLHTMLV1, ISO21090STRDCLENV1, ISO21090STRDCLISTV1, ISO21090STRDCPARGV1,
      ISO21090STRDCRDMMV1, ISO21090STRDCSUBV1, ISO21090STRDCTCELV1, ISO21090STRDCTRGRPV1, ISO21090STRDCTRPTV1,
      ISO21090STRDCTRV1, ISO21090STRDCTBLEIT1, ISO21090STRDCTBLEV1, ISO21090STRDCTCELLV1, ISO21090STRDCTITFTV1,
      ISO21090STRDOCBAV1, ISO21090STRDOCBRV1, ISO21090STRDOCCAPV1, ISO21090STRDOCCLITV1, ISO21090STRDOCCOLV1,
      ISO21090STRDOCSUPV1, ISO21090STRDOCTEXTV1, ISO21090STRDOCTITV1, ISO21090TELEMAILV1, ISO21090TELPERSV1,
      ISO21090TELPHONV1, ISO21090TSBIRTHV1, ISO21090TSDATEV1, ISO21090TSDTTIFLV1, ISO21090URGHIGHV1, ISO21090URGLOWV1
      , ISO21090UVPV1, MUTABLETREENOTE, SVG, STRING_ARRAY, TABLE, EVS_DOMAIN_PROPERTY, EVS_DOMAIN_ROLE,
      EVS_DOMAIN_SOURCE, EVS_DOMAIN_TREENODE, JAVA_LANG_STRING, JAVA_LANG_VOID, JAVA_MATH_BIGDECIMAL, JAVA_SQL_BLOB,
      JAVA_SQL_CLOB, JAVA_UTIL_ARRAYLIST, JAVA_UTIL_HASHSET, JAVA_UTIL_HASHTABLE, JAVA_UTIL_SET, JAVA_UTIL_VECTOR,
      XSD_ID_THEN_XSD_ID, XSD_IDREF_THEN_XSD_IDREF, XSD_BASE64BINARY, XSD_BOOLEAN_THEN_XSD_BOOLEAN,
      XSD_BYTE_THEN_XSD_BYTE, XSD_DATE_THEN_XSD_DATE, XSD_DATETIME_THEN_XSD_DATETIME, XSD_DECIMAL_THEN_XSD_DECIMAL,
      XSD_DOUBLE_THEN_XSD_DOUBLE, XSD_FLOAT_THEN_XSD_FLOAT, XSD_INT_THEN_XSD_INT, XSD_INTEGER_THEN_XSD_INTEGER,
      XSD_LONG_THEN_XSD_LONG, XSD_SHORT_THEN_XSD_SHORT, XSD_STRING_THEN_XSD_STRING,
      XSD_TIME_THEN_XSD_TIMEPUBLIC_STATIC_FINAL_STRING_XXX);

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
    ALL_STRING_LIST.addAll(ADDITIONAL_STRING_LIST);
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
  public static final List<String> INPUT_TIME_FORMAT_24H_FORMATS = Lists.newArrayList("TIME (HR(24):MN)");
  public static final List<String> INPUT_TIME_FORMAT_AMPM_FORMATS = Lists.newArrayList();

}
















