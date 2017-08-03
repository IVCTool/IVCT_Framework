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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import de.fraunhofer.iosb.tc_lib.LineUtil;
import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdQuit;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.Factory;

public final class RuntimeParameters {
	private static boolean abortTestScheduleBool = false;
	private boolean conformanceTestBool = false;
	private boolean testCaseRunningBool = false;
	private boolean testScheduleRunningBool = false;
	private boolean testSuiteNameNew = true;
	private int counter = 0;
	private CmdListBadges cmdListBadges = null;
	private CmdListSuT sutList = null;
	private static List<String> suts = null;
	private String sutName = null;
	private static String testCaseName = null;
	private static String testScheduleName = null;
	private String testSuiteName = null;
    private Factory ivctCmdFactory = null;

    public RuntimeParameters () {
    	ivctCmdFactory = new Factory();
    	try {
    		ivctCmdFactory.initialize();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

	protected boolean checkSutKnown(final String sut) {
		for (String entry : suts) {
			if (sut.equals(entry)) {
				return false;
			}
		}

		return true;
	}

	/*
	 * Some commands have no meaning without knowing the SUT involved.
	 */
	protected boolean checkSUTselected() {
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
		CmdStartTc cmdStartTc = ivctCmdFactory.createCmdStartTc(sutName, theTestSuiteName, testCase, getTsRunFolder(theTestSuiteName));
		cmdStartTc.execute();
		return false;
	}

	/*
	 * Check if the test case name occurs in the test schedule.
	 */
	protected boolean checkTestCaseNameKnown(final String testsuite, final String testCase) {
		List<String> ls = new ArrayList<String>();
		
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

	protected int fetchCounters(int n) {
		int ret = counter;
		counter += n;
		return ret;
	}

	public static boolean getAbortTestScheduleBool() {
		return abortTestScheduleBool;
	}

	public static void setAbortTestScheduleBool(boolean b) {
		abortTestScheduleBool = b;
	}

	public boolean getConformanceTestBool() {
		return conformanceTestBool;
	}

	public void setConformanceTestBool(boolean b) {
		conformanceTestBool = b;
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

		cmdListBadges = ivctCmdFactory.createCmdListBadges();
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
	
	protected static List<String> getSUTS() {
		return suts;
	}

	protected List<String> getSutBadges(final String theSutName) {
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
					if (getRecursiveBadges(badges, conformanceStatment[i])) {
						return null;
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
		suts = new LinkedList<>();
		sutList = ivctCmdFactory.createCmdListSut();
		sutList.execute();
	}

	protected void setSUTS() {
		listSUTs();
		for (String it: sutList.sutMap.keySet()) {
			suts.add(it);
		}
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
		sutName = theSutName;

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
		return ivctCmdFactory.createCmdSetLogLevel(level);
	}
	
	protected CmdQuit createCmdQuit() {
		return ivctCmdFactory.createCmdQuit();
	}
}
