package nato.ivct.commander;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class InitializationTest {

    @Before
    public void setUp() throws Exception {
        Factory.initialize();
    }

    @Test
    public void testReadVersion() {
        Factory.readVersion();
    }

    @Test
    public void testInitialize() {
        Factory.initialize();
    }

}
