# RTI compatibility
The ETC must be developed and tested with both Pitch and MÄK RTI, last versions if possible.  
**Warning:** France has tested the compilation and execution with MÄK RTI, and some adjustments are required.

# Overall ETC structure  

## GitHub project name / Java package project name / Class names
GitHub Repository name: Badge name.  
Ex.: `TS_HLA-BASE` (no year in name, it will be a release name)

Java main package name: nato.ivct.etc.[ETC country].tc_[ETC name].  
Ex. : `nato.ivct.etc.fr.tc_cs_verification` or `nato.ivct.etc.fr.tc_hla_declaration_management`

Main class names: TC_[ETC name]_XXXX.java.  
Ex.: `TC_CS_Verification_0001.java` or `TC_HLA_Declaration_Management_0001.java`

Java model and parameters package name: nato.ivct.etc.[ETC country].tc_**lib_**[ETC name].  
Ex. : `nato.ivct.etc.fr.tc_lib_cs_verification` or `nato.ivct.etc.fr.tc_lib_hla_declaration_management`

ETC model and parameters classes names: TC_[ETC name]__BaseModel.java and TC_[ETC name]_TcParam.java.  
Ex.: `TC_CS_Verification_BaseModel.java` or `TC_CS_Verification_TcParam.java`

**Consequence:** Several countries will contribute to the same GitHub repository, but with different Java packages (one country “owns” one package).

**Warning:** The full name of a class may be very long. Ex. : `nato.ivct.etc.fr.tc_hla_declaration_management.TC_HLA_Declaration_Management_0001`

## 3rd party libraries
Because one of the main problem with Java development is the management of dependencies (i.e. librairies), _**all new development (new ETC) must use already existing libraries**_. It will avoid some inconsistency and incompatibility between codes developed by different developers that may use different versions of the same library.
All 3rd party libraries must be declared in ETC Gradle files, as it is done in HelloWorld example.   
**Question:** Where do new 3rd party libraries must be dropped off? It seems that Gradle manages some cache.  
**Question:** How to have access to the libraries list that are already used by developed ETC? Who is responsible for the management of that list?

# Development best practices
## Test Case Parameters
3 steps in the initialization of Test Case Parameters (file `TC_[ETC name]_TcParam.java`) :  
  1. Declaration with a NULL value  
  2. Assignation to the value read using JSON parser  
  3. Systematic check to detect invalid value  

## Test Case Variables
Management of Test Case Variables in the model (file `TC_[ETC name]_BaseModel.java`).

## Logging
It is mandatory to use of the logger class delivered by the framework.  
**Question:** How to configure the log level? LogSink configuration files?  
**Question:** Why are the logs different between file and standard output?  
**Question:** Where do the log files are created? log/ directory of UI?

## GUI
No GUI in the ETC, only parameters.

