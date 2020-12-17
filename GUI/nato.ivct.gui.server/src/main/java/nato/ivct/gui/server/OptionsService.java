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

package nato.ivct.gui.server;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;

import nato.ivct.commander.Factory;
import nato.ivct.gui.shared.CreateOptionsPermission;
import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.OptionsFormData;
import nato.ivct.gui.shared.ReadOptionsPermission;
import nato.ivct.gui.shared.UpdateOptionsPermission;

public class OptionsService implements IOptionsService {

	@Override
	public OptionsFormData prepareCreate(OptionsFormData formData) {
		if (!ACCESS.check(new CreateOptionsPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public OptionsFormData create(OptionsFormData formData) {
		if (!ACCESS.check(new CreateOptionsPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public OptionsFormData load(OptionsFormData formData) {
		if (!ACCESS.check(new ReadOptionsPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public OptionsFormData store(OptionsFormData formData) {
		if (!ACCESS.check(new UpdateOptionsPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}
	
	public void setLogLevel (String level) {
		ServerSession.get().setLogLevel(level);
	}
	
	public String getIvctVersion() {
		return Factory.getVersion();
	}
	
	public String getIvctBuild() {
		return Factory.getBuild();
	}

    public void setTestEngine (String testEngine) {
        ServerSession.get().setTestEngine(testEngine);
    }
}
