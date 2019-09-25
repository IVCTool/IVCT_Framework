/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

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

public class CmdStartTcListener implements MessageListener, Command {

	private OnStartTestCaseListener listener;
	
	public class TcInfo {
		public String sutName;
		public String sutDir;
		public String badge;
		public String testCaseId;
		public String testCaseParam;
		public String settingsDesignator;
		public String federationName;
		public String sutFederateName;
	}

	public interface OnStartTestCaseListener {
		public void onStartTestCase(TcInfo info);
	}
	
	public CmdStartTcListener (OnStartTestCaseListener listener){
		this.listener = listener;
	}

	@Override
	public void execute() {
		Factory.LOGGER.trace("subsribing the commands listener");
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
					String commandTypeName = (String) jsonObject.get(CmdStartTc.COMMAND_ID);

					if (commandTypeName.equals(CmdStartTc.COMMAND)) {
						Factory.LOGGER.trace("JMS Message received: " + content);
						TcInfo info = new TcInfo();

						info.sutName = (String) jsonObject.get(CmdStartTc.SUT_NAME);
						info.sutDir = (String) jsonObject.get(CmdStartTc.SUT_DIR);
						info.badge = (String) jsonObject.get(CmdStartTc.BADGE);
                        info.testCaseId = (String) jsonObject.get(CmdStartTc.TC_ID);
                        info.settingsDesignator = (String) jsonObject.get(CmdStartTc.SETTINGS_DESIGNATOR);
                        info.federationName = (String) jsonObject.get(CmdStartTc.FEDERATION);
                        info.sutFederateName = (String) jsonObject.get(CmdStartTc.FEDERATE);
						info.testCaseParam = jsonObject.get(CmdStartTc.TC_PARAM).toString();

						Factory.LOGGER.info("StartTcListener Command received: " + jsonObject.toString());

						// check for missing values
                        if (info.sutName == null) Factory.LOGGER.error("sutName is missing");
                        if (info.sutDir == null) Factory.LOGGER.error("sutDir is missing");
                        if (info.badge == null) Factory.LOGGER.error("badge is missing");
                        if (info.testCaseId == null) Factory.LOGGER.error("testCaseId is missing");
                        if (info.settingsDesignator == null) Factory.LOGGER.error("settingsDesignator is missing");
                        if (info.federationName == null) Factory.LOGGER.error("federationName is missing");
                        if (info.sutFederateName == null) Factory.LOGGER.error("sutFederateName is missing");
                        if (info.testCaseParam == null) Factory.LOGGER.error("testCaseParam is missing");
						
						listener.onStartTestCase(info);
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
