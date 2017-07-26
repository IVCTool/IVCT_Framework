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
import org.slf4j.MDC;


/**
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase {

    /**
     * @param tcParamJson a JSON string containing values to use in the testcase
     * @param logger The {@link Logger} to use
     * @return the IVCT base model to use in the test cases
     * @throws TcInconclusive if test is inconclusive
     */
	protected abstract IVCT_BaseModel getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusive;


    /**
     * @param logger The {@link Logger} to use
     */
	protected abstract void logTestPurpose(final Logger logger);


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     * @throws TcFailed if test case failed
     */
	protected abstract void performTest(final Logger logger) throws TcInconclusive, TcFailed;


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     */
	protected abstract void preambleAction(final Logger logger) throws TcInconclusive;


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusive if test is inconclusive
     */
	protected abstract void postambleAction(final Logger logger) throws TcInconclusive;


    /**
     * @param tcParamJson test case parameters
     * @param logger The {@link Logger} to use
     * @return the verdict
     */
    public IVCT_Verdict execute(final String tcParamJson, final Logger logger) {
    	
    	IVCT_BaseModel ivct_BaseModel = null;
    	IVCT_Verdict ivct_Verdict = new IVCT_Verdict();
        MDC.put("testcase", this.getClass().getSimpleName());
    	
    	try {
			ivct_BaseModel = getIVCT_BaseModel(tcParamJson, logger);
		} catch (TcInconclusive e) {
			String s = "getIVCT_BaseModel unsuccessful";
        	logger.info("TC INCONCLUSIVE " + s);
        	ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
        	ivct_Verdict.text = s;
        	return ivct_Verdict;
		}
    	
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
        	if (ivct_BaseModel != null) {
        		ivct_BaseModel.terminateRti();
        	}
        	logger.info("TC INCONCLUSIVE " + ex.getMessage());
        	ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
        	ivct_Verdict.text = ex.getMessage();
        	return ivct_Verdict;
        }

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            this.performTest(logger);

        }
        catch (final TcInconclusive ex) {
        	if (ivct_BaseModel != null) {
        		ivct_BaseModel.terminateRti();
        	}
        	logger.info("TC INCONCLUSIVE " + ex.getMessage());
        	ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
        	ivct_Verdict.text = ex.getMessage();
        	return ivct_Verdict;
        }
        catch (final TcFailed ex) {
        	if (ivct_BaseModel != null) {
        		ivct_BaseModel.terminateRti();
        	}
        	logger.info("TC FAILED " + ex.getMessage());
        	ivct_Verdict.verdict = IVCT_Verdict.Verdict.FAILED;
        	ivct_Verdict.text = ex.getMessage();
        	return ivct_Verdict;
        }

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            this.postambleAction(logger);
            logger.info("TC PASSED");
        }
        catch (final TcInconclusive ex) {
        	if (ivct_BaseModel != null) {
        		ivct_BaseModel.terminateRti();
        	}
        	logger.info("TC INCONCLUSIVE " + ex.getMessage());
        	ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
        	ivct_Verdict.text = ex.getMessage();
        	return ivct_Verdict;
        }
        
        ivct_Verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        return ivct_Verdict;
    }
}
