package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.testenvironment.TestEnvironmentClientSession;
import org.eclipse.scout.rt.testing.client.runner.ClientTestRunner;
import org.eclipse.scout.rt.testing.client.runner.RunWithClientSession;
import org.eclipse.scout.rt.testing.platform.mock.BeanMock;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;

import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTFormData;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class SuTFormTest {

	@BeanMock
	private ISuTService m_mockSvc;

	@Before
	public void setup() {
		SuTFormData answer = new SuTFormData();
		Mockito.when(m_mockSvc.load(Matchers.any(SuTFormData.class))).thenReturn(answer);
	}

	// TODO [hzg] add test cases
	
	@Test
	public void testDummy () {
		
	}
}
