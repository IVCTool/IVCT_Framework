/* Copyright 2020, Reinhard Herzog (Fraunhofer IOSB)

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

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.json.simple.JSONObject;


public class TestCmdHeartbeat {
	private static BrokerService broker = new BrokerService();
    static Logger logger = LoggerFactory.getLogger(Use_CmdHeartbeatSend.class);

    public class Use_CmdHeartbeatListen implements CmdHeartbeatListen.OnCmdHeartbeatListen {
        public String heartbeatSender = "undefined";
        public String timestamp = "undefined";
        public boolean healthstatus = false;

        //  work with a Json Object as return
        public void hearHeartbeat(JSONObject jsonObject) {
            assertTrue("heartbeat message must not be null", jsonObject != null);
            logger.info(jsonObject.toString());
            try {
                heartbeatSender = (String) jsonObject.get("HeartbeatSender");
                timestamp = (String) jsonObject.get("LastSendingTime");
// not used?                healthstatus = (boolean) jsonObject.get("SenderHealthState");
                logger.debug("HeartbeatSender: {}", heartbeatSender); 
                logger.debug("LastSendingTime: {}", timestamp);
//                logger.debug("SenderHealthState: {}", healthstatus);
            } catch (final Exception e) {
                fail("Use_CmdHeartbeatListen.hearHearbeat has problems with with the delivered String", e);
            } 
    
        }    
    }

    public class Use_CmdHeartbeatSend  implements CmdHeartbeatSend.OnCmdHeartbeatSend{
	
        private boolean health = true;
        private String myClassName = Use_CmdHeartbeatSend.this.getMyClassName();
        private String testEngineLabel= "JTestUnitLabel";

        public  boolean getMyHealth() {
            return health;
        }
        
        public String getMyClassName() {
            return myClassName;
        }

          public String getMyTestEngineLabel() {
            return testEngineLabel;
        }
    }    

	@BeforeAll
	public static void startBroker() throws Exception {
		// configure the broker
		broker.addConnector("tcp://localhost:61616"); 
		broker.setPersistent(false);

		broker.start();
        Factory.initialize();
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
	public void testStartListener() throws Exception {
        Use_CmdHeartbeatListen querryClient = new Use_CmdHeartbeatListen();
        CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(querryClient);
        heartbeatListener.execute();

		Use_CmdHeartbeatSend testHeartbeatSender = new  Use_CmdHeartbeatSend();
		CmdHeartbeatSend cmdHeartbeatSend = new CmdHeartbeatSend(testHeartbeatSender);

		testHeartbeatSender.health=true;
        cmdHeartbeatSend.execute();
        Thread.sleep(1000);
        //assertTrue("health status was set to true", querryClient.healthstatus);
    }
}
