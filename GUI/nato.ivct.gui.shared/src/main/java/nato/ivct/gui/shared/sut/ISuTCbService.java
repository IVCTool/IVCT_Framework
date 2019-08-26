package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTCbService extends IService {

//	SuTCbTablePageData getSuTCbTableData(SearchFilter filter);
	void executeTestCase(String sut, String tc, String badge);
	
	SuTCbFormData load(SuTCbFormData formData);
	SuTCbFormData store(SuTCbFormData formData);
	
	SuTCbFormData prepareCreate(SuTCbFormData formData);
	SuTCbFormData create(SuTCbFormData formData);
	
	String loadTcParams (String sutId, String badgeId);
	boolean storeTcParams (String sutId, String badgeId, String parameters);
	boolean copyUploadedTcExtraParameterFile(String sutId, String cbId, BinaryResource file);
	BinaryResource getFileContent(String sutId, String cbId, String fileName);
}

