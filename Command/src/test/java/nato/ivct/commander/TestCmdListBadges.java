/* Copyright 2020, Reinhard Herzog, Johannes Mulder (Fraunhofer IOSB)

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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        assertNotNull(this.lb, "Factory Test createCmdListBadges should return CmdListBadges");
        this.lb.execute();
    }

    @Test
    public void testCreateCmdListBadgesMethod() {
        assertTrue(this.lb.badgeMap.size() > 0, "badge list should not be empty");
    }

    @Test
    public void testCollectIrForCs() {

        Set<String> irSet = new HashSet<>();
        Set<String> cs = new HashSet<>();
        cs.add("HelloWorld-2019");
        cs.add("HLA-BASE-2019");
        this.lb.collectIrForCs(irSet, cs);
        assertTrue(irSet.size() > 0, "interoperability set should not be empty");
    }


}
