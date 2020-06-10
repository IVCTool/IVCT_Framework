/* Copyright 2020, Reinhard Herzog, Michael Theis (Fraunhofer IOSB)

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

public class CmdLogMsgListener implements Command, MessageListener {

	public class LogMsg {
		public String level = null;
		public String tc = null;
		public String sut = null;
		public String badge = null;
		public long time = 0;
		public String txt = null;
	}

	public interface OnLogMsgListener {
		public void onLogMsg(LogMsg msg);
	}

	public interface OnJsonLogMsgListener {
		public void onLogMsgJson(JSONObject msg);
	}

	private OnLogMsgListener listener = null;
	private OnJsonLogMsgListener jsonListener = null;

	public CmdLogMsgListener(OnLogMsgListener myListener) {
		listener = myListener;
	}

	public CmdLogMsgListener(OnJsonLogMsgListener myJsonListener) {
		jsonListener = myJsonListener;
	}

	@Override
	public void onMessage(Message message) {
		if (message instanceof TextMessage) {
			final TextMessage textMessage = (TextMessage) message;
			try {
				final String content = textMessage.getText();
				Factory.LOGGER.trace("JMS Message received: {}", content);
				try {
					final JSONParser jsonParser = new JSONParser();
					final LogMsg msg = new LogMsg();
					final JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
					if (jsonListener != null) {
						jsonListener.onLogMsgJson(jsonObject);
					} 
					if (listener != null) {
						msg.level = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_LEVEL);
						msg.tc = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_TESTCASE);
						msg.sut = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_SUT);
						msg.badge = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_BADGE);
						msg.time = (long) jsonObject.get(CmdSendLogMsg.LOG_MSG_TIME);
						msg.txt = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_EVENT);
						listener.onLogMsg(msg);
					}
				} catch (final ParseException exc) {
					Factory.LOGGER.error("onMessage: ", exc);
				}
			} catch (final JMSException exc) {
				Factory.LOGGER.error("onMessage: problems with getText", exc);
			}
		}

	}

	@Override
	public void execute() {
		Factory.LOGGER.trace("subsribing the LogMessage listener");
		Factory.jmsHelper.setupTopicListener(Factory.props.getProperty(CmdSendLogMsg.LOG_MSG_TOPIC, "LogEvent"), this);
	}

	public void terminate() {
		Factory.LOGGER.trace("terminate the LogMessage listener");
		Factory.jmsHelper.disconnect();
	}
}
