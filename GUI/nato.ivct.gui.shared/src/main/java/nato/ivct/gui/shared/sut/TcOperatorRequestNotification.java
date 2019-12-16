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
