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

package de.fraunhofer.iosb.tc_lib;

import org.slf4j.Logger;


/**
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase {

	protected abstract IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger);


	protected abstract void logTestPurpose(final Logger logger);


	protected abstract void performTest(final Logger logger) throws TcInconclusive, TcFailed;


	protected abstract void preambleAction(final Logger logger) throws TcInconclusive;


	protected abstract void postambleAction(final Logger logger) throws TcInconclusive;


    /**
     * @param tcParamJson test case parameters
     * @param logger The {@link Logger} to use
     */
    public void execute(final String tcParamJson, final Logger logger) {
    	
    	IVCT_BaseModel ivct_BaseModel;
    	
    	ivct_BaseModel = getIVCT_BaseModel(tcParamJson, logger);
    	
    	logTestPurpose(logger);

        // Print out test case parameters
        // logger.info(tcParam.toString());

        // preamble block
        try {
            // Test case phase
            logger.info("TEST CASE PREAMBLE");

            // Publish interaction / object classes
            // Subscribe interaction / object classes

            this.preambleAction(logger);
        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti();
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            this.performTest(logger);

        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti();
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }
        catch (final TcFailed ex) {
            ivct_BaseModel.terminateRti();
            logger.info("TC FAILED " + ex.getMessage());
            return;
        }

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            this.postambleAction(logger);
            logger.info("TC PASSED");
        }
        catch (final TcInconclusive ex) {
            ivct_BaseModel.terminateRti();
            logger.info("TC INCONCLUSIVE " + ex.getMessage());
            return;
        }
    }
}
