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

public class CmdOperatorConfirmationListener implements MessageListener, Command {

	private OnOperatorConfirmationListener listener;
	
	public class OperatorConfirmationInfo {
		public String testCaseId;
		public boolean confirmationBool;
		public String text;
	}

	public interface OnOperatorConfirmationListener {
		public void onOperatorConfirmation(OperatorConfirmationInfo operatorConfirmationInfo);
	}
	
	public CmdOperatorConfirmationListener (OnOperatorConfirmationListener listener){
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
					String commandTypeName = (String) jsonObject.get(CmdOperatorConfirmation.COMMAND_ID);

					if (commandTypeName.equals(CmdOperatorConfirmation.COMMAND)) {
						Factory.LOGGER.trace("JMS Message received: " + content);
						OperatorConfirmationInfo info = new OperatorConfirmationInfo();

                        info.testCaseId = (String) jsonObject.get(CmdOperatorConfirmation.TC_ID);
                        String confirmationBoolString = (String) jsonObject.get(CmdOperatorConfirmation.CONFIRMATION_BOOL);
                        if (confirmationBoolString.equals("true")) {
                        	info.confirmationBool = true;
                        } else {
                        	info.confirmationBool = false;
                        }
                        info.text = (String) jsonObject.get(CmdOperatorConfirmation.TEXT);

						Factory.LOGGER.info("CmdOperatorConfirmationListener Command received: " + jsonObject.toString());

						// check for missing values
                        if (info.testCaseId == null) Factory.LOGGER.error("testCaseId is missing");
                        // Text is optional
						
						listener.onOperatorConfirmation(info);
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
