package nato.ivct.gui.server;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;

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
}
