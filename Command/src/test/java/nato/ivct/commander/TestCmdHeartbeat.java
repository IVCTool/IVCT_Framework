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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.Semaphore;

import org.json.simple.JSONObject;

public class TestCmdHeartbeat extends EmbeddedBrokerTest {

    static Logger LOGGER = LoggerFactory.getLogger(Use_CmdHeartbeatSend.class);

    public class Use_CmdHeartbeatListen implements CmdHeartbeatListen.OnCmdHeartbeatListen {
        public String heartbeatSender = "undefined";
        public String timestamp = "undefined";
        public boolean healthStatus = false;
        public Semaphore semaphore = new Semaphore(0);

        // work with a Json Object as return
        public void hearHeartbeat(JSONObject jsonObject) {
            LOGGER.info("receiving heartbeat message {}", jsonObject.toString());
            assertNotNull(jsonObject, "heartbeat message must not be null");
            try {
                heartbeatSender = (String) jsonObject.get("HeartbeatSender");
                timestamp = (String) jsonObject.get("LastSendingTime");
                Object healthStatusRaw = jsonObject.get("SenderHealthState");
                if (healthStatusRaw != null) {
                    healthStatus = (boolean) healthStatusRaw;
                }
                LOGGER.debug("HeartbeatSender: {}", heartbeatSender);
                LOGGER.debug("LastSendingTime: {}", timestamp);
                LOGGER.debug("SenderHealthState: {}", healthStatus);
            } catch (final Exception e) {
                fail("Use_CmdHeartbeatListen.hearHearbeat has problems with with the delivered String", e);
            }
            semaphore.release(1);
        }
    }

    public class Use_CmdHeartbeatSend implements CmdHeartbeatSend.OnCmdHeartbeatSend {

        private boolean health = true;
        private String myClassName = Use_CmdHeartbeatSend.this.getMyClassName();
        private String testEngineLabel = "JTestUnitLabel";

        public boolean getMyHealth() {
            return health;
        }

        public String getMyClassName() {
            return myClassName;
        }

        public String getMyTestEngineLabel() {
            return testEngineLabel;
        }
    }

    // due to broker synchronization issues between unit tests, this test will be
    // called within the Factory test class
    // @Test
    public void testCmdHeartbeat() throws Exception {
        LOGGER.info("Starting test testCmdHeartbeat");
        Use_CmdHeartbeatListen queryClient = new Use_CmdHeartbeatListen();
        CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(queryClient);
        heartbeatListener.execute();

        Use_CmdHeartbeatSend testHeartbeatSender = new Use_CmdHeartbeatSend();
        CmdHeartbeatSend cmdHeartbeatSend = new CmdHeartbeatSend(testHeartbeatSender);

        testHeartbeatSender.health = true;
        cmdHeartbeatSend.execute();
        for (int i = 0; (i < 10 && !queryClient.healthStatus); i++) {
            queryClient.semaphore.acquire(1);
            LOGGER.info("testCmdHeartbeat received with status {}", queryClient.healthStatus);
        }
        assertTrue("health status was set to true", queryClient.healthStatus);
        heartbeatListener.timer.cancel();
        cmdHeartbeatSend.cancel();
        LOGGER.info("Done with test testCmdHeartbeat");
    }
}
