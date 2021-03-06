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

import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;


@TunnelToServer
public interface ISuTTcService extends IService {
    SuTTcRequirementFormData prepareCreate(SuTTcRequirementFormData formData);


    SuTTcRequirementFormData create(SuTTcRequirementFormData formData);


    SuTTcExecutionFormData load(SuTTcExecutionFormData formData);
    
    
    String getTcLastVerdict(String sutId, String testsuiteId, String tcId);


    SuTTcExecutionFormData updateLogFileTable(SuTTcExecutionFormData formData);


    void executeTestCase(String sutId, String tc, String badgeId, String testEngine);


    SuTTcExecutionFormData loadJSONLogFileContent(String sutId, String testsuiteId, String fileName, SuTTcExecutionFormData formData);
    
    
    BinaryResource getLogfileContent(String sutId, String testsuiteId, String fileName);


    void abortTestCase(String sutId, String testCaseId, String testEngine);
}
