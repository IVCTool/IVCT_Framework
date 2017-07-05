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
		public void OnResult(TcResult result);
	}

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTestResultListener.class);

	public CmdStartTestResultListener(OnResultListener resultListener) {
		listener = resultListener;
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
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
						listener.OnResult(tcr);
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
