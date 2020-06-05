/* Copyright 2020, Reinhard Herzog, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.server.cb;

import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.junit.Test;

import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.CbDependenciesLookupCall;

//@RunWithSubject("anonymous")
//@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class CbDependenciesLookupCallTest {

	protected CbDependenciesLookupCall createLookupCall() {
		return new CbDependenciesLookupCall();
	}

	@Test
	public void testLookupByAll() {
//		CbDependenciesLookupCall call = createLookupCall();
		// TODO [the] fill call
//		List<? extends ILookupRow<String>> data = call.getDataByAll();
		// TODO [the] verify data
	}

	@Test
	public void testLookupByKey() {
//		CbDependenciesLookupCall call = createLookupCall();
		// TODO [the] fill call
//		List<? extends ILookupRow<String>> data = call.getDataByKey();
		// TODO [the] verify data
	}

	@Test
	public void testLookupByText() {
//		CbDependenciesLookupCall call = createLookupCall();
		// TODO [the] fill call
//		List<? extends ILookupRow<String>> data = call.getDataByText();
		// TODO [the] verify data
	}

	// TODO [the] add test cases
}
