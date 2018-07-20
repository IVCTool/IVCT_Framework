package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.shared.sut.SuTCbFormData;

@TunnelToServer
public interface ISuTCbService extends IService {

	SuTCbTablePageData getSuTCbTableData(SearchFilter filter);
	void executeTestCase(String sut, String tc, String badge);
	
	SuTCbFormData load(SuTCbFormData formData);
	SuTCbFormData store(SuTCbFormData formData);
	
	SuTCbFormData prepareCreate(SuTCbFormData formData);
	SuTCbFormData create(SuTCbFormData formData);
}
