package nato.ivct.gui.shared.sut;

import java.util.ArrayList;

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface ISuTCbService extends IService {

    String loadTcParams(String sutId, String tsId);


    boolean storeTcParams(String sutId, String tsId, String parameters);


    boolean copyUploadedTcExtraParameterFile(String sutId, String tsId, BinaryResource file);
    
   
    boolean deleteUploadedTcExtraParameterFile(String sutId, String tsId, BinaryResource file);


    BinaryResource getFileContent(String sutId, String tsId, String fileName);


    ArrayList<String> loadTcExtraParameterFiles(String sutId, String activeTsId);
}
