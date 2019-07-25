# Using the framework with a 3rd-party RTI implementation
In most cases you will want to use the IVCT-Framework together with a RTI-implementation, e.g. the Pitch RTI.
Since such an implementation can not be included in this open-source project, you will have to install that RTI by yourself and afterwards do some adjustments to the configuration of the IVCT_Framework.
These adjustments are described below.

## Removing the IEEE1516e project/library from the classpath
Since the IEEE1516e project inside the IVCT_Framework acts as a placeholder for the IEEE1516e classes delivered with most of the RTI implementations, you will have to remove this project or the generated library from the classpath.
If you are using eclipse, you will have to go to the "Run configurations" settings to do that.

### Remarks for setting up the eclipse launch configurations

Unfortunately you cannot simply remove the IEEE1516e project from the default classpath of a launch configuration. You have to remove the default configuration instead and (re-)add all other projects and libraries except IEEE1516e manually (via "add Project" and "add external jar"). We suggest to expand the default configuration first and then add the needed projects and jars listed in that default configuration (except the IEEE1516e project/jar) and as the last step remove the default configuration. By doing so, you will be able to check if you have forgotten something.
(see [[launch configuration screenshots|lc_screenshots]])

## Adding the necessary RTI libraries to the classpath
The required *.jar files for the RTI have to be set in the $(LRC_CLASSPATH) environment variable.

An example for the Pitch RTI is:

    set LRC_CLASSPATH=C:\Program Files\prti1516e\lib\prti1516e.jar
