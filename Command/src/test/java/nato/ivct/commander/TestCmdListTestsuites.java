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

        Set<String> ir_list = cmd.getIrForTc("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertFalse("TC does not test IR", ir_list.isEmpty());

        Set<String> ir_set = new HashSet<>();
        ir_set.add("IR-SOM-0017");
        ir_set.add("IR-SOM-0018");
        Set<String> ts_set = cmd.getTsForIr(ir_set);
        assertTrue("Testsuite Set should be not empty", ts_set.size() == 1);
        
        ir_set.add("IR-SOM-0001");
        ir_set.add("IR-SOM-0002");
        ir_set.add("IR-SOM-0003");
        ir_set.add("IR-SOM-0015");

    	Map<String, TestSuiteDescription> fts = cmd.filterForIr (ir_set);
    	assertTrue("filtered Testsuite list shall not be empty", fts.size() > 0);

    }

}
