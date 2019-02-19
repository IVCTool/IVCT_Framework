package nato.ivct.gui.shared.sut;

import java.util.Set;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

@TunnelToServer
public interface ISuTService extends IService {
	Set<String> loadSuts();
	
	/*
	 * SuTFormData
	 */

	SuTFormData load(SuTFormData formData);
	
	/*
	 * SuTEditFormData
	 */

    SuTEditFormData load(SuTEditFormData formData);

	SuTEditFormData store(SuTEditFormData formData);

    SuTEditFormData prepareCreate(SuTEditFormData formData);

    SuTEditFormData create(SuTEditFormData formData);
}