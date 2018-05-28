package de.fraunhofer.iosb.messaginghelpers;

import java.util.Properties;
import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * helper class for setting up JMS topics and Queues according to the entries of
 * the given properties instance.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public final class PropertyBasedClientSetup {

    private static Logger             LOGGER            = LoggerFactory.getLogger(PropertyBasedClientSetup.class);

    /**
     * properties key for username for JMS connection.
     */
    public static final String        PROPERTY_USER     = "messaging.user";
    /**
     * properties key for password for JMS connection
     */
    public static final String        PROPERTY_PASSWORD = "messaging.password";
    /**
     * properties key for hostname for JMS connection
     */
    public static final String        PROPERTY_HOST     = "messaging.host";
    /**
     * properties key for port number for JMS connection
     */
    public static final String        PROPERTY_PORT     = "messaging.port";

    private final Properties          properties;

    private String                    user;
    private String                    password;
    private String                    host;
    private int                       port;
    private ActiveMQConnectionFactory factory;
    private Session                   session;
    private State                     state;

    private Connection                connection;

    /**
     * State of the Helper Class
     *
     * @author sen (Fraunhofer IOSB)
     */
    public enum State {
        /**
         * Helper class has been created.
         */
        CREATED,
        /**
         * Properties have been parsed and Factory is initialized.
         */
        PROPERTIES_PARSED,
        /**
         * Connected to JMS broker
         */
        CONNECTED,
        /**
         * Active Session available.
         */
        SESSION_ACTIVE,
        /**
         * Disconnected from JMS broker.
         */
        DISCONNECTED,
        /**
         * Failure State.
         */
        FAILURE
    }


    /**
     * public constructor taking a {@link Properties} instance as argument.
     *
     * @param properties The properties instance to use for setup
     */
    public PropertyBasedClientSetup(final Properties properties) {
        this.properties = properties;
        this.state = State.CREATED;
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
    }


    /**
     * check if the current state is allowed. If state is not allowed, an
     * IllegalStateException is thrown
     *
     * @param allowedStates the allowed states to check against
     */
    private synchronized void checkAllowedState(final State... allowedStates) {
        boolean allowed = false;
        for (final State checkState: allowedStates) {
            if (this.state == checkState) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            throw new IllegalStateException(this.state.toString() + " is not allowed in this method.");
        }
    }


    /**
     * parse the properties and set up attributes.
     * @return true means failure
     */
    public synchronized boolean parseProperties() {
        this.checkAllowedState(State.CREATED, State.FAILURE);
        try {
            this.user = this.properties.getProperty(PROPERTY_USER, "admin");
            this.password = this.properties.getProperty(PROPERTY_PASSWORD, "password");
            this.host = System.getenv("ACTIVEMQ_HOST");
            if (this.host == null) {
                this.host = "localhost";
                LOGGER.warn("Environment variable ACTIVEMQ_HOST not found: using default ", this.host);
            }
            String portString;
            portString = System.getenv("ACTIVEMQ_PORT");
            if (portString == null) {
                portString = "61616";
                LOGGER.warn("Environment variable ACTIVEMQ_PORT not found: using default ", portString);
            }
            this.port = Integer.parseInt(portString);
            this.factory = new ActiveMQConnectionFactory("tcp://" + this.host + ":" + this.port);
            this.state = State.PROPERTIES_PARSED;
        }
        catch (final IllegalArgumentException iae) {
            LOGGER.error("Problems during parsing of properties.", iae);
            this.state = State.FAILURE;
            return true;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
        return false;
    }


    /**
     * initialize the JMS connection.
     * @return true means failure
     */
    public synchronized boolean initConnection() {
        this.checkAllowedState(State.PROPERTIES_PARSED, State.DISCONNECTED);
        boolean tryAgain = true;
        int count = 1;
        LOGGER.info("initConnection: connect to activemq please wait...");
        for (;tryAgain;) {
            try {
                this.connection = this.factory.createConnection(this.user, this.password);
                this.connection.start();
                tryAgain = false;
                this.state = State.CONNECTED;
            }
            catch (final JMSException ex) {
                if (count > 10) {
                    LOGGER.error("Problems during initializing connection.", ex);
                    tryAgain = false;
                    this.state = State.FAILURE;
                    LOGGER.error("initConnection: failed to connect to activemq.");
                    System.exit(-1);
                }
            }
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("State: {}", this.state.toString());
            }
            try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
                LOGGER.error("initConnection: sleep interrupted: ", e);
			}
			count++;
        }
        LOGGER.info("initConnection: connect to activemq OK");
        return false;
    }


    /**
     * disconnect from the JMS broker.
     * @return true means failure
     */
    public synchronized boolean disconnect() {
        this.checkAllowedState(State.CONNECTED, State.SESSION_ACTIVE);
        try {
        	this.connection.stop();
        	this.session.close();
            this.connection.close();
            this.state = State.DISCONNECTED;
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during disconnect.", ex);
            this.state = State.FAILURE;
            return true;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
        return false;
    }


    /**
     * Initialize JMS Session.
     * @return true means failure
     */
    public synchronized boolean initSession() {
        this.checkAllowedState(State.CONNECTED);
        try {
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.state = State.SESSION_ACTIVE;
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during initialization of Session.", ex);
            this.state = State.FAILURE;
            return true;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
        return false;
    }


    /**
     * Create A Consumer for the given Queue and set the given
     * {@link MessageListener}.
     *
     * @param destination The destination of the {@link Queue}
     * @param listener The {@link MessageListener} to set
     * @return true means failure
     */
    public synchronized boolean setupQueueListener(final String destination, final MessageListener listener) {
        this.checkAllowedState(State.SESSION_ACTIVE);
        final Destination dest = new ActiveMQQueue(destination);
        MessageConsumer consumer;
        try {
            consumer = this.session.createConsumer(dest);
            consumer.setMessageListener(listener);
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during setup of QueueListener.", ex);
            this.state = State.FAILURE;
            return true;
        }
        return false;
    }


    /**
     * Create A Consumer for the given Topic and set the given
     * {@link MessageListener}.
     *
     * @param topic The {@link Topic} to use
     * @param listener The {@link MessageListener} to set
     * @return true means failure
     */
    public synchronized boolean setupTopicListener(final String topic, final MessageListener listener) {
        this.checkAllowedState(State.SESSION_ACTIVE);
        final ActiveMQTopic top = new ActiveMQTopic(topic);
        MessageConsumer consumer;
        try {
            consumer = this.session.createConsumer(top);
            consumer.setMessageListener(listener);
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during setup of TopicListener.", ex);
            this.state = State.FAILURE;
            return true;
        }
        return false;
    }


    /**
     * Create a {@link MessageProducer} for a given destination queue.
     *
     * @param destination the {@link Queue} to use
     * @return a configured {@link MessageProducer} or null if there were errors
     */
    public synchronized MessageProducer setupQueueProducer(final String destination) {
        this.checkAllowedState(State.SESSION_ACTIVE);
        final Destination dest = new ActiveMQQueue(destination);
        MessageProducer producer = null;
        try {
            producer = this.session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during setup of QueueProducer.", ex);
            this.state = State.FAILURE;
        }

        return producer;
    }


    /**
     * Create a {@link MessageProducer} for a given destination queue.
     *
     * @param destination the {@link Queue} to use
     * @return a configured {@link MessageProducer} or null if there were errors
     */
    public synchronized MessageProducer setupTopicProducer(final String destination) {
        this.checkAllowedState(State.SESSION_ACTIVE);
        final Destination dest = new ActiveMQTopic(destination);
        MessageProducer producer = null;
        try {
            producer = this.session.createProducer(dest);
            producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during setup of TopicProducer.", ex);
            this.state = State.FAILURE;
        }

        return producer;
    }


    /**
     * create a {@link TextMessage}
     *
     * @param messageContent the content to use for the message
     * @return the created {@link TextMessage} or null if there were errors
     */
    public synchronized Message createTextMessage(final String messageContent) {
        this.checkAllowedState(State.SESSION_ACTIVE);
        TextMessage message = null;
        try {
            message = this.session.createTextMessage(messageContent);
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during Message creation.", ex);
            this.state = State.FAILURE;
        }
        return message;
    }
}
