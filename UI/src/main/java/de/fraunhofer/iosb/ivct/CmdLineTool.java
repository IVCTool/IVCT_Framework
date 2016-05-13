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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

import de.fraunhofer.iosb.testrunner.LogConfigurationHelper;

/*
 * Dialog program using keyboard input.
 */
public class CmdLineTool {
	public static int counter = 0;
    Thread writer;
    protected static boolean conformanceTestBool = false;
    protected static String sutName = null;
    protected static String testCaseName = null;
    protected static String testSuiteName = null;
    protected static RuntimeParameters rtp = new RuntimeParameters();
    public static IVCTcommander ivctCommander;

    // Create the client by creating a writer thread
    // and starting them.
    public CmdLineTool() {
            // Create writer            
            writer = new Writer(this);
            writer.setPriority(5);
            // Start the thread
            writer.start();
    }

    /*
     * Main entry point.
     */
    public static void main(String[] args) {
        //        LogConfigurationHelper.configureLogging(JMSTestRunner.class);
        LogConfigurationHelper.configureLogging();

        new CmdLineTool();
    	CmdLineTool.ivctCommander.listenToJms();
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
    
    private boolean checkSutKnown(final String testCase) {
    	for (String entry : CmdLineTool.rtp.suts) {
    		if (testCase.equals(entry)) {
    			return false;
    		}
    	}
    	
    	return true;
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
     * Some commands have no meaning without knowing the SUT involved.
     */
    private boolean checkSUTselected() {
    	if (CmdLineTool.sutName == null) {
            System.out.println("SUT not selected yet: use setSUT command first");
            return true;
    	}
    	return false;
    }
    
    /*
     * Some commands have no meaning without knowing the test suite involved.
     */
    private boolean checkSutAndTestSuiteSelected() {
    	if (checkSUTselected()) {
            return true;
    	}
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
            out.println ("Enter command: or help (h)");
            out.print("> ");
            while(true) {
                line = in.readLine();
                if (line == null) break;
                String split[]= line.trim().split("\\s+");
                switch(split[0]) {
                case "":
                	break;
                case "listSUT":
                case "lsut":
                	if (split.length > 1) {
                        out.println("listSUT: Warning extra parameter: " + split[1]);
                	}
                	command = new ListSUT(CmdLineTool.ivctCommander, CmdLineTool.rtp, true);
                    break;
                case "setSUT":
                case "ssut":
                	// Cannot change SUT when a conformance test is running
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("setSUT: Warning conformance test is running cannot change SUT");
                        break;
                	}

                	// Need an input parameter
                	if (split.length == 1) {
                        out.println("setSUT: Error missing SUT name");
                        break;
                	}
                	
                    // get SUT list
                	command = new ListSUT(CmdLineTool.ivctCommander, CmdLineTool.rtp, false);
                	command.execute();
                	command = null;
                    // check if SUT entered exists in SUT list
                	if (checkSutKnown(split[1])) {
                        out.println("setSUT: unknown SUT: " + split[1]);
                        break;
                	}
                	CmdLineTool.sutName = split[1];
                	command = new SetSUT(split[1], CmdLineTool.ivctCommander, CmdLineTool.counter++);
                	CmdLineTool.testSuiteName = null;
                	IVCTcommander.resetSUT();
                    break;
                case "listTestSuites":
                case "lt":
                	if (checkSUTselected()) {
                		break;
                	}
                	if (split.length > 1) {
                        out.println("listTestSuites: Warning extra parameter: " + split[1]);
                	}
                	command = new ListTestSuites(CmdLineTool.ivctCommander, CmdLineTool.rtp, true);
                    break;
                case "setTestSuite":
                case "st":
                	if (checkSUTselected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("setTestSuite: Error conformance test is running");
                        break;
                	}
                	if (split.length == 1) {
                		out.println("setTestSuite: Error missing test suite name");
                		break;
                	}
                	if (CmdLineTool.rtp.ls == null) {
                		command = new ListTestSuites(CmdLineTool.ivctCommander, CmdLineTool.rtp, false);
                		command.execute();
                    	command = null;
                	}
                	boolean gotTestSuite = false;
        			for (Map.Entry<String, String> entry : CmdLineTool.rtp.ls.entrySet()) {
                		if (split[1].equals(entry.getKey())) {
                			gotTestSuite = true;
                		}
                	}
                	if (gotTestSuite) {
                		CmdLineTool.testSuiteName = split[1];
                    	String tcParamFile = new String(IVCTcommander.getSUTdir() + "\\" + CmdLineTool.sutName + "\\" + CmdLineTool.testSuiteName + "\\" + "TcParam.json");
                    	CmdLineTool.rtp.paramJson = IVCTcommander.readWholeFile(tcParamFile);
                    	if (CmdLineTool.rtp.paramJson == null) {
                            out.println("setSUT: cannot read file: " + tcParamFile);
                            break;
                    	}
                		command = new SetTestSuite(split[1], CmdLineTool.ivctCommander, CmdLineTool.counter++);
                	} else {
                		out.println("Unknown test suite " + split[1]);
                	}
                	break;
                case "startConformanceTest":
                case "sct":
                	// Cannot run conformance test if SUT is not set
                	if (checkSUTselected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("startConformanceTest: Warning conformance test is already running");
                        break;
                	}
                	if (split.length > 1) {
                        out.println("startConformanceTest: Warning extra parameter: " + split[1]);
                	}
//                	command = new StartConformanceTest(CmdLineTool.ivctCommander);
                    out.println("startConformanceTest: Warning start conformance test logic is NOT IMPLEMENTED yet");
                	CmdLineTool.conformanceTestBool = true;
                    break;
                case "abortConformanceTest":
                case "act":
                	// Cannot abort conformance test if SUT is not set
                	if (checkSUTselected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool == false) {
                        out.println("abortConformanceTest: Warning no conformance test is running");
                        break;
                	}
                	if (split.length > 1) {
                        out.println("abortConformanceTest: Warning extra parameter: " + split[1]);
                	}
//                	command = new AbortConformanceTest(CmdLineTool.ivctCommander);
                    out.println("abortConformanceTest: Warning abort conformance test logic is NOT IMPLEMENTED yet");
                	CmdLineTool.conformanceTestBool = false;
                    break;
                case "listTestSchedules":
                case "lts":
                	if (checkSutAndTestSuiteSelected()) {
                		break;
                	}
                	if (split.length > 1) {
                        out.println("listTestSchedules: Warning extra parameter: " + split[1]);
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, true);
                    break;
                case "startTestSchedule":
                case "sts":
                	if (checkSutAndTestSuiteSelected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("startTestSchedule: Warning conformance test is running");
                        break;
                	}
                	if (split.length == 1) {
                        out.println("startTestSchedule: Warning missing test schedule name");
                        break;
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, false);
                	command.execute();
                	command = null;
                	if (CmdLineTool.rtp.testsuiteTestcases.containsKey(split[1]) == false) {
                        out.println("startTestSchedule: unknown test schedule " + split[1]);
                        break;
                	}
                	List<String> testcases = CmdLineTool.rtp.testsuiteTestcases.get(split[1]);
            		
                	// Check if test case exists
                	for (String testcase : testcases) {
                    	if (checkTestCaseNameKnown(testcase)) {
                            out.println("startTestSchedule: unknown test case " + testcase);
                            break;
                    	}
                	}
                	
                	// Create a command structure to share between threads
                	// One thread works through the list
                	// Other thread receives test case verdicts and releases semaphore in first thread
                	// to start next test case
                	CommandCache commandCache = new CommandCache(split[1], testcases);
                	
                	// This will create one thread, other thread listens to JMS bus anyway
                	command = new StartTestSchedule(commandCache, CmdLineTool.ivctCommander, CmdLineTool.counter, CmdLineTool.testSuiteName, CmdLineTool.rtp.paramJson);
                	CmdLineTool.counter += testcases.size();
                    break;
                case "abortTestSchedule":
                case "ats":
                	if (checkSUTselected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("abortTestSchedule: Warning conformance test is running");
                        break;
                	}
                	if (split.length > 1) {
                        out.println("abortTestSchedule: Warning extra parameter: " + split[1]);
                	}
//                	command = new AbortTestSchedule(CmdLineTool.ivctCommander);
                    out.println("abortTestSchedule: Warning abort test schedule logic is NOT IMPLEMENTED yet");
                    break;
                case "listTestCases":
                case "ltc":
                	if (checkSutAndTestSuiteSelected()) {
                		break;
                	}
                	if (split.length > 1) {
                        out.println("listTestCases: Warning extra parameter: " + split[1]);
                	}
                	command = new ListTestCases(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, true);
                    break;
                case "startTestCase":
                case "stc":
                	if (checkSutAndTestSuiteSelected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("startTestCase: Warning conformance test is running");
                        break;
                	}
                	if (split.length == 1) {
                        out.println("startTestCase: Error missing test case id");
                        break;
                	}
                	command = new ListTestSchedules(CmdLineTool.ivctCommander, CmdLineTool.rtp, CmdLineTool.testSuiteName, false);
                	command.execute();
                	command = null;
                	if (checkTestCaseNameKnown(split[1])) {
                        out.println("startTestCase: unknown test case " + split[1]);
                        break;
                	}
                	command = new StartTestCase(split[1], CmdLineTool.ivctCommander, CmdLineTool.counter++, CmdLineTool.testSuiteName, CmdLineTool.rtp.paramJson);
                	CmdLineTool.testCaseName = split[1];
                    break;
                case "abortTestCase":
                case "atc":
                	if (checkSUTselected()) {
                		break;
                	}
                	if (CmdLineTool.conformanceTestBool) {
                        out.println("abortTestCase: Warning conformance test is running");
                        break;
                	}
                	if (split.length > 1) {
                        out.println("abortTestCase: Warning extra parameter: " + split[1]);
                	}
//                	command = new AbortTestCase(CmdLineTool.ivctCommander);
                    out.println("abortTestCase: Warning abort test case logic is NOT IMPLEMENTED yet");
                	break;
                case "listVerdicts":
                case "lv":
                	if (checkSutAndTestSuiteSelected()) {
                		break;
                	}
                	if (split.length > 1) {
                        out.println("listVerdicts: Warning extra parameter: " + split[1]);
                	}
                	IVCTcommander.listVerdicts();
                	break;
                case "setLogLevel":
                case "sll":
                	if (split.length == 1) {
                		out.println("setLogLevel: Error missing log level: error, warning, debug or info");
                		break;
                	}
                	if (split[1].equals("error") || split[1].equals("warning") || split[1].equals("debug") || split[1].equals("info")) {
                		command = new SetLogLevel(split[1], CmdLineTool.ivctCommander, CmdLineTool.counter++);
                        out.println("setLogLevel: Warning abort set log level logic is NOT IMPLEMENTED yet");
                	} else {
                        out.println("Unknown log level: " + split[1]);
                	}
                	break;
                case "quit":
                case "q":
                	command = new QuitCmd("quit", CmdLineTool.ivctCommander, CmdLineTool.counter++);
                	command.execute();
                    out.println("quit");
                    System.exit(0);
                case "help":
                case "h":
                    out.println("listSUT (lsut) - list SUT folders");
                    out.println("setSUT (ssut) - set active SUT");
                    out.println("listTestSuites (lt) - list the available test suites");
                    out.println("setTestSuite (st) - set the name of the test suite to be used");
                    out.println("startConformanceTest (sct) - start conformance test");
                    out.println("abortConformanceTest (act) - abort conformance test");
                    out.println("listTestSchedules (lts) - list the available test schedules for the test suite");
                    out.println("startTestSchedule (sts) - start the named test schedule");
                    out.println("abortTestSchedule (ats) - abort the running test schedule");
                    out.println("listTestCases (ltc) - list the available test cases for the test suite");
                    out.println("startTestCase (stc) - start the named test case");
                    out.println("abortTestCase (atc) - abort the running test case");
                    out.println("setLogLevel (sll) - set the log level for logging - error, warning, debug, info");
                    out.println("listVerdicts (lv) - list the verdicts of the current session");
                    out.println("quit (q) - quit the program");
                    out.println("help (h) - display the help information");
                    break;
                default:
                    out.println("Unknown command: " + split[0]);
                    break;
                }
                
                if (command != null) {
                	command.execute();
                	command = null;
                }
                out.print("> ");
            }
        }
        catch (IOException e) { System.err.println("Writer: " + e); }
        finally { if (out != null) out.close(); }
        System.exit(0);
    }
}
