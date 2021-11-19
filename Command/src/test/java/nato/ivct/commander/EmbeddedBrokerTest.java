package nato.ivct.commander;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;

public class EmbeddedBrokerTest {
    static final private Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EmbeddedBrokerTest.class);
	private static BrokerService broker;

	@BeforeAll
	public static void startBroker() throws Exception {
		LOGGER.info("Starting embedded broker for class {}", EmbeddedBrokerTest.class.getName());
		if (broker == null) {
			broker = new BrokerService();
			broker.addConnector("tcp://localhost:61616"); 
			broker.setPersistent(false);
			broker.start();
		}
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		if (broker != null) {
			broker.stop();
			broker = null;
		}
		LOGGER.info("Embedded broker stopped for class {}", EmbeddedBrokerTest.class.getName());
	}
}
