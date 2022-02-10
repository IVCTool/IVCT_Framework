package dis_lib;

import org.slf4j.Logger;
import de.fraunhofer.iosb.tc_lib_if.*;

public abstract class AbstractDisTestCase extends AbstractTestCaseIf {

    protected abstract IVCT_BaseModel getIVCT_BaseModel(final Logger logger) throws TcInconclusive;

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
}
