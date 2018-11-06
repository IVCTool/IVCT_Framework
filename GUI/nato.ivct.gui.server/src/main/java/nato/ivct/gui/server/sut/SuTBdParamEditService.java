package nato.ivct.gui.server.sut;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;

import nato.ivct.gui.shared.sut.CreateSuTBdParamPermission;
import nato.ivct.gui.shared.sut.ISuTBdParamEditService;
import nato.ivct.gui.shared.sut.ReadSuTBdParamEditPermission;
import nato.ivct.gui.shared.sut.SuTBdParamFormData;
import nato.ivct.gui.shared.sut.UpdateSuTBdParamPermission;

public class SuTBdParamEditService implements ISuTBdParamEditService {

	@Override
	public SuTBdParamFormData prepareCreate(SuTBdParamFormData formData) {
		if (!ACCESS.check(new CreateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTBdParamFormData create(SuTBdParamFormData formData) {
		if (!ACCESS.check(new CreateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTBdParamFormData load(SuTBdParamFormData formData) {
		if (!ACCESS.check(new ReadSuTBdParamEditPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}

	@Override
	public SuTBdParamFormData store(SuTBdParamFormData formData) {
		if (!ACCESS.check(new UpdateSuTBdParamPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [the] add business logic here.
		return formData;
	}
}
