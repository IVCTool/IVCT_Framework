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


/**
 * The class AbstractTestCaseIf is a generic Interface Declaration to connect a simulation runtime
 * infrastructure, like HLA or DIS to the test case engine. It may be implemented in each test case 
 * individually or in a dedicated library. 
 * <p>
 * The standard workflow for a test case execution starts with the instantiation of the requested 
 * test case class and a interoperability check of the test case code against the framework version. 
 * The version compatibilities between the test framework version and the test cases are defined in 
 * the IVCT_Framework configuration. Only test cases which are compatible with the current test case
 * interface version can be executed. 
 * <p>
 * After a successfully compliance check, the engine continue to initialize the test case by setting
 * up the context information for the test case. This is done by calling the setter methods:
 * <ul>
 *   <li>{@link #setDefaultLogger}
 *   <li>{@link #setSutName}
 *   <li>{@link #setTsName}
 *   <li>{@link #setTcName}
 *   <li>{@link #setSettingsDesignator}
 *   <li>{@link #setFederationName}
 *   <li>{@link #setSutFederateName}
 *   <li>{@link #setTcParam}
 *   <li>{@link #setOperatorService}
 * </ul>
 * <p>
 * The actual test execution is implemented in the {@link #execute(Logger)} method. This method  
 * will be called by the test case engine and it implements the default behavior in the 
 * {@link #AbstractTestCaseIf} class. It may be overwritten if a specific run-time adapter requires 
 * a different behavior. The individual test logic is typically implemented in the following abstract
 * methods:
 * <ul>
 *   <li>{@link #logTestPurpose}
 *   <li>{@link #preambleAction}
 *   <li>{@link #performTest}
 *   <li>{@link #postambleAction}
 * </ul>
 * <p>
 * The purpose of a test case is to provide a statement about the test result. This is called
 * the test verdict. A verdict can be either PASSED, FAILED, or INCONCLUSIVE. 
 * If the methods above are executed without any exceptions, the test case is considered as PASSED.
 * Any failures during the preambleAction or postambleAction may cause a test case to be inconclusive.
 * Only a failure during the performTest method may cause a test case to be FAILED. If anything goes wrong
 * during the preamble or postamble phase, this is not considered as test relevant and may only
 * cause a inconclusive verdict. 
 * <p>
 * A very important requirement for any test case is the understandable to the test operator. 
 * For that reason the test case shall make generous use of the logging interface. All significant 
 * steps and findings within the test case execution shall be documented with the use of the 
 * provided loggers. The test case shall only use the loggers provided by the test engine, in order
 * guarantee a consistent handling of the log messages. All available log levels are supported and
 * shall be used to differentiate the relevance of the messages. 
 * <p>
 * Interactions with the test operator are enabled via the {@link #myOperator} reference. This allows
 * the test case to synchronize with operator and it can be used to provide information about the 
 * execution progress. See also {@link OperatorService}.
 * 
 * 
 * @since 4.2.0
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public abstract class AbstractTestCaseIf {

    /**
     * The logTestPurpose method shall provide information of the intended 
     * purpose of this specific test case. This information shall be reported to 
     * the logger object. It is recommended to use the {@code logger.info} method 
     * to inform the operation in a compact format about the test case purpose, as 
     * this is the typical log level for standard information about the test execution. 
     * It is possible to give addition information on other levels, 
     * like {@code warn}, {@code trace} or {@code debug}, in order to provide additional
     * information for more detailed analysis. 
     * 
     * @param logger The {@link Logger} to use
     */
    protected abstract void logTestPurpose(final Logger logger);

    /**
     * The preambleAction method shall prepare and validate the start conditions for
     * the test case. If the required initial conditions can not be established, the 
     * method shall raise the {@link TcInconclusiveIf} exception. This will end the 
     * test execution. 
     *  
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     */
    protected abstract void preambleAction(final Logger logger) throws TcInconclusiveIf;

    /**
     * The performTest method contains the code for actual test. If the test passed all
     * required conditions, the method shall end normally. If the test case detects a 
     * condition where the system under test can not be tested, the method shall raise 
     * a {@link TcInconclusiveIf} exception. If the test case detects a invalid behavior
     * of the specified test case, it shall raise a {@link TcFailedIf} exception. 
     * <p>
     * During the test execution it is recommended to provide sufficient log information
     * in order to give the IVCT operator sufficient feedback to understand whats going
     * on while performing the test. It is also recommended to use the 
     * {@link OperatorService#sendTcStatus} function to inform the operator about the 
     * test case progress.
     * 
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     * @throws TcFailedIf if test case failed
     */ 
    protected abstract void performTest(final Logger logger) throws TcInconclusiveIf, TcFailedIf;

    /**
     * The postambleAction method will be called as the final part of the test case. 
     * The purpose of this function is to leave the test environment, specifically the system
     * under test, in a defined condition. If the postambleAction method is not able to close
     * the test case, the {@link TcInconclusiveIf} exception shall be raised. This may overwrite
     * a earlier "passed" verdict from the {@link AbstractTestCaseIf#performTest} execution.  
     * 
     * @param logger The {@link Logger} to use
     * @throws TcInconclusiveIf if test is inconclusive
     */
    protected abstract void postambleAction(final Logger logger) throws TcInconclusiveIf;

    
    
    /*************************************************************************
     * The remaining methods are generic implementations of standard behavior. 
     * It is not recommended to change any of these methods.
     */
    
    private boolean skipOperatorMsg;
    protected Logger defaultLogger = null;
    protected String testSuiteId = null;
    protected String tcName  = null;
    protected String tcParam = null;
    protected String sutName = null;
    protected String settingsDesignator;
    protected String federationName;
    protected String sutFederateName;
    protected OperatorService myOperator;
    

    /**
     * The execute method is used to perform the test, including the initializing preamble 
     * and the closing postamble. It implements the standard behavior of a normal test case.
     * This method may be overwritten by run-time library adapters to include special behavior. 
     * 
     * 
     * @param logger The {@link Logger} to be used by the test case. 
     * @return the verdict
     */
    public IVCT_Verdict execute(final Logger logger) {
        final IVCT_Verdict verdict = new IVCT_Verdict();
        sendTcStatus("begin test case executing", 0);

        logger.info("<<<<<< Test Case Started >>>>>>");
        logger.info("Test Suite ID       : {}", testSuiteId);
        logger.info("Test Case Name      : {}", tcName);
        logger.info("SuT Name            : {}", sutName);
        logger.info("Test Case Parameter : {}", tcParam);
        logger.info("Settings Designator : {}", settingsDesignator);
        logger.info("Federation Name     : {}", federationName);
        logger.info("Federate Name       : {}", sutFederateName);

        logTestPurpose(logger);
        try {
			preambleAction(logger);
            performTest(logger);
            postambleAction(logger);
        } catch (TcInconclusiveIf exInconclusiveIf) {
            logger.warn("TC INCONCLUSIVE " + exInconclusiveIf.getMessage());
            verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            verdict.text = exInconclusiveIf.getMessage();
            return verdict;
        } catch (TcFailedIf exFailedIf) {
            logger.info("TC INCONCLUSIVE " + exFailedIf.getMessage());
            verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
            verdict.text = exFailedIf.getMessage();
            return verdict;
        }
        verdict.verdict = IVCT_Verdict.Verdict.PASSED;
        sendTcStatus("end test case executing", 100);
        return verdict;
    }


    /**
     * A Test Case may want to notify the IVCT-operator about any specific events in the test 
     * procedure. A typical usage is the operator request to start the system under test when
     * the test case is ready to react.
     * 
     * @param aOperator The operator interface to be used by the test case
     */
    public void setOperatorService(OperatorService aOperator) {
        myOperator = aOperator;
    }

    /**
     * Access method to the assigned operator.
     * 
     * @return the assigned operator
     */
    public OperatorService operator () {
        return myOperator;
    }
    
    /**
     * Send a text message to the IVCT operator and wait for confirmation.
     * 
     * @param text  message to be send to the IVCT operator. 
     * @throws TcInconclusiveIf if anything goes wrong of if the operator cancels the message
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
     * The SkipOperatorMsg flag can be set to true, if shall be skipped. This is useful during
     * unit test without a IVCT operator interface. 
     * 
     * @param value true if messages shall be skipped, false otherwise
     */
	public void setSkipOperatorMsg (boolean value) {
		skipOperatorMsg = value;
	}

    /**
     * Assign the logger object to be used as default for all test case logging messages.
     * 
     * @param logger default logger reference if no logger
     * 
     * @deprecated is a default logger required? Are specific loggers of the test phases required? 
     */
    public void setDefaultLogger(final Logger logger) {
    	this.defaultLogger = logger;
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
     * @param testSuiteId Identification of the test suite
     */
    public void setTsName(String testSuiteId) {
        this.testSuiteId = testSuiteId;
    }


    /**
     * Returns the name of the fully qualified class name of the test case
     *
     * @deprecated is the test case name ever required inside the test case?
     * @return Test Case Name
     */
    public String getTcName() {
        return tcName;
    }


    /**
     * Set the test case name.
     * 
     * @param tcName the name of the test case class, assigned by the test engine
     */
    public void setTcName(String tcName) {
        this.tcName = tcName;
    }


    /**
     * Returns the name of the System under Test.
     * 
     * @return SutName name of the system under test
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
     * @param federationName name of the federation with the system under test
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
     * Assign the test case parameter string. This is typically a JSON string.
     * 
     * @param param test case parameter JSON string
     */
    public void setTcParam (String param) {
        this.tcParam = param;
    }

    /**
     * Returns the assigned test case parameter string.
     * 
     * @return the test case parameter string.
     */
    public String getTcParam () {
        return this.tcParam;
    }
    
    /**
     * Returns the IVCT-version string, created at compile time within the test suite. This
     * is required to be checked for compliance against the IVCT-version at runtime.
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
