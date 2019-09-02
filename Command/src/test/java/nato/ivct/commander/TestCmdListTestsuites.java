package nato.ivct.commander;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import org.junit.Before;
import org.junit.Test;

public class TestCmdListTestsuites {

    @Before
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
