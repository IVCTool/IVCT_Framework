package nato.ivct.commander;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CmdLogMsgListener implements Command, MessageListener {

    public interface OnLogMsgListener {
        public void onLogMsg(String level, String msg);
    }

    private OnLogMsgListener listener;

    public CmdLogMsgListener(OnLogMsgListener myListener) {
        listener = myListener;
    }

    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            try {
                final String content = textMessage.getText();
                Factory.LOGGER.trace("JMS Message received: " + content);
                try {
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
                    String level = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_LEVEL);
                    String msg = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_EVENT);
                    listener.onLogMsg(level, msg);
                } catch (ParseException e) {
                    e.printStackTrace();
                    Factory.LOGGER.error("onMessage: ", e);
                }
            } catch (final JMSException e) {
                Factory.LOGGER.error("onMessage: problems with getText", e);
            }
        }

    }

    @Override
    public void execute() {
        Factory.LOGGER.trace("subsribing the LogMessage listener");
        Factory.jmsHelper.setupTopicListener(Factory.props.getProperty(CmdSendLogMsg.LOG_MSG_TOPIC, "LogMessage"),
                this);
    }

}
