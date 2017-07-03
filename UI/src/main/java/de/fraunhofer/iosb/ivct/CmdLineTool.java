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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.concurrent.Semaphore;

import de.fraunhofer.iosb.testrunner.LogConfigurationHelper;
import nato.ivct.commander.CmdQuit;
import nato.ivct.commander.CmdSetLogLevel;

/*
 * Dialog program using keyboard input.
 */
public class CmdLineTool {
    Thread writer;
	private boolean keepGoing = true;
    private Command command = null;
	private Semaphore semaphore = new Semaphore(0);
	public static Process p;
    public static IVCTcommander ivctCommander;

    // Create the client by creating a writer thread
    // and starting them.
    public CmdLineTool() throws IOException {
    	// Create IVCTcommander
    	CmdLineTool.ivctCommander = new IVCTcommander();
    	// Create writer            
    	writer = new Writer(ivctCommander);
    	writer.setPriority(5);
    	// Start the thread
    	writer.start();

    	(new Thread(new commandRunnable(this.semaphore, this.keepGoing))).start();

//    	(new Thread(new TcRunnable())).start();	
    }
    
    /**
     * Main entry point.
     *
     * @param args command line parameters
     */
    public static void main(String[] args) {
    	// LogConfigurationHelper.configureLogging(JMSTestRunner.class);
    	LogConfigurationHelper.configureLogging();

    	try {
    		new CmdLineTool();
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.out.println("CmdLineTool: new IVCTcommander: " + e);
    		return;
    	}

    	// Handle callbacks
    	if (CmdLineTool.ivctCommander.listenToJms()) {
        	System.exit(1);
    	}
    }

    class commandRunnable implements Runnable {
    	boolean keepGoing;
    	Semaphore semaphore;

    	public commandRunnable(Semaphore semaphore, boolean keepGoing) {
    		this.semaphore = semaphore;
    		this.keepGoing = keepGoing;
    	}

    	public void run() {
    		while (keepGoing) {
    			try {
    				this.semaphore.acquire();
    				if (command != null) {
    					command.execute();
    					command = null;
    				}
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}

    		}
    	}
    }
    
class TcRunnable implements Runnable {
	 
    public void run() {
		File f = getCwd();
    	String javaHome = System.getenv("JAVA_HOME");
    	if (javaHome == null) {
    		System.out.println("JAVA_HOME is not assigned.");
    	} else {
    		System.out.println("JAVA_HOME is " + javaHome);
    	}
    	
        String javaExe = javaHome + File.separator + "bin" + File.separator + "java";
        
    	String classPath = System.getenv("CLASSPATH");
    	if (classPath == null) {
    		System.out.println("CLASSPATH is not assigned.");
    	} else {
    		System.out.println("CLASSPATH is " + classPath);
    	}

		try {
			String javaOpts = System.getenv("JAVA_OPTS");
			if (javaOpts == null) {
				javaOpts = "-Duser.country=US -Duser.language=EN";
			}
			System.out.println("\"" + javaExe + "\" " + javaOpts +" -classpath \"" + classPath + "\" de.fraunhofer.iosb.testrunner.JMSTestRunner");
			CmdLineTool.p = Runtime.getRuntime().exec("\"" + javaExe + "\" " + javaOpts + " -classpath \"" + classPath + "\" de.fraunhofer.iosb.testrunner.JMSTestRunner", null, f);

			BufferedReader stdInput = new BufferedReader(new InputStreamReader(CmdLineTool.p.getInputStream()));

	    	BufferedReader stdError = new BufferedReader(new InputStreamReader(CmdLineTool.p.getErrorStream()));

	    	// read the output from the command
	        String s = null;
	    	System.out.println("Here is the standard output of the command:\n");
	    	while ((s = stdInput.readLine()) != null) {
//	    		System.out.println(s);
	    	}

	    	// read any errors from the attempted command

	    	System.out.println("Here is the standard error of the command (if any):\n");
	    	while ((s = stdError.readLine()) != null) {
//	    		System.out.println(s);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    private File getCwd() {
  	  return new File("").getAbsoluteFile();
  	}
}


// This thread reads user input from the console and sends it to the server.
class Writer extends Thread {
	IVCTcommander ivctCommander;

    public Writer(IVCTcommander i) {
        super("CmdLineTool Writer");
        ivctCommander = i;
    }
    
    /*
     * Read user input and execute valid commands.
     */
    public void run() {
    	boolean gotNewCommand = false;
    	BufferedReader in = null;
        PrintStream out = null;
        String logLevelString = "default";
    	String sutNotSelected = new String("SUT not selected yet: use setSUT command first");
    	String tsNotSelected = new String("Testsuite not selected yet: use setTestSuite command first");

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
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listSUT: Warning extra parameter: " + split[1]);
                	}
                	List<String> suts1 = ivctCommander.listSUT();
                	if (suts1.isEmpty()) {
                		System.out.println("No SUT found. Please load a SUT onto the file system.");
                		break;
                	}
        			System.out.println("The SUTs are:");
        			for (String entry : suts1)
        			{
        				System.out.println(entry);
        			}
                    break;
                case "setSUT":
                case "ssut":
                	// Check any critical tasks are running
                	if (ivctCommander.checkCtTcTsRunning("setSUT", out)) {
                		break;
                	}

                	// Need an input parameter
                	if (split.length == 1) {
                		out.println("setSUT: Error missing SUT name");
                		break;
                	}

                	// get SUT list
                	List<String> suts2 = ivctCommander.listSUT();
                	if (suts2.isEmpty()) {
                		out.println("No SUT found. Please load a SUT onto the file system.");
                		break;
                	}
                	// check if SUT entered exists in SUT list
                	if (ivctCommander.checkSutKnown(split[1])) {
                		out.println("setSUT: unknown SUT: " + split[1]);
                		break;
                	}
                	ivctCommander.rtp.setSutName(split[1]);
                	IVCTcommander.resetSUT();
                	break;
                case "startConformanceTest":
                case "sct":
                	// Check any critical tasks are running
                	if (ivctCommander.checkCtTcTsRunning("startConformanceTest", out)) {
                		break;
                	}
                	// Cannot start conformance test if SUT is not set
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Warn for extra parameter
                	if (split.length > 1) {
                        out.println("startConformanceTest: Warning extra parameter: " + split[1]);
                	}
//                	command = new StartConformanceTest(ivctCommander);
                    out.println("startConformanceTest: Warning start conformance test logic is NOT IMPLEMENTED yet");
                	ivctCommander.setConformanceTestBool(true);
                    break;
                case "abortConformanceTest":
                case "act":
                	// Cannot abort conformance test if SUT is not set
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Cannot abort conformance test if not running
                	if (ivctCommander.getConformanceTestBool() == false) {
                        out.println("abortConformanceTest: Warning no conformance test is running");
                        break;
                	}
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("abortConformanceTest: Warning extra parameter: " + split[1]);
                	}
//                	command = new AbortConformanceTest(ivctCommander);
                    out.println("abortConformanceTest: Warning abort conformance test logic is NOT IMPLEMENTED yet");
                	ivctCommander.setConformanceTestBool(false);
                    break;
                case "listTestSchedules":
                case "lts":
                	if (ivctCommander.checkSUTselected()) {
                		System.out.println(sutNotSelected);
                		break;
                	}
                	// Warn for extra parameter
                	if (split.length > 1) {
                		out.println("listTestSchedules: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls2 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName());
                	for (String temp : ls2) {
                		System.out.println(temp);
                	}
                	break;
                case "startTestSchedule":
                case "sts":
                	// Check any critical tasks are running
                	if (ivctCommander.checkCtTcTsRunning("startTestSchedule", out)) {
                		break;
                	}
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Need an input parameter
                	if (split.length == 1) {
                        out.println("startTestSchedule: Warning missing test schedule name");
                        break;
                	}
                	List<String> ls1 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName());
                	boolean gotTestSchedule = false;
        			for (String entry : ls1) {
                		if (split[1].equals(entry)) {
                			gotTestSchedule = true;
                			break;
                		}
                	}
                	if (gotTestSchedule) {
                		ivctCommander.rtp.setTestSuiteName(split[1]);
                	} else {
                		out.println("Unknown test schedule " + split[1]);
                		break;
                	}
                	List<String> testcases0 = ivctCommander.rtp.getTestcases(split[1]);
            		
                	// Create a command structure to share between threads
                	// One thread works through the list
                	// Other thread receives test case verdicts and releases semaphore in first thread
                	// to start next test case
                	CommandCache commandCache = new CommandCache(split[1], testcases0);
                	
                	// This will create one thread, other thread listens to JMS bus anyway
                	command = new StartTestSchedule(commandCache, ivctCommander);
                	gotNewCommand = true;
                	RuntimeParameters.setTestScheduleName(split[1]);
                    break;
                case "abortTestSchedule":
                case "ats":
                	// Cannot abort test schedule if SUT is not set
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Cannot abort test schedule if conformance test is running
                	if (ivctCommander.getConformanceTestBool()) {
                        out.println("abortTestSchedule: Warning conformance test is running");
                        break;
                	}
                	// Cannot abort test schedule if it is not running
                	if (ivctCommander.getTestScheduleRunningBool() == false) {
                        out.println("abortTestSchedule: no test schedule is running");
                		break;
                	}
                	// Warn about extra parameters
                	if (split.length > 1) {
                        out.println("abortTestSchedule: Warning extra parameter: " + split[1]);
                	}
                	RuntimeParameters.setAbortTestScheduleBool(true);
//                	command = new AbortTestSchedule(ivctCommander);
                    out.println("abortTestSchedule: N.B: only stops running remaining test cases");
                    break;
                case "listTestCases":
                case "ltc":
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Warn about extra parameter
                	if (split.length > 1) {
                		out.println("listTestCases: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls3 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName());
                	for (String temp : ls3) {
                		System.out.println(temp);
                    	List<String> testcases1 = ivctCommander.rtp.getTestcases(temp);
                    	for (String testcase : testcases1) {
                    			System.out.println('\t' + testcase.substring(testcase.lastIndexOf(".") + 1));
                    	}			
                	}
                    break;
                case "startTestCase":
                case "stc":
                	// Check any critical tasks are running
                	if (ivctCommander.checkCtTcTsRunning("startTestCase", out)) {
                		break;
                	}
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Need an input parameter
                	if (split.length < 3) {
                		if (split.length < 2) {
                			out.println("startTestCase: Error missing badge name and test case id");
                		} else {
                			out.println("startTestCase: Error missing badge name or test case id");
                		}
                		break;
                	}
                	String fullTestcaseName = ivctCommander.rtp.getFullTestcaseName(split[1], split[2]);
                	if (ivctCommander.rtp.checkTestCaseNameKnown(split[1], fullTestcaseName)) {
                        out.println("startTestCase: unknown testSchedule testCase: " + split[1] + " " + split[2]);
                        break;
                	}
                	ivctCommander.rtp.startTestCase(split[1], fullTestcaseName);
                	RuntimeParameters.setTestCaseName(split[2]);
                    break;
                case "abortTestCase":
                case "atc":
                	// Cannot abort test case if SUT is not set
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Cannot abort test case if conformance test is running
                	if (ivctCommander.getConformanceTestBool()) {
                        out.println("abortTestCase: Warning conformance test is running");
                        break;
                	}
                	// Cannot abort test case if it is not running
                	if (ivctCommander.getTestCaseRunningBool() == false) {
                        out.println("abortTestCase: no test case is running");
                        break;
                	}
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("abortTestCase: Warning extra parameter: " + split[1]);
                	}
//                	command = new AbortTestCase(ivctCommander);
                    out.println("abortTestCase: Warning abort test case logic is NOT IMPLEMENTED yet");
                	break;
                case "setLogLevel":
                case "sll":
                	// Need an input parameter
                	if (split.length == 1) {
                		out.println("setLogLevel: Error missing log level: error, warning, info, debug or trace");
                		break;
                	}
                	if (split[1].equals("error") || split[1].equals("warning") || split[1].equals("info") || split[1].equals("debug") || split[1].equals("trace")) {
                		logLevelString = split[1];
                		CmdSetLogLevel cmdSetLogLevel = ivctCommander.rtp.createCmdSetLogLevel(split[1]);
                		cmdSetLogLevel.execute();
                    	command = null;
                	} else {
                        out.println("Unknown log level: " + split[1]);
                	}
                	break;
                case "listVerdicts":
                case "lv":
                	if (ivctCommander.checkSUTselected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listVerdicts: Warning extra parameter: " + split[1]);
                	}
                	ivctCommander.listVerdicts();
                	break;
                case "status":
                case "s":
                	String sut = ivctCommander.rtp.getSutName();
                	if (sut == null) {
                		out.println("SUT:");
                	} else {
                		out.println("SUT: " + sut);
                	}
                	String testScheduleName = RuntimeParameters.getTestScheduleName();
                	if (testScheduleName != null) {
                		if (ivctCommander.rtp.getTestScheduleRunningBool()) {
                			out.println("TestScheduleName: " + testScheduleName + " running");
                		} else {
                			out.println("TestScheduleName: " + testScheduleName + " finished");
                		}
                	}
                	String testCaseName = RuntimeParameters.getTestCaseName();
                	if (testCaseName != null) {
                		if (ivctCommander.rtp.getTestCaseRunningBool()) {
                			out.println("TestCaseName: " + testCaseName + " running");
                		} else {
                			out.println("TestCaseName: " + testCaseName + " finished");
                		}
                	}
                	out.println("loglevel: " + logLevelString);
                	break;
                case "terse":
                case "t":
                	ivctCommander.setCmdVerboseBool(false);
            		out.println("Command line output is now terse.");
                	break;
                case "verbose":
                case "v":
                	ivctCommander.setCmdVerboseBool(true);
            		out.println("Command line output is now verbose.");
                	break;
                case "quit":
                case "q":
                	// Check any critical tasks are running
                	if (ivctCommander.checkCtTcTsRunning("quit", out)) {
                		out.println("Do you want to quit anyway? (answer yes to quit UI)");
                        line = in.readLine();
                        if (line == null) break;
                        String splitQuit[]= line.trim().split("\\s+");
                        if (splitQuit[0].equalsIgnoreCase("yes") == false) {
                    		break;
                        }
                	}
                	CmdQuit cmdQuit = ivctCommander.rtp.createCmdQuit();
                	cmdQuit.execute();
                    out.println("quit");
                    System.exit(0);
                case "help":
                case "h":
                    out.println("listSUT (lsut) - list SUT folders");
                    out.println("setSUT (ssut) sut - set active SUT");
                    out.println("startConformanceTest (sct) - start conformance test");
                    out.println("abortConformanceTest (act) - abort conformance test");
                    out.println("listTestSchedules (lts) - list the available test schedules for the test suite");
                    out.println("startTestSchedule (sts) testSchedule - start the named test schedule");
                    out.println("abortTestSchedule (ats) - abort the running test schedule");
                    out.println("listTestCases (ltc) - list the available test cases for the test suite");
                    out.println("startTestCase (stc) testSchedule testcase - start the named test case");
                    out.println("abortTestCase (atc) - abort the running test case");
                    out.println("setLogLevel (sll) loglevel - set the log level for logging - error, warning, info, debug, trace");
                    out.println("listVerdicts (lv) - list the verdicts of the current session");
                    out.println("status (s) - display status information");
                    out.println("terse (t) - display only important session information");
                    out.println("verbose (v) - display detailed session information");
                    out.println("quit (q) - quit the program");
                    out.println("help (h) - display the help information");
                    break;
                default:
                    out.println("Unknown command: " + split[0]);
                    break;
                }
                
                if (gotNewCommand) {
                	semaphore.release();
                	gotNewCommand = false;
                }
                out.print("> ");
            }
        }
        catch (IOException e) { System.err.println("Writer: " + e); }
        finally { if (out != null) out.close(); }
        System.exit(0);
    }
}
}
