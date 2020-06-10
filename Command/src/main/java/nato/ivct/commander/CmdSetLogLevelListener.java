/* Copyright 2020, Reinhard Herzog (Fraunhofer IOSB)

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

import nato.ivct.commander.CmdSetLogLevel.LogLevel;

public class CmdSetLogLevelListener implements Command, MessageListener {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdSetLogLevelListener.class);
	private OnSetLogLevelListener listener;

	public interface OnSetLogLevelListener {
		public void onSetLogLevel(LogLevel level);
	}

	public CmdSetLogLevelListener(OnSetLogLevelListener listener) {
		this.listener = listener;
	}

	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			final TextMessage textMessage = (TextMessage) message;
			try {
				final String content = textMessage.getText();
				LOGGER.trace("JMS Message received: {}", content);
				try {
					JSONParser jsonParser = new JSONParser();
					JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
					String commandTypeName = (String) jsonObject.get("commandType");

					if (commandTypeName.equals("setLogLevel")) {
						LogLevel logLevelId = LogLevel.valueOf((String) jsonObject.get("logLevelId"));
						listener.onSetLogLevel(logLevelId);
					}
				} catch (ParseException exc) {
					LOGGER.error("onMessage: ", exc);
				}
			} catch (final JMSException exc) {
				LOGGER.error("onMessage: problems with getText ", exc);
			}
		}
	}

	@Override
	public void execute() {
		LOGGER.trace("subsribing the CmdStartChangeLogLevelListener");
		Factory.jmsHelper
				.setupTopicListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE, "commands"), this);
	}

}
