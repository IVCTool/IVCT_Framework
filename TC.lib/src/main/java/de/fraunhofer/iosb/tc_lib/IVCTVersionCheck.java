/*
Copyright 2019, brf (Fraunhofer IOSB)
(v  18.07.2019)

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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.Factory;


/**
 * Tests for verifying if the IVCT-Version of the components are compatible
 */

public class IVCTVersionCheck {

  String testCaseIVCTVersion;
  //String FactoryIVCtVersion;
  String FactoryIVCtVersion = Factory.getVersion() ;
  String foundInCompatibleList;

  private static Logger logger = LoggerFactory.getLogger(IVCTVersionCheck.class);
  

  //public IVCTVersionCheck() {
  //  this.testCaseIVCTVersion = null;
  //  this.FactoryIVCtVersion = null;
  //}

  public IVCTVersionCheck(String _testCaseIVCTVersion ) throws IVCTVersionCheckFailed {
    this.testCaseIVCTVersion = _testCaseIVCTVersion;
  }
  
  public IVCTVersionCheck(String _testCaseIVCTVersion, String _FactoryIVCtVersion) throws IVCTVersionCheckFailed {
    this.testCaseIVCTVersion = _testCaseIVCTVersion;
    this.FactoryIVCtVersion = _FactoryIVCtVersion;
  }

  

  public void compare() throws IVCTVersionCheckFailed {

    boolean testresult = false;
    
    // if the Version-Number ist the same  we are content
    if (testCaseIVCTVersion.equals(FactoryIVCtVersion)) {
      testresult = true;
      
    /**
     *  if the Version differ, we have to test the testCaseIVCTVersion against
     *  a list of compatible versions which we get from TC.exec 
     */
      
    } else {
      
      // we get a propertie-file with compatible Version from TC.exec         
      InputStream in = this.getClass().getResourceAsStream("/compatibleVersions.properties");
      
      if (in == null) {
        throw new IVCTVersionCheckFailed("/compatibleVersions.properties could not be read ");
      }   
      
      Properties compaProperties = new Properties();
      
      try {
        compaProperties.load(in);
        // compaProperties.list(System.out);            // Debug
      
        // we have to  know each Key of the property           
        Set<Object> set = compaProperties.keySet();
      
        for (Object obj : set) {
          logger.info("#### should show a key "+obj+" and it's value: "+compaProperties.getProperty(obj.toString()) ); // Debug
        
          //if(testCaseIVCTVersion.equals(obj) ) {
          if( testCaseIVCTVersion.equals(obj) && ( (compaProperties.getProperty(obj.toString()).equals("compatible")) ) ) {
            testresult = true;
            foundInCompatibleList = (String)obj ;
            logger.info("#### found in TC.exec compatibleVersions List " + foundInCompatibleList );  // Debug       
          }
        }
        
        
      } catch (IOException ex) {
        ex.getStackTrace();
        //infoIVCTVersion = "undefined";
        
      }      
    }
      
 
    if (testresult) {
      logger.info("VCTVersionCheck: found in TC.exec compatibleVersions List "+foundInCompatibleList ); 
      logger.info("IVCTVersionCheck: the versions are known as compatible: "+FactoryIVCtVersion+" - "+testCaseIVCTVersion);
      
    } else {
      logger.info ("IVCTVersionCheck.compare: Versions: "+FactoryIVCtVersion+" - "+testCaseIVCTVersion);
      throw new IVCTVersionCheckFailed("The IVCT-Versions of Testrunner and TestCase are not known as compatible ");
    }
        
  
    
  }
}
