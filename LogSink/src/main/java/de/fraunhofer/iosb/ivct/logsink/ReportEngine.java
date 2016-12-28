package de.fraunhofer.iosb.ivct.logsink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
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

	private String      LISTENER_TOPIC = "listener.topic";
    private static long count = 1;
    private static PropertyBasedClientSetup jmshelper;
    private static InputStream in;
    private boolean havePrevFile = false;
    private int inCalls = 0;
    private int num = 0;
    private int numFailed = 0;
    private int numInconclusive = 0;
    private int numPassed = 0;
    private BufferedWriter writer;
    private Path file = null;
    private Path path;
    private JSONParser jsonParser = new JSONParser();
    private String baseFileName = "Report";
	private final String dashes = "//------------------------------------------------------------------------------";
	private final String failedStr = "FAILED";
	private final String inconclusiveStr = "INCONCLUSIVE";
	private final String passedStr = "PASSED";

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
        Logger LOGGER = LoggerFactory.getLogger(ReportEngine.class);
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

        LOGGER.info("ReportEngine: Waiting for messages...");
    }
    
    /** {@inheritDoc} */
    @Override
    public void onMessage(final Message message) {
    	inCalls++;
    	try {
			if (message.getJMSRedelivered() ) {
				System.out.println("MESSAGE REDELIVERED");
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
    				System.out.println("SHUTDOWN " + count + " " + inCalls);
    		    	try {
    					Thread.sleep(10000);
    				} catch (InterruptedException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}
    				jmshelper.disconnect();
    			} else {
        				count++;
        				System.out.println("ReportEngine " + count + " " + body);
        	    		checkMessage(body);
    			}
    		} catch (JMSException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}

    	} else {
    		System.out.println("Unexpected message type: "+message.getClass());
    	}
    }
    
    private void checkMessage(String content) {
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
			String commandTypeName =  (String) jsonObject.get("commandType");


			switch (commandTypeName) {
			case "announceVerdict":
				System.out.println("checkMessage: announceVerdict");
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
			case "setSUT":
				LocalDateTime ldt = LocalDateTime.now();
				String formattedMM = String.format("%02d", ldt.getMonthValue());
				String formatteddd = String.format("%02d", ldt.getDayOfMonth());
				String formattedhh = String.format("%02d", ldt.getHour());
				String formattedmm = String.format("%02d", ldt.getMinute());
				System.out.println("checkMessage: setSUT " + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + " " + formattedhh + " " + formattedmm);
				String sut =  (String) jsonObject.get("sut");
				String sutPath =  (String) jsonObject.get("sutPath");
				System.out.println("checkMessage: sutPath: " + sutPath);
		    	String fName = baseFileName + "_" + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + "_" + formattedhh + "-" + formattedmm + ".txt";
		    	num += 1;
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
				break;
			case "startTestCase":
				System.out.println("checkMessage: startTestCase");
				break;
			default:
				System.out.println("Unknown commandType name is: " + commandTypeName);
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

}
