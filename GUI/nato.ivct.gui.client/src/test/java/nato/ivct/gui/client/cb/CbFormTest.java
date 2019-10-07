package nato.ivct.gui.client.cb;

import org.eclipse.scout.rt.client.testenvironment.TestEnvironmentClientSession;
import org.eclipse.scout.rt.testing.client.runner.ClientTestRunner;
import org.eclipse.scout.rt.testing.client.runner.RunWithClientSession;
import org.eclipse.scout.rt.testing.platform.mock.BeanMock;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.ICbService;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class CbFormTest {

	@BeanMock
	private ICbService m_mockSvc;

	@Before
	public void setup() {
		CbFormData answer = new CbFormData();
		Mockito.when(m_mockSvc.prepareCreate(ArgumentMatchers.any(CbFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.create(ArgumentMatchers.any(CbFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.load(ArgumentMatchers.any(CbFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.store(ArgumentMatchers.any(CbFormData.class))).thenReturn(answer);
	}

	// TODO [hzg] add test cases
	@Test
	public void testDummy() {

	}
}
