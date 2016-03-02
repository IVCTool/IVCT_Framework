/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_BaseModel;
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
    static TcParamTmr           tcParam;

    // Get logging-IVCT-RTI using tc_param federation name, host
    private static IVCT_RTIambassador ivct_rti;
    private static TcBaseModel          tcBaseModel;

    final static TcFederateAmbassador tcFederateAmbassador = new TcFederateAmbassador(tcBaseModel, logger);


    public static void main(final String[] args) {
    	String paramJson = "{\"federationName\",\"HelloWorld\"}";
        new TC00003().execute(paramJson, logger);
    }
    
    @Override
    public IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) {
    	tcParam              = new TcParamTmr(tcParamJson);
    	ivct_rti             = IVCT_RTI_Factory.getIVCT_RTI(logger);
    	tcBaseModel          = new TcBaseModel(logger, ivct_rti, tcParam);
    	return tcBaseModel;
    }

    @Override
    protected void logTestPurpose(final Logger logger) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        stringBuilder.append("TEST PURPOSE\n");
        stringBuilder.append("Test initiateRti and terminateRti calls\n");
        stringBuilder.append("---------------------------------------------------------------------\n");
        final String testPurpose = stringBuilder.toString();

        logger.info(testPurpose);
    }

    @Override
    protected void preambleAction(final Logger logger) {
        // Initiate rti
        tcBaseModel.initiateRti(this.federateName, tcFederateAmbassador);

    }


    @Override
    protected void performTest(final Logger logger) {
        // TODO Auto-generated method stub

    }


    @Override
    protected void postambleAction(final Logger logger) {
        // Terminate rti
        tcBaseModel.terminateRti();
    }

}
