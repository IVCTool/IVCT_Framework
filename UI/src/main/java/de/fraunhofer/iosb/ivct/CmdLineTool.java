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

import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter ;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/*
 * Dialog program using keyboard input.
 */
public class CmdLineTool {
	private static final int DEFAULT_PORT = 6789;
//    Socket socket;
    Thread reader, writer;
    private static String hostname = null;
    protected static boolean conformanceTestBool = false;
    protected static boolean displayLogBool = false;
    protected static String testCaseName = null;
    protected static String testSuiteName = null;
    protected static RuntimeParameters rtp = new RuntimeParameters();
    private static int port = DEFAULT_PORT;
    public static IVCTcommander ivctCommander;

    // Create the client by creating its reader and writer threads
    // and starting them.
    public CmdLineTool(String host, int port) {
//            socket = new Socket(host, port);
            // Create reader and writer sockets
            System.out.println("Host: " + host + " Port: " + port);
            reader = new Reader(this);
            writer = new Writer(this);
            // Give the reader a higher priority to work around
            // a problem with shared access to the console.
            reader.setPriority(6);
            writer.setPriority(5);
            // Start the threads 
            reader.start();
            writer.start();
    }

    /*
     * Example of program arguments - not currently evaluated.
     */
    public static void usage(Options options) {
    	// automatically generate the help statement
    	HelpFormatter formatter = new HelpFormatter();
    	formatter.printHelp( "CmdLineTool", options );
    }

    /*
     * Example of parsing arguments - not currently evaluated.
     */
    public static void handleArgs(String[] args) {
        // create Options object
        Options options = new Options();

        // add options
        options.addOption("h", true, "hostname");        
        options.addOption("p", true, "port");
        
        CommandLineParser parser = new DefaultParser();
        
        try {
          CommandLine cmd = parser.parse( options, args);
          
          if (cmd.hasOption("h")) {
          	// get h option value
          	hostname = cmd.getOptionValue("h");

          	if (hostname == null) {
           	    usage(options);
          		System.exit(1);
          	}
          } else {
              System.out.println("Missing: -h arg");
        	  usage(options);
              System.exit(1);
          }

          if (cmd.hasOption("p")) {
        	  String portString = cmd.getOptionValue("p");
              try {
            	  port = Integer.parseInt(portString);
              }
              catch (NumberFormatException e) {
                  System.out.println( "Integer parse exception:" + e.getMessage() );
            	  usage(options);
                  System.exit(1);
              }
          }
        }
        catch( ParseException exp ) {
          System.out.println( "Unexpected exception:" + exp.getMessage() );
    	  usage(options);
          System.exit(1);
        }
    }

    /*
     * Main entry point.
     */
    public static void main(String[] args) {

    	handleArgs(args);

        new CmdLineTool(hostname, port);
    }
}

// This thread reads data from the server and prints it on the console
// As usual, the run() method does the interesting stuff.
class Reader extends Thread {
    CmdLineTool client;
    public Reader(CmdLineTool c) {
        super("CmdLineTool Reader");
        this.client = c;
    }
    public void run() {
      for (int i = 0; i < 1000000; i++)
      {
//        System.out.println("Loop: " + i);
        try {
        	CmdLineTool.ivctCommander.listenToJms();
            Thread.sleep(100);
        }
        catch (InterruptedException ex) {
        }
      }
        System.exit(0);
    }
}

// This thread reads user input from the console and sends it to the server.
class Writer extends Thread {
    CmdLineTool client;

    public Writer(CmdLineTool c) {
        super("CmdLineTool Writer");
        client = c;
        try {
        	CmdLineTool.ivctCommander = new IVCTcommander();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /*
     * Check if the test case name occurs in any test schedule.
     */
    private boolean checkTestCaseNameKnown(final String testCase) {
    	for (Map.Entry<String, List<String>> entry : CmdLineTool.rtp.testsuiteTestcases.entrySet()) {
    		for (String entry0 : entry.getValue()) {
    			if (testCase.equals(entry0)) {
    				return false;
    			}
    		}
    	}

    	return true;
    }
    
    /*
     * Check if the test schedule exists for the test suite.
     */
    private boolean checkTestScheduleKnown(final String testSchedule) {
    	for (Map.Entry<String, List<String>> entry : CmdLineTool.rtp.testsuiteTestcases.entrySet()) {
    		if (testSchedule.equals(entry.getKey())) {
    			return false;
    		}
    	}
    	
    	return true;
    }
    
    /*
     * Many commands have no meaning without knowing the test suite involved.
     */
    private boolean checkTestSuiteSelected() {
    	if (CmdLineTool.testSuiteName == null) {
            System.out.println("Testsuite not selected yet: use setTestSuite command first");
            return true;
    	}
    	return false;
    }
    
    /*
     * Read user input and execute valid commands.
     */
    public void run() {
    	BufferedReader in = null;
        PrintStream out = null;
        Command command = null;
        try {
            String line;
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintStream(System.out);
            while(true) {
                line = in.readLine();
                if (line == null) break;
                String split[]= line.trim().split("\\s+");
                switch(split[0]) {
                case "setTestSuite":
                	if (split.length == 1) {
                		System.out.println("setTestSuite: Error missing test suite name");
                		break;
                	}
                	if (CmdLineTool.rtp.ls == null) {
                		command = new ListTestSuites(CmdLineTool.ivctCommander, CmdLineTool.rtp, false);
                		command.execute();
                	}
                	boolean gotTestSuite = false;
                	for (String entry : CmdLineTool.rtp.ls)
                	{
                		if (split[1].equals(entry)) {
                			gotTestSuite = true;
                		}
                	}
                	if (gotTestSuite) {
                		command = new SetTestSuite(split[1], CmdLineTool.ivctCommander);
                		CmdLineTool.testSuiteName = split[1];
                	} else {
                		System.out.println("Unknown test suite " + split[1]);
                	}
                	break;
                case "setConformanceTest":
                case "sct":
                	if (split.length > 1) {
                        System.out.println("setConformanceTest: Warning extra parameter: " + split[1]);
                	}
                	command = new SetConformanceTest(CmdLineTool.ivctCommander);
                	CmdLineTool.conformanceTestBool = true;
                    break;
                case "unsetConformanceTest":
                case "uct":
                	if (split.length > 1) {
                        System.out.println("unsetConformanceTest: Warning extra parameter: " + split[1]);
                	}
                	command = new UnsetConformanceTest(CmdLineTool.ivctCommander);
                	CmdLineTool.conformanceTestBool = false;
                    break;
                case "addSUT":
                case "ast":
                	if (split.length == 1) {
                        System.out.println("addSUT: Error missing SUT name");
                	}
        			// TODO
                    break;
                case "deleteSUT":
                case "dst":
                	if (split.length == 1) {
                        System.out.println("deleteSUT: Error missing SUT name");
                	}
        			// TODO
                    break;
                case "listSUT":
                case "lst":
                	if (split.length > 1) {
                        System.out.println("listSUT: Warning extra parameter: " + split[1]);
                	}
        			// TODO
                    break;
                case "startTestCase":
                case "stc":
                	if (split.length == 1) {
                        System.out.println("startTestCase: Error missing test case id");
                        break;
                	}
                	if (checkTestSuiteSelected()) {
                		break;
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, false);
                	command.execute();
                	if (checkTestCaseNameKnown(split[1])) {
                        System.out.println("startTestCase: unknown test case " + split[1]);
                        break;
                	}
                	command = new StartTestCase(split[1], CmdLineTool.ivctCommander);
                	CmdLineTool.testCaseName = split[1];
                    break;
                case "startTestSchedule":
                case "sts":
                	if (split.length == 1) {
                        System.out.println("startTestSchedule: Error missing test schedule name");
                        break;
                	}
                	if (checkTestSuiteSelected()) {
                		break;
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, false);
                	command.execute();
                	if (checkTestScheduleKnown(split[1])) {
                        System.out.println("startTestSchedule: unknown test schedule " + split[1]);
                        break;
                	}
                	command = new StartTestSchedule(split[1], CmdLineTool.ivctCommander);
                    break;
                case "abortTestCase":
                case "atc":
                	if (split.length > 1) {
                        System.out.println("abortTestCase: Warning extra parameter: " + split[1]);
                	}
                	command = new AbortTestCase(CmdLineTool.ivctCommander);
                	break;
                case "abortTestSchedule":
                case "ats":
                	if (split.length > 1) {
                        System.out.println("abortTestSchedule: Warning extra parameter: " + split[1]);
                	}
                	command = new AbortTestSchedule(CmdLineTool.ivctCommander);
                    break;
                case "listTestCases":
                case "ltc":
                	if (split.length > 1) {
                        System.out.println("listTestCases: Warning extra parameter: " + split[1]);
                	}
                	if (checkTestSuiteSelected()) {
                		break;
                	}
                	command = new ListTestCases(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, true);
                    break;
                case "listTestSchedules":
                case "lts":
                	if (split.length > 1) {
                        System.out.println("listTestSchedules: Warning extra parameter: " + split[1]);
                	}
                	if (checkTestSuiteSelected()) {
                		break;
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, true);
                    break;
                case "listTestSuites":
                	if (split.length > 1) {
                        System.out.println("listTestSuites: Warning extra parameter: " + split[1]);
                	}
                	command = new ListTestSuites(CmdLineTool.ivctCommander, CmdLineTool.rtp, true);
                    break;
                case "setLogLevel":
                case "sll":
                	if (split.length == 1) {
                		System.out.println("setLogLevel: Error missing log level: error, warning, debug, info");
                		break;
                	}
                	if (split[1].equals("error") || split[1].equals("warning") || split[1].equals("debug") || split[1].equals("info")) {
                		command = new SetLogLevel(split[1], CmdLineTool.ivctCommander);
                	} else {
                        System.out.println("Unknown log level: " + split[1]);
                	}
                	break;
                case "displayLog":
                case "dl":
                	if (split.length > 1) {
                        System.out.println("displayLog: Warning extra parameter: " + split[1]);
                	}
                	CmdLineTool.displayLogBool = true;
                    break;
                case "undisplayLog":
                case "ul":
                	if (split.length > 1) {
                        System.out.println("undisplayLog: Warning extra parameter: " + split[1]);
                	}
                	CmdLineTool.displayLogBool = false;
                    break;
                case "quit":
                case "q":
                    System.out.println("quit");
                    System.exit(0);
                case "help":
                case "h":
                    System.out.println("setTestSuite - set the name of the test suite to be used");
                    System.out.println("setConformanceTest (sct) - set conformance test mode");
                    System.out.println("unsetConformanceTest (uct) - unset conformance test mode");
                    System.out.println("startTestCase (stc) - start the named test case");
                    System.out.println("startTestSchedule (sts) - start the named test schedule");
                    System.out.println("abortTestCase (atc) - abort the running test case");
                    System.out.println("abortTestSchedule (ats) - abort the running test schedule");
                    System.out.println("listTestCases (ltc) - list the available test cases for the test suite");
                    System.out.println("listTestSchedules (lts) - list the available test schedules for the test suite");
                    System.out.println("listTestSuites - list the available test suites");
                    System.out.println("setLogLevel (sll) - set the log level for logging - error, warning, debug, info");
                    System.out.println("displayLog (dl) - display the current test case log");
                    System.out.println("undisplayLog (ul) - do not display the current test case log");
                    System.out.println("help (h) - display the help information");
                    System.out.println("quit (q) - quit the program");
                    break;
                default:
                    System.out.println("Unknown command: " + split[0]);
                    break;
                }
                
                if (command != null) {
                	command.execute();
                	command = null;
                }
            }
        }
        catch (IOException e) { System.err.println("Writer: " + e); }
        finally { if (out != null) out.close(); }
        System.exit(0);
    }
}
