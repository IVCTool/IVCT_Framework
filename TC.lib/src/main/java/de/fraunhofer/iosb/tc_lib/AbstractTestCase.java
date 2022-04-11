/*
 * Copyright 2015, Johannes Mulder (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.tc_lib;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import de.fraunhofer.iosb.tc_lib_if.*;

/**
 * Abstract base class for test cases. In the concrete test cases, the four
 * methods logTestPurpose, preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase extends AbstractTestCaseIf {

    IVCT_BaseModel ivct_BaseModel = null;

    /**
     * @param tcParamJson a JSON string containing values to use in the testcase
     * @param logger The {@link Logger} to use
     * @return the IVCT base model to use in the test cases
     * @throws TcInconclusive if test is inconclusive
     */
    protected abstract IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive;


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

    /**
     * The execute method is used to perform the test, including preamble and postamble.
     * 
     * @param logger The {@link Logger} to use
     * @return the verdict
     */
    @Override
    public IVCT_Verdict execute(final Logger logger) {

            final IVCT_Verdict ivct_Verdict = new IVCT_Verdict();

        // A one-time start message
        logger.info("Test Case Started");

        final StringBuilder tcGlobalVariables = new StringBuilder();
        tcGlobalVariables.append("\nTEST CASE GLOBAL VARIABLES -------------------------------------- BEGIN");
        if (sutName != null) {
            tcGlobalVariables.append("\nSUT Name: ");
            tcGlobalVariables.append(sutName);
        }
        if (sutFederateName != null) {
            tcGlobalVariables.append("\nSUT Federate Name: ");
            tcGlobalVariables.append(sutFederateName);
        }
        if (federationName != null) {
            tcGlobalVariables.append("\nSUT Federation Name: ");
            tcGlobalVariables.append(federationName);
        }
        if (settingsDesignator != null) {
            tcGlobalVariables.append("\nSettings Designator: ");
            tcGlobalVariables.append(settingsDesignator);
        }
        tcGlobalVariables.append("\nTEST CASE GLOBAL VARIABLES -------------------------------------- END");
        logger.info(tcGlobalVariables.toString());

        String tcParamTmp = new String();
        tcParamTmp = "\n" + "TEST CASE PARAMETERS -------------------------------------- BEGIN\n" + tcParam + "\nTEST CASE PARAMETERS -------------------------------------- END";

        logger.info(tcParamTmp);

        try {
            ivct_BaseModel = getIVCT_BaseModel(tcParam, logger);
            ivct_BaseModel.setFederationName(federationName);
            ivct_BaseModel.setSettingsDesignator(settingsDesignator);
        }
        catch (final TcInconclusiveIf e) {
            final String verdictText = "getIVCT_BaseModel unsuccessful";
            logger.warn("TC INCONCLUSIVE Initialization Error <{}>: {}", verdictText, e);
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = verdictText;
            return ivct_Verdict;
        }

        sendTcStatus("initiated", 0);

        logTestPurpose(logger);

        // preamble block
        try {
            // Test case phase
            logger.info("TEST CASE PREAMBLE");

            // Publish interaction / object classes
            // Subscribe interaction / object classes

            preambleAction(logger);
        }
        catch (final TcInconclusiveIf ex) {
            if (ivct_BaseModel != null) {
                ivct_BaseModel.shutdown();
            }
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("started", 0);

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            performTest(logger);

        }
        catch (final TcInconclusiveIf ex) {
            if (ivct_BaseModel != null) {
                ivct_BaseModel.shutdown();
            }
            logger.warn("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }
        catch (final TcFailedIf ex) {
            if (ivct_BaseModel != null) {
                ivct_BaseModel.shutdown();
            }
            logger.warn("TC FAILED " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.FAILED;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("done", 99);

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            postambleAction(logger);
            logger.info("TC PASSED");
        }
        catch (final TcInconclusiveIf ex) {
            if (ivct_BaseModel != null) {
                ivct_BaseModel.shutdown();
            }
            logger.warn("TC INCONCLUSIVE " + ex.getMessage());
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = ex.getMessage();
            return ivct_Verdict;
        }

        sendTcStatus("finished", 100);
        logger.info("TEST CASE FINISHED");

        ivct_Verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        return ivct_Verdict;
    }

}
