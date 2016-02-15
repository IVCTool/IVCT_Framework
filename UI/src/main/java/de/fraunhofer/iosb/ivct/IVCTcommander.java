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
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
    private static Document domTestsuite;
    private static ConfigParameters configParameters = null;

    /**
     * Main entry point from the command line.
     *
     * @param args The command line arguments
     */
    public static void main(final String[] args) {
        //        LogConfigurationHelper.configureLogging(JMSTestRunner.class);
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
        final InputStream in = this.getClass().getResourceAsStream("/IVCTcommander.properties");
        final Document domConfig;
        properties.load(in);
        this.jmshelper = new PropertyBasedClientSetup(properties);
        this.jmshelper.parseProperties();
        this.jmshelper.initConnection();
        this.jmshelper.initSession();
        this.destination = properties.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, "commands");
        producer = jmshelper.setupQueueProducer(destination);
        String ivct_path = System.getenv("IVCT_HOME");
        System.out.println ("ivct_path: " + ivct_path);

        domConfig = parseXmlFile(ivct_path + "\\IVCTconfig.xml");
        if (domConfig != null) {
        	configParameters = parseConfig(domConfig);
            System.out.println ("pathTestsuite: " + configParameters.pathTestsuite);
        }
        System.out.println ("ivct_path: " + ivct_path);
        System.out.println ("pathTestsuite: " + configParameters.pathTestsuite);
        
        System.out.println ("IVCTtestsuites");
        domTestsuite = parseXmlFile(configParameters.pathTestsuite + "\\IVCTtestsuites.xml");
        List<String> ls;
        ls = getTestSuiteNames();
		for (String temp : ls) {
			System.out.println(temp);
			Map <String, List<String>> xyz;
			xyz = readTestSuiteFiles(temp);
			if (xyz == null) {
				continue;
			}
			for (Map.Entry<String, List<String>> entry : xyz.entrySet())
			{
			    System.out.println("OUT " + entry.getKey());
				for (String entry0 : entry.getValue())
				{
				    System.out.println("XYZ " + entry0);
				}
			}
		}
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
      
    public static List<String> getTestSuiteNames() {
    	List<String> ls = new ArrayList<String>();

    	Element elem = domTestsuite.getDocumentElement();
    	for (Node child = elem.getFirstChild(); child != null; child=child.getNextSibling())
    	{
    		String s = child.getNodeName();
    		if (s.compareTo("testSuites") == 0) {
    			for (Node child0 = child.getFirstChild(); child0 != null; child0=child0.getNextSibling())
    			{
    				if (child0.getNodeName().compareTo("testSuite") == 0 )
    				{
    					for (Node child1 = child0.getFirstChild(); child1 != null; child1=child1.getNextSibling())
    					{
    						if (child1.getNodeName().compareTo("name") == 0 )
    						{
    							ls.add(child1.getFirstChild().getNodeValue());
    						}                	  
    					}
    				}
    			}
    		}
    	}

    	return ls;
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

      /*
       * The return class may be changed to hold other kinds of information as
       * required.
       */
      private static ConfigParameters parseConfig(Document dom) {
        System.out.println ("parseConfig");
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

      public static String printJson(String command) {
      	String s = new String("{\n  \"commandType\" : \"" + command + "\"\n}");
      	System.out.println(s);
      	return s;
      }
      
      public static String printJson(String command, String param, String value) {
      	String s = new String("{\n  \"commandType\" : \"" + command + "\",\n  \"" + param + "\" : \"" + value + "\"\n}");
      	System.out.println(s);
      	return s;
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
        this.jmshelper.setupQueueListener(this.destination, this);
    }


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
                System.out.println("IVCT::onMessage " + content);
            }
            catch (final JMSException ex) {
                LOGGER.warn("Problems with parsing Message", ex);
            }
        }

    }
}
