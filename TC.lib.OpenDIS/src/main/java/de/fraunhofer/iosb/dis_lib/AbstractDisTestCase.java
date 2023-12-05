package de.fraunhofer.iosb.dis_lib;

import org.slf4j.Logger;
import de.fraunhofer.iosb.tc_lib_if.*;

public abstract class AbstractDisTestCase extends AbstractTestCaseIf {

    @Override
    public IVCT_Verdict execute(Logger logger) {
        final IVCT_Verdict ivct_Verdict = new IVCT_Verdict();
        logTestPurpose(logger);
        
        try {
            preambleAction(logger);
            performTest(logger);
            postambleAction(logger);
        } catch (TcInconclusiveIf e) {
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = e.getMessage();
            return ivct_Verdict;
        } catch (TcFailedIf e) {
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.FAILED;
            ivct_Verdict.text = e.getMessage();
            return ivct_Verdict;
        }
        ivct_Verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        return ivct_Verdict;
    }

     /**
     * Send a text message to the IVCT operator and wait for confirmation
     * This method overwrites the inherited method and remaps the exception, 
     * in order to hide the internal API.
     * 
     * @param text String zu be sent to the ivct operator
     * @throws TcInconclusive Exception thrown if operator cancels confirmation
     */
    public void sendOperatorRequest(String text) throws TcInconclusive {
        try {
            super.sendOperatorRequest(text);
        } catch (TcInconclusiveIf e) {
            throw new TcInconclusive(e.getMessage());
        }
    }    
}
