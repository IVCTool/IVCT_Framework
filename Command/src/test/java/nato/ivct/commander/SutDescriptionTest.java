package nato.ivct.commander;

import static org.junit.Assert.*;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SutDescriptionTest {
	private static BrokerService broker = new BrokerService();

	@BeforeAll
	public static void startBroker() throws Exception {
		// configure the broker
		broker.addConnector("tcp://localhost:61616"); 
		broker.setPersistent(false);

		broker.start();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		try {
			broker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    @Test
    public void testExecute() {
        CmdListSuT listSuT = Factory.createCmdListSuT();
        assertTrue("CmdListSuT should be created", listSuT != null);
        listSuT.execute();
        
    }

}
