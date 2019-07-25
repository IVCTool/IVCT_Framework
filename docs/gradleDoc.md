# Building the IVCT_framework with gradle

The decision to use gradle is described in more detail in [["The decision to choose gradle"|https://github.com/MSG134/LoggingTests/wiki/The-decision-to-choose-gradle]] inside the Loggingtests repository.

## Using Gradle Wrapper
By using the gradle Wrapper for the IVCT_Framework there is no need to install gradle on your own since the wrapper will automatically download and setup the required version of gradle for building the framework. To be able to do this, the wrapper has to access the internet. If there are any proxies, be sure to adjust the proxy configuration.

## Required Tools

The following describes using gradle with Java and Eclipse. The minimum versions required are Java 8 and Eclipse Mars. These tools should be installed before continuing with the gradlew commands.

## Used gradle tasks for IVCT_Framework
All of the following tasks should be executed in a commandline window opened in the root folder of the repository.

* `gradlew clean`  : clean up (remove compiled classes, jars, distributions)
* `gradlew cleanEclipse`  : clean up eclipse specific files (.settings folder, .classpath, etc.)
* `gradlew eclipse`   : create eclipse project files
* `gradlew compileJava`   : compile all projects
* `gradlew install`   : build artifacts for all projects and deploy them to the local maven repository (normally located inside the .m2 folder of the user profile)
* `gradlew installDist`   : build distributions for running the Framework without eclipse. The zip files will be stored in the `<projectname>/build/distributions` folder of each project
* `gradlew run`   : run the main class of each project defined in the project's gradle configuration. Since all those classes share the same commandline window for logging, this is probably not the best idea.

## Necessary tasks
If you want to use eclipse for working with the projects, then you should use `gradlew eclipse` to let gradle create the eclipse projects and afterwards import them into eclipse. This is done by using  `import / General / Existing Projects into Workspace`  for the IVCT_Frameframe and e.g. TS_HelloWorld folders where these have been checked out.

If you want to compile the example testsuites, then you have to use `gradlew install` because the projects from the other repositories will search for the IVCT_framework artifacts inside the local maven repository on your computer.
