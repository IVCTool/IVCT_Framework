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
		public String tsRunFolder;
		public String badge;
		public String testCaseId;
		public String testCaseParam;

	}

	public interface OnStartTestCaseListener {
		public void onStartTestCase(TcInfo info);
	}
	
	public CmdStartTcListener (OnStartTestCaseListener listener){
		this.listener = listener;
	}

	@Override
	public void execute() {
		Factory.LOGGER.info("subsribing the commands listener");
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
					String commandTypeName = (String) jsonObject.get("commandType");

					if (commandTypeName.equals("startTestCase")) {
						Factory.LOGGER.info("JMS Message received: " + content);
						TcInfo info = new TcInfo();

						info.sutName = (String) jsonObject.get("sutName");
						info.sutDir = (String) jsonObject.get("sutDir");
						info.tsRunFolder = (String) jsonObject.get("tsRunFolder");
						info.badge = (String) jsonObject.get("badge");
						info.testCaseId = (String) jsonObject.get("testCaseId");
						info.testCaseParam = jsonObject.get("tcParam").toString();
						
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
