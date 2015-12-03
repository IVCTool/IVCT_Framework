package de.fraunhofer.iosb.tc;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcBaseModel;
import de.fraunhofer.iosb.tc_lib.TcFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.TcParamTmr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TC00003 extends AbstractTestCase {
    // Test case parameters
    private static Logger             logger               = LoggerFactory.getLogger(TC00003.class);
    private String                    federateName         = "B";

    // Build test case parameters to use
    final static TcParamTmr           tcParam              = new TcParamTmr();

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador ivct_rti             = IVCT_RTI_Factory.getIVCT_RTI(logger);
    final static TcBaseModel          tcBaseModel          = new TcBaseModel(logger, ivct_rti);

    final static TcFederateAmbassador tcFederateAmbassador = new TcFederateAmbassador(tcBaseModel, logger);


    public static void main(final String[] args) {
        new TC00003().execute(tcParam, tcBaseModel, logger);
    }


    @Override
    protected void preambleAction() {
        // Initiate rti
        tcBaseModel.initiateRti(this.federateName, tcFederateAmbassador, tcParam);

    }


    @Override
    protected void performTest() {
        // TODO Auto-generated method stub

    }


    @Override
    protected void postambleAction() {
        // Terminate rti
        tcBaseModel.terminateRti(tcParam);
    }

}
