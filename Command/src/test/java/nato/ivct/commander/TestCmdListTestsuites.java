/* Copyright 2020, Reinhard Herzog, Johannes Mulder (Fraunhofer IOSB)

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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;

public class TestCmdListTestsuites extends EmbeddedBrokerTest {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestCmdListTestsuites.class);

    protected CmdListTestSuites cmd = null;

    @BeforeEach
    public void init() {
        if (cmd == null) {
            cmd = Factory.createCmdListTestSuites();
            try {
                cmd.execute();
            } catch (Exception e) {
                e.printStackTrace();
                fail("running the ListTestSuites command failed!");
            }    
        }
    }

    // due to broker synchronization issues between unit tests, this test will be
    // called within the Factory test class
    @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testCmdListTestSuites() {
        LOGGER.info("Starting test testCmdListTestSuites");
        init();
        assertTrue(cmd.testsuites.size() > 0, "Some test suites's should be found");
        // all test suites should be well formed
        for (CmdListTestSuites.TestSuiteDescription value : cmd.testsuites.values()) {
            assertNotNull(value.id, "Test suite  has no id");
            assertNotNull(value.description, "Test suite missing description");
            assertNotNull(value.name, "Test suite missing name");
            assertNotNull(value.tsLibTimeFolder, "Test suite missing lib folder");
            assertNotNull(value.tsRunTimeFolder, "Test suite missing runTime folder");
            assertNotNull(value.version, "Test suite mission version");
        }
    }

    @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testGetTestSuiteForTc() {
        TestSuiteDescription ts = null;
        ts = cmd.getTestSuiteForTc("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertNotNull(ts, "TestSuite not found");
        ts = cmd.getTestSuiteforIr("IR-SOM-0014");
        assertNotNull(ts, "TestSuite not found");
    }

    @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testGetTestCaseDescrforIr() {
        CmdListTestSuites.TestCaseDesc tc = null;
        tc = cmd.getTestCaseDescrforIr("IR-SOM-0014");
        assertNotNull(tc, "TestCase not found");
    }

    @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testGetIrForTc() {
        Set<String> irList = cmd.getIrForTc("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertFalse(irList.isEmpty(), "TC does not test IR");
    }

    @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testGetTsForIr() {
        Set<String> irSet = new HashSet<>();
        irSet.add("IR-SOM-0017");
        irSet.add("IR-SOM-0018");
        Set<String> tsSet = cmd.getTsForIr(irSet);
        assertTrue(tsSet.size() == 1, "Test suite Set should be not empty");        
        irSet.add("IR-SOM-0001");
        irSet.add("IR-SOM-0002");
        irSet.add("IR-SOM-0003");
        irSet.add("IR-SOM-0015");
    	Map<String, TestSuiteDescription> fts = cmd.filterForIr (irSet);
    	assertTrue(fts.size() > 0, "filtered Test suite list shall not be empty");
    }
}
