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

import de.fraunhofer.iosb.tc_lib.IVCT_RTI_Factory;
import de.fraunhofer.iosb.tc_lib.IVCT_RTIambassador;
import de.fraunhofer.iosb.tc_lib.TcBaseModel;
import de.fraunhofer.iosb.tc_lib.TcFederateAmbassador;
import de.fraunhofer.iosb.tc_lib.TcInconclusive;
import de.fraunhofer.iosb.tc_lib.TcParamTmr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TC00002 {
    // Test case parameters
    private static Logger                   logger             = LoggerFactory.getLogger(TC00002.class);
    private static String                   federateName       = "B";


    public static void main(final String[] args) {
        // Build test case parameters to use
    	String paramJson = "{\"federationName\" : \"HelloWorld\"}";
		TcParamTmr tcParam = null;
		try {
			tcParam = new TcParamTmr(paramJson);
		} catch (TcInconclusive e) {
			System.exit(0);
		}
		if (tcParam != null) {
			execute(tcParam);
		}
    }


    /**
     * @param tcParam test case parameters
     */
    public static void execute(final TcParamTmr tcParam) {
        final IVCT_RTIambassador ivct_FederateAmbassador = IVCT_RTI_Factory.getIVCT_RTI(logger);
        final TcBaseModel baseModelTc = new TcBaseModel(logger, ivct_FederateAmbassador, tcParam);
        final TcFederateAmbassador tcFederateAmbassador = new TcFederateAmbassador(baseModelTc, logger);
        // Get logging-IVCT-RTI using tc_param federation name, host

        // Test case phase
        logger.info("TEST CASE PREAMBLE");

        // Initiate rti
        baseModelTc.initiateRti(federateName, tcFederateAmbassador);

        // Publish interaction / object classes

        // Subscribe interaction / object classes

        // Test case phase
        logger.info("TEST CASE BODY");

        // PERFORM TEST

        // Test case phase
        logger.info("TEST CASE POSTAMBLE");

        // Terminate rti
        baseModelTc.terminateRti();
    }
}
