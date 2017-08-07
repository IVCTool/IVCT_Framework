/*
Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

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

package de.fraunhofer.iosb.ivct.logsink;

import java.io.BufferedWriter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;

public class ReportEngine implements MessageListener, Runnable  {
    Logger LOGGER = LoggerFactory.getLogger(ReportEngine.class);
	private String      LISTENER_TOPIC = "listener.topic";
    private static long count = 1;
    private static PropertyBasedClientSetup jmshelper;
    private static InputStream in;
    private boolean havePrevFile = false;
    private int inCalls = 0;
    private int numFailed = 0;
    private int numInconclusive = 0;
    private int numPassed = 0;
    private BufferedWriter writer = null;
    private Path file = null;
    private Path path;
    private JSONParser jsonParser = new JSONParser();
    private String knownSut = new String();
    private String baseFileName = "Report";
	private final String dashes = "//------------------------------------------------------------------------------";
	private final String failedStr = "FAILED";
	private final String inconclusiveStr = "INCONCLUSIVE";
	private final String passedStr = "PASSED";
	public TcChangedListener tcListener = null;

	public Map<String, String> status = new HashMap<String, String>();
	
    /**
     * 
     */
    public ReportEngine() {
    }


    private void openFile(String sutPath, String fName) {
    	Charset charset = Charset.forName("ISO-8859-1");

    	if (havePrevFile) {
    		closeFile();
    	}
    	file = FileSystems.getDefault().getPath(sutPath, fName);
    	try {
    		writer = Files.newBufferedWriter(file, charset);
    	} catch (IOException x) {
    		System.err.format("IOException: %s%n", x);
    	}
    	havePrevFile = true;
    }
    
    private void closeFile() {
    	try {
    		if (writer != null) {
    			writer.newLine();
    			writer.write(dashes, 0, dashes.length());
    			writer.newLine();
    			String verdicts = "// Verdicts: Passed: " + numPassed + " Failed: " + numFailed + " Inconclusive: " + numInconclusive;
    			writer.write(verdicts);
    			writer.newLine();
    			writer.write(dashes, 0, dashes.length());
    			writer.newLine();
    			writer.close();
    			if (numFailed == 0 && numInconclusive == 0 && numPassed == 0) {
    				Files.deleteIfExists(path);
    			}
    		}
    		numFailed = 0;
    		numInconclusive = 0;
    		numPassed = 0;
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

    /**
     * perform the listener role
     */
    public void run() {
        final Properties properties = new Properties();
        in = getClass().getResourceAsStream("/ReportEngine.properties");
        try {
			properties.load(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        jmshelper = new PropertyBasedClientSetup(properties);
        jmshelper.parseProperties();
        jmshelper.initConnection();
        jmshelper.initSession();
        String destination = properties.getProperty(LISTENER_TOPIC, "commands");

        //        this.jmshelper.setupTopicListener(destination, this);
        jmshelper.setupTopicListener(destination, this);

        LOGGER.info("ReportEngine:run: Waiting for messages...");
    }
    
    /** {@inheritDoc} */
    @Override
    public void onMessage(final Message message) {
    	inCalls++;
    	try {
			if (message.getJMSRedelivered() ) {
				LOGGER.warn("ReportEngine:onMessage: MESSAGE REDELIVERED");
			}
		} catch (JMSException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	if( message instanceof  TextMessage ) {
    		String body;
    		try {
    			body = ((TextMessage) message).getText();
    			if( "SHUTDOWN".equals(body)) {
    				LOGGER.info("ReportEngine:onMessage: SHUTDOWN " + count + " " + inCalls);
    		    	try {
    					Thread.sleep(10000);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				jmshelper.disconnect();
    			} else {
        				count++;
        				LOGGER.trace("ReportEngine:onMessage: ReportEngine " + count + " " + body);
        	    		checkMessage(body);
    			}
    		} catch (JMSException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    	} else {
    		LOGGER.warn("ReportEngine:onMessage: Unexpected message type: "+message.getClass());
    	}
    }
    
    private void checkMessage(String content) {
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
			String commandTypeName =  (String) jsonObject.get("commandType");


			switch (commandTypeName) {
			case "announceVerdict":
				LOGGER.info("ReportEngine:checkMessage: announceVerdict");
				String sut =  (String) jsonObject.get("sutName");
				if (sut.equals(knownSut) == false) {
					doSutChanged(jsonObject);
					knownSut = sut;
				}
				String testScheduleName = (String) jsonObject.get("testScheduleName");
				String testcase = (String) jsonObject.get("testcase");
				String verdict = (String) jsonObject.get("verdict");
				switch (verdict) {
				case failedStr:
					numFailed++;
					break;
				case inconclusiveStr:
					numInconclusive++;
					break;
				case passedStr:
					numPassed++;
					break;
				}
				status.put(testcase, verdict);
				
				String verdictText = (String) jsonObject.get("verdictText");
				String announceVerdict;
				if (testScheduleName.isEmpty()) {
					announceVerdict = "VERDICT: " + testScheduleName + "(single tc) " + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + "    " + verdictText;
				} else {
					announceVerdict = "VERDICT: " + testScheduleName + "." + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + "    " + verdictText;
				}
	    		writer.write(announceVerdict, 0, announceVerdict.length());
	    		writer.newLine();
	    		writer.flush();
				break;
    		case "quit":
        		closeFile();
                System.exit(0);
            case "setLogLevel":
    			// Should ignore
    			break;
			case "setSUT":
//				doSutChanged(jsonObject);
				break;
			case "startTestCase":
				if (tcListener != null) tcListener.tcChanged(jsonObject.get("testCaseId").toString());
				LOGGER.info("ReportEngine:checkMessage: startTestCase");
				break;
			default:
				LOGGER.error("ReportEngine:checkMessage: Unknown commandType name is: " + commandTypeName);
				break;
			}    					
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private void doSutChanged (JSONObject jsonObject) throws IOException {
		LocalDateTime ldt = LocalDateTime.now();
		String formattedMM = String.format("%02d", ldt.getMonthValue());
		String formatteddd = String.format("%02d", ldt.getDayOfMonth());
		String formattedhh = String.format("%02d", ldt.getHour());
		String formattedmm = String.format("%02d", ldt.getMinute());
		LOGGER.info("ReportEngine:doSutChanged: setSUT " + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + " " + formattedhh + " " + formattedmm);
		String sut =  (String) jsonObject.get("sutName");
		String sutPath =  (String) jsonObject.get("sutDir");
		LOGGER.info("ReportEngine:doSutChanged: sutPath: " + sutPath);
    	String fName = baseFileName + "_" + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + "_" + formattedhh + "-" + formattedmm + ".txt";
    	openFile(sutPath, fName);
    	path = FileSystems.getDefault().getPath(sutPath, fName);
    	writer.write(dashes, 0, dashes.length());
		writer.newLine();
		String a = "// SUT: " + sut + "             Date: " + ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth() + " " + ldt.getHour() + ":" + ldt.getMinute();
		writer.write(a, 0, a.length());
		writer.newLine();
    	writer.write(dashes, 0, dashes.length());
		writer.newLine();
		writer.newLine();
		writer.flush();
    }
}
