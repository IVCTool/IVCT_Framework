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

import java.util.concurrent.Semaphore;

import javax.xml.soap.Text;

import org.slf4j.Logger;
import org.slf4j.MDC;

import nato.ivct.commander.CmdOperatorConfirmationListener.OperatorConfirmationInfo;
import nato.ivct.commander.CmdOperatorRequest;
import nato.ivct.commander.CmdSendTcStatus;
import nato.ivct.commander.Factory;

import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase {

    private final CmdSendTcStatus statusCmd = Factory.createCmdSendTcStatus();

    public void sendTcStatus(String status, int percent) {
        statusCmd.setStatus(status);
        statusCmd.setPercentFinshed(percent);
        statusCmd.setTcName(tcName);
        statusCmd.setSutName(sutName);
        statusCmd.execute();
    }
    
    private Logger defaultLogger = null;

    private String testSuiteId = null;
    private String tcName  = null;
    private String sutName = null;
    private String settingsDesignator;
    private String federationName;
    private String sutFederateName;
    
    private Semaphore semOperatorRequest = new Semaphore(0);
    private boolean confirmationBool = false;
    private String cnfText;
    private String testCaseId;


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
    
    public void setDefaultLogger(final Logger logger) {
    	this.defaultLogger = logger;
    }

    public void sendOperatorRequest(String text) throws InterruptedException, TcInconclusive {
    	if (text == null) {
    		// Make an empty string
    		text = new String();
    	}
    	CmdOperatorRequest operatorRequestCmd = Factory.createCmdOperatorRequest(sutName, testSuiteId, tcName, text);
    	operatorRequestCmd.execute();
    	semOperatorRequest.acquire();
    	if (confirmationBool == false) {
    		if (cnfText != null) {
                throw new TcInconclusive("Operator reject message: " + cnfText);
    		} else {
                throw new TcInconclusive("Operator reject message: - no reject text -");
    		}
    	}
    }

    public void onOperatorConfirmation(OperatorConfirmationInfo operatorConfirmationInfo) {
        MDC.put("testcase", this.getClass().getName());
        MDC.put("sutName", sutName);
        MDC.put("badge", testSuiteId);
    	testCaseId = operatorConfirmationInfo.testCaseId;
    	confirmationBool = operatorConfirmationInfo.confirmationBool;
    	cnfText = operatorConfirmationInfo.text;
		String boolText;
		if (operatorConfirmationInfo.confirmationBool) {
			boolText = "true";
		} else {
			boolText = "false";
		}
		if (defaultLogger != null) {
		    defaultLogger.info("OperatorConfirmation: " + boolText + " Text: " + operatorConfirmationInfo.text);
		}
    	semOperatorRequest.release();
    }
    /**
     * @param tcParamJson test case parameters
     * @param logger The {@link Logger} to use
     * @return the verdict
     */
    public IVCT_Verdict execute(final String tcParamJson, final Logger logger) {

        IVCT_BaseModel ivct_BaseModel = null;
        final IVCT_Verdict ivct_Verdict = new IVCT_Verdict();
        MDC.put("testcase", this.getClass().getName());

        // A one-time start message
        MDC.put("tcStatus", "started");
        logger.info("Test Case Started");

        MDC.put("tcStatus", "running");

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
            tcGlobalVariables.append("\nsettingsDesignator: ");
            tcGlobalVariables.append(settingsDesignator);
        }
        tcGlobalVariables.append("\nTEST CASE GLOBAL VARIABLES -------------------------------------- END");
        logger.info(tcGlobalVariables.toString());

        String tcParam = new String();
        tcParam = "\n" + "TEST CASE PARAMETERS -------------------------------------- BEGIN\n" + tcParamJson + "\nTEST CASE PARAMETERS -------------------------------------- END";

        logger.info(tcParam);

        try {
            ivct_BaseModel = getIVCT_BaseModel(tcParamJson, logger);
            ivct_BaseModel.setFederationName(federationName);
            ivct_BaseModel.setSettingsDesignator(settingsDesignator);
        }
        catch (final TcInconclusive e) {
            logger.info("Exception: " + e);
            final String s = "getIVCT_BaseModel unsuccessful";
            logger.info("TC INCONCLUSIVE " + s);
            ivct_Verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            ivct_Verdict.text = s;
            return ivct_Verdict;
        }

        sendTcStatus("initiated", 0);

        logTestPurpose(logger);

        // Print out test case parameters
        // logger.info(tcParam.toString());

        // preamble block
        try {
            // Test case phase
            logger.info("TEST CASE PREAMBLE");

            // Publish interaction / object classes
            // Subscribe interaction / object classes

            preambleAction(logger);
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

        sendTcStatus("started", 0);

        //test body block
        try {
            // Test case phase
            logger.info("TEST CASE BODY");

            // PERFORM TEST
            performTest(logger);

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

        sendTcStatus("done", 99);

        // postamble block
        try {
            // Test case phase
            logger.info("TEST CASE POSTAMBLE");
            postambleAction(logger);
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

        sendTcStatus("finished", 100);

        ivct_Verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        return ivct_Verdict;
    }


    /**
     * Returns the name test suite
     *
     * @return Test Suite Name
     */
    public String getTsName() {
        return testSuiteId;
    }


    public void setTsName(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }


    /**
     * Returns the name of the fully qualified class name of the test case
     *
     * @return Test Case Name
     */
    public String getTcName() {
        return tcName;
    }


    public void setTcName(String tcName) {
        this.tcName = tcName;
    }


    /**
     * Returns the name of the System under Test. This may be different from the
     * federate name.
     * 
     * @return SutName
     */
    public String getSutName() {
        return sutName;
    }


    /**
     * Set the name of the System under Test.
     * 
     * @param sutName Name of the system under test
     */
    public void setSutName(String sutName) {
        this.sutName = sutName;
    }


    /**
     * Set the connection string to be used to connect to the RTI
     * 
     * @param settingsDesignator Connection String
     */
    public void setSettingsDesignator(String settingsDesignator) {
        this.settingsDesignator = settingsDesignator;
    }


    public void setFederationName(String federationName) {
        this.federationName = federationName;
    }


    /**
     * Set the name for the SuT federate which is being used in the HLA federation.
     * This value will be set by test case engine and may be used by the test case
     * logic to identify the federate to be tested.
     *
     * @param sutFederateName Federate name of the sut
     */
    public void setSutFederateName(String sutFederateName) {
        this.sutFederateName = sutFederateName;
    }


    /**
     * Returns the federate name of the SuT
     * 
     * @return Federate Name of the SuT
     */
    public String getSutFederateName() {
        return sutFederateName;
    }
    
    
    
    /**
     * Returns the IVCT-Version which has this TestCase at building-time for
     * checking against the IVCT-Version of at Runtime
     *  
     */
    public String getIVCTVersion()  throws IVCTVersionCheckException {
      
      String infoIVCTVersion = "not defined yet";  
      InputStream in = this.getClass().getResourceAsStream("/testCaseBuild.properties");
      
      
      if (in == null) {
        throw new IVCTVersionCheckException("/testCaseBuild.properties could not be read ");
      }   
      
      Properties versionProperties = new Properties();
      
      try {
        versionProperties.load(in);
        infoIVCTVersion = versionProperties.getProperty("ivctVersion");
      } catch (IOException ex) {      
        infoIVCTVersion = "undefined";      
        throw new IVCTVersionCheckException("/testCaseBuild.properties could not be load ", ex );
      }
      return infoIVCTVersion;
    }


    
    
    
    
    
    
    
}
