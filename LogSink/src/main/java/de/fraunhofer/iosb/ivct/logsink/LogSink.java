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
        instance.init();
        reportEngine.tcListener = instance.jmsLogSink;
        instance.execute();
        System.exit(0);;
    }


    /**
     * initialize the LogSink.
     */
    protected void init() {
		final String tcfBindingName = Factory.props.getProperty(Factory.LOGSINK_TCF_BINDINGNAME_ID);
        final String topicBindingName = Factory.props.getProperty(Factory.LOGSINK_TOPIC_BINDINGNAME_ID);
        final String username = Factory.props.getProperty(Factory.LOGSINK_USER_ID);
        final String password = Factory.props.getProperty(Factory.LOGSINK_PASSWORD_ID);
        this.jmsLogSink = new JMSLogSink(tcfBindingName, topicBindingName, username, password);
    }


    /**
     * execute = do wait for termination.
     */
    protected void execute() {
        LOGGER.debug("successfully initialized for topic {} .", Factory.props.getProperty("java.naming.provider.url"));
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        // Loop until "exit", "quit" or "q" is typed
        LOGGER.info("Type \"quit\" to exit JMSTopicSink.");
        while (true) {
            String s;
            try {
                s = stdin.readLine();
                if(s == null) {
                	// ignore - probably running in a container so we don't have system in
                	// LogSink will be killed when the container is killed.
                }
                else if (s.equalsIgnoreCase("exit") || s.equalsIgnoreCase("q") || s.equalsIgnoreCase("quit")) {
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
