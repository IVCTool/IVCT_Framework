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
import java.util.List;
import java.util.Map;

public final class RuntimeParameters {
	private boolean conformanceTestBool = false;
    private int counter = 0;
	public Map<String, String> ls = null;
	private static List<String> suts = null;
	public Map <String, List<String>> testsuiteTestcases = null;
	public String paramJson;
    private static String sutName = null;
    private static String testCaseName = null;
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
    	testSuiteName = null;
    }
    
    protected static String getTestCaseName() {
    	return testCaseName;
    }
    
    protected static void setTestCaseName(String theTestCaseName) {
    	testCaseName = theTestCaseName;
    }
    
    protected static String getTestSuiteName() {
    	return testSuiteName;
    }
    
    protected static void setTestSuiteName(String theTestSuiteName) {
    	testSuiteName = theTestSuiteName;
    }
}
