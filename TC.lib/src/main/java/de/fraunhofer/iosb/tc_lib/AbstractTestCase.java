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
 * Abstract base class for test cases. In the concrete test cases, the three
 * methods preambleAction, performTest and postambleAction have to be
 * implemented as they will be called by the execute method of this abstract
 * class. Empty implementations of these classes are also valid.
 *
 * @author sen (Fraunhofer IOSB)
 */
public abstract class AbstractTestCase extends AbstractTestCaseIf {

    public void sendTcStatus(String status, int percent) {
        operator().sendTcStatus(status, percent);
    }
    

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
