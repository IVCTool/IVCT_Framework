package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTBdParamEditService extends IService {

	SuTTcParamFormData prepareCreate(SuTTcParamFormData formData);

	SuTTcParamFormData create(SuTTcParamFormData formData);

	SuTTcParamFormData load(SuTTcParamFormData formData);

	SuTTcParamFormData store(SuTTcParamFormData formData);
}
