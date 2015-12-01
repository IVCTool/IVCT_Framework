package de.fraunhofer.iosb.testrunner;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import java.net.URL;
import org.slf4j.LoggerFactory;


/**
 * Helper class finding the correct logback configuration file for a given main
 * class.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class LogConfigurationHelper extends SecurityManager {

    private LogConfigurationHelper() {
        // empty on purpose
    }


    private Class<?> getCallerClass() {
        return this.getClassContext()[2];
    }


    /**
     * Set the correct logback configuration file based on the classname of the
     * caller class.
     */
    public static void configureLogging() {
        final LogConfigurationHelper helper = new LogConfigurationHelper();
        final Class<?> clazz = helper.getCallerClass();
        final String filename = "/" + clazz.getSimpleName() + "_logback.xml";
        final URL location = clazz.getResource(filename);
        final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        try {
            final JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(location);
        }
        catch (final JoranException je) {
            // do nothing, it will be handled by StatusPrinter
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

}
