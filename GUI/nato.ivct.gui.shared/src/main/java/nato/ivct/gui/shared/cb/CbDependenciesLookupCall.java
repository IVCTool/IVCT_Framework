/* Copyright 2020, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class CbDependenciesLookupCall extends LookupCall<String> {

	private static final long serialVersionUID = 1L;
	
	private String m_cbId;
	
	public String getCbId() {
		return m_cbId;
	}
	
	public void setCbId (String cbId) {
		m_cbId = cbId;
	}

	@Override
	protected Class<? extends ILookupService<String>> getConfiguredService() {
		return ICbDependenciesLookupService.class;
	}
}
