/* Copyright 2020, Reinhard Herzog, Johannes Mulder, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.client;

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

import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.OptionsFormData;

@RunWithSubject("anonymous")
@RunWith(ClientTestRunner.class)
@RunWithClientSession(TestEnvironmentClientSession.class)
public class OptionsFormTest {

	@BeanMock
	private IOptionsService m_mockSvc;

	@Before
	public void setup() {
		OptionsFormData answer = new OptionsFormData();
		Mockito.when(m_mockSvc.prepareCreate(ArgumentMatchers.any(OptionsFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.create(ArgumentMatchers.any(OptionsFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.load(ArgumentMatchers.any(OptionsFormData.class))).thenReturn(answer);
		Mockito.when(m_mockSvc.store(ArgumentMatchers.any(OptionsFormData.class))).thenReturn(answer);
	}

	// TODO [hzg] add test cases
	@Test
	public void testDummy () {
		
	}
}
