/*
 * Copyright 2023, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.dis_lib;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.TestSuite;


public class DisTestSuite implements TestSuite {

    public static final org.slf4j.Logger log = LoggerFactory.getLogger(DisTestSuite.class);

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

    public DisTestSuite () throws FileNotFoundException, IOException, ParseException {
        log.trace("Test Suite {} loading", this.getClass());
        String resourceFileName = "/" + this.getClass().getSimpleName() + ".json";
        InputStream inputStream = this.getClass().getResourceAsStream(resourceFileName);
        JSONParser parser = new JSONParser();
        description = (JSONObject) parser.parse (new InputStreamReader(inputStream));
    }

    @Override
    public JSONObject getJSONDescriptionObject() {
        return description;
    }

    @Override
    public String getId() {
        return (String) description.get(TS_ID);
    }

    /**
     * Generic implementation only valid if the fully qualified test case class name
     * is used as test case id. Test Suites which are using a different naming 
     * convention need to overwrite this method.
     */
    @Override
    public AbstractTestCaseIf getTestCase(String TestCaseId) {
        AbstractTestCaseIf tc = null;
        try {
            tc = (AbstractTestCaseIf) Class.forName(TestCaseId).getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException | ClassNotFoundException e) {
            log.error("unknown test case id", e);
        }
        return tc;
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
        InputStream inputStream = this.getClass().getResourceAsStream(resourceFileName);
        JSONParser parser = new JSONParser();
        parameter = (JSONObject) parser.parse (new InputStreamReader(inputStream));
        return parameter;
    }
    
}
