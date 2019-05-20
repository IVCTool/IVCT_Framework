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
                    LogMsg msg = new LogMsg();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
                    msg.level = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_LEVEL);
                    msg.tc = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_TESTCASE);
                    msg.sut = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_SUT);
                    msg.badge = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_BADGE);
                    msg.time = (long) jsonObject.get(CmdSendLogMsg.LOG_MSG_TIME);
                    msg.txt = (String) jsonObject.get(CmdSendLogMsg.LOG_MSG_EVENT);
                    listener.onLogMsg(msg);
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
        Factory.jmsHelper.setupTopicListener(Factory.props.getProperty(CmdSendLogMsg.LOG_MSG_TOPIC, "LogEvent"),
                this);
    }

}
