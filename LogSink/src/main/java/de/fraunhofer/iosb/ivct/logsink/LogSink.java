package de.fraunhofer.iosb.ivct.logsink;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.Factory;


/**
 * Log sink for logging events collected via JMS or AMQP. For JMS use the
 * already existing JMSTopicSink from logback as first attempt.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class LogSink {

    private static Logger LOGGER     = LoggerFactory.getLogger(LogSink.class);
    private Properties    properties = new Properties();
    //private JMSTopicSink  jmsTopicSink;
    private JMSLogSink  jmsLogSink;


    /**
     * Main Method of the class.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        MDC.put("testcase", "LogSink");
        LOGGER.info("in main");
        final ReportEngine reportEngine = new ReportEngine();
		Factory.initialize();
		(new CmdStartTestResultListener(reportEngine)).execute();
		(new CmdQuitListener(reportEngine)).execute();
        final LogSink instance = new LogSink();
        instance.loadProperties();
        instance.init();
        reportEngine.tcListener = instance.jmsLogSink;
        instance.execute();
        System.exit(0);;
    }


    /**
     * load the properties from the file LogSink.properties in the default
     * package
     */
    protected void loadProperties() {
        final InputStream in = this.getClass().getResourceAsStream("/LogSink.properties");
        try {
            this.properties.load(in);
            in.close();
        }
        catch (final IOException ex) {
            LOGGER.error("Could not load properties. ", ex);
        }
    }


    /**
     * initialize the LogSink.
     */
    protected void init() {
        final String tcfBindingName = this.properties.getProperty("logsink.tcf.bindingname");
        final String topicBindingName = this.properties.getProperty("logsink.topic.bindingname");
        final String username = this.properties.getProperty("logsink.user");
        final String password = this.properties.getProperty("logsink.password");
        this.jmsLogSink = new JMSLogSink(tcfBindingName, topicBindingName, username, password);
    }


    /**
     * execute = do wait for termination.
     */
    protected void execute() {
        LOGGER.debug("successfully initialized for topic {} .", this.properties.getProperty("java.naming.provider.url"));
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        // Loop until "exit", "quit" or "q" is typed
        LOGGER.info("Type \"quit\" to exit JMSTopicSink.");
        while (true) {
            String s;
            try {
                s = stdin.readLine();
                if (s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) {
                	LOGGER.info("Exiting. Kill the application if it does not exit " + "due to daemon threads.");
                    return;
                }
            }
            catch (final IOException ex) {
                LOGGER.error("Error in input.", ex);
            }
        }
    }
}
