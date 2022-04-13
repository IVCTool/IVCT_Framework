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
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

public class TestCmdListBadges extends EmbeddedBrokerTest {

    public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TestCmdListBadges.class);
    CmdListBadges lb;
    
    @Test
    public void testCmdListBadges()  {
        LOGGER.info("Starting test testCmdListBadges");
        this.lb = Factory.createCmdListBadges();
        this.lb.execute();
        assertNotNull(this.lb, "Factory Test createCmdListBadges should return CmdListBadges");
        assertTrue(this.lb.badgeMap.size() > 0, "badge list should not be empty");
        Set<String> irSet = new HashSet<>();
        Set<String> cs = new HashSet<>();
        cs.add("HelloWorld-2019");
        cs.add("HLA-BASE-2019");
        this.lb.collectIrForCs(irSet, cs);
        assertTrue(irSet.size() > 0, "interoperability set should not be empty");
        cs.clear();
        irSet.clear();
        cs.add("NETN-4.0");
        lb.collectIrForCs(irSet, cs);
        assertTrue(irSet.size() > 0, "interoperability set for NETN badge should not be empty");
    }
}
