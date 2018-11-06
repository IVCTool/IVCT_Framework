package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTBdParamEditService extends IService {

	SuTBdParamFormData prepareCreate(SuTBdParamFormData formData);

	SuTBdParamFormData create(SuTBdParamFormData formData);

	SuTBdParamFormData load(SuTBdParamFormData formData);

	SuTBdParamFormData store(SuTBdParamFormData formData);
}
