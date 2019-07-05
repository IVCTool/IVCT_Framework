/*
Copyright 2019, brf (Fraunhofer IOSB)
(v  05.07.2019) 

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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;


    public class Use_CmdHeartbeatListen implements CmdHeartbeatListen.OnHeartbeatListener {
        
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Use_CmdHeartbeatListen.class);

    static Use_CmdHeartbeatListen client;
        
        
    public static void main(String[] args) throws Exception {        
        
     // creating a instance of this 'client'  to  deliver it to the listener
     Use_CmdHeartbeatListen client = new Use_CmdHeartbeatListen();
     //logger.info("Start_CmdHeartbeatListener_main create his instance: " +client);    // Debug
     
    
     // instantiating a new heartbeatListener and deliver this client
     CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(client);
     heartbeatListener.execute();

        // The process has to run for a while
        int count = 0;
        while (count < 100) {
            Thread.sleep(3000);
            count++; 
        }
    }

    /*  */
    @Override
    public void hearHeartbeat(String backinfo) {
        logger.info("Rueckgabe an Use_CmdHeartbeatListen ist: " + backinfo);
        
        // work with the  json  what's comming back  , an example
        /*
        try {
            JSONParser jsonParser = new JSONParser();            
            JSONObject jsonObject = (JSONObject) jsonParser.parse(backinfo);
            String heartbeatSender = (String) jsonObject.get("HeartbeatSender");
            String timestamp = (String) jsonObject.get("LastSendingTime");
            //String healthstatus = (String) jsonObject.get("SenderHealthState");
            boolean  healthstatus = (boolean) jsonObject.get("SenderHealthState");
            String testOutput=  heartbeatSender+" , "+ timestamp +" , "+ healthstatus;
            logger.info("## If we got back json following output shoud be correct: " + testOutput ); // Debug
            
        } catch (final Exception e) {
            Factory.LOGGER.error("Use_CmdHeartbeatListen.hearHearbeat hat Probleme mit der Rueckgabe", e);
        }
        */
        
        
      
        
    }

}
