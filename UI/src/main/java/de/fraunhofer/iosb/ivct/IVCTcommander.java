/*
Copyright 2016, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.ivct;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import de.fraunhofer.iosb.tc_lib.LineUtil;

/**
 * IVCTcommander takes user input strings, creates and sends messages to the JMS bus,
 * listens to the JMS bus and forwards the messages via callbacks to the user
 * interface.
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCTcommander implements MessageListener {

    private static final String      PROPERTY_IVCTCOMMANDER_QUEUE = "ivctcommander.queue";
    private static Logger            LOGGER                       = LoggerFactory.getLogger(IVCTcommander.class);
    private PropertyBasedClientSetup jmshelper;
    private String                   destination;
    private String testSchedulePath = null;
    private MessageProducer producer;
	private static Semaphore semaphore = new Semaphore(0);
	private int countSemaphore = 0;
	private Set<Long> setSequence = new HashSet<Long>();
	private static Vector<String> listOfVerdicts = new Vector<String>();
	private static boolean cmdVerboseBool = false;
    public RuntimeParameters rtp = new RuntimeParameters();

    /**
     * Main entry point from the command line.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        //        LogConfigurationHelper.configureLogging(IVCTcommander.class);
//        LogConfigurationHelper.configureLogging();
        try {
            final IVCTcommander runner = new IVCTcommander();
            String userCommand = "Freddy";
            runner.sendToJms(userCommand);
            if (runner.listenToJms()) {
            	System.exit(1);
            }
        }
        catch (final IOException ex) {
            LOGGER.error("main: IOException", ex);
        }
    }

    /**
     * public constructor.
     *
     * @throws IOException problems with loading properties
     */
    public IVCTcommander() throws IOException {
        final Properties properties = new Properties();
        final InputStream in = this.getClass().getResourceAsStream("/CmdLineTool.properties");
        properties.load(in);
        this.jmshelper = new PropertyBasedClientSetup(properties);
        if (this.jmshelper.parseProperties()) {
        	System.exit(1);
        }
        if (this.jmshelper.initConnection()) {
        	System.exit(1);
        }
        if (this.jmshelper.initSession()) {
        	System.exit(1);
        }
        this.destination = properties.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, "commands");
        producer = jmshelper.setupTopicProducer(destination);
    }

    public void acquireSemaphore() {
    	try {
        	countSemaphore++;
    		semaphore.acquire();
    	} catch (InterruptedException e) {
    		e.printStackTrace();
            LOGGER.error("acquireSemaphore: ", e);
    	}
    }

    public void releaseSemaphore() {
    	if (countSemaphore > 0) {
    		semaphore.release();
    		countSemaphore--;
    	}
    }
    
    public void addTestSessionSeparator() {
    	String blank = new String(" ");
		listOfVerdicts.addElement(blank);    	
    }
	/*
	 * JMS will deliver multiple messages. Need to check if the message was already
	 *  seen
	 */
	private boolean checkDuplicateSequenceNumber(final JSONObject jsonObject) {
		Long temp = Long.valueOf((String)jsonObject.get("sequence"));
		if (temp == null) {
			System.out.println("The sequence number is: null");
		} else {
			if (setSequence.contains(temp)) {
				return true;
			} else {
				setSequence.add(temp);
			}
		}
		return false;
	}

    public boolean checkSutKnown(final String sut) {
    	return rtp.checkSutKnown(sut);
    }
	
    protected boolean checkSUTselected() {
    	return rtp.checkSUTselected();
    }
    
    /*
     * Check if a conformance test, test case or test schedule are running.
     * 
     * @param theCaller name of the calling method
     * @param out the calling method
     * 
     * @return whether a critical task is running
     */
    protected boolean checkCtTcTsRunning(final String theCaller, PrintStream out) {
    	if (rtp.getConformanceTestBool()) {
    		out.println(theCaller + ": Warning conformance test is running - command not allowed");
    		return true;
    	}
    	if (rtp.getTestCaseRunningBool()) {
    		out.println(theCaller + ": Warning test case is running - command not allowed");
    		return true;
    	}
    	if (rtp.getTestScheduleRunningBool()) {
    		out.println(theCaller + ": Warning test schedule is running - command not allowed");
    		return true;
    	}
    	return false;
    }
    
    protected int fetchCounter() {
    	int i = 1;
    	return rtp.fetchCounters(i);
    }
    
    protected int fetchCounters(int n) {
    	return rtp.fetchCounters(n);
    }
    
	public boolean getConformanceTestBool() {
		return rtp.getConformanceTestBool();
	}
	
	public void setConformanceTestBool(boolean b) {
		rtp.setConformanceTestBool(b);
	}

	public boolean getTestCaseRunningBool() {
		return rtp.getTestCaseRunningBool();
	}
	
	public void setTestCaseRunningBool(boolean b) {
		rtp.setTestCaseRunningBool(b);
	}

	public boolean getTestScheduleRunningBool() {
		return rtp.getTestScheduleRunningBool();
	}
	
	public void setTestScheduleRunningBool(boolean b) {
		rtp.setTestScheduleRunningBool(b);
	}

    protected void setCmdVerboseBool(final boolean b) {
    	cmdVerboseBool = b;
    }
    
      public List<String> listSUT() {
    	  rtp.setSUTS();
    	  
    	  List<String> suts = RuntimeParameters.getSUTS();
    	  return suts;
      }
      
      public void listVerdicts() {
			System.out.println("SUT: " + rtp.getSutName());
			if (listOfVerdicts.isEmpty()) {
	            System.out.println("--No verdicts found--");
			}
	        Iterator<String> itr = listOfVerdicts.iterator();
	        while(itr.hasNext()){
	            System.out.println(itr.next());
	        }
      }

      public static void resetSUT() {
    	  listOfVerdicts.clear();
      }
      /**
       * sendToJms
       * @param userCommand command as a json value
       */
      public void sendToJms(final String userCommand) {
    	  Message message = jmshelper.createTextMessage(userCommand);
    	  try {
    		  producer.send(message);
    	  } catch (JMSException e) {
    		  // TODO Auto-generated catch block
    		  e.printStackTrace();
    	  }
      }

    /**
     * Initialize the Listening on the JMS Queue
     * @return true means failure
     */
    public boolean listenToJms() {
        if (this.jmshelper.setupTopicListener(this.destination, this)) {
        	return true;
        }
        return false;
    }


    private class OnMessageUiConsumer {

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Runnable#run()
    	 */
    	public void run(final JSONObject jsonObject, final Vector<String> listOfVerdicts, final boolean cmdVerboseBool) {
    		String commandTypeName =  (String) jsonObject.get("commandType");

    		switch (commandTypeName) {
    		case "announceVerdict":
    			if (checkDuplicateSequenceNumber(jsonObject)) {
    				return;
    			}
    			if (cmdVerboseBool) {
    				System.out.println("The commandType name is: " + commandTypeName);
    			}
    			Long temp = Long.valueOf((String)jsonObject.get("sequence"));
    			if (cmdVerboseBool) {
    				if (temp == null) {
    					System.out.println("The sequence number is: null");
    				} else {
    					System.out.println("The sequence number is: " + temp);
    				}
    			}
    			String testSchedule = rtp.getTestScheduleName();
    			String testcase =  (String) jsonObject.get("testcase");
    			if (testcase == null) {
    				System.out.println("Error: the test case name is null");
    			}
    			String verdict =  (String) jsonObject.get("verdict");
    			if (verdict == null) {
    				System.out.println("Error: test case verdict is null");
    			}
    			String verdictText =  (String) jsonObject.get("verdictText");
    			if (verdictText == null) {
    				System.out.println("Error: the test case verdict text is null");
    			}
    			if (testcase != null && verdict != null && verdictText != null) {
    				System.out.println("The verdict is: " + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + " " + verdictText);
    			}
    			System.out.println("\n");
    			String verdictStr = null;
    			if (testSchedule == null) {
    				verdictStr = new String("(single tc) " + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
    			} else {
    				verdictStr = new String(testSchedule + "." + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
    			}
    			if (rtp.checkTestSuiteNameNew()) {
    				String testSuiteStr = new String("Verdicts are:");
    				listOfVerdicts.addElement(testSuiteStr);
    				addTestSessionSeparator();
    				rtp.setTestSuiteNameUsed();
    			}
				listOfVerdicts.addElement(verdictStr);
    			releaseSemaphore();
    			break;
    		default:
    			System.out.println("Unknown commandType name is: " + commandTypeName);
    			break;
    		}    					
    	}
    }
    
    private OnMessageUiConsumer onMessageUiConsumer = new OnMessageUiConsumer();
    private JSONParser jsonParser = new JSONParser();

/** {@inheritDoc} */
    @Override
    public void onMessage(final Message message) {
    	if (LOGGER.isTraceEnabled()) {
    		LOGGER.trace("Received Command message");
    	}

    	if (message instanceof TextMessage) {
    		final TextMessage textMessage = (TextMessage) message;
    		try {
    			final String content = textMessage.getText();
    			try {
    				JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
    				String commandTypeName =  (String) jsonObject.get("commandType");


    				switch (commandTypeName) {
    				case "announceVerdict":
    					onMessageUiConsumer.run(jsonObject, IVCTcommander.listOfVerdicts, cmdVerboseBool);
    					break;
    	    		case "quit":
    	    			// Should ignore
    	    			break;
                    case "setLogLevel":
    	    			// Should ignore
    	    			break;
    				case "setSUT":
    	    			// Should ignore
    					break;
    				case "startTestCase":
    	    			// Should ignore
    					break;
    				default:
    					System.out.println("Unknown commandType name is: " + commandTypeName);
    					break;
    				}    					
    			} catch (ParseException e) {
    				e.printStackTrace();
    	            LOGGER.error("onMessage: ", e);
    			}

    		}
    		catch (final JMSException e) {
    			LOGGER.error("onMessage: problems with getText", e);
    		}
    	}
    }
}
