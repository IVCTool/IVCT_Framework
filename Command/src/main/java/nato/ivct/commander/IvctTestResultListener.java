package nato.ivct.commander;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.slf4j.LoggerFactory;

public class IvctTestResultListener implements MessageListener {
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(IvctTestResultListener.class);

	public  IvctTestResultListener(String topic) {
		// TODO Auto-generated constructor stub
        Factory.jmsHelper.setupTopicListener(topic, this);

	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub

		LOGGER.debug("JMS Message received: " + message);
	}

}
