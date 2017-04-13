package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface ISuTService extends IService {

	SuTTablePageData getSuTTableData(SearchFilter filter);

	SuTFormData prepareCreate(SuTFormData formData);

	SuTFormData create(SuTFormData formData);

	SuTFormData load(SuTFormData formData);

	SuTFormData store(SuTFormData formData);
}
