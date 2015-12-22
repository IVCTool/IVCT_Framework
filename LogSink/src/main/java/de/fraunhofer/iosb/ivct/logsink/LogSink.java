package de.fraunhofer.iosb.ivct.logsink;

import ch.qos.logback.classic.net.JMSTopicSink;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Log sink for logging events collected via JMS or AMQP. For JMS use the
 * already existing JMSTopicSink from logback as first attempt.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class LogSink {

    private static Logger LOGGER     = LoggerFactory.getLogger(LogSink.class);
    private Properties    properties = new Properties();
    private JMSTopicSink  jmsTopicSink;


    /**
     * Main Method of the class.
     *
     * @param args command line arguments
     */
    public static void main(final String[] args) {
        // TODO Auto-generated method stub
        LOGGER.info("in main");
        final LogSink instance = new LogSink();
        instance.loadProperties();
        instance.init();
        instance.execute();
    }


    /**
     * load the properties from the file LogSink.properties in the default
     * package
     */
    private void loadProperties() {
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
    private void init() {
        final String tcfBindingName = this.properties.getProperty("logsink.tcf.bindingname");
        final String topicBindingName = this.properties.getProperty("logsink.topic.bindingname");
        final String username = this.properties.getProperty("logsink.user");
        final String password = this.properties.getProperty("logsink.password");
        this.jmsTopicSink = new JMSTopicSink(tcfBindingName, topicBindingName, username, password);
    }


    /**
     * execute = do wait for termination.
     */
    private void execute() {
        LOGGER.debug("successfully initialized for topic {} .", this.properties.getProperty("java.naming.provider.url"));
        final BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));
        // Loop until the word "exit" is typed
        System.out.println("Type \"exit\" to quit JMSTopicSink.");
        while (true) {
            String s;
            try {
                s = stdin.readLine();
                if (s.equalsIgnoreCase("exit")) {
                    System.out.println("Exiting. Kill the application if it does not exit " + "due to daemon threads.");
                    return;
                }
            }
            catch (final IOException ex) {
                LOGGER.error("Error in input.", ex);
            }
        }
    }
}
