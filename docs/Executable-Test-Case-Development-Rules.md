These rules were discussed between nations on 18 October 2016.

# RTI compatibility
The ETC must be developed and tested with both Pitch and MÄK RTI, last versions if possible.  
Some specific libraries (RTI adapters) will be developed in integrated in IVCT. ETC development must be done upon those RTI adapters.

# Overall ETC structure  

## GitHub project name / Java package project name / Class names
GitHub Repository name: TC_[ETC name]  
Ex.: `TC_CS_Verification`

Java main package name: nato.ivct.etc.tc_[ETC name].  
Ex.: `nato.ivct.etc.tc_cs_verification` or `nato.ivct.etc.fr.tc_hla_declaration_management`

Main class names: TC_XXXX_[meaningful name].java.  
N.B. : [meaningful name] is meaningful in the context of ETC, and is different of [ETC name] to avoid redundancy.  
Ex.: For ETC HLA Declaration Management, `TC_0001_Object_Publication.java` or `TC_0002_Interaction_Subscription.java`

Java model and parameters package name: nato.ivct.etc.tc_**lib_**[ETC name].  
Ex.: `nato.ivct.etc.tc_lib_cs_verification` or `nato.ivct.etc.fr.tc_lib_hla_declaration_management`

ETC model and parameters classes names: TC_[ETC name]__BaseModel.java and TC_[ETC name]_TcParam.java.  
Ex.: `TC_CS_Verification_BaseModel.java` or `TC_CS_Verification_TcParam.java`

**Warning:** The full name of a class may be very long.  
Ex.: `nato.ivct.etc.tc_hla_declaration_management.TC_0001_Object_Publication`

## 3rd party libraries
Because one of the main problem with Java development is the management of dependencies (i.e. librairies), _**all new development (new ETC) must use already existing libraries**_. It will avoid some inconsistency and incompatibility between codes developed by different developers that may use different versions of the same library.
All 3rd party libraries must be declared in ETC Gradle files, as it is done in HelloWorld example.     
**Question:** Where do new 3rd party libraries must be dropped off? It seems that Gradle manages some cache.    
**Question:** How to have access to the libraries list that are already used by developed ETC? Who is responsible for the management of that list?  

# Development best practices
## Test Case Parameters
It is mandatory to use JSON file to define parameter values.  
3 steps in the initialization of Test Case Parameters (file `TC_[ETC name]_TcParam.java`) :  
  1. Declaration with a NULL value  
  2. Assignation to the value read using JSON parser  
  3. Systematic check to detect invalid value  

## Test Case Variables
It is mandatory to manage Test Case Variables in the model source code (file `TC_[ETC name]_BaseModel.java`).

## Logging

It is mandatory to use of the logger implementation (lockback) delivered by the framework.  
Lockback configuration file: `[ETC name]/src/main/resources/logback.xml`.  
There is 2 appenders: FILE and STDOUT.  
There is currently a technical limitation that does not allow to place the output of a test case into a test case specific file. So, in order to facilitate the reading of the log file :  
* At the beginning of each text case, the following specific message is logged:  
`*** Start of tc_[ETC name].TC_XXXX_[meaningful name] ***`  
Ex.: `*** Start of tc_hla_declaration_management.TC_0001_Object_Publication ***`
* At the beginning of each text case, the following specific message is logged:  
`*** End of tc_[ETC name].TC_XXXX_[meaningful name] ***`  
Ex.: `*** End of tc_hla_declaration_management.TC_0001_Object_Publication ***`

## GUI
No GUI in the ETC, only parameters.

## Results
The result of an ETC must be the following:  
* INCONCLUSIVE: Corresponds to an error during initialisation (preambleAction method) or termination (postambleAction method) of the test. It is equivalent to FAILED  
* FAILED: An error / a problem occurred during the execution of the ETC that leads to a failure  
* PASSED: No error / problem occurred during the execution of the ETC, the test is passed   

The ATC must be very clear and explicit on success condition.
