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
import org.slf4j.LoggerFactory;

public class CmdStartTestResultListener implements MessageListener, Command {
	private OnResultListener listener;

	public class TcResult {
		public String tc;
		public String verdict;
		public String text;
	}

	public interface OnResultListener {
		public void onResult(TcResult result);
	}

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTestResultListener.class);

	public CmdStartTestResultListener(OnResultListener resultListener) {
		listener = resultListener;
	}

	@Override
	public void execute() {
		LOGGER.info("subsribing the commands listener");
		Factory.jmsHelper
				.setupTopicListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE, "commands"), this);
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			final TextMessage textMessage = (TextMessage) message;
			try {
				final String content = textMessage.getText();
				LOGGER.info("JMS Message received: " + content);
				try {
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
					String commandTypeName = (String) jsonObject.get("commandType");

					switch (commandTypeName) {
					case "announceVerdict":
						TcResult tcr = new TcResult();
						tcr.tc = (String) jsonObject.get("testcase");
						if (tcr.tc == null) {
							LOGGER.warn("Error: the test case name is null");
						}
						tcr.verdict = (String) jsonObject.get("verdict");
						if (tcr.verdict == null) {
							LOGGER.warn("Error: test case verdict is null");
						}
						tcr.text = (String) jsonObject.get("verdictText");
						if (tcr.text == null) {
							LOGGER.warn("Error: the test case verdict text is null");
						}
						listener.onResult(tcr);
						break;
					case "quit":
						// Should ignore
						break;
					case "setSUT":
						break;
					case "startTestCase":
						break;
					default:
						System.out.println("Unknown commandType name is: " + commandTypeName);
						break;
					}
				} catch (ParseException e) {
					e.printStackTrace();
					LOGGER.error("onMessage: ", e);
				}

			} catch (final JMSException e) {
				LOGGER.error("onMessage: problems with getText", e);
			}
		}
	}

}
