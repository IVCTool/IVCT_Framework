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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;

/**
 * IVCTcommander takes user input strings, creates and sends messages to the JMS bus,
 * listens to the JMS bus and forwards the messages via callbacks to the user
 * interface.
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCTcommander implements OnResultListener {

    private static final String      PROPERTY_IVCTCOMMANDER_QUEUE = "ivctcommander.queue";
    private static Logger            LOGGER                       = LoggerFactory.getLogger(IVCTcommander.class);
    private PropertyBasedClientSetup jmshelper;
    private String                   destination;
    private MessageProducer producer;
	private static Semaphore semaphore = new Semaphore(0);
	private int countSemaphore = 0;
	private Set<Long> setSequence = new HashSet<Long>();
	private static Vector<String> listOfVerdicts = new Vector<String>();
	private static boolean cmdVerboseBool = false;
    public RuntimeParameters rtp = new RuntimeParameters();
    private CmdStartTestResultListener cmdStartTestResultListener;

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
        cmdStartTestResultListener = new CmdStartTestResultListener(this);
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
     * Check if a test case or test schedule are running.
     * 
     * @param theCaller name of the calling method
     * @param out the calling method
     * 
     * @return whether a critical task is running
     */
    protected boolean checkCtTcTsRunning(final String theCaller, PrintStream out) {
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
    
	public boolean getTestCaseRunningBool() {
		return rtp.getTestCaseRunningBool();
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

    public void onResult(TcResult result) {
		String testSchedule = RuntimeParameters.getTestScheduleName();
		String testcase =  result.testcase;
		String verdict =  result.verdict;
		String verdictText =  result.verdictText;
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
		rtp.setTestCaseRunningBool(false);
		releaseSemaphore();
    }

    private JSONParser jsonParser = new JSONParser();

}