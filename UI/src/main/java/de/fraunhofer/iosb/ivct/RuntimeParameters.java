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

/*
 * This class holds variables to use as out parameters.
 */
package de.fraunhofer.iosb.ivct;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdQuit;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;

public final class RuntimeParameters {
    private static Logger LOGGER = LoggerFactory.getLogger(RuntimeParameters.class);
	private static boolean abortTestScheduleBool = false;
	private boolean testCaseRunningBool = false;
	private boolean testScheduleRunningBool = false;
	private boolean testSuiteNameNew = true;
	private int countSemaphore = 0;
	private CmdListBadges cmdListBadges = null;
	private CmdListSuT sutList = null;
	private PrintStream printStream = new PrintStream(System.out);
	private static Semaphore semaphore = new Semaphore(0);
	private String sutName = null;
	private static String testCaseName = null;
	private static String testScheduleName = null;
	private String testSuiteName = null;
	private SutDescription sutDescription;

    public RuntimeParameters () {
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

    /*
     * Check if a test case or test schedule are running.
     *
     * @param theCaller name of the calling method
     * @param out the calling method
     *
     * @return whether a critical task is running
     */
    protected boolean checkCtTcTsRunning(final String theCaller) {
        if (theCaller == null) {
            printStream.println("checkCtTcTsRunning: theCaller: null pointer found");
            return true;
        }
        if (testCaseRunningBool) {
            printStream.println(theCaller + ": Warning test case is running - command not allowed");
            return true;
        }
        if (testScheduleRunningBool) {
            printStream.println(theCaller + ": Warning test schedule is running - command not allowed");
            return true;
        }
        return false;
    }

	/*
	 * Some commands have no meaning without knowing the SUT involved.
	 */
	protected boolean checkSutNotSelected() {
	    if (sutName == null) {
	        return true;
	    }
	    return false;
	}
	
	protected String getFullTestcaseName(final String testsuite, final String testCase) {
		getTestSuiteNames();
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			String testSuiteNameTmp = s.getKey();
			if (testSuiteNameTmp.equals(testsuite)) {
				for (int i = 0; i < s.getValue().requirements.length; i++) {
					String tc = s.getValue().requirements[i].TC.substring(s.getValue().requirements[i].TC.lastIndexOf(".") + 1);
					if(tc.equals(testCase)) {
						return s.getValue().requirements[i].TC;
					}
				}
			}
		}
		
		return null;
	}

	/*
	 * 
	 */
	protected boolean startTestCase(final String theTestSuiteName, final String testCase) {
		CmdStartTc cmdStartTc = Factory.createCmdStartTc(sutName, theTestSuiteName, testCase, getTsRunFolder(theTestSuiteName));
		setTestCaseRunningBool(true);
		cmdStartTc.execute();
		return false;
	}

	/*
	 * Check if the test case name occurs in the test schedule.
	 */
	protected boolean checkTestCaseNameKnown(final String testsuite, final String testCase) {
		getTestSuiteNames();
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			String testSuiteNameTmp = s.getKey();
			if (testSuiteNameTmp.equals(testsuite)) {
				for (int i = 0; i < s.getValue().requirements.length; i++) {
					if(s.getValue().requirements[i].TC.equals(testCase)) {
						return false;
					}
				}
			}
		}

		return true;
	}
	
	/*
	 * Check if the test suite name changed since last check.
	 */
	protected boolean checkTestSuiteNameNew() {
		return testSuiteNameNew;
	}

	public static boolean getAbortTestScheduleBool() {
		return abortTestScheduleBool;
	}

	public static void setAbortTestScheduleBool(boolean b) {
		abortTestScheduleBool = b;
	}

	public boolean getTestCaseRunningBool() {
		return testCaseRunningBool;
	}

	public void setTestCaseRunningBool(boolean b) {
		testCaseRunningBool = b;
	}

	public boolean getTestScheduleRunningBool() {
		return testScheduleRunningBool;
	}

	public void setTestScheduleRunningBool(boolean b) {
		testScheduleRunningBool = b;
	}
	
	public void setTestSuiteNameUsed() {
		testSuiteNameNew = false;
	}
	
	public List<String> getTestcases(final String testsuite) {
		List<String> ls = new ArrayList<String>();

		getTestSuiteNames();
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			String testSuiteNameTmp = s.getKey();
			if (testSuiteNameTmp.equals(testsuite)) {
				for (int i = 0; i < s.getValue().requirements.length; i++) {
					int ind = ls.indexOf(s.getValue().requirements[i].TC);
					if (ind < 0) {
						ls.add(s.getValue().requirements[i].TC);
					}
				}
			}
		}

		return ls;
	}

	public List<String> getTestSuiteNames() {

		cmdListBadges = Factory.createCmdListBadges();
		cmdListBadges.execute();
		List<String> ls = new ArrayList<String>();

		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			String testSuiteNameTmp = s.getKey();
			ls.add(testSuiteNameTmp);
		}

		return ls;
	}
    
	private String getTsRunFolder(final String testsuite) {
		String tsRunFolder = null;
		getTestSuiteNames();
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			BadgeDescription bd = s.getValue();
			if (bd.ID.equals(testsuite) == true) {
				tsRunFolder = bd.tsRunTimeFolder;
				break;
			}
		}
		return tsRunFolder;
	}
	
	protected List<String> getSutBadges(final String theSutName, final boolean recursive) {
		List<String> badges = null;
		listSUTs();
		for (String it: sutList.sutMap.keySet()) {
			int len = sutList.sutMap.get(it).conformanceStatment.length;
			if (it.equals(theSutName)) {
				getTestSuiteNames();
				badges = new ArrayList<String>();
				String[] conformanceStatment = sutList.sutMap.get(it).conformanceStatment;
				for (int i = 0; i < len; i++) {
					int ind = badges.indexOf(conformanceStatment[i]);
					if (ind < 0) {
						badges.add(conformanceStatment[i]);
					}
					if (recursive) {
						if (getRecursiveBadges(badges, conformanceStatment[i])) {
							return null;
						}
					}
				}
				return badges;
			}
		}
		return badges;
	}
	
	private boolean getRecursiveBadges(List<String> badges, final String currentBadge) {
		for (Map.Entry<String, BadgeDescription> s : cmdListBadges.badgeMap.entrySet()) {
			BadgeDescription bd = s.getValue();
			if (bd.ID.equals(currentBadge)) {
				for (int j = 0; j < s.getValue().dependency.length; j++) {
					int indd = badges.indexOf(s.getValue().dependency[j]);
					if (indd < 0) {
						badges.add(s.getValue().dependency[j]);
						getRecursiveBadges(badges, s.getValue().dependency[j]);
					}
				}
			}
		}
		return false;
	}
	
	private void listSUTs() {
		sutList = Factory.createCmdListSut();
		sutList.execute();
	}

	protected String getSutName() {
		return sutName;
	}

	protected void setSutName(String theSutName) {
		// Same sut just return.
		if (theSutName.equals(sutName)) {
			return;
		}

		// Set the sut name.
		listSUTs();
		sutName = theSutName;
		sutDescription = sutList.sutMap.get(sutName);

		// Reset values.
		testCaseName = null;
		testScheduleName = null;
		testSuiteName = null;
	}

	protected String getSutDescription() {
		return this.sutDescription.description;
	}

	protected String getVendorName() {
		return this.sutDescription.vendor;
	}

	protected static String getTestCaseName() {
		return testCaseName;
	}

	protected static void setTestCaseName(String theTestCaseName) {
		testScheduleName = null;
		testCaseName = theTestCaseName;
	}

	protected static String getTestScheduleName() {
		return testScheduleName;
	}

	protected static void setTestScheduleName(String theTestScheduleName) {
		testCaseName = null;
		testScheduleName = theTestScheduleName;
	}

	protected String getTestSuiteName() {
		return testSuiteName;
	}

	protected void setTestSuiteName(String testSuiteName) {
		if (this.testSuiteName != null) {
			if (this.testSuiteName.equals(testSuiteName) == false) {
				this.testSuiteNameNew = true;
			}
		}
		this.testSuiteName = testSuiteName;
	}
	
	protected CmdSetLogLevel createCmdSetLogLevel(final String level) {
		String levelUpper = level.toUpperCase();
		LogLevel logLevel = LogLevel.valueOf(levelUpper);
		return Factory.createCmdSetLogLevel(logLevel);
	}
	
	protected CmdQuit createCmdQuit() {
		return Factory.createCmdQuit();
	}
}
