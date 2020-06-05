/* Copyright 2020, Felix Schöppenthau (Fraunhofer IOSB)

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

import java.io.Serializable;


public class TcOperatorRequestNotification implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sutName;
    private String testSuiteId;
    private String testCaseId;
    private String operatorMessage;

    public String getSutName() {
        return sutName;
    }


    public void setSutName(final String sutName) {
        this.sutName = sutName;
    }


    public String getTestSuiteId() {
        return testSuiteId;
    }


    public void setTestSuiteId(final String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }


    public String getTestCaseId() {
        return testCaseId;
    }


    public void setTestCaseId(final String testCaseId) {
        this.testCaseId = testCaseId;
    }


    public String getOperatorMessage() {
        return operatorMessage;
    }


    public void setOperatorMessage(final String operatorMessage) {
        this.operatorMessage = operatorMessage;
    }

}
