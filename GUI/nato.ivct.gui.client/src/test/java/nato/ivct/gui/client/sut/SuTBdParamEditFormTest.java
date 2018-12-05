package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.testenvironment.TestEnvironmentClientSession;
import org.eclipse.scout.rt.testing.client.runner.ClientTestRunner;
import org.eclipse.scout.rt.testing.client.runner.RunWithClientSession;
import org.eclipse.scout.rt.testing.platform.mock.BeanMock;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import nato.ivct.gui.shared.sut.ISuTBdParamEditService;
import nato.ivct.gui.shared.sut.SuTTcParamFormData;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class SuTBdParamEditFormTest {

	@BeanMock
	private ISuTBdParamEditService m_mockSvc;

	@Before
	public void setup() {
		SuTTcParamFormData answer = new SuTTcParamFormData();
		Mockito.when(m_mockSvc.prepareCreate(ArgumentMatchers.any())).thenReturn(answer);
		Mockito.when(m_mockSvc.create(ArgumentMatchers.any())).thenReturn(answer);
		Mockito.when(m_mockSvc.load(ArgumentMatchers.any())).thenReturn(answer);
		Mockito.when(m_mockSvc.store(ArgumentMatchers.any())).thenReturn(answer);
	}

	// TODO [the] add test cases
}
