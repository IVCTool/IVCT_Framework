package nato.ivct.gui.shared.sut;

import java.util.Set;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;

@TunnelToServer
public interface ISuTService extends IService {
	Set<String> loadSuts();
	
	/*
	 * SuTFormData
	 */
	
	SuTFormData load(SuTFormData formData);
	
	boolean deleteSut(String sutId);
	
	/*
	 * SuTEditFormData
	 */

    SuTEditFormData load(SuTEditFormData formData);

	SuTEditFormData store(SuTEditFormData formData);

    SuTEditFormData prepareCreate(SuTEditFormData formData);

    SuTEditFormData create(SuTEditFormData formData);
    
    /*
     * TestReport
     */

    BinaryResource getTestReportFileContent(String sutId, String fileName);

    String createTestreport(String sutId);


}