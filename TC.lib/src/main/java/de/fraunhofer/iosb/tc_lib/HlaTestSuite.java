package de.fraunhofer.iosb.tc_lib;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.TestSuite;

public class HlaTestSuite implements TestSuite {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(HlaTestSuite.class);

    public static final String TS_ID = "id";
    public static final String TS_NAME = "name";
    public static final String TS_VERSION = "version";
    public static final String TS_DESCR = "description";
    public static final String TS_RUN_FOLDER = "tsRunTimeFolder";
    public static final String TS_LIB_FOLDER = "tsLibTimeFolder";
    public static final String TS_TESTCASES = "testcases";
    public static final String TS_TC = "TC";
    public static final String TS_IR = "IR";
    public static final String TS_PARAMS_FILE = "TcParam";

    private JSONObject description;

    public HlaTestSuite () throws FileNotFoundException, IOException, ParseException {
        log.trace("Test Suite {} loading", this.getClass());
        String resourceFileName = "/" + this.getClass().getSimpleName() + ".json";
        java.net.URL url = this.getClass().getResource(resourceFileName);
        JSONParser parser = new JSONParser();
        description = (JSONObject) parser.parse (new FileReader(url.getFile()));
    }

    @Override
    public JSONObject getJSONDescriptionObject() {
        return description;
    }

    @Override
    public String getId() {
        return (String) description.get(TS_ID);
    }

    @Override
    public AbstractTestCaseIf getTestCase(String TestCaseId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        return (String) description.get(TS_NAME);
    }

    @Override
    public String getVersion() {
        return (String) description.get(TS_VERSION);
    }

    @Override
    public String getDescription() {
        return (String) description.get(TS_DESCR);
    }

    @Override
    public String getRunTimeFolder() {
        return (String) description.get(TS_RUN_FOLDER);
    }

    @Override
    public String getLibFolder() {
        return (String) description.get(TS_LIB_FOLDER);
    }

    @Override
    public JSONObject getParameterTemplate() throws FileNotFoundException, IOException, ParseException {
        JSONObject parameter;
        log.trace("Parameter Template for class {} loading", this.getClass());
        String resourceFileName = "/" + TS_PARAMS_FILE + ".json";
        java.net.URL url = this.getClass().getResource(resourceFileName);
        JSONParser parser = new JSONParser();
        parameter = (JSONObject) parser.parse (new FileReader(url.getFile()));
        return parameter;
    }
    
}
