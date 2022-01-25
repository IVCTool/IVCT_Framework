package dis_lib;

import org.slf4j.Logger;
import de.fraunhofer.iosb.tc_lib_if.*;

public abstract class AbstractDisTestCase extends AbstractTestCaseIf {

    protected abstract IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive;

    @Override
    public IVCT_Verdict execute(String tcParamJson, Logger logger) {
        // TODO Auto-generated method stub
        return super.execute(tcParamJson, logger);
    }
}
