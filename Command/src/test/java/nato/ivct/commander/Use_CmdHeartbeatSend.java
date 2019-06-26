/*
Copyright 2019, brf (Fraunhofer IOSB)
(v  24.06.2019) 

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


package nato.ivct.commander;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import nato.ivct.commander.Factory;


public class Use_CmdHeartbeatSend {
	
	static Logger logger = LoggerFactory.getLogger(Use_CmdHeartbeatSend.class);

	/* this es an example for a application sending heartbeat-Information 
	 *  with CmdHeartbeatSend to a ActiveMQ
	 */
	
	
	  //  for this  a ActiveMQ has to be started
	  public static void main(String[] args) throws Exception {
	      
	      logger.info(" UserDir ist : " + System.getProperty("user.dir")  );      // Debug

		  logger.info(" Use_CmdHeartbeatSend  wurde gestartet");      // Debug
		  
		  CmdHeartbeatSend  heartbeatSend = new CmdHeartbeatSend();
		  
		  //  Basic Information for the Heartbeat
		  heartbeatSend.setHealth(true);
		  
		  heartbeatSend.setHeartbeatSender("Use_CmdHeartbeatSend");
		  
	      // for testing the CmdHeartbeatSend  we start it and change the 'health' - variable after a while
		  logger.info(" ### Use_CmdHeartbeatSend starts now  heartbeatSend.execute() ");      // Debug
		  
		  heartbeatSend.execute();
		  
		  
		  logger.info(" ### Use_CmdHeartbeatSend has startet heartbeatSend.execute() ");
		  int count = 0;
	        while (count < 15) {
	            Thread.sleep(3000);
	            count++;
	        }
		  
	       logger.info(" Use_CmdHeartbeatSend change the CmdHeartbeatSend.health to false ");
	       heartbeatSend.setHealth(false);
		  
	       
	       count = 0;
           while (count < 15) {
               Thread.sleep(3000);
               count++;
           }
	      
          System.exit(0) ;
	      
   
    }		

}
