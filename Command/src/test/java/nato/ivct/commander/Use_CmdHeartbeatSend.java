/* Copyright 2020, brf (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */


package nato.ivct.commander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import nato.ivct.commander.Factory;


public class Use_CmdHeartbeatSend  implements CmdHeartbeatSend.OnCmdHeartbeatSend{
	
	static Logger logger = LoggerFactory.getLogger(Use_CmdHeartbeatSend.class);

	/* this es an example for a application sending heartbeat-Information 
	 *  with CmdHeartbeatSend to a ActiveMQ
	 */

	 private boolean health;
	 
	 private String myClassName = "Use_CmdHeartbeatSend";
	 
	// for enhanced heartbeat with RTI-Type-Information brf 22.10.2020
	// private String rtiTypeEngineLabel;
    private String testEngineLabel="notInUseIn_CmdHeartbeatSend" ;
	
	
	  //  for this  a ActiveMQ has to be started
	  public static void main(String[] args) throws Exception {
	      
	      logger.info(" UserDir ist : {}", System.getProperty("user.dir"));      // Debug

		  logger.info(" Use_CmdHeartbeatSend  has started");      // Debug
		  
		  // we need a Instance of this class
		  Use_CmdHeartbeatSend testHeartbeatSender = new  Use_CmdHeartbeatSend();
		  
		  // and deliver it to  an new instance of  CmdHeartbeatSend
		  CmdHeartbeatSend  cmdHeartbeatSend = new CmdHeartbeatSend(testHeartbeatSender);
		  
		  testHeartbeatSender.health=true;
		  
		  
	      // for testing the CmdHeartbeatSend  we start it and change the 'health' - variable after a while
		  logger.info(" ### Use_CmdHeartbeatSend starts now  cmdHeartbeatSend.execute() ");      // Debug
		  
		  cmdHeartbeatSend.execute();
		  
		  
		  // ----------   for testing ------------------------
		  logger.info(" ### Use_CmdHeartbeatSend has startet cmdheartbeatSend.execute() ");
		  int count = 0;
	        while (count < 15) {
	            Thread.sleep(3000);
	            count++;
	        }
		  
	       logger.info(" Use_CmdHeartbeatSend change health to false ");
	       testHeartbeatSender.health = false;
		  
	       
	       count = 0;
           while (count < 15) {
           //while (count < 100) {
               Thread.sleep(3000);
               count++;
           }
	      
          System.exit(0) ;
          // ------------------------------------------------
	      
   
    }
	  
	  
	  public  boolean getMyHealth() {
	      return health;
	  }
	  
      public String getMyClassName() {
          return myClassName;
      }
	  
   // for enhanced heartbeat with RTI-Type-Information brf 22.10.2020
  	//public String getMyRtiTypeEngineLabel() {
  	//	return rtiTypeEngineLabel;
  	//}
  	
  	public String getMyTestEngineLabel() {
		return testEngineLabel;
	}
	  

}
