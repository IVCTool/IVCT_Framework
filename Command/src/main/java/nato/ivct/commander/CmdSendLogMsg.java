package nato.ivct.commander;

import javax.jms.Message;
import javax.jms.MessageProducer;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

public class CmdSendLogMsg implements Command {

    public static final String LOG_MSG_TOPIC    = "LogEvent";
    public static final String LOG_MSG_LEVEL    = "level";
    public static final String LOG_MSG_TESTCASE = "testcase";
    public static final String LOG_MSG_SUT      = "sut";
    public static final String LOG_MSG_BADGE    = "badge";
    public static final String LOG_MSG_EVENT    = "event";

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdSendLogMsg.class);
    private MessageProducer logProducer;
    private String tc;
    private String sut;
    private String badge;
    private String logMessage;

    public CmdSendLogMsg(String forTc, String forSut, String forBadge) {
        tc = forTc;
        sut = forSut;
        badge = forBadge;
        logProducer = Factory.createTopicProducer(LOG_MSG_TOPIC);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute() throws Exception {
        JSONObject startCmd = new JSONObject();
        startCmd.put(LOG_MSG_LEVEL, "INFO");
        startCmd.put(LOG_MSG_TESTCASE, tc);
        startCmd.put(LOG_MSG_SUT, sut);
        startCmd.put(LOG_MSG_BADGE, badge);
        startCmd.put(LOG_MSG_EVENT, logMessage);

        Message message = Factory.jmsHelper.createTextMessage(startCmd.toString());
        logProducer.send(message);
    }

    public void send(String newMsg) {
        logMessage = newMsg;
        try {
            execute();
        } catch (Exception ex) {
            LOGGER.error("could not send command: " + newMsg);
        }
    }
}
