/* Copyright 2020, Reinhard Herzog, Felix Schoeppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import de.fraunhofer.iosb.tc_lib_if.TestSuite;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.ServiceLoader;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * The CmdListTestsuites implements the loading of test suite description files
 * and provides access methods to process the content. A test suite is defined
 * by a json file, similar to the following example: 
 * { 
 *   "id": "TS_HelloWorld-2017", 
 *   "version": "2.0.0", 
 *   "name": "HelloWorld Tutorial Badge", 
 *   "description": "This is a simple example for a testsuite to test the compliance of an federate to the hello world federation.", 
 *   "tsRunTimeFolder": "TS_HelloWorld-2.0.0/bin", 
 *   "tsLibTimeFolder": "TS_HelloWorld-2.0.0/lib",
 *   "testcases": [
 *     {  
 *       "TC": "de.fraunhofer.iosb.tc_helloworld.TC0001" 
 *       "IR": ["IR-HW-0001"], 
 *       "description": "Test population growing rate"
 *     }, { 
 *       "TC": "de.fraunhofer.iosb.tc_helloworld.TC0002",
 *       "IR": ["IP-HW-0002"], 
 *       "description": "Test inter-country communication" 
 *     } 
 *   ] 
 * }
 *
 * @author hzg
 */
public class CmdListTestSuites implements Command {

    public static final String TS_ID = "id";
    public static final String TS_NAME = "name";
    public static final String TS_VERSION = "version";
    public static final String TS_DESCR = "description";
    public static final String TS_RUN_FOLDER = "tsRunTimeFolder";
    public static final String TS_LIB_FOLDER = "tsLibTimeFolder";
    public static final String TS_TESTCASES = "testcases";
    public static final String TS_TC = "TC";
    public static final String TS_IR = "IR";

    public class TestCaseDesc {

        public String      tc;          // fully qualified class name for test case
        public String      description; // explanation about the test case
        public Set<String> IR;          // set of interoperability requirements tested
    };

    public class TSParameters {

        public String name;        // parameter name
        public String description; // explanation about the parameter
    };

    public class TestSuiteDescription {

        public String                    id;
        public String                    version;
        public String                    name;
        public String                    description;
        public String                    tsRunTimeFolder;
        public String                    tsLibTimeFolder;
        public Map<String, TestCaseDesc> testcases;
        public Map<String, TSParameters> parameters;
    };

    public Map<String, TestSuiteDescription> testsuites = new HashMap<>();
    // public HashMap<String, TestSuite> tsMap = new HashMap<>();    // key TestSuiteId

    @Override
    public void execute() throws Exception {
        Factory.LOGGER.trace("Factory.IVCT_TS_HOME_ID = {}", Factory.props.getProperty(Factory.IVCT_TS_DEF_HOME_ID));
        File dir = new File(Factory.props.getProperty(Factory.IVCT_TS_DEF_HOME_ID));
        if (!dir.exists()) {
            Factory.LOGGER.error("test suite folder: {} does not exist", Factory.props.getProperty(Factory.IVCT_TS_DEF_HOME_ID));
            return;
        }

        // allow re-execution of the command
        testsuites.clear();
        ArrayList<URL> jarFiles = new ArrayList<>();

        if (dir.isDirectory()) {
            Factory.LOGGER.trace("Read Testsuite descriptions from {}", dir.getAbsolutePath());
            JSONParser parser = new JSONParser();
            File[] filesList = dir.listFiles();
            for (File file: filesList) {
                Object obj;
                if (file.isFile() && file.getName().toLowerCase().endsWith(".json")) {
                    Factory.LOGGER.trace("reading testsuite description: {}", file.getAbsolutePath());
                    FileReader fr = null;
                    try {
                        fr = new FileReader(file);
                        obj = parser.parse(fr);
                        JSONObject jsonObj = (JSONObject) obj;
                        TestSuiteDescription testSuite = createTestSuiteDescription(jsonObj);
                        this.testsuites.put(testSuite.id, testSuite);
                        fr.close();
                        fr = null;
                    }
                    catch (IOException | ParseException exc) {
                        Factory.LOGGER.error("error reading file", exc);
                    }
                } 
                else if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                    Factory.LOGGER.trace("reading testsuite description: {}", file.getAbsolutePath());
                    jarFiles.add(file.toURI().toURL());


                }

            }
        }
        else {
            Factory.LOGGER.error("value = {} is not a folder", dir.getAbsolutePath());
        }

        // add the jar files found in TestSuites folder to the current thread class loader
        URLClassLoader child = new URLClassLoader(jarFiles.toArray(new URL[0]), this.getClass().getClassLoader());
        Thread.currentThread().setContextClassLoader(child);
        // load test suites via ServiceLoader
        ServiceLoader<TestSuite> loader = ServiceLoader.load(TestSuite.class);
        for (TestSuite factory : loader) {
            String label = factory.getId();
            TestSuiteDescription testSuite = createTestSuiteDescription(factory.getJSONDescriptionObject());
            this.testsuites.put(label, testSuite);
            Factory.LOGGER.trace("found {} test suite", label);
        }
    };

    private TestSuiteDescription createTestSuiteDescription (JSONObject description) {
        TestSuiteDescription testSuite = new TestSuiteDescription();

        testSuite.id = (String) description.get(TS_ID);
        testSuite.version = (String) description.get(TS_VERSION);
        testSuite.name = (String) description.get(TS_NAME);
        testSuite.description = (String) description.get(TS_DESCR);
        testSuite.tsRunTimeFolder = (String) description.get(TS_RUN_FOLDER);
        testSuite.tsLibTimeFolder = (String) description.get(TS_LIB_FOLDER);

        JSONArray testcases = (JSONArray) description.get(TS_TESTCASES);
        if (testcases != null) {
            testSuite.testcases = new HashMap<String, TestCaseDesc>();
            for (int i = 0; i < testcases.size(); i++) {
                JSONObject req = (JSONObject) testcases.get(i);
                TestCaseDesc testCaseDesc = new TestCaseDesc();
                testCaseDesc.tc = (String) req.get(TS_TC);
                testCaseDesc.description = (String) req.get(TS_DESCR);

                JSONArray ir = (JSONArray) req.get(TS_IR);
                testCaseDesc.IR = new HashSet<String>();
                for (int j = 0; j < ir.size(); j++) {
                    testCaseDesc.IR.add((String) ir.get(j));
                }
                testSuite.testcases.put(testCaseDesc.tc, testCaseDesc);
            }
        }
        else {
            testSuite.testcases = null;
        }

        JSONArray parameters = (JSONArray) description.get("parameters");
        if (parameters != null) {
            testSuite.parameters = new HashMap<>();
            for (int i = 0; i < parameters.size(); i++) {
                JSONObject req = (JSONObject) parameters.get(i);
                TSParameters tsParameters = new TSParameters();
                tsParameters.name = (String) req.get(TS_NAME);
                tsParameters.description = (String) req.get(TS_DESCR);
                testSuite.parameters.put(tsParameters.name, tsParameters);
            }
        }
        else {
            testSuite.parameters = null;
        }

        return testSuite;
    }


    public TestSuiteDescription getTestSuiteForTc(String tcId) {
        for (TestSuiteDescription value: this.testsuites.values()) {
            for (Map.Entry<String, TestCaseDesc> tc: value.testcases.entrySet()) {
                if (tc.getValue().tc.equalsIgnoreCase(tcId)) {
                    return value;
                }
            }
        }
        return null;
    }


    public Map<String, TSParameters> getParametersForTs(String tsId) {
        final TestSuiteDescription tsDesc = this.testsuites.get(tsId);
        return tsDesc == null || tsDesc.parameters == null ? new HashMap<>() : tsDesc.parameters;
    }


    public TestSuiteDescription getTestSuiteforIr(String irId) {
        for (TestSuiteDescription value: this.testsuites.values()) {
            // for (TestCaseDesc tc : value.testcases)
            for (Map.Entry<String, TestCaseDesc> tc: value.testcases.entrySet()) {
                for (String ir: tc.getValue().IR) {
                    if (ir.equalsIgnoreCase(irId)) {
                        return value;
                    }
                }
            }
        }
        return null;
    }


    public TestCaseDesc getTestCaseDescrforIr(String irId) {
        for (TestSuiteDescription value: this.testsuites.values()) {
            for (Map.Entry<String, TestCaseDesc> tc: value.testcases.entrySet()) {
                for (String ir: tc.getValue().IR) {
                    if (ir.equalsIgnoreCase(irId)) {
                        return tc.getValue();
                    }
                }
            }
        }
        return null;
    }


    public Set<String> getIrForTc(String tcId) {
        for (TestSuiteDescription value: this.testsuites.values()) {
            for (Map.Entry<String, TestCaseDesc> tc: value.testcases.entrySet()) {
                if (tc.getValue().tc.equalsIgnoreCase(tcId)) {
                    return tc.getValue().IR;
                }
            }
        }
        return null;
    }


    public Set<String> getTsForIr(Set<String> irList) {
        Set<String> tsSet = new HashSet<>();

        for (String ir: irList) {
            TestSuiteDescription ts = getTestSuiteforIr(ir);
            if (ts != null)
                tsSet.add(ts.id);
        }
        return tsSet;
    }


    public Map<String, TestSuiteDescription> filterForIr(Set<String> irSet) {
        Map<String, TestSuiteDescription> filteredTestsuites = new HashMap<>();

        for (String ir: irSet) {
            TestSuiteDescription ts = getTestSuiteforIr(ir);
            if (ts != null) {
                TestSuiteDescription fts = filteredTestsuites.get(ts.id);
                if (fts == null) {
                    fts = new TestSuiteDescription();
                    fts.id = ts.id;
                    fts.name = ts.name;
                    fts.version = ts.version;
                    fts.description = ts.description;
                    fts.tsLibTimeFolder = ts.tsLibTimeFolder;
                    fts.tsRunTimeFolder = ts.tsRunTimeFolder;
                    fts.testcases = new HashMap<String, CmdListTestSuites.TestCaseDesc>();
                    filteredTestsuites.put(fts.id, fts);
                }
                TestCaseDesc tc = getTestCaseDescrforIr(ir);
                fts.testcases.put(tc.tc, tc);
            }
        }
        return filteredTestsuites;
    }
}
