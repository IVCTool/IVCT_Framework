package nato.ivct.commander;

import static org.junit.Assert.*;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import org.junit.Test;

public class TestCmdListTestsuites {

    @Test
    public void test() {
        Factory.initialize();
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

        String[] ir_list = cmd.getIrForTc ("de.fraunhofer.iosb.tc_helloworld.TC0002");
        assertTrue("TC does not test IR", ir_list.length > 0);

    }

}
