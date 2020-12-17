/* Copyright 2020, Felix Schoeppenthau (Fraunhofer IOSB)

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

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;


public class CmdAbortTc implements Command {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdAbortTc.class);

    public static final String COMMAND      = "abortTestCase";
    public static final String COMMAND_ID   = "commandType";
    public static final String SUT_NAME     = "sutName";
    public static final String TC_ID        = "testCaseId";
    public static final String TEST_ENGINE  = "testEngine";

    private String sut;
    private String tc;
    private String testEngine;

    public CmdAbortTc(String sut, String tc, String testEngine) {
        this.sut = sut;
        this.tc = tc;
        this.testEngine = testEngine;
    }


    @SuppressWarnings("unchecked")
    @Override
    public void execute() {

        // build the stop parameter
        JSONObject abortCmd = new JSONObject();
        abortCmd.put(COMMAND_ID, COMMAND);
        abortCmd.put(SUT_NAME, sut);
        abortCmd.put(TC_ID, tc);
        abortCmd.put(TEST_ENGINE, testEngine);

        LOGGER.info("Abort TC Command: {}", abortCmd);

        // send the abort message
        Factory.sendToJms(abortCmd.toString());
    }
}
