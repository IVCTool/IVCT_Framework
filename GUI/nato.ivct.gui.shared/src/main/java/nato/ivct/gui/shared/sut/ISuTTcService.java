package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTTcService extends IService {
	SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData create(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData load(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData store(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData loadLogFile(SuTTcRequirementFormData formData, String fileName);

	SuTTcExecutionFormData load(SuTTcExecutionFormData formData);
}
