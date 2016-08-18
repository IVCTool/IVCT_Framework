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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

class TestSuiteParameters {
	String packageName;
	String tsRunFolder;
	TestSuiteParameters() {
		this.packageName = null;
		this.tsRunFolder = null;
	}
}
  
public final class RuntimeParameters {
	private boolean conformanceTestBool = false;
	private boolean gotTestSuiteNames = false;
	private boolean testCaseRunningBool = false;
	private boolean testScheduleRunningBool = false;
	private boolean testSuiteNameNew = true;
    public Document domTestsuite;
	private int counter = 0;
	public Map<String, TestSuiteParameters> ls = new HashMap <String, TestSuiteParameters>();
	private static List<String> suts = null;
	public Map <String, List<String>> testsuiteTestcases = null;
	public String paramJson;
	private static String sutName = null;
	private static String testCaseName = null;
	private static String testScheduleName = null;
	private static String testSuiteName = null;

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

	/*
	 * Check if the test case name occurs in any test schedule.
	 */
	protected boolean checkTestCaseNameKnown(final String testCase) {
		for (Map.Entry<String, List<String>> entry : testsuiteTestcases.entrySet()) {
			for (String entry0 : entry.getValue()) {
				if (testCase.equals(entry0)) {
					return false;
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

	/*
	 * Some commands have no meaning without knowing the test suite involved.
	 */
	protected boolean checkSutAndTestSuiteSelected(String sutNotSelected, String tsNotSelected) {
		if (checkSUTselected()) {
			System.out.println(sutNotSelected);
			return true;
		}
		if (testSuiteName == null) {
			System.out.println(tsNotSelected);
			return true;
		}
		return false;
	}

	protected int fetchCounters(int n) {
		int ret = counter;
		counter += n;
		return ret;
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

	public void getTestSuiteNames() {

		if (gotTestSuiteNames) {
			return;
		}
		Element elem = domTestsuite.getDocumentElement();
		for (Node child = elem.getFirstChild(); child != null; child = child.getNextSibling()) {
			String s = child.getNodeName();
			if (s.compareTo("testSuites") == 0) {
				for (Node child0 = child.getFirstChild(); child0 != null; child0 = child0.getNextSibling()) {
					if (child0.getNodeName().compareTo("testSuite") == 0) {
						TestSuiteParameters testSuiteParameters = new TestSuiteParameters();
						testSuiteParameters.packageName = new String();
						testSuiteParameters.tsRunFolder = new String();
						String testSuiteName = new String();
						for (Node child1 = child0.getFirstChild(); child1 != null; child1 = child1.getNextSibling()) {
							if (child1.getNodeName().compareTo("name") == 0) {
								testSuiteName = child1.getFirstChild().getNodeValue();
							}
							if (child1.getNodeName().compareTo("packageName") == 0) {
								testSuiteParameters.packageName = child1.getFirstChild().getNodeValue();
							}
							if (child1.getNodeName().compareTo("tsRunFolder") == 0) {
								testSuiteParameters.tsRunFolder = child1.getFirstChild().getNodeValue();
							}
						}
						ls.put(testSuiteName, testSuiteParameters);
					}
				}
			}
		}
		gotTestSuiteNames = true;
	}
    
	public String getPackageName(final String testsuite) {
		String packageName = null;
		getTestSuiteNames();
		for (Map.Entry<String, TestSuiteParameters> temp : ls.entrySet()) {
			if (temp.getKey().equals(testsuite)) {
				packageName = temp.getValue().packageName;
				System.out.println(temp.getValue());
			}
		}
		return packageName;
	}
	
	public String getTsRunFolder() {
		String tsRunFolder = null;
		getTestSuiteNames();
		for (Map.Entry<String, TestSuiteParameters> temp : ls.entrySet()) {
			if (temp.getKey().equals(testSuiteName)) {
				tsRunFolder = temp.getValue().tsRunFolder;
			}
		}
		return tsRunFolder;
	}
	
	protected static List<String> getSUTS() {
		return suts;
	}

	protected static void setSUTS(String pathSutDir) {
		suts = new ArrayList<String>();
		File dir = new File(pathSutDir);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				suts.add(file.getName());
			}
		}
	}

	protected static String getSutName() {
		return sutName;
	}

	protected static void setSutName(String theSutName) {
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

	protected static String getTestSuiteName() {
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
}
