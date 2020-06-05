/* Copyright 2020, Michael Theis, Felix Schoeppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

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
