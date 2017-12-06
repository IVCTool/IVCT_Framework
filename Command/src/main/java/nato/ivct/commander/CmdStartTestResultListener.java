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
		public String sutName;
		public String sutDir;
		public String testScheduleName;
		public String testcase;
		public String verdict;
		public String verdictText;
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
		LOGGER.info("subsribing the CmdStartTestResultListener");
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
					if (commandTypeName.equals("announceVerdict")) {
						LOGGER.info("JMS Message received: " + content);
						TcResult tcr = new TcResult();
						tcr.sutName = (String) jsonObject.get("sutName");
						if (tcr.sutName == null) {
							LOGGER.warn("Error: sutName is null");
						}
						tcr.sutDir = (String) jsonObject.get("sutDir");
						if (tcr.sutDir == null) {
							LOGGER.warn("Error: sutDir is null");
						}
						tcr.testScheduleName = (String) jsonObject.get("testScheduleName");
						if (tcr.testScheduleName == null) {
							LOGGER.warn("Error: testScheduleName is null");
						}
						tcr.testcase = (String) jsonObject.get("testcase");
						if (tcr.testcase == null) {
							LOGGER.warn("Error: testcase is null");
						}
						tcr.verdict = (String) jsonObject.get("verdict");
						if (tcr.verdict == null) {
							LOGGER.warn("Error: verdict is null");
						}
						tcr.verdictText = (String) jsonObject.get("verdictText");
						if (tcr.verdictText == null) {
							LOGGER.warn("Error: the test case verdict text is null");
						}
						listener.onResult(tcr);
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
