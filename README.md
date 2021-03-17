CEDAR caDSR Tools
=================

This project contains command-line tools to perform the following actions:

- caDSR CDEs:
    - Download XML-encoded [caDSR](https://wiki.nci.nih.gov/display/caDSR/caDSR+Wiki) [11179-based](http://metadata-standards.org/11179/) common data elements (CDEs) from NCI's caDSR FTP servers.
    - Download XML-encoded caDSR contexts, classification schemes, and classification scheme items from NCI's caDSR FTP servers.
    - Transform CDEs to CEDAR CDE fields.
    - Transform caDSR contexts, classification schemes, and classification scheme items to CEDAR categories.
    - Upload CEDAR CDE fields to the CEDAR system.
    - Upload CEDAR categories to the CEDAR system.
    - Attach CEDAR CDE fields to CEDAR caDSR categories.

- caDSR Forms:
    - Translate XML-encoded caDSR forms into CEDAR templates.

### Common Data Elements

The format of the caDSR CDEs is described by an [XML Schema document](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/xsd/cde/DataElement_V5.3.4.xsd).
The [JAXB](http://www.oracle.com/technetwork/articles/javase/index-140168.html) library uses this document to generate Java classes to read the XML-encoded instances of caDSR CDEs.

Similarly, the [jsonchema2pojo](http://www.jsonschema2pojo.org/) library uses a
[JSON Schema-encoded CEDAR CDE](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/resources/json-schema/CDE.json)
template to automatically generate Java classes that can serialize JSON instances conforming to this template.

The [core translation routines](https://github.com/metadatacenter/cadsr2cedar/blob/master/src/main/java/org/metadatacenter/ingestor/cadsr/CDEXMLInstances2CEDARCDEInstances.java)
extract information from the JAXB-generated Java objects and insert the information into the jsonschema2pojo-generated Java objects.
These latter objects are then serialized into CEDAR-conformant JSON instances.

Note that the [caDSR CDE XML Schema](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/xsd/cde/DataElement_V5.3.4.xsd)
was produced semi-automatically from the caDSR-supplied [DTD-encoded schema](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/dtd/DataElement_V5.3.4.dtd).
[JAXB bindings](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/xjb/bindings-cde.xjb)
were required to rename some generated classes and these bindings do not seem to work with DTD-based documents.

### Categories

The NCI provided us with [an XML file](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/files-used/categories/ContextCsCsi_09192019.xml) containing caDSR contexts, classification schemes, and classification items. The format of this file is specified by an [XML Schema document](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/xsd/category/ContextCsCsi_0923_mmr.xsd).

#### Building and Running

To build this library you must have the following items installed:

+ [Java 11 (or later)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
+ A tool for checking out a [Git](http://git-scm.com/) repository.
+ Apache's [Maven](http://maven.apache.org/index.html).

Get a copy of the latest code:

    git clone https://github.com/metadatacenter/cedar-cadsr-tools.git

Go to the cedar-cadsr-tools directory:

    cd cedar-cadsr-tools 

Then build it with Maven:

    mvn clean install

## Usage (CDEs updater):

    mvn exec:java@cedar-cadsr-updater -Dexec.args="[options]"

Options:
```
 -c,--update-cdes                   Update CEDAR CDEs and attach them to
                                    the corresponding CEDAR categories.
 -d,--delete-categories             Delete existing CEDAR caDSR categories
                                    (excluding its root).
 -e,--ftp-cdes-folder <arg>         caDSR FTP CDEs working directory.
 -E,--cdes-file <arg>               caDSR XML CDEs .zip file path.
 -f,--folder <arg>                  [REQUIRED] Identifier of the CEDAR
                                    folder where the CDEs will be stored.
 -g,--ftp-categories-folder <arg>   caDSR FTP categories working directory.
 -G,--categories-file <arg>         caDSR XML Categories .zip file path.
 -h,--ftp-host <arg>                caDSR FTP host.
 -k,--apikey <arg>                  [REQUIRED] API key of CEDAR's caDSR
                                    Admin user.
 -o,--ontology-folder <arg>         Path to the folder the CADSR-VS
                                    ontology will be saved in.
 -p,--ftp-password <arg>            caDSR FTP password.
 -s,--server <arg>                  [REQUIRED] Target CEDAR server.
                                    Possible values: local, staging,
                                    production.
 -t,--update-categories             Update CEDAR categories.
 -u,--ftp-user <arg>                caDSR FTP user name.
 -x,--cadsr-exec-folder <arg>       Path to a local folder with temporal
                                    files used during execution. The
                                    folder will be removed after execution.
```
Example of usage with CEDAR environment variables. In this case, the user wants to retrieve both classifications and CDEs from NCI's FTP servers and upload them to CEDAR.

```
mvn exec:java@cedar-cadsr-updater -Dexec.args="--update-categories --update-cdes --server local --folder ${CEDAR_CDE_FOLDER_ID} --apikey ${CEDAR_CADSR_ADMIN_USER_API_KEY} --ftp-host ${CEDAR_NCI_CADSR_FTP_HOST} --ftp-user ${CEDAR_NCI_CADSR_FTP_USER} --ftp-password ${CEDAR_NCI_CADSR_FTP_PASSWORD} --ftp-categories-folder ${CEDAR_NCI_CADSR_FTP_CLASSIFICATIONS_DIRECTORY} --ftp-cdes-folder ${CEDAR_NCI_CADSR_FTP_CDES_DIRECTORY} --ontology-folder ${CEDAR_CADSR_ONTOLOGIES_FOLDER}"
```
    
Example of usage with local classifications and CDE files. Note that the identifiers, paths, and API key used are just examples.

```
mvn exec:java@cedar-cadsr-updater -Dexec.args="--update-categories --update-cdes --server local --folder 03f2d7f0-a54c-4a37-a0a8-c53159ec4aab --apikey 8d1fdf56f8147054388432716b06e4dac940aa86b326d13e7bfceb17a9ec4b9c --categories-file /var/tmp/xml_cscsi_20205210521.zip --cdes-file /var/tmp/xml_cde_20205210558.zip --ontology-folder /var/tmp/ontology"
```

## Usage (Forms translation):

    mvn exec:java@cedar-cadsr-form-translate -Dexec.args="[options]"

Options:
```
 -f,--form <arg>   [REQUIRED] Path of the .xml file with the caDSR Form to
                   be imported into CEDAR.
```

Example of usage.
```
mvn exec:java@cedar-cadsr-form-translate -Dexec.args="--f /var/tmp/caDSRforms/form2.xml"
```
