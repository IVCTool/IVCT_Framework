package nato.ivct.gui.server.sut;

import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import nato.ivct.gui.server.ServerSession;

@RunWithSubject("anonymous")
@RunWith(ServerTestRunner.class)
// The following annotation was commented out because it did not
// work with the use of an embedded ActiveMQ. A future developer
// will have to find a solution to this problem
//@RunWithServerSession(ServerSession.class)
public class SuTBdParamEditServiceTest {

	// TODO [the] add test cases
    @Test
    public void testDummy() {

    }
}
