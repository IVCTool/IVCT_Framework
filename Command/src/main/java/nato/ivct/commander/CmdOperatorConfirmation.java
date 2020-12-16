/* Copyright 2020, Johannes Mulder (Fraunhofer IOSB)

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

public class CmdOperatorConfirmation implements Command {

    public static final String COMMAND_ID			= "commandType";
    public static final String COMMAND				= "operatorConfirmation";
    public static final String SEQ					= "sequence";
    public static final String SUT_NAME				= "sutName";
    public static final String TS_ID				= "testSuiteId";
    public static final String TC_ID				= "testCaseId";
    public static final String TEST_ENGINE          = "testEngine";
    public static final String CONFIRMATION_BOOL	= "confirmationBool";
    public static final String TEXT					= "text";

	// private MessageProducer producer;
    private String sutName;
    private String testSuiteId;
    private String tc;
    private String testEngine;
	private boolean confirmationBool;
	private String text;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdOperatorConfirmation.class);

	/***
	 * 
	 * @param sutName the SUT name
	 * @param testSuiteId the testsuite id
	 * @param tc the fully qualified text case name
	 * @param testEngine the TestEngine name
	 * @param confirmationBool true means positive, false negative
	 * @param text the text to be written to the tc log
	 */
	public CmdOperatorConfirmation(String sutName, String testSuiteId, String tc, String testEngine, boolean confirmationBool, String text) {
		this.sutName = sutName;
		this.testSuiteId = testSuiteId;
		this.tc = tc;
		this.testEngine = testEngine;
		this.confirmationBool = confirmationBool;
		this.text = text;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * The Structure of the CmdOperatorConfirmation command message looks like the following:
	 *
	 * { 
	 *   "commandType":"operatorConfirmation",
	 *   "sequence":"0",
	 *   "testCaseId":"TC0002",
	 *   "positiveConfirmation":"true",
	 *   "text":"Start SUT"
	 * }
	 *
	 * @see nato.ivct.commander.Command#execute()
	 */
	public void execute() {
		// build the OperatorConfirmation parameters
		JSONObject operatorConfirmationCmd = new JSONObject();
		operatorConfirmationCmd.put(COMMAND_ID, COMMAND);
		operatorConfirmationCmd.put(SEQ, Integer.toString(Factory.newCmdCount()));
		operatorConfirmationCmd.put(SUT_NAME, sutName);
		operatorConfirmationCmd.put(TS_ID, testSuiteId);
		operatorConfirmationCmd.put(TC_ID, tc);
	    operatorConfirmationCmd.put(TEST_ENGINE, testEngine);
		if (confirmationBool) {
			operatorConfirmationCmd.put(CONFIRMATION_BOOL, "true");
		} else {
			operatorConfirmationCmd.put(CONFIRMATION_BOOL, "false");
		}
		operatorConfirmationCmd.put(TEXT, text);
		
		LOGGER.info("operatorConfirmation Command: {}", operatorConfirmationCmd);

		// send the OperatorConfirmation message
		Factory.sendToJms(operatorConfirmationCmd.toString());
	}

}
