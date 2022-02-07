/*
 * Copyright 2021, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.tc_lib_if;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;

import nato.ivct.commander.Factory;

/**
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public abstract class AbstractTestCaseIf {



    /********************************************************************************************
     * The class AbstractTestCaseIf is generic the Interface Declaration to be implemented by any
     * communication layer, like HLA or DIS. It may be implemented in each test case or in a 
     * dedicated library. The following abstract methods must be implemented. 
     * 
     */

    /**
     * @param tcParamJson a JSON string containing values to use in the testcase
     * @param logger The {@link Logger} to use
     * @return the IVCT base model to use in the test cases
     * @throws TcInconclusiveIf if test is inconclusive
     */
    protected abstract IVCT_BaseModelIf getIVCT_BaseModel(final String tcParamJson, final Logger logger) throws TcInconclusiveIf;

    /**
     * @param logger The {@link Logger} to use
     */
    protected abstract void logTestPurpose(final Logger logger);

    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     * @throws TcFailedIf if test case failed
     */
    protected abstract void performTest(final Logger logger) throws TcInconclusiveIf, TcFailedIf;

    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     */
    protected abstract void preambleAction(final Logger logger) throws TcInconclusiveIf;


    /**
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     */
    protected abstract void postambleAction(final Logger logger) throws TcInconclusiveIf;



    /*************************************************************************
     * The remaining methods are generic implementations of standard behavior. 
     * It is not recommended to change any of these methods.
     */

    protected Logger defaultLogger = null;
    protected String testSuiteId = null;
    protected String tcName  = null;
    protected String sutName = null;
    protected String settingsDesignator;
    protected String federationName;
    protected String sutFederateName;
    

    /** 
     * OperatorService registration 
     * 
     * A Test Case may want to notify the IVCT-operator about any specific events in the test 
     * procedure. A typical usage is the operator request to start the system under test when
     * the test case is ready to react.
     * 
     */
    protected OperatorService myOperator;
    private boolean skipOperatorMsg;

    /**
     * assign the OperatorService so the test case can interact with the IVCT operator
     * 
     * @param aOperator
     */
    public void setOperatorService(OperatorService aOperator) {
        myOperator = aOperator;
    }

    /**
     * access method to the assigned operator
     * 
     * @return
     */
    public OperatorService operator () {
        return myOperator;
    }
    
    /**
     * Send a text message to the IVCT operator and wait for confirmation
     * 
     * @param text
     * @throws TcInconclusiveIf
     */
    public void sendOperatorRequest(String text) throws TcInconclusiveIf {
		if (skipOperatorMsg) return;
    	if (text == null) {
    		// Make an empty string
    		text = new String();
    	}
    	myOperator.sendOperatorMsgAndWaitConfirmation(text);
    }

    public void sendTcStatus(String status, int percent) {
		if (skipOperatorMsg) return;
        myOperator.sendTcStatus(status, percent);
    }

    
    /**
     * Set the SkipOperatorMsg flag to true. This will cause operator instructions to be ignored.
     * This feature is intended to test purpose only.
     * 
     * @param value
     */
	public void setSkipOperatorMsg (boolean value) {
		skipOperatorMsg = value;
	}

    /**
     * Assign the logger object to be used as default for all test case logging messages.
     * 
     * @param logger
     */
    public void setDefaultLogger(final Logger logger) {
    	this.defaultLogger = logger;
    }

    /**
     * The execute method is used to perform the test, including preamble and postamble.
     * 
     * @param tcParamJson test case parameters
     * @param logger The {@link Logger} to use
     * @return the verdict
     */
    public IVCT_Verdict execute(final String tcParamJson, final Logger logger) {

        IVCT_BaseModelIf ivct_BaseModel = null;
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
        if (Factory.props.getProperty("TESTENGINE_LABEL") != null) {
            tcGlobalVariables.append("\nEngine Label: ");
            tcGlobalVariables.append(Factory.props.getProperty("TESTENGINE_LABEL"));
        }
        if (settingsDesignator != null) {
            tcGlobalVariables.append("\nSettings Designator: ");
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


    /**
     * Returns the name test suite
     *
     * @return Test Suite Name
     */
    public String getTsName() {
        return testSuiteId;
    }


    /**
     * Set the test suite name
     * 
     * @param testSuiteId
     */
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


    /**
     * Set the test case name.
     * @param tcName
     */
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


    /**
     * Set the federation name.
     * 
     * @param federationName
     */
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
     * Returns the IVCT-Version String, created at compile time within the test suite-this
     * for testing  against the IVCT-Version of at Runtime deployment
     *  
     * @throws IVCTVersionCheckException of version id can not be resolved
     * @return IVCT version id
     */
    public String getIVCTVersion()  throws IVCTVersionCheckException {
      
      String infoIVCTVersion;  
      InputStream in = this.getClass().getResourceAsStream("/testCaseBuild.properties");
      
      
      if (in == null) {
        throw new IVCTVersionCheckException("/testCaseBuild.properties could not be read ");
      }   
      
      Properties versionProperties = new Properties();
      
      try {
        versionProperties.load(in);
        infoIVCTVersion = versionProperties.getProperty("ivctVersion");
      } catch (IOException ex) {      
        throw new IVCTVersionCheckException("/testCaseBuild.properties could not be load ", ex );
      }
      return infoIVCTVersion;
    }
}
