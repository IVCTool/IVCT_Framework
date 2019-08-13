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
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	private PrintStream printStream = new PrintStream(System.out);
	private static Semaphore semaphore = new Semaphore(0);
	private static String testCaseName = null;
	private static String testScheduleName = null;
	private String testSuiteName = null;

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
	 * 
	 */
	protected boolean startTestCase(final String sutName, final SutDescription sutDescription, final String theTestSuiteName, final String testCase) {
	    
		CmdStartTc cmdStartTc = Factory.createCmdStartTc(sutName, theTestSuiteName, testCase, sutDescription.settingsDesignator, sutDescription.federation);
		setTestCaseRunningBool(true);
		cmdStartTc.execute();
		return false;
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
	
	protected void resetSut() {
		// Reset values.
		testCaseName = null;
		testScheduleName = null;
		testSuiteName = null;
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
