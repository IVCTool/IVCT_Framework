package nato.ivct.commander;

import javax.jms.Message;
import javax.jms.MessageListener;

public class IvctTestResultListener implements MessageListener {
	
	public  IvctTestResultListener(String topic) {
		// TODO Auto-generated constructor stub
        Factory.jmsHelper.setupTopicListener(topic, this);

	}

	@Override
	public void onMessage(Message message) {
		// TODO Auto-generated method stub

		Factory.LOGGER.debug("JMS Message received: " + message);
	}

}
