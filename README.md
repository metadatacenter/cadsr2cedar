CEDAR caDSR Tools
=================

This project contains command-line tools to perform the following actions:
- Download XML-encoded [caDSR](https://wiki.nci.nih.gov/display/caDSR/caDSR+Wiki) [11179-based](http://metadata-standards.org/11179/) common data elements (CDEs) from NCI's caDSR FTP servers.
- Download XML-encoded caDSR contexts, classification schemes, and classification scheme items from NCI's caDSR FTP servers.
- Transform CDEs to CEDAR CDE fields.
- Transform caDSR contexts, classification schemes, and classification scheme items to CEDAR categories.
- Upload CEDAR CDE fields to the CEDAR system.
- Upload CEDAR categories to the CEDAR system.
- Attach CEDAR CDE fields to CEDAR caDSR categories.

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

## Usage:

    mvn exec:java@cedar-cadsr-updater -Dexec.args="[options]"

where the available options are:

usage: mvn exec:java@cadsr-updater -Dexec.args="[options]"
Options:
 -c,--update-cdes                   Update CEDAR CDEs and attach them to
                                    the corresponding CEDAR categories
 -d,--delete-categories             Delete existing CEDAR caDSR categories
                                    (excluding its root)
 -e,--ftp-cdes-folder <arg>         caDSR FTP CDEs working directory
 -E,--cdes-file <arg>               caDSR XML CDEs .zip file path
 -f,--folder <arg>                  [REQUIRED] Identifier of the CEDAR
                                    folder where the CDEs will be stored
 -g,--ftp-categories-folder <arg>   caDSR FTP categories working directory
 -G,--categories-file <arg>         caDSR XML Categories .zip file path
 -h,--ftp-host <arg>                caDSR FTP host
 -k,--apikey <arg>                  [REQUIRED] API key of CEDAR's caDSR
                                    Admin user
 -o,--ontology-folder <arg>         Path to the folder the CADSR-VS
                                    ontology will be saved in
 -p,--ftp-password <arg>            caDSR FTP password
 -s,--server <arg>                  [REQUIRED] Target CEDAR server.
                                    Possible values: local, staging,
                                    production
 -t,--update-categories             Update CEDAR categories
 -u,--ftp-user <arg>                caDSR FTP user name
 -x,--cadsr-exec-folder <arg>       Path to a local folder with temporal
                                    files used during execution. The
                                    folder will be removed after execution

- `/path/to/input` is the input caDSR categories file. The categories file used can be found in the folder [src/main/resources/files-used/categories](https://github.com/metadatacenter/cedar-cadsr-tools/tree/develop/src/main/resources/files-used/categories).
- `/path/to/output` is the directory location to store the category tree generated.

## 2. Upload JSON category tree to CEDAR

    mvn exec:java@upload-categories -Dexec.args="/path/to/input parent-category-id target-server 'cedar-apikey'"

where:
- `/path/to/input` is the path to the JSON file with the category tree generated in the previous step.
- `parent-category-id` is the full CEDAR identifier of the CEDAR category that will be used as the root of the tree that will be uploaded (e.g., `https://repo.staging.metadatacenter.org/categories/ec211045-6881-4094-898e-96b0d2f4329a`).
- `target-server` is the target CEDAR server. The options are "local", "staging", and "production".
- `cedar-apikey` is the CEDAR API key that will give the permission to upload files to the server (e.g., `'apiKey 0000111122223333444455556666777788889999aaaabbbbccccddddeeeeffff'`). Note that the user associated to this api key will need to have permissions to create CEDAR categories.

## 3. Transform XML caDSR CDEs to CEDAR fields, upload them to CEDAR, and (optionally) attach them to CEDAR categories 

    mvn exec:java@upload-cdes -Dexec.args="/path/to/input target-server folder-id 'cedar-apikey' [-a]"

where:
- `/path/to/input` is the input [caDSR XML file](https://wiki.nci.nih.gov/display/caDSR/caDSR+Hosted+Data+Standards%2C+Downloads%2C+and+Transformation+Utilities) location in your local machine, which can be either a directory or a file. A sample caDSR XML file is available in the folder [src/main/resources/files-used/cdes](https://github.com/metadatacenter/cedar-cadsr-tools/blob/develop/src/main/resources/files-used/cdes/xml_cde_20198153730.zip).
- `target-server` is the CEDAR server types and the options are "local", "staging", "production".
- `folder-id` is the short identifier of the CEDAR folder where the CDEs will be stored (e.g., `4aabbfc6-f953-4779-b667-e0d6a1234ce8`).
- `cedar-apikey` is your CEDAR API key that will give the permission to upload files to the server (e.g., `'apiKey 0000111122223333444455556666777788889999aaaabbbbccccddddeeeeffff'`).
- `-a` is an option to attach the uploaded CDEs to the corresponding CEDAR categories.

## 4. Transform XML caDSR CDEs to CEDAR fields (optional, since it is part of #3) 
   
    mvn exec:java@transform-cdes -Dexec.args="/path/to/input /path/to/output"

where:
- `/path/to/input` is the input [caDSR XML file](https://wiki.nci.nih.gov/display/caDSR/caDSR+Hosted+Data+Standards%2C+Downloads%2C+and+Transformation+Utilities) location in your local machine, which can be either a directory or a file.
- `/path/to/output` is the directory location to store the output files.
    
