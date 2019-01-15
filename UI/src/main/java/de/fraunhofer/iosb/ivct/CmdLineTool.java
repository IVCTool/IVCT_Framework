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
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Semaphore;

import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import nato.ivct.commander.BadgeTcParam;
import nato.ivct.commander.CmdQuit;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutPathsFiles;
import java.util.Set;

/*
 * Dialog program using keyboard input.
 */
public class CmdLineTool {
    Thread writer;
	private boolean keepGoing = true;
    private nato.ivct.commander.Command command = null;
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

        // Handle callbacks
		Factory.initialize();

    	try {
    		new CmdLineTool();
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.out.println("CmdLineTool: new IVCTcommander: " + e);
    		return;
    	}
		(new CmdStartTestResultListener(CmdLineTool.ivctCommander)).execute();
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
    			} catch (Exception e) {
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

class SutDescriptionVendor {
	String sutName;
	String sutDescription;
	String vendorName;
}

// This thread reads user input from the console and sends it to the server.
class Writer extends Thread {
	IVCTcommander ivctCommander;
	SutPathsFiles sutPathsFiles = Factory.getSutPathsFiles();

    public Writer(IVCTcommander i) {
        super("CmdLineTool Writer");
        ivctCommander = i;
    }
    
    /**
     * This method will get the sut name, sut description and vendor from the user input
     * @param out the logging stream
     * @param line the user input
     * @param addMode whether add or modify mode
     * @return the sut name, sut description and vendor in a class structure or
     *         null when error
     */
    private SutDescriptionVendor getSutDescriptionVendor(final PrintStream out, final String line, final boolean addMode) {
        String split[]= line.trim().split("\\s+");
    	String sutName = split[1];
    	int posSutName = sutName.indexOf("\"");
    	if (posSutName != -1) {
    		out.println("getSutDescriptionVendor: SUT name must NOT be quoted: " + line);
    		return null;
    	}
    	// check if SUT entered exists in SUT list
    	if (addMode) {
            boolean sutFound = false;
            List<String> suts = sutPathsFiles.getSuts();
            for (String t : suts) {
                if (t.equals(split[1])) {
                    sutFound = true;
                }
            }
            if (sutFound) {
    			out.println("getSutDescriptionVendor: SUT already exists: " + sutName);
    			return null;
    		}
    	}
        int posDesc = line.indexOf("\"");
        if (posDesc == -1) {
    		out.println("getSutDescriptionVendor: missing description start quote: " + line);
    		return null;
        }
        int posDescEnd = line.indexOf("\"", posDesc + 1);
        if (posDescEnd == -1) {
    		out.println("getSutDescriptionVendor: missing description end quote: " + line);
    		return null;
        }
    	if (posDescEnd == posDesc + 1) {
    		out.println("getSutDescriptionVendor: no description: " + line);
    		return null;
    	}
    	String sutDescription = line.substring(posDesc + 1, posDescEnd);
        int posVen = line.indexOf("\"", posDescEnd + 1);
        if (posVen == -1) {
    		out.println("getSutDescriptionVendor: missing vendor start quote: " + line);
    		return null;
        }
        int posVenEnd = line.indexOf("\"", posVen + 1);
        if (posVenEnd == -1) {
    		out.println("getSutDescriptionVendor: missing vendor end quote: " + line);
    		return null;
        }
    	if (posVenEnd == posVen + 1) {
    		out.println("getSutDescriptionVendor: no vendor: " + line);
    		return null;
    	}
    	String vendorName = line.substring(posVen + 1, posVenEnd);
    	SutDescriptionVendor sutDescriptionVendor = new SutDescriptionVendor();
    	sutDescriptionVendor.sutName = sutName;
    	sutDescriptionVendor.sutDescription = sutDescription;
    	sutDescriptionVendor.vendorName = vendorName;
    	return sutDescriptionVendor;
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

    	try {
            String line;
            in = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintStream(System.out);
            out.println ("\nEnter command: or help (h)");
            out.print("> ");
            while(true) {
                line = in.readLine();
                if (line == null) break;
                String split[]= line.trim().split("\\s+");
                switch(split[0]) {
                case "":
                	break;
                case "addSUT":
                case "asut":
            		// If property is not set, do not have any access to any SUTs
            		if (Factory.props.containsKey(Factory.IVCT_SUT_HOME_ID) == false) {
            			out.println("No IVCT_SUT_HOME_ID environment variable found: cannot add SUT");
            			break;
            		}
            		// The SUT is placed in a known folder
            		String sutsDir = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
            		File f = new File(sutsDir);
            		if (f.exists() == false) {
            			out.println("No SUT directory found");
            			break;
            		}
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("addSUT: need SUT name");
                        break;
                	}
                	SutDescriptionVendor sutDescriptionVendorAdd = getSutDescriptionVendor(out, line, true);
                	if (sutDescriptionVendorAdd == null) {
                		break;
                	}
                	command = Factory.createCmdUpdateSUT(sutDescriptionVendorAdd.sutName, sutDescriptionVendorAdd.sutDescription, sutDescriptionVendorAdd.vendorName, null);
                	try {
						command.execute();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                    List<String> sutsAsut = sutPathsFiles.getSuts();
                	if (sutsAsut.isEmpty()) {
                		out.println("addSUT: cannot load SUT onto the file system.");
                		break;
                	}
                	ivctCommander.rtp.setSutName(split[1]);
                    ivctCommander.resetSUT();
                	break;
                case "modifySUT":
                case "msut":
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUT: need SUT name");
                        break;
                	}
                    List<String> sutsMsut = sutPathsFiles.getSuts();
                    if (sutsMsut.isEmpty()) {
                        out.println("modifySUT: cannot find any SUT on the file system.");
                        break;
                    }
                    boolean sutFoundMsut = false;
                    for (String t : sutsMsut) {
                        if (t.equals(split[1])) {
                            sutFoundMsut = true;
                        }
                    }
                    if (sutFoundMsut == false) {
                        out.println("modifySUT: cannot find SUT: " + split[1]);
                        break;
                    }
                	SutDescriptionVendor sutDescriptionVendorModify = getSutDescriptionVendor(out, line, false);
                	if (sutDescriptionVendorModify == null) {
                		break;
                	}
                	command = Factory.createCmdUpdateSUT(sutDescriptionVendorModify.sutName, sutDescriptionVendorModify.sutDescription, sutDescriptionVendorModify.vendorName, null);
                	try {
						command.execute();
					} catch (Exception e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	ivctCommander.rtp.setSutName(split[1]);
                    ivctCommander.resetSUT();
                	break;
                case "listBadges":
                case "lbg":
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listBadges: Warning extra parameter: " + split[1]);
                	}
            		List<String> badges = null;
            		badges = ivctCommander.rtp.getTestSuiteNames();
            		for (String entry  : badges) {
                        out.println(entry);
            		}
                	break;
                case "addBadge":
                case "abg":
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                		out.println(sutNotSelected);
                		break;
                	}
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("addBadge: need badge name");
                        break;
                	}
                	List<String> allBadges = ivctCommander.rtp.getTestSuiteNames();
                	List<String> sutBadges = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName(), false);
                	boolean errorOccurred = false;
                	String newBadge = null;
                	for (int i = 0; i < split.length - 1; i++) {
                		newBadge = split[i + 1];
                		if (allBadges.contains(newBadge) == false) {
                            out.println("addBadge: unknown badge name: " + newBadge);
                            errorOccurred = true;
                            break;
                		}
                		if (sutBadges.contains(newBadge) == false) {
                		sutBadges.add(newBadge);
                		}
                	}
                    Set<BadgeTcParam> badgeTcParams = new HashSet<BadgeTcParam>();
                	for (String Entry : sutBadges) {
                		if (allBadges.contains(newBadge) == false) {
                            out.println("addBadge: unknown badge name: " + newBadge);
                            errorOccurred = true;
                            break;
                		}
                        BadgeTcParam badgeTcParam = new BadgeTcParam();
                        badgeTcParam.id = new String (Entry);
                        badgeTcParam.tcParam = null;
                        badgeTcParams.add(badgeTcParam);
                	}
                	if (errorOccurred) {
                		break;
                	}
                	command = Factory.createCmdUpdateSUT(ivctCommander.rtp.getSutName(), ivctCommander.rtp.getSutDescription(), ivctCommander.rtp.getVendorName(), badgeTcParams);
                	try {
						command.execute();
					} catch (Exception e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                	command = null;
                	break;
                case "deleteBadge":
                case "dbg":
                	if (ivctCommander.rtp.checkSutNotSelected()) {
                		out.println(sutNotSelected);
                		break;
                	}
                	// Need an input parameter
                	if (split.length < 2) {
                		out.println("deleteBadge: need badge name(s)");
                		break;
                	}
                	List<String> sutBadgesDbg = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName(), false);
                	String newBadgeDbg = null;
                	for (int i = 0; i < split.length - 1; i++) {
                		newBadgeDbg = split[i + 1];
                		if (sutBadgesDbg.contains(newBadgeDbg) == false) {
                			out.println("deleteBadge: badge name not in SUT list: " + newBadgeDbg);
                			break;
                		}
            			sutBadgesDbg.remove(newBadgeDbg);
                	}
                    Set<BadgeTcParam> badgeTcParamsDbg =new HashSet<BadgeTcParam>();
                	for (String Entry : sutBadgesDbg) {
                        BadgeTcParam badgeTcParamDbg = new BadgeTcParam();
                        badgeTcParamDbg.id = new String (Entry);
                        badgeTcParamDbg.tcParam = null;
                        badgeTcParamsDbg.add(badgeTcParamDbg);
                	}
                	command = Factory.createCmdUpdateSUT(ivctCommander.rtp.getSutName(), ivctCommander.rtp.getSutDescription(), ivctCommander.rtp.getVendorName(), badgeTcParamsDbg);
                	try {
						command.execute();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	command = null;
                	break;
                case "listSUT":
                case "lsut":
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listSUT: Warning extra parameter: " + split[1]);
                	}
                    List<String> sutsLsut = sutPathsFiles.getSuts();
                    if (sutsLsut.isEmpty()) {
                		System.out.println("No SUT found. Please load a SUT onto the file system.");
                		break;
                	}
        			System.out.println("The SUTs are:");
                    for (String entry : sutsLsut) {
        				System.out.println(entry);
        			}
                    break;
                case "setSUT":
                case "ssut":
                	// Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("setSUT")) {
                		break;
                	}

                	// Need an input parameter
                	if (split.length == 1) {
                		out.println("setSUT: Error missing SUT name");
                		break;
                	}

                	// get SUT list
                    List<String> sutsSetSut = sutPathsFiles.getSuts();
                    if (sutsSetSut.isEmpty()) {
                		out.println("No SUT found. Please load a SUT onto the file system.");
                		break;
                	}
                	// check if SUT entered exists in SUT list
                    boolean sutFoundSetSut = false;
                    for (String t : sutsSetSut) {
                        if (t.equals(split[1])) {
                            sutFoundSetSut = true;
                        }
                    }
                    if (sutFoundSetSut == false) {
                		out.println("setSUT: unknown SUT: " + split[1]);
                		break;
                	}
                	ivctCommander.rtp.setSutName(split[1]);
                    ivctCommander.resetSUT();
                	break;
                case "listTestSchedules":
                case "lts":
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                		System.out.println(sutNotSelected);
                		break;
                	}
                	// Warn for extra parameter
                	if (split.length > 1) {
                		out.println("listTestSchedules: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls2 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName(), true);
                	for (String temp : ls2) {
                		System.out.println(temp);
                	}
                	break;
                case "startTestSchedule":
                case "sts":
                	// Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("startTestSchedule")) {
                		break;
                	}
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Need an input parameter
                	if (split.length == 1) {
                        out.println("startTestSchedule: Warning missing test schedule name");
                        break;
                	}
                	List<String> ls1 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName(), true);
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
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Cannot abort test schedule if it is not running
                    if (ivctCommander.rtp.getTestScheduleRunningBool() == false) {
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
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Warn about extra parameter
                	if (split.length > 1) {
                		out.println("listTestCases: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls3 = ivctCommander.rtp.getSutBadges(ivctCommander.rtp.getSutName(), true);
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
                    if (ivctCommander.rtp.checkCtTcTsRunning("startTestCase")) {
                		break;
                	}
                    if (ivctCommander.rtp.checkSutNotSelected()) {
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
                    if (ivctCommander.rtp.checkSutNotSelected()) {
                        out.println(sutNotSelected);
                		break;
                	}
                	// Cannot abort test case if it is not running
                    if (ivctCommander.rtp.getTestCaseRunningBool() == false) {
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
                        // Do NOT allow ERROR, since end test case relies on a warn message to close log file.
                        out.println("setLogLevel: Error missing log level: warning, info, debug or trace");
                		break;
                	}
                    if (split[1].equalsIgnoreCase("warning") || split[1].equalsIgnoreCase("info") || split[1].equalsIgnoreCase("debug") || split[1].equalsIgnoreCase("trace")) {
                		logLevelString = split[1].toLowerCase();
                		CmdSetLogLevel cmdSetLogLevel = ivctCommander.rtp.createCmdSetLogLevel(split[1]);
                		cmdSetLogLevel.execute();
                    	command = null;
                	} else {
                        out.println("Unknown log level: " + split[1]);
                	}
                	break;
                case "listVerdicts":
                case "lv":
                    if (ivctCommander.rtp.checkSutNotSelected()) {
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
                case "quit":
                case "q":
                	// Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("quit")) {
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
                    out.println("addSUT (asut) sut \"description text quoted\" \"vendor text quoted\"- add an SUT");
                    out.println("modifySUT (msut) sut \"description text quoted\" \"vendor text quoted\"- modify an SUT");
                    out.println("listBadges (lbg) - list all available badges");
                    out.println("addBadge (abg) badge ... badge - add one or more badges to SUT");
                    out.println("deleteBadge (dbg) badge ... badge - delete one or more badges from SUT");
                    out.println("listSUT (lsut) - list SUT folders");
                    out.println("setSUT (ssut) sut - set active SUT");
                    out.println("listTestSchedules (lts) - list the available test schedules for the test suite");
                    out.println("startTestSchedule (sts) testSchedule - start the named test schedule");
                    out.println("abortTestSchedule (ats) - abort the running test schedule");
                    out.println("listTestCases (ltc) - list the available test cases for the test suite");
                    out.println("startTestCase (stc) testSchedule testcase - start the named test case");
                    out.println("abortTestCase (atc) - abort the running test case");
                    out.println("setLogLevel (sll) loglevel - set the log level for logging - error, warning, info, debug, trace");
                    out.println("listVerdicts (lv) - list the verdicts of the current session");
                    out.println("status (s) - display status information");
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
        finally {
        	if (out != null) {
        		out.close();
        	}
        }
    }
}
}
