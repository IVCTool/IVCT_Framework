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

public class IVCTVersionCheck {

  String testCaseIVCTVersion;
  String FactoryIVCtVersion;

  private static Logger logger = LoggerFactory.getLogger(IVCTVersionCheck.class);

  public IVCTVersionCheck() {
  }

  public IVCTVersionCheck(String _testCaseIVCTVersion, String _FactoryIVCtVersion) throws IVCTVersionCheckFailed {
    this.testCaseIVCTVersion = _testCaseIVCTVersion;
    this.FactoryIVCtVersion = _FactoryIVCtVersion;
    //this.compare() ;
  }

  // inner Class with own exception
  public class IVCTVersionCheckFailed extends Exception {
    private static final long serialVersionUID = 1L;

    public IVCTVersionCheckFailed() {
      super("IVCTVersionCheck without exception text");
    }

    public IVCTVersionCheckFailed(String exceptionText) {
      super(exceptionText);
    }
  }

  /*
   * Tests for verifying if the IVCT-Version of the components are compatible
   * 
   * here has to be created some extended test, with former Version-Numbers in the
   * moment we test only if there is the same Version-Number
   */

  public void compare() throws IVCTVersionCheckFailed {

    if (testCaseIVCTVersion.equals(FactoryIVCtVersion)) {
      logger.info("IVCTVersionCheck.compare: the versions match " + testCaseIVCTVersion + " - " + FactoryIVCtVersion);
    } else {
      throw new IVCTVersionCheckFailed("The IVCT-Versions of Testrunner and TestCase doesn't match ");
    }
  }
}
