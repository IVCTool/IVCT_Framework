package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface ISuTTcService extends IService {
    SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData);


    SuTTcRequirementFormData create(SuTTcRequirementFormData formData);


    SuTTcExecutionFormData load(SuTTcExecutionFormData formData);
    
    
    String getTcLastVerdict(String sutId, String testsuiteId, String tcId);


    SuTTcExecutionFormData updateLogFileTable(SuTTcExecutionFormData formData);


    void executeTestCase(String sutId, String tc, String badgeId);


    SuTTcExecutionFormData loadJSONLogFileContent(String sutId, String testsuiteId, String fileName, SuTTcExecutionFormData formData);
}
