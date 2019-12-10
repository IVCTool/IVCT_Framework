/* Copyright 2019, Johannes Mulder (Fraunhofer IOSB)

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

public class CmdOperatorRequest implements Command {

    public static final String COMMAND_ID      = "commandType";
    public static final String COMMAND         = "operatorRequest";
    public static final String SEQ             = "sequence";
    public static final String TC_ID           = "testCaseId";
    public static final String TEXT            = "text";

	// private MessageProducer producer;
    private String tc;
	private String text;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdOperatorRequest.class);

	public CmdOperatorRequest(String tc, String text) {
		this.tc = tc;
		this.text = text;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * The Structure of the operatorRequest command message looks like the following:
	 *
	 * { 
	 *   "commandType":"operatorRequest",
	 *   "sequence":"0",
	 *   "testCaseId":"TC0002",
	 *   "text":"Start SUT"
	 * }
	 *
	 * @see nato.ivct.commander.Command#execute()
	 */
	public void execute() {
		// build the operatorRequest parameters
		JSONObject operatorRequestCmd = new JSONObject();
		operatorRequestCmd.put(COMMAND_ID, COMMAND);
		operatorRequestCmd.put(SEQ, Integer.toString(Factory.newCmdCount()));
		operatorRequestCmd.put(TC_ID, tc);
		operatorRequestCmd.put(TEXT, text);
		
		LOGGER.info("Operatorrequest Command: " + operatorRequestCmd.toString());

		// send the operatorRequest message
		Factory.sendToJms(operatorRequestCmd.toString());
	}

}
