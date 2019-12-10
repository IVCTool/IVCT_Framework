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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;

import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdListTestSuites.TestCaseDesc;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.commander.CmdOperatorRequestListener;
import nato.ivct.commander.CmdQuit;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdUpdateSUT;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.SutPathsFiles;

class NamePosition {
	String string;
	int position;
}
/*
 * Dialog program using keyboard input.
 */
public class CmdLineTool {
    Thread writer;
	private boolean keepGoing = true;
    private nato.ivct.commander.Command command = null;
    private CmdUpdateSUT cmdUpdateSUT = null;
	private Semaphore semaphore = new Semaphore(0);
	public static Process p;
    public static IVCTcommander ivctCommander;
	private static CmdListTestSuites cmdListTestSuites = new CmdListTestSuites();
	private CmdListBadges cmdListBadges;
	private CmdListSuT sutList = null;

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
		(new CmdOperatorRequestListener(CmdLineTool.ivctCommander)).execute();
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
    				e.printStackTrace();
    			} catch (Exception e) {
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

	    	System.out.println("Here is the standard output of the command:\n");
	    	while ((stdInput.readLine()) != null) {
//	    		System.out.println(s);
	    	}

	    	// read any errors from the attempted command

	    	System.out.println("Here is the standard error of the command (if any):\n");
	    	while ((stdError.readLine()) != null) {
//	    		System.out.println(s);
	    	}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    private File getCwd() {
  	  return new File("").getAbsoluteFile();
  	}
}

class SutDescriptionVendor {
	String sutID;
	String sutName;
	String sutDescription;
	String vendorName;
	String version;
	String settingsDesignator;
	String sutFederateName;
	String federation;
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
     * @return the sut name, sut description and vendor in a class structure or
     *         null when error
     */
    private SutDescriptionVendor getSutDescriptionVendor(final PrintStream out, final String line) {
    	// Get name
    	NamePosition nameStr = getQuotedString(out, line, 0, "SUT name");
    	if (nameStr == null) {
    		return null;
    	}
    	String sutName = new String(nameStr.string);

    	// Generate ID
    	String sutID = sutName.replaceAll("\\W", "_");
    	// check if SUT entered exists in SUT list
        boolean sutFound = false;
        List<String> suts = sutPathsFiles.getSuts();
        for (String t : suts) {
            if (t.equals(sutID)) {
                sutFound = true;
            }
        }
        
        if (sutFound) {
			out.println("getSutDescriptionVendor: SUT already exists: " + sutID);
			return null;
		}

    	// Get description
    	NamePosition descriptionStr = getQuotedString(out, line, nameStr.position, "SUT description");
    	if (descriptionStr == null) {
    		return null;
    	}

    	// Get vendor
    	NamePosition vendorStr = getQuotedString(out, line, descriptionStr.position, "SUT vendor");
    	if (vendorStr == null) {
    		return null;
    	}

    	// Get version
    	NamePosition versionStr = getQuotedString(out, line, vendorStr.position, "SUT version");
    	if (versionStr == null) {
    		return null;
    	}

    	// Get settingsDesignator
    	NamePosition settingsDesignatorStr = getQuotedString(out, line, versionStr.position, "SUT settingsDesignator");
    	if (settingsDesignatorStr == null) {
    		return null;
    	}

    	// Get federate
    	NamePosition sutFederateNameStr = getQuotedString(out, line, settingsDesignatorStr.position, "SUT federateName");
    	if (sutFederateNameStr == null) {
    		return null;
    	}

    	// Get federate
    	NamePosition federationStr = getQuotedString(out, line, sutFederateNameStr.position, "SUT federation");
    	if (federationStr == null) {
    		return null;
    	}

    	SutDescriptionVendor sutDescriptionVendor = new SutDescriptionVendor();

    	sutDescriptionVendor.sutID = sutID;
    	sutDescriptionVendor.sutName = nameStr.string;
    	sutDescriptionVendor.sutDescription = descriptionStr.string;
    	sutDescriptionVendor.vendorName = vendorStr.string;
    	sutDescriptionVendor.version = versionStr.string;
    	sutDescriptionVendor.settingsDesignator = settingsDesignatorStr.string;
    	sutDescriptionVendor.sutFederateName = sutFederateNameStr.string;
    	sutDescriptionVendor.federation = federationStr.string;
    	return sutDescriptionVendor;
    }

    // Get quoted string
    private NamePosition getQuotedString(final PrintStream out, final String line, final int posInString, final String callerAdvice) {
    	NamePosition namePosition = new NamePosition();
        int posBegin = line.indexOf("\"", posInString + 1);
        if (posBegin == -1) {
    		out.println("getQuotedString: missing start quote: " + callerAdvice + ": " + line);
    		return null;
        }
        int posEnd = line.indexOf("\"", posBegin + 1);
        if (posEnd == -1) {
    		out.println("getQuotedString: missing end quote: " + callerAdvice + ": " + line);
    		return null;
        }
    	if (posEnd == posBegin + 1) {
    		out.println("getQuotedString: no string: " + callerAdvice + ": " + line);
    		return null;
    	}
    	namePosition.string = line.substring(posBegin + 1, posEnd);
    	namePosition.position = posEnd;
    	return namePosition;
    }

	public List<String> getTestcasesForBadge(final String badge) {
		List<String> ls = new ArrayList<String>();

        // Change badge to array of one
        Set <String> oneBadge = new HashSet<String>();
        oneBadge.add(badge);

        // Get IRs for badge
        Set<String> ir_set = new HashSet <String>();
        cmdListBadges.collectIrForCs(ir_set, oneBadge);

		// For each IR, get TestCaseDesc
		for (String ir : ir_set) {
			TestCaseDesc testCaseDesc;
			testCaseDesc = cmdListTestSuites.getTestCaseDescrforIr(ir);
			if (testCaseDesc != null) {
				if (ls.contains(testCaseDesc.tc) == false)
				ls.add(testCaseDesc.tc);
			}
		}

		return ls;
	}

	private List<String> getTestSuiteNames() {
		List<String> ls = new ArrayList<String>();

		cmdListTestSuites = Factory.createCmdListTestSuites();
		try {
			cmdListTestSuites.execute();
		} catch (Exception e) {
			e.printStackTrace();
			return ls;
		}

		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			String testSuiteNameTmp = s.getKey();
			ls.add(testSuiteNameTmp);
		}

		return ls;
	}

	/*
	 * Check if the test case name occurs in the badge.
	 */
	private boolean checkTestCaseNameKnown(final String badgeName, final String testCase) {

		List<String> ls = getTestcasesForBadge(badgeName);

		if (ls.contains(testCase)) {
			return true;
		}

		return false;
	}

	private String getFullTestcaseName(final String badgeName, final String testCase) {

		List<String> ls = getTestcasesForBadge(badgeName);

		for (String tc: ls) {
			if (testCase.contentEquals(tc.substring(tc.lastIndexOf(".") + 1))) {
				return tc;
			}
		}

		return null;
	}

	private boolean getRecursiveBadges(List<String> badges, final String currentBadge) {
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			BadgeDescription bd = s.getValue();
			if (bd.ID.equals(currentBadge)) {
				for (String entry : s.getValue().dependency) {
					int indd = badges.indexOf(entry);
					if (indd < 0) {
						badges.add(entry);
						getRecursiveBadges(badges, entry);
					}
				}
			}
		}
		return false;
	}

	protected SutDescription getSutDescription(final String sutName) {
		listSUTs();
		return sutList.sutMap.get(sutName);
	}

	private void listSUTs() {
		sutList = Factory.createCmdListSut();
		sutList.execute();
	}

	protected List<String> getSutBadges(final String theSutName, final boolean recursive) {
		List<String> badges = new ArrayList<String>();
		listSUTs();
		for (String it: sutList.sutMap.keySet()) {
			if (it.equals(theSutName)) {
				getTestSuiteNames();
				for (String entry : sutList.sutMap.get(it).badges) {
					int ind = badges.indexOf(entry);
					if (ind < 0) {
						badges.add(entry);
					}
					if (recursive) {
						if (getRecursiveBadges(badges, entry)) {
							return badges;
						}
					}
				}
				return badges;
			}
		}
		return badges;
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
    	String sutID = null;
    	SutDescription sutDescription = new SutDescription();
    	try {
			cmdListTestSuites.execute();
		} catch (Exception e3) {
			e3.printStackTrace();
		}
		cmdListBadges = Factory.createCmdListBadges();
		cmdListBadges.execute();
		listSUTs();

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
                	SutDescriptionVendor sutDescriptionVendorAdd = getSutDescriptionVendor(out, line);
                	if (sutDescriptionVendorAdd == null) {
                		break;
                	}
                	sutID = sutDescriptionVendorAdd.sutID;
                	sutDescription.ID = sutDescriptionVendorAdd.sutID;
                	sutDescription.name = sutDescriptionVendorAdd.sutName;
                	sutDescription.description = sutDescriptionVendorAdd.sutDescription;
                	sutDescription.vendor = sutDescriptionVendorAdd.vendorName;
                	sutDescription.version = sutDescriptionVendorAdd.version;
                	sutDescription.settingsDesignator = sutDescriptionVendorAdd.settingsDesignator;
                	sutDescription.sutFederateName = sutDescriptionVendorAdd.sutFederateName;
                	sutDescription.federation = sutDescriptionVendorAdd.federation;
                	sutDescription.badges.clear();
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	break;
					}
                    List<String> sutsAsut = sutPathsFiles.getSuts();
                	if (sutsAsut.isEmpty()) {
                		out.println("addSUT: cannot load SUT onto the file system.");
                		break;
                	}
                	ivctCommander.rtp.resetSut();
                    ivctCommander.resetSUT();
                	break;
                case "modifySUTname":
                case "msnam":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTname: need SUT name");
                        break;
                	}
                	NamePosition namePosition = getQuotedString(out, line, 0, "SUT name");
                	if (namePosition == null) {
                		break;
                	}
                	sutDescription.name = namePosition.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTversion":
                case "msver":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTname: need SUT name");
                        break;
                	}
                	NamePosition versionStr = getQuotedString(out, line, 0, "SUT version");
                	if (versionStr == null) {
                		break;
                	}
                	sutDescription.version = versionStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTdescription":
                case "msdes":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTname: need SUT name");
                        break;
                	}
                	NamePosition descriptionStr = getQuotedString(out, line, 0, "SUT description");
                	if (descriptionStr == null) {
                		break;
                	}
                	sutDescription.description = descriptionStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTvendor":
                case "msven":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTname: need SUT name");
                        break;
                	}
                	NamePosition vendorStr = getQuotedString(out, line, 0, "SUT vendor");
                	if (vendorStr == null) {
                		break;
                	}
                	sutDescription.vendor = vendorStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTsettingsDesignator":
                case "mssetdes":
                    // Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("mssetdes")) {
                        break;
                    }
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                   	// Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("setSettingsDesignator")) {
                		break;
                	}
                 	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTsettingsDesignator: need SettingsDesignator");
                        break;
                	}
                	// Warn about extra parameter
                	if (split.length > 2) {
                        out.println("modifySUTsettingsDesignator: Warning extra parameter: " + split[3]);
                	}
                	NamePosition settingsDesignatorStr = getQuotedString(out, line, 0, "modifySUTsettingsDesignator");
                	if (settingsDesignatorStr == null) {
                		break;
                	}
                	sutDescription.settingsDesignator = settingsDesignatorStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTfederate":
                case "msfederate":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTfederate: need federate name");
                        break;
                	}
                	NamePosition federateStr = getQuotedString(out, line, 0, "SUT federate");
                	if (federateStr == null) {
                		break;
                	}
                	sutDescription.sutFederateName = federateStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "modifySUTfederation":
                case "msfederation":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("modifySUTname: need SUT name");
                        break;
                	}
                	NamePosition federationStr = getQuotedString(out, line, 0, "SUT federation");
                	if (federationStr == null) {
                		break;
                	}
                	sutDescription.federation = federationStr.string;
                	cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e2) {
						e2.printStackTrace();
	                	command = null;
	                	break;
					}
                	command = null;
                	break;
                case "listBadges":
                case "lbg":
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listBadges: Warning extra parameter: " + split[1]);
                	}
            		List<String> badges = null;
            		badges = getTestSuiteNames();
            		for (String entry  : badges) {
                        out.println(entry);
            		}
                	break;
                case "addBadge":
                case "abg":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("addBadge: need badge name");
                        break;
                	}
                	List<String> allBadges = getTestSuiteNames();
                	List<String> sutBadges = getSutBadges(sutID, false);
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
                    Set<String> badgesAbg = new HashSet<String>();
                	for (String Entry : sutBadges) {
                		if (allBadges.contains(newBadge) == false) {
                            out.println("addBadge: unknown badge name: " + newBadge);
                            errorOccurred = true;
                            break;
                		}
                        badgesAbg.add(new String (Entry));
                	}
                	if (errorOccurred) {
                		break;
                	}
                	sutDescription = getSutDescription(sutID);
                	for (String entry : badgesAbg) {
						sutDescription.badges.add(entry);
					}
					cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e1) {
						e1.printStackTrace();
					}
                	command = null;
                	break;
                case "deleteBadge":
                case "dbg":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length < 2) {
                		out.println("deleteBadge: need badge name(s)");
                		break;
                	}
                	List<String> sutBadgesDbg = getSutBadges(sutID, false);
                	String newBadgeDbg = null;
                	for (int i = 0; i < split.length - 1; i++) {
                		newBadgeDbg = split[i + 1];
                		if (sutBadgesDbg.contains(newBadgeDbg) == false) {
                			out.println("deleteBadge: badge name not in SUT list: " + newBadgeDbg);
                			break;
                		}
            			sutBadgesDbg.remove(newBadgeDbg);
                	}
                    Set<String> badgesDbg = new HashSet<String>();
                	for (String Entry : sutBadgesDbg) {
                        badgesDbg.add(new String (Entry));
                	}
                	sutDescription = getSutDescription(sutID);
                	sutDescription.badges = badgesDbg;
					cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e) {
						e.printStackTrace();
					}
                	command = null;
                	break;
                //TODO: Check if this case is still needed
                case "ssd":
                case "setSettingsDesignator":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Check any critical tasks are running
                    if (ivctCommander.rtp.checkCtTcTsRunning("setSettingsDesignator")) {
                		break;
                	}
                	// Warn about missing parameter
                	if (split.length < 2) {
                        out.println("setSettingsDesignator: Warning missing SettingsDesignator");
                	}
                	// Warn about extra parameter
                	if (split.length > 2) {
                        out.println("setSettingsDesignator: Warning extra parameter: " + split[3]);
                	}
                	sutDescription.settingsDesignator = split[1];
					cmdUpdateSUT = Factory.createCmdUpdateSUT(sutDescription);
                	try {
                		cmdUpdateSUT.execute();
					} catch (Exception e) {
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
                    sutID = split[1];
                	ivctCommander.rtp.resetSut();
                	sutDescription = getSutDescription(sutID);
                    ivctCommander.resetSUT();
                	break;
                case "listTestSchedules":
                case "lts":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Warn for extra parameter
                	if (split.length > 1) {
                		out.println("listTestSchedules: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls2 = getSutBadges(sutID, true);
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
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Need an input parameter
                	if (split.length == 1) {
                        out.println("startTestSchedule: Warning missing test schedule name");
                        break;
                	}
                	List<String> ls1 = getSutBadges(sutID, true);
                	boolean gotTestSchedule = false;
        			for (String entry : ls1) {
                		if (split[1].equals(entry)) {
                			gotTestSchedule = true;
                			break;
                		}
                	}
                	if (gotTestSchedule == false) {
                		out.println("Unknown test schedule " + split[1]);
                		break;
                	}
                	List<String> testcases0 = getTestcasesForBadge(split[1]);
            		
                	// Create a command structure to share between threads
                	// One thread works through the list
                	// Other thread receives test case verdicts and releases semaphore in first thread
                	// to start next test case
                	CommandCache commandCache = new CommandCache(testcases0);
                	
                	// This will create one thread, other thread listens to JMS bus anyway
                	command = new StartTestSchedule(sutID, sutDescription, cmdListTestSuites, commandCache, ivctCommander);
                	gotNewCommand = true;
                	ivctCommander.rtp.setTestScheduleName(split[1]);
                    break;
                case "abortTestSchedule":
                case "ats":
                	// Cannot abort test schedule if SUT is not set
            	    if (sutID == null) {
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
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Warn about extra parameter
                	if (split.length > 1) {
                		out.println("listTestCases: Warning extra parameter: " + split[1]);
                	}
                	List<String> ls3 = getSutBadges(sutID, true);
                	for (String temp : ls3) {
                		System.out.println(temp);
                    	List<String> testcases1 = getTestcasesForBadge(temp);
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
            	    if (sutID == null) {
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
                	String fullTestcaseName = getFullTestcaseName(split[1], split[2]);
                	if (checkTestCaseNameKnown(split[1], fullTestcaseName) == false) {
                        out.println("startTestCase: unknown testSchedule testCase: " + split[1] + " " + split[2]);
                        break;
                	}
                	TestSuiteDescription tsd = cmdListTestSuites.getTestSuiteForTc(fullTestcaseName);
                	ivctCommander.rtp.startTestCase(sutID, sutDescription, tsd.id, fullTestcaseName);
                	ivctCommander.rtp.setTestCaseName(split[2]);
                    break;
                case "abortTestCase":
                case "atc":
                	// Cannot abort test case if SUT is not set
            	    if (sutID == null) {
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
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	// Warn about extra parameter
                	if (split.length > 1) {
                        out.println("listVerdicts: Warning extra parameter: " + split[1]);
                	}
                	ivctCommander.listVerdicts(sutID);
                	break;
                case "status":
                case "s":
                	if (sutID == null) {
                		out.println("SUT:");
                	} else {
                		sutDescription = getSutDescription(sutID);
                		out.println("SUT ID: " + sutDescription.ID);
                		out.println("SUT name: " + sutDescription.name);
                		out.println("SUT version: " + sutDescription.version);
                		out.println("SUT description: " + sutDescription.description);
                		out.println("SUT vendor: " + sutDescription.vendor);
                		out.println("SUT settingsDesignator: " + sutDescription.settingsDesignator);
                		out.println("SUT sutFederateName: " + sutDescription.sutFederateName);
                		out.println("SUT federation: " + sutDescription.federation);
                	}
                	String testScheduleName = ivctCommander.rtp.getTestScheduleName();
                	if (testScheduleName != null) {
                		if (ivctCommander.rtp.getTestScheduleRunningBool()) {
                			out.println("TestScheduleName: " + testScheduleName + " running");
                		} else {
                			out.println("TestScheduleName: " + testScheduleName + " finished");
                		}
                	}
                	String testCaseName = ivctCommander.rtp.getTestCaseName();
                	if (testCaseName != null) {
                		if (ivctCommander.rtp.getTestCaseRunningBool()) {
                			out.println("TestCaseName: " + testCaseName + " running");
                		} else {
                			out.println("TestCaseName: " + testCaseName + " finished");
                		}
                	}
                	out.println("loglevel: " + logLevelString);
                	if (ivctCommander.isOperatorRequestOutstanding()) {
                    	out.println("OPERATOR REQUEST OUTSTANDING: " + ivctCommander.getOperatorRequestTcId() + " " + ivctCommander.getOperatorRequestText());
                	}
                	break;
                case "cnf":
            	    if (sutID == null) {
                		out.println(sutNotSelected);
                		break;
            	    }
                	boolean b = ivctCommander.isOperatorRequestOutstanding();
                	if (b == false) {
                    	out.println("cnf: No operator request outstanding");
                		break;
                	}
                	// Need an input parameter
                	if (split.length < 2) {
                        out.println("cnf: Error missing true/false value");
                	}
                	if (split[1].contentEquals("true")) {
                        ivctCommander.sendOperatorConfirmation(ivctCommander.getOperatorRequestTcId(), true, "beta");
                	} else {
						if (split[1].contentEquals("false")) {
	                        ivctCommander.sendOperatorConfirmation(ivctCommander.getOperatorRequestTcId(), false, "beta");
						} else {
	                        out.println("cnf: incorrect true/false value - found: " + split[1]);
						}
					}
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
                    out.println("asut (addSUT) \"sut name text quoted\" \"description text quoted\" \"vendor text quoted\" \"version text quoted\" \"settings designator text quoted\" \"sut federate name quoted\" \"federation text quoted\" - add an SUT");
                    out.println("msnam (modifySUTname) \"name text quoted\" - modify the SUT name");
                    out.println("msver (modifySUTversion) \"version text quoted\" - modify the SUT version");
                    out.println("msdes (modifySUTdescription) \"description text quoted\" - modify the SUT description");
                    out.println("msven (modifySUTvendor) \"vendor name text quoted\" - modify the SUT vendor");
                    out.println("mssetdes (modifySUTsettingsDesignator) \"settings designator text quoted\" - modify the SUT settingsDesignator");
                    out.println("msfederate (modifySUTfederate) \"federate name text quoted\" - modify the SUT federate name");
                    out.println("msfederation (modifySUTfederation) \"federation name text quoted\" - modify the SUT federation");
                    out.println("lbg (listBadges) - list all available badges");
                    out.println("abg (addBadge) badge ... badge - add one or more badges to SUT");
                    out.println("dbg (deleteBadge) badge ... badge - delete one or more badges from SUT");
                    //TODO: Check if still relevant (see TODO above)
                    out.println("ssd (setSettingsDesignator) settingsDesignator - set settings designator");
                    out.println("lsut (listSUT) - list SUT folders");
                    out.println("ssut (setSUT) sut - set active SUT");
                    out.println("lts (listTestSchedules) - list the available test schedules for the test suite");
                    out.println("sts (startTestSchedule) testSchedule - start the named test schedule");
                    out.println("ats (abortTestSchedule) - abort the running test schedule");
                    out.println("ltc (listTestCases) - list the available test cases for the test suite");
                    out.println("stc (startTestCase) testSchedule testcase - start the named test case");
                    out.println("atc (abortTestCase) - abort the running test case");
                    out.println("sll (setLogLevel) loglevel - set the log level for logging - error, warning, info, debug, trace");
                    out.println("lv (listVerdicts) - list the verdicts of the current session");
                    out.println("s (status) - display status information");
                    out.println("q (quit) - quit the program");
                    out.println("h (help) - display the help information");
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
