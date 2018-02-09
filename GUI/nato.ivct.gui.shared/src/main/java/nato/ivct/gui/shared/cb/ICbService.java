package nato.ivct.gui.shared.cb;

import java.util.Set;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;

@TunnelToServer
public interface ICbService extends IService {

	CbTablePageData getCbTableData(SearchFilter filter);

	CbTablePageData getCbTableData(SearchFilter filter, String sutId);

	CbFormData prepareCreate(CbFormData formData);

	CbFormData create(CbFormData formData);

	CbFormData load(CbFormData formData);

	CbFormData store(CbFormData formData);
	
	CbRequirementsTable loadRequirements(Set<String> badges);
}
