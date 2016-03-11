package de.fraunhofer.iosb.testrunner;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Testrunner that takes listens on a certain JMS queue for commands to start a
 * certain test case.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class JMSTestRunner extends TestRunner implements MessageListener {

	private int counter = 0;
    private static final String      PROPERTY_JMSTESTRUNNER_QUEUE = "jmstestrunner.queue";
    private static Logger            LOGGER                       = LoggerFactory.getLogger(JMSTestRunner.class);
    private PropertyBasedClientSetup jmshelper;
    private String                   destination;
    private MessageProducer producer;

    /**
     * Main entry point from the command line.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        //        LogConfigurationHelper.configureLogging(JMSTestRunner.class);
        LogConfigurationHelper.configureLogging();
        try {
            final JMSTestRunner runner = new JMSTestRunner();
            runner.listenToJms();
        }
        catch (final IOException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }


    /**
     * public constructor.
     *
     * @throws IOException problems with loading properties
     */
    public JMSTestRunner() throws IOException {
        final Properties properties = new Properties();
        final InputStream in = this.getClass().getResourceAsStream("/JMSTestRunner.properties");
        properties.load(in);
        this.jmshelper = new PropertyBasedClientSetup(properties);
        this.jmshelper.parseProperties();
        this.jmshelper.initConnection();
        this.jmshelper.initSession();
        this.destination = properties.getProperty(PROPERTY_JMSTESTRUNNER_QUEUE, "commands");
        producer = jmshelper.setupTopicProducer(destination);
    }

    /**
   * sendToJms
   */
  public void sendToJms(final String userCommand) {
  	Message message = jmshelper.createTextMessage(userCommand);
  	System.out.println("SEND TO JMS");
  	try {
			producer.send(message);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }

    /**
     * Initialize the Listening on the JMS Queue
     */
    public void listenToJms() {
        this.jmshelper.setupTopicListener(this.destination, this);
    }

    private class onMessageConsumer implements Runnable {
    	private Message message;
    	private TestRunner testRunner;
    	
    	onMessageConsumer(final Message message, final TestRunner testRunner) {
    		this.message = message;
    		this.testRunner = testRunner;
    	}
    	
    	public void run() {
    		System.out.println("onMessageConsumer before");
            if (message instanceof TextMessage) {
                final TextMessage textMessage = (TextMessage) message;
                String testCaseId = null;
                JSONObject testCaseParam = null;
                try {
                    final String content = textMessage.getText();
                    System.out.println("JMSTestRunner:onMessage " + content);
        			JSONParser jsonParser = new JSONParser();
        			try {
    					JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
    					String commandTypeName =  (String) jsonObject.get("commandType");
    					System.out.println("The commandType name is: " + commandTypeName);
    					if (commandTypeName.equals("startTestCase")) {
    						testCaseId = (String) jsonObject.get("testCaseId");
    						System.out.println("The test case class is: " + testCaseId);
    						testCaseParam = (JSONObject) jsonObject.get("tcParam");
    						System.out.println("The test case parameters are: " + testCaseParam.toString());
    						String[] testcases = testCaseId.split("\\s");
    						IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

    		                this.testRunner.executeTests(testCaseId.split("\\s"), testCaseParam.toString(), verdicts);
    		                for (int i = 0; i < testcases.length; i++) {
    		                	sendToJms(verdicts[i].toJson(testcases[i], counter++));
    		                }
    					}
    				} catch (ParseException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

                }
                catch (final JMSException ex) {
                    LOGGER.warn("Problems with parsing Message", ex);
                }
            }
    		System.out.println("onMessageConsumer after");
    	}
    }


    /** {@inheritDoc} */
    @Override
    public void onMessage(final Message message) {
    	if (LOGGER.isTraceEnabled()) {
    		LOGGER.trace("Received Command message");
    	}

    	Thread th1 = new Thread(new onMessageConsumer(message, this));
    	th1.start();
    }
}
