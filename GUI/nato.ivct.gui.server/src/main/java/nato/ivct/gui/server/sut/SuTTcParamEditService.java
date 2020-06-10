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

package nato.ivct.gui.server.sut;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;

import nato.ivct.gui.shared.sut.CreateSuTBdParamPermission;
import nato.ivct.gui.shared.sut.ISuTBdParamEditService;
import nato.ivct.gui.shared.sut.ReadSuTBdParamEditPermission;
import nato.ivct.gui.shared.sut.SuTTcParamFormData;
import nato.ivct.gui.shared.sut.UpdateSuTBdParamPermission;

public class SuTTcParamEditService implements ISuTBdParamEditService {

	@Override
	public SuTTcParamFormData prepareCreate(SuTTcParamFormData formData) {
		if (!ACCESS.check(new CreateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTTcParamFormData create(SuTTcParamFormData formData) {
		if (!ACCESS.check(new CreateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTTcParamFormData load(SuTTcParamFormData formData) {
		if (!ACCESS.check(new ReadSuTBdParamEditPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTTcParamFormData store(SuTTcParamFormData formData) {
		if (!ACCESS.check(new UpdateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}
}
