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

package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class CapabilityCodeType extends AbstractCodeType<Integer, String> {

	private static final long serialVersionUID = 1L;
	public static final int ID = 0;

	@Override
	public Integer getId() {
		return ID;
	}

	@Order(1000)
	public static class CapCode extends AbstractCode<Integer> {
		private static final long serialVersionUID = 1L;
		public static final Integer ID = 0;

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("MyNlsKey");
		}

		@Override
		public Integer getId() {
			return ID;
		}
	}
	
	
}
