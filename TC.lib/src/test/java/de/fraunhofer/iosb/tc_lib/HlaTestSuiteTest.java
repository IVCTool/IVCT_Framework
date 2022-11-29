package de.fraunhofer.iosb.tc_lib;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.TestSuite;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ServiceLoader;

public class HlaTestSuiteTest {


    @Test
    public void testGetter() throws FileNotFoundException, IOException, ParseException {
        HlaTestSuite ts = new HlaTestSuite();
        String desc = ts.getDescription();
        assertNotNull(desc);
        testNETNbasicAttributes(ts);
    }

    private void testNETNbasicAttributes(TestSuite ts) {
        // assuming TS_NETN is available in the test resource folder
        assertEquals("TS-NETN-BASE-4_0", ts.getId());
        assertEquals("NETN-BASE 4.0 Test Suite", ts.getName());
        assertEquals("0.0.1-SNAPSHOT", ts.getVersion());
        assertEquals("Collection of NETN-BASE test cases", ts.getDescription());
        assertEquals("TS-NETN-4_0-0.0.1-SNAPSHOT/lib", ts.getLibFolder());
        assertEquals("TS-NETN-4_0-0.0.1-SNAPSHOT/bin", ts.getRunTimeFolder());
    }

    @Test
    public void testServiceLoader() {
        ServiceLoader<TestSuite> loader = ServiceLoader.load(TestSuite.class);

         for (TestSuite factory : loader) {
             String id = factory.getId();
             testNETNbasicAttributes(factory);
        }
    }

    @Test
    public void testGetJSONDescriptionObject() throws FileNotFoundException, IOException, ParseException {
        HlaTestSuite ts = new HlaTestSuite();
        JSONObject tc = ts.getJSONDescriptionObject();
        assertTrue(tc.size() > 0);
    }

    @Test
    public void testGetParameterTemplate() throws FileNotFoundException, IOException, ParseException {
        HlaTestSuite ts = new HlaTestSuite();
        JSONObject params = ts.getParameterTemplate();
        String p1 = (String) params.get("p1");
        assertEquals("valueA", p1);
        String p2 = (String) params.get("p2");
        assertEquals("valueB", p2);

    }

    @Test
    public void testGetTestCase() throws FileNotFoundException, IOException, ParseException {
        HlaTestSuite ts = new HlaTestSuite();
        AbstractTestCaseIf tc = ts.getTestCase("org.nato.netn.base.TC_BASE_0001");
        assertNull(tc); // because HlaTestSuite is not implementing any test case
    }
}
