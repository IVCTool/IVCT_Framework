package nato.ivct.commander;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class TestCmdListBadges {
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

    CmdListBadges lb;

    @BeforeEach 
    public void setUp()  {
        this.lb = Factory.createCmdListBadges();
        assertTrue("Factory Test createCmdListBadges should return CmdListBadges", this.lb != null);
        this.lb.execute();
    }

    @Test
    public void testCreateCmdListBadgesMethod() {
        assertTrue("badge list should not be empty", this.lb.badgeMap.size() > 0);
    }

    @Test
    public void testCollectIrForCs() {

        Set<String> ir_set = new HashSet<String>();
        Set<String> cs = new HashSet<String>();
        cs.add("HelloWorld-2019");
        cs.add("HLA-BASE-2019");
        this.lb.collectIrForCs(ir_set, cs);
        assertTrue("interoperability set should not be empty", ir_set.size() > 0);
    }


}
