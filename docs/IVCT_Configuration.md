# Introduction

After checking out the IVCT and test suites out from GitHub, and using gradle build, the IVCT needs some information to adapt to the local environment. There are several options to provide this information. 

## Environment Variables

The user may provide initialisation information via environment variables.

## Default Settings

For each configuration parameter, there is a default setting encoded in the IVCT software.

## Properties file

The IVCT user interfaces (UI and GUI) are using a property file called IVCT.properties to configure the location of the definition files. An example of this file looks like:


    #IVCT Properties File
    #Mon May 08 14:06:01 CEST 2017
    IVCT_TS_HOME_ID=C\:/MSG134/DemoFolders/IVCTtestSuites
    IVCT_SUT_HOME_ID=C\:/MSG134/DemoFolders/IVCTsut
    IVCT_BADGE_HOME_ID=C\:/MSG134/DemoFolders/Badges
    java.naming.factory.initial=org.apache.activemq.jndi.ActiveMQInitialContextFactory
    logsink.user=
    logsink.password=
    messaging.user=admin
    messaging.password=password
    messaging.host=localhost
    RTI_ID=pRTI
    logsink.tcf.bindingname=ConnectionFactory
    logsink.topic.bindingname=dynamicTopics/LogTopic.jms
    java.naming.provider.url=tcp\://localhost\:61616
    messaging.port=61616
    jmstestrunner.queue=commands

Please note, if you want to use the Windows style file separator '\\', you need to use the escape char (e.g. C:\\\\IVCT_Runtime\\\\Badges).

See [IVCT_Runtime](https://github.com/MSG134/IVCT_Runtime) for more information on the following

RTI_ID is a nickname for the RTI being used

The IVCT_SUT_HOME_ID is the folder where the SUT descriptions are located.

The IVCT_TS_HOME_ID is the folder where the IVCT test suites are installed.

The IVCT_BADGE_HOME_ID is the folder where the badges are located.

## Resolve Policy

The initialization procedure will start with the build in default values. After that the system will check if there the IVCT_CONF environment variable is defined. If this variable referes to a file, then this file will be used overwrite the default values. If the value referes to a folder, then the default file name 'IVCT.properties' will be used to read the properties file. Finally the system will try to read each of above mentioned parameters from the environment. If environment variables are found, they will be used to overwrite the parameters. 
