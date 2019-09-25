package nato.ivct.commander;

import static org.junit.Assert.*;

import org.junit.Test;

public class SutDescriptionTest {

    @Test
    public void testExecute() {
        CmdListSuT listSuT = Factory.createCmdListSuT();
        assertTrue("CmdListSuT should be created", listSuT != null);
        listSuT.execute();
        
    }

}
