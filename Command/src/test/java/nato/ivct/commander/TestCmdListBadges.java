package nato.ivct.commander;

import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;


public class TestCmdListBadges {

    CmdListBadges lb;

    @Before
    public void setUp() throws Exception {
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
