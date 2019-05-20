package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTTcService extends IService {
	SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData create(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData load(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData store(SuTTcRequirementFormData formData);

	SuTTcRequirementFormData loadLogFileContent(SuTTcRequirementFormData formData, String fileName);

	SuTTcExecutionFormData load(SuTTcExecutionFormData formData);

	SuTTcExecutionFormData loadLogFileContent(SuTTcExecutionFormData formData, String fileName);

	SuTTcRequirementFormData updateLogFileTable(SuTTcRequirementFormData formData);
}
