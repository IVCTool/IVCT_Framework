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
    private MessageProducer producer;
    private static ConfigParameters configParameters = null;
	private static Semaphore semaphore = new Semaphore(0);
	private int countSemaphore = 0;
	private Set<Long> setSequence = new HashSet<Long>();
	private static Vector<String> listOfVerdicts = new Vector<String>();
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
    public IVCTcommander() throws IOException {
        final Properties properties = new Properties();
        final InputStream in = this.getClass().getResourceAsStream("/CmdLineTool.properties");
        final Document domConfig;
        properties.load(in);
        this.jmshelper = new PropertyBasedClientSetup(properties);
        this.jmshelper.parseProperties();
        this.jmshelper.initConnection();
        this.jmshelper.initSession();
        this.destination = properties.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, "commands");
        producer = jmshelper.setupTopicProducer(destination);
        String ivct_path = System.getenv("IVCT_CONF");
        if (ivct_path == null) {
            System.out.println ("The global variable IVCT_CONF is NOT set");
        	System.exit(1);
        }

        domConfig = parseXmlFile(ivct_path + "\\IVCTconfig.xml");
        if (domConfig != null) {
        	configParameters = parseConfig(domConfig);
            File f = new File(configParameters.pathSutDir);
            if (f.isDirectory() == false) {
                System.out.println ("PATH SUT DIR in IVCTconfig.xml is NOT a FOLDER: " + configParameters.pathSutDir);
            	System.exit(1);
            }
            File f0 = new File(configParameters.pathTestsuite);
            if (f0.isDirectory() == false) {
                System.out.println ("PATH TEST SUITE in IVCTconfig.xml is NOT a FOLDER: " + configParameters.pathTestsuite);
            	System.exit(1);
            }
        } else {
            System.out.println ("Cannot parse: " + ivct_path + "\\IVCTconfig.xml");
        	System.exit(1);
        }
        System.out.println ("pathTestsuite: " + configParameters.pathTestsuite);
        System.out.println ("pathSutDir: " + configParameters.pathSutDir);
        rtp.domTestsuite = parseXmlFile(configParameters.pathTestsuite + "\\IVCTtestsuites.xml");
    }

    private static Document parseXmlFile(final String fileName){
        //get the factory
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        Document dom;

        try {

          //Using factory get an instance of document builder
          DocumentBuilder db = dbf.newDocumentBuilder();

          //parse using builder to get DOM representation of the XML file
          dom = db.parse(fileName);
          return dom;

        }catch(ParserConfigurationException pce) {
          pce.printStackTrace();
        }catch(SAXException se) {
          se.printStackTrace();
        }catch(IOException ioe) {
          ioe.printStackTrace();
        }
        return null;
      }
    
    public void acquireSemaphore() {
    	try {
        	countSemaphore++;
    		semaphore.acquire();
    	} catch (InterruptedException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
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
	
	/*
	 * Some commands have no meaning without knowing the test suite involved.
	 */
	protected boolean checkSutAndTestSuiteSelected(String sutNotSelected, String tsNotSelected) {
		return rtp.checkSutAndTestSuiteSelected(sutNotSelected, tsNotSelected);
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
    
    public String getTsRunFolder() {
    	return rtp.getTsRunFolder();
    }
    
	public String getPackageName(final String testsuite) {
		return rtp.getPackageName(testsuite);
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

    public static String getSUTdir() {
    	return configParameters.pathSutDir;
    }
    
    protected static String getTestSuiteName() {
    	return RuntimeParameters.getTestSuiteName();
    }
    
    protected static Map <String, List<String>> readTestSuiteFiles(final String testsuite) {
        Map <String, List<String>> xyz = new HashMap <String, List<String>>();
    	File mine;
    	int i;
    	String path = configParameters.pathTestsuite + "\\" + testsuite;
    	String files[];
    	mine = new File(path);
    	files = mine.list ();
    	if (files == null) {
    		return null;
    	}
    	for (i = 0; i < files.length; i++) {
    		String p = new String(path + "\\" + files[i]);
    		mine = new File (p);
    		if (mine.isFile()) {
    	    	List<String> ls;
    			ls = readFile(p);
    			xyz.put(files[i], ls);
    		}
    	}
    	return xyz;
    }
      
    private static List<String> readFile(String filename)
    {
    	List<String> records = new ArrayList<String>();
    	try
    	{
    		BufferedReader reader = new BufferedReader(new FileReader(filename));
    		String line;
    		while ((line = reader.readLine()) != null)
    		{
    			records.add(line);
    		}
    		reader.close();
    		return records;
    	}
    	catch (Exception e)
    	{
    		System.err.format("Exception occurred trying to read '%s'.", filename);
    		e.printStackTrace();
    		return null;
    	}
    }

    public static String readWholeFile(final String filename) {
    	BufferedReader br = null;
    	String everything = null;

    	File myFile = new File(filename);
    	if (myFile.isFile() == false) {
    		return everything;
    	}

    	try {
    		br = new BufferedReader(new FileReader(filename));
    		StringBuilder sb = new StringBuilder();
    		String line = br.readLine();

    		while (line != null) {
    			sb.append(line);
    			sb.append(System.lineSeparator());
    			line = br.readLine();
    		}
    		everything = sb.toString();
    	} catch (FileNotFoundException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	} finally {
    		if (br != null) {
    			try {
    				br.close();
    			} catch (IOException e) {
    			}
    		}
    	}

    	return everything;
    }
    
      /*
       * The return class may be changed to hold other kinds of information as
       * required.
       */
      private static ConfigParameters parseConfig(Document dom) {
        ConfigParameters configParameters = new ConfigParameters();
        
        Element elem = dom.getDocumentElement();
    	for (Node child = elem.getFirstChild(); child != null; child=child.getNextSibling())
    	{
          String s = child.getNodeName();
          if (s.compareTo("pathNames") == 0) {
            for (Node child0 = child.getFirstChild(); child0 != null; child0=child0.getNextSibling())
            {
              if (child0.getNodeName().compareTo("sutDir") == 0 )
              {
                if (child0.getNodeType() == Node.ELEMENT_NODE) {
                	configParameters.pathSutDir = child0.getFirstChild().getNodeValue();
                }
              }
              if (child0.getNodeName().compareTo("testSuites") == 0 )
              {
                if (child0.getNodeType() == Node.ELEMENT_NODE) {
                	configParameters.pathTestsuite = child0.getFirstChild().getNodeValue();
                }
              }
            }
          }
    	}
    	
    	return configParameters;
      }
      
      public static List<String> listSUT() {
    	  RuntimeParameters.setSUTS(configParameters.pathSutDir);
    	  
    	  List<String> suts = RuntimeParameters.getSUTS();
    	  
    	  return suts;
      }
      
      public void listVerdicts() {
			System.out.println("Verdicts are:");
			System.out.println("SUT: " + RuntimeParameters.getSutName());
			if (listOfVerdicts.isEmpty()) {
	            System.out.println("--No verdicts found--");
			}
	        Iterator<String> itr = listOfVerdicts.iterator();
	        while(itr.hasNext()){
	            System.out.println(itr.next());
	        }
      }

      public static String printJson(String command, final int counter) {
   		String s = new String("{\n  \"commandType\" : \"" + command + "\"\n  \"sequence\" : \"" + counter + "\",\n}");
      	System.out.println(s);
      	return s;
      }
      
      public static String printJson(String command, final int counter, String param, String value) {
      	String s = new String("{\n  \"commandType\" : \"" + command + "\",\n  \"sequence\" : \"" + counter + "\",\n  \"" + param + "\" : \"" + value + "\"\n}");
      	System.out.println(s);
      	return s;
      }

      public static String printTestCaseJson(final int counter, final String testScheduleName, final String testCaseId, final String value1, final String value2) {
	  	String s = new String("{\n  \"commandType\" : \"" + "startTestCase" + "\",\n  \"sequence\" : \"" + counter + "\",\n  \"testScheduleName\" : \"" + testScheduleName + "\",\n  \"testCaseId\" : \"" + testCaseId + "\",\n  \"tsRunFolder\" : \"" + value1 + "\",\n  \"tcParam\" : " + value2 + "}");
	    	System.out.println(s);
	    	return s;
	  }

      public static void resetSUT() {
    	  listOfVerdicts.clear();
      }
      /**
       * sendToJms
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
     */
    public void listenToJms() {
        this.jmshelper.setupTopicListener(this.destination, this);
    }


    private class OnMessageUiConsumer {

    	/*
    	 * (non-Javadoc)
    	 * @see java.lang.Runnable#run()
    	 */
    	public void run(final JSONObject jsonObject, final Vector<String> listOfVerdicts) {
    		String commandTypeName =  (String) jsonObject.get("commandType");

    		switch (commandTypeName) {
    		case "announceVerdict":
    			if (checkDuplicateSequenceNumber(jsonObject)) {
    				return;
    			}
    			System.out.println("The commandType name is: " + commandTypeName);
    			Long temp = Long.valueOf((String)jsonObject.get("sequence"));
    			if (temp == null) {
    				System.out.println("The sequence number is: null");
    			} else {
    				System.out.println("The sequence number is: " + temp);
    			}
    			String testSchedule = rtp.getTestScheduleName();
    			String testcase =  (String) jsonObject.get("testcase");
    			if (testcase != null) {
    				System.out.println("The test case name is: " + testcase.substring(testcase.lastIndexOf(".") + 1));
    			}
    			String verdict =  (String) jsonObject.get("verdict");
    			if (verdict != null) {
    				System.out.println("The test case verdict is: " + verdict);
    			}
    			String verdictText =  (String) jsonObject.get("verdictText");
    			if (verdictText != null) {
    				System.out.println("The test case verdict text is: " + verdictText + "\n");
    			}
    			String verdictStr = null;
    			if (testSchedule == null) {
    				verdictStr = new String("(single tc) " + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
    			} else {
    				verdictStr = new String(testSchedule + "." + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
    			}
    			if (rtp.checkTestSuiteNameNew()) {
    				String testSuiteStr = new String("Test Suite: " + rtp.getTestSuiteName());
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
    					onMessageUiConsumer.run(jsonObject, IVCTcommander.listOfVerdicts);
    					break;
    	    		case "quit":
    	    			// Should ignore
    	    			break;
    				case "setSUT":
    					break;
    				case "startTestCase":
    					break;
    				default:
    					System.out.println("Unknown commandType name is: " + commandTypeName);
    					break;
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
    }
}
