package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

/**
 * Testrunner that takes listens on a certain JMS queue for commands to start a
 * certain test case.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class JMSTestRunner extends TestRunner implements MessageListener {

	private int counter = 0;
    private static final String      PROPERTY_JMSTESTRUNNER_QUEUE = "jmstestrunner.queue";
    private static Logger            logger                       = LoggerFactory.getLogger(JMSTestRunner.class);
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
            logger.error(ex.getMessage(), ex);
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
	logger.debug("JMSTestRunner:sendToJms");
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

    	private File getCwd() {
    		return new File("").getAbsoluteFile();
    	}

    	/**
    	 * This method provides a way to set the current working directory
    	 * which is not available as such in java.
    	 * 
    	 * N.B. This method uses a trick to get the desired result
    	 *
    	 * @param directory_name name of directory to be the current directory
    	 * @return true if successful
    	 */
    	private boolean setCurrentDirectory(String directory_name)
    	{
    		boolean result = false;  // Boolean indicating whether directory was set
    		File    directory;       // Desired current working directory

    		directory = new File(directory_name).getAbsoluteFile();
    		if (directory.exists())
    		{
    			directory.mkdirs();
    			result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
    		}

    		return result;
    	}

    	public void run() {
    		logger.debug("JMSTestRunner:onMessageConsumer:run: enter");
    		if (message instanceof TextMessage) {
    			final TextMessage textMessage = (TextMessage) message;
    			String testCaseId = null;
    			String testScheduleName = null;
    			JSONObject testCaseParam = null;

    			String ivctRootPath = System.getenv("IVCT_TS_HOME");
    			if (ivctRootPath == null) {
    	    		logger.error("JMSTestRunner:onMessageConsumer:run: IVCT_TS_HOME is not assigned");
    			} else {
    				logger.info("JMSTestRunner:onMessageConsumer:run: IVCT_TS_HOME is " + ivctRootPath);
    			}

    			try {
    				final String content = textMessage.getText();
    			    String                   sutName;
    			    String                   sutDir;
    			    logger.info("JMSTestRunner:onMessageConsumer:run: " + content);
    				JSONParser jsonParser = new JSONParser();
    				try {
    					JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
    					String commandTypeName =  (String) jsonObject.get("commandType");
    					logger.info("JMSTestRunner:onMessageConsumer:run: The commandType name is: " + commandTypeName);
    					if (commandTypeName.equals("quit")) {
    						System.exit(0);
    					}
    					if (commandTypeName.equals("startTestCase")) {
    						Long temp = Long.valueOf((String)jsonObject.get("sequence"));
    						if (temp == null) {
    							logger.error("JMSTestRunner:onMessageConsumer:run: the sequence number is: null");
    						} else {
    							counter = temp.intValue();
    						}
    						
    						sutName = (String) jsonObject.get("sutName");
    						sutDir =  (String) jsonObject.get("sutDir");

    						String tsRunFolder = (String) jsonObject.get("tsRunFolder");
    						logger.info("JMSTestRunner:onMessageConsumer:run: tsRunFolder is " + tsRunFolder);
    						if (setCurrentDirectory(ivctRootPath + "\\" + tsRunFolder)) {
    							logger.info("JMSTestRunner:onMessageConsumer:run: setCurrentDirectory true");
    						}

    		    			File f = getCwd();
    						String tcDir = f.getAbsolutePath();
    						logger.info("JMSTestRunner:onMessageConsumer:run: TC DIR is " + tcDir);

    			            testScheduleName = (String) jsonObject.get("testScheduleName");
    			            testCaseId = (String) jsonObject.get("testCaseId");
    			            logger.info("JMSTestRunner:onMessageConsumer:run: The test case class is: " + testCaseId);
    						testCaseParam = (JSONObject) jsonObject.get("tcParam");
    						logger.info("JMSTestRunner:onMessageConsumer:run: The test case parameters are: " + testCaseParam.toString());
    						String[] testcases = testCaseId.split("\\s");
    						IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

    						this.testRunner.executeTests(testCaseId.split("\\s"), testCaseParam.toString(), verdicts);
    						for (int i = 0; i < testcases.length; i++) {
    							sendToJms(verdicts[i].toJson(sutName, sutDir, testScheduleName, testcases[i], counter++));
    						}
    					}
    				} catch (ParseException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

    			}
    			catch (final JMSException ex) {
    				logger.error("JMSTestRunner:onMessageConsumer:run: Problems with parsing Message", ex);
    			}
    		}
    		logger.debug("JMSTestRunner:onMessageConsumer:run: after");
    	}
    }


    /** {@inheritDoc} */
    @Override
    public void onMessage(final Message message) {
    	if (logger.isTraceEnabled()) {
    		logger.trace("JMSTestRunner:onMessage: Received Command message");
    	}

    	Thread th1 = new Thread(new onMessageConsumer(message, this));
    	th1.start();
    }
}
