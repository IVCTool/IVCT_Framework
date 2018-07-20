package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTTcService extends IService {
	SuTTcExecutionFormData prepareCreate(SuTTcExecutionFormData formData);

	SuTTcExecutionFormData create(SuTTcExecutionFormData formData);

	SuTTcExecutionFormData load(SuTTcExecutionFormData formData);

	SuTTcExecutionFormData store(SuTTcExecutionFormData formData);

	SuTTcExecutionFormData loadLogFile(SuTTcExecutionFormData formData, String fileName);
}
