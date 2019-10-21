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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Tests for verifying if the IVCT-Version of the components are compatible
 */

public class IVCTVersionCheck {

  String testCaseIVCTVersion;
  String FactoryIVCtVersion;

  private static Logger logger = LoggerFactory.getLogger(IVCTVersionCheck.class);
  

  public IVCTVersionCheck() {
    this.testCaseIVCTVersion = null;
    this.FactoryIVCtVersion = null;
  }

  public IVCTVersionCheck(String _testCaseIVCTVersion, String _FactoryIVCtVersion) throws IVCTVersionCheckFailed {
    this.testCaseIVCTVersion = _testCaseIVCTVersion;
    this.FactoryIVCtVersion = _FactoryIVCtVersion;
  }

  

  public void compare() throws IVCTVersionCheckFailed {

    boolean testresult = false;
    
    // which are  possible values for IVCTVersion - Numbers ?
    // 2.1.1   2.1.0 2.0.0
    
    // if the Version-Number ist the same  we are content
    if (testCaseIVCTVersion.equals(FactoryIVCtVersion)) {
      testresult = true;
 
    // if the Version-Numbers  differ we have to do some extended tests with former Version-Numbers  
    } else { 
      switch (FactoryIVCtVersion ) {
        case "2.1.1" :
          if ( testCaseIVCTVersion.equals("2.1.0") ) testresult = false;
          if ( testCaseIVCTVersion.equals("2.0.0") ) testresult = false;          
          break;   
        case "2.1.0" :
          if ( testCaseIVCTVersion.equals("2.1.1") ) testresult = false;
          if ( testCaseIVCTVersion.equals("2.0.0") ) testresult = false;
          break;
        default:
          testresult = false;
      }
    }
    
    if (testresult) {
      logger.info("IVCTVersionCheck.compare: the versions match " + testCaseIVCTVersion + " - " + FactoryIVCtVersion);
    } else {
      logger.info ("IVCTVersionCheck.compare: Versions: "+testCaseIVCTVersion+" - "+FactoryIVCtVersion);
      throw new IVCTVersionCheckFailed("The IVCT-Versions of Testrunner and TestCase doesn't match ");
    }
        
    /*    the former code
    if (testCaseIVCTVersion.equals(FactoryIVCtVersion)) {
      logger.info("IVCTVersionCheck.compare: the versions match " + testCaseIVCTVersion + " - " + FactoryIVCtVersion);
    } else {
      logger.info ("IVCTVersionCheck.compare  testCaseIVCTVersion: "+testCaseIVCTVersion+" FactoryIVCtVersion: "+FactoryIVCtVersion);
      throw new IVCTVersionCheckFailed("The IVCT-Versions of Testrunner and TestCase doesn't match ");
    }
    */
    
    
    
  }
}
