package de.fraunhofer.iosb.tc_lib;

import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.Test;

import de.fraunhofer.iosb.tc_lib_if.TestSuite;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ServiceLoader;

public class HlaTestSuiteTest {


    @Test
    public void testGetter() throws FileNotFoundException, IOException, ParseException {

        HlaTestSuite ts = new HlaTestSuite();
        String desc = ts.getDescription();
        assertNotNull(desc);
        assertEquals("TS-NETN-BASE-4_0", ts.getId());
        assertEquals("0.0.1-SNAPSHOT", ts.getVersion());
        assertEquals("NETN-BASE 4.0 Test Suite", ts.getName());
    }

    @Test
    public void testServiceLoader() {
        ServiceLoader<TestSuite> loader = ServiceLoader.load(TestSuite.class);

         for (TestSuite factory : loader) {
             String id = factory.getId();
             assertNotNull(id);
        }
    }

    @Test
    public void testGetJSONDescriptionObject() {

    }

    @Test
    public void testGetLibFolder() {

    }

    @Test
    public void testGetName() {

    }

    @Test
    public void testGetParameterTemplate() {

    }

    @Test
    public void testGetRunTimeFolder() {

    }

    @Test
    public void testGetTestCase() {

    }

    @Test
    public void testGetVersion() {

    }
}
