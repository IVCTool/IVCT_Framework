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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;

public class TestCmdListTestsuites {
	private static BrokerService broker = new BrokerService();

	@BeforeAll
	public static void startBroker() throws Exception {
		// configure the broker
		broker.addConnector("tcp://localhost:61616"); 
		broker.setPersistent(false);

		broker.start();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		try {
			broker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @BeforeEach
    public void setUp() throws Exception {
        Factory.initialize();
    }

    @Test
    public void test() {
        CmdListTestSuites cmd = new CmdListTestSuites();

        try {
            cmd.execute();
        } catch (Exception e) {
            fail("exception during execute");
            e.printStackTrace();
        }
        assertTrue("Some Testsuites's should be found", cmd.testsuites.size() > 0);

        // all testsuites should be well formed
        for (CmdListTestSuites.TestSuiteDescription value : cmd.testsuites.values()) {
            assertTrue("Test case has a name", value.id != null);
            assertTrue("Test case has a name", value.description != null);
            assertTrue("Test case has a name", value.name != null);
            assertTrue("Test case has a name", value.tsLibTimeFolder != null);
            assertTrue("Test case has a name", value.tsRunTimeFolder != null);
            assertTrue("Test case has a name", value.version != null);
        }

        TestSuiteDescription ts = null;
        ts = cmd.getTestSuiteForTc("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertTrue("TestSuite not found", ts != null);
        ts = cmd.getTestSuiteforIr("IR-SOM-0014");
        assertTrue("TestSuite not found", ts != null);

        CmdListTestSuites.TestCaseDesc tc = null;
        tc = cmd.getTestCaseDescrforIr("IR-SOM-0014");
        assertTrue("TestCase not found", tc != null);

        Set<String> irList = cmd.getIrForTc("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertFalse("TC does not test IR", irList.isEmpty());

        Set<String> irSet = new HashSet<>();
        irSet.add("IR-SOM-0017");
        irSet.add("IR-SOM-0018");
        Set<String> tsSet = cmd.getTsForIr(irSet);
        assertTrue("Testsuite Set should be not empty", tsSet.size() == 1);
        
        irSet.add("IR-SOM-0001");
        irSet.add("IR-SOM-0002");
        irSet.add("IR-SOM-0003");
        irSet.add("IR-SOM-0015");

    	Map<String, TestSuiteDescription> fts = cmd.filterForIr (irSet);
    	assertTrue("filtered Testsuite list shall not be empty", fts.size() > 0);

    }

}
