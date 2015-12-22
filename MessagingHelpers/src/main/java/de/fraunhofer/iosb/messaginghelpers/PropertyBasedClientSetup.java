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
     */
    public synchronized void parseProperties() {
        this.checkAllowedState(State.CREATED, State.FAILURE);
        try {
            this.user = this.properties.getProperty(PROPERTY_USER, "admin");
            this.password = this.properties.getProperty(PROPERTY_PASSWORD, "password");
            this.host = this.properties.getProperty(PROPERTY_HOST, "localhost");
            final String portString = this.properties.getProperty(PROPERTY_PORT, "61616");
            this.port = Integer.parseInt(portString);
            this.factory = new ActiveMQConnectionFactory("tcp://" + this.host + ":" + this.port);
            this.state = State.PROPERTIES_PARSED;
        }
        catch (final IllegalArgumentException iae) {
            LOGGER.error("Problems during parsing of properties.", iae);
            this.state = State.FAILURE;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
    }


    /**
     * initialize the JMS connection.
     */
    public synchronized void initConnection() {
        this.checkAllowedState(State.PROPERTIES_PARSED, State.DISCONNECTED);
        try {
            this.connection = this.factory.createConnection(this.user, this.password);
            this.connection.start();
            this.state = State.CONNECTED;
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during initializing connection.", ex);
            this.state = State.FAILURE;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
    }


    /**
     * disconnect from the JMS broker.
     */
    public synchronized void disconnect() {
        this.checkAllowedState(State.CONNECTED, State.SESSION_ACTIVE);
        try {
            this.connection.close();
            this.state = State.DISCONNECTED;
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during disconnect.", ex);
            this.state = State.FAILURE;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
    }


    /**
     * Initialize JMS Session.
     */
    public synchronized void initSession() {
        this.checkAllowedState(State.CONNECTED);
        try {
            this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            this.state = State.SESSION_ACTIVE;
        }
        catch (final JMSException ex) {
            LOGGER.error("Problems during initialization of Session.", ex);
            this.state = State.FAILURE;
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("State: {}", this.state.toString());
        }
    }


    /**
     * Create A Consumer for the given Queue and set the given
     * {@link MessageListener}.
     *
     * @param destination The destination of the {@link Queue}
     * @param listener The {@link MessageListener} to set
     */
    public synchronized void setupQueueListener(final String destination, final MessageListener listener) {
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
        }
    }


    /**
     * Create A Consumer for the given Topic and set the given
     * {@link MessageListener}.
     *
     * @param topic The {@link Topic} to use
     * @param listener The {@link MessageListener} to set
     */
    public synchronized void setupTopicListener(final String topic, final MessageListener listener) {
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
        }
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
