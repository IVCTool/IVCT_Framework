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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.slf4j.Logger;

public class SutDescriptionTest extends EmbeddedBrokerTest {
    static final private Logger LOGGER = org.slf4j.LoggerFactory.getLogger(SutDescriptionTest.class);

    // due to broker synchronization issues between unit tests, this test will be
    // called within the Factory test class
    // @Test
	@EnabledIfEnvironmentVariable(named = "IVCT_CONF", matches = ".+")
    public void testSutDescriptionTest() {
		LOGGER.info("Starting testSutDescriptionTest");
        CmdListSuT listSuT = Factory.createCmdListSuT();
        assertNotNull(listSuT, "CmdListSuT should be created");
        listSuT.execute();
        assertTrue("SuT list shall not be empty", !listSuT.sutMap.isEmpty());
    }

}
