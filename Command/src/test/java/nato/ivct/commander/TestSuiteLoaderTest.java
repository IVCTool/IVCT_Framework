/*
 * Copyright 2022, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package nato.ivct.commander;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.TestSuite;

import java.util.ServiceLoader;

/*
 * Testing the ServiceLoader for test suites
 */
class TestSuiteLoaderTest {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(TestSuiteLoaderTest.class);
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
    @Test 
    void testServiceLoader() {
        log.trace("ServiceLoader test");
         AbstractTestCaseIf tc = null;
         ServiceLoader<TestSuite> loader = ServiceLoader.load(TestSuite.class);
         for (TestSuite factory : loader) {
             tc = factory.getTestCase("org.nato.netn.ais.TC_AIS_0001");
             if (tc != null) break;
        }
        assertNull(tc, "in this test context there will be not test suite to be found");
    }

    @Test 
    void testDISWarfareServiceLoader() {
        log.trace("ServiceLoader test");
        AbstractTestCaseIf tc = null;
        TestSuite factory = cmd.tsServiceLoaders.get("DIS-WARFARE-6_0");
        tc = factory.getTestCase("org.nato.ivct.dis.warfare.TC_WarfareBasic");
        assertNotNull(tc, "Test suite DIS-WARFARE-6_0 shall be located in TestSuites run-time folder");
    }
}
