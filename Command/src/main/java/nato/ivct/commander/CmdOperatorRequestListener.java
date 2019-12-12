/* Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CmdOperatorRequestListener implements MessageListener, Command {

	private OnOperatorRequestListener listener;
	
	public class OperatorRequestInfo {
		public String sutName;
		public String testSuiteId;
		public String testCaseId;
		public String text;
	}

	public interface OnOperatorRequestListener {
		public void onOperatorRequest(OperatorRequestInfo operatorRequestInfo);
	}
	
	public CmdOperatorRequestListener (OnOperatorRequestListener listener){
		this.listener = listener;
	}

	@Override
	public void execute() {
		Factory.LOGGER.trace("subscribing the commands listener");
		Factory.jmsHelper
				.setupTopicListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE, "commands"), this);
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			final TextMessage textMessage = (TextMessage) message;
			try {
				final String content = textMessage.getText();
				try {
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
					String commandTypeName = (String) jsonObject.get(CmdOperatorRequest.COMMAND_ID);

					if (commandTypeName.equals(CmdOperatorRequest.COMMAND)) {
						Factory.LOGGER.trace("JMS Message received: " + content);
						OperatorRequestInfo info = new OperatorRequestInfo();

                        info.sutName = (String) jsonObject.get(CmdOperatorRequest.SUT_NAME);
                        info.testSuiteId = (String) jsonObject.get(CmdOperatorRequest.TS_ID);
                        info.testCaseId = (String) jsonObject.get(CmdOperatorRequest.TC_ID);
                        info.text = (String) jsonObject.get(CmdOperatorRequest.TEXT);

						Factory.LOGGER.info("CmdOperatorRequestListener Command received: " + jsonObject.toString());

						// check for missing values
                        if (info.testCaseId == null) Factory.LOGGER.error("testCaseId is missing");
                        // Text is optional
						
						listener.onOperatorRequest(info);
					}

				} catch (ParseException e) {
					e.printStackTrace();
					Factory.LOGGER.error("onMessage: ", e);
				}

			} catch (final JMSException e) {
				Factory.LOGGER.error("onMessage: problems with getText", e);
			}
		}
	}

}
