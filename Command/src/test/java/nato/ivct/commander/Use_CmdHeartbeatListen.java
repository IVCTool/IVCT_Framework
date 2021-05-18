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

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;


    public class Use_CmdHeartbeatListen implements CmdHeartbeatListen.OnCmdHeartbeatListen {
        
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Use_CmdHeartbeatListen.class);

    static Use_CmdHeartbeatListen client; 
    
    // You can choose a special  Class to be monitored
    //private static String desiredHeartBeatSenderClass="Use_CmdHeartbeatSend";
    //private static String desiredHeartBeatSenderClass="TestRunner";
    private static String desiredHeartBeatSenderClass="LogSink";
        
    public static void main(String[] args) throws Exception {     
        
     // creating a instance of this 'client'  to  deliver it to the listener
     Use_CmdHeartbeatListen queryClient = new Use_CmdHeartbeatListen();
     
    
     // instantiating a new heartbeatListener and deliver this client  without a special Sender-class to observe
     CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(queryClient);

     // instantiating a new heartbeatListener and deliver this client  an  a special Sender-class to observe
     //CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(queryClient, Use_CmdHeartbeatListen.desiredHeartBeatSenderClass );
     

     heartbeatListener.execute();
     
        // The process has to run for a while
        int count = 0;
        while (count < 100) {
            Thread.sleep(3000);
            count++; 
        }
    }

    /*   // work with the  json as a String  what's coming back  , an example
    @Override
    public void hearHeartbeat(String backinfo) {
        logger.info("delivered string to Use_CmdHeartbeatListen is: " + backinfo);
        
        try {
            JSONParser jsonParser = new JSONParser();            
            JSONObject jsonObject = (JSONObject) jsonParser.parse(backinfo);
            String heartbeatSender = (String) jsonObject.get("HeartbeatSender");
            String timestamp = (String) jsonObject.get("LastSendingTime");
            //String healthstatus = (String) jsonObject.get("SenderHealthState");
            boolean  healthstatus = (boolean) jsonObject.get("SenderHealthState");
            String testOutput=  heartbeatSender+" , "+ timestamp +" , "+ healthstatus;
            logger.info("## If we got back json following output should be correct: " + testOutput ); // Debug
            
        } catch (final Exception e) {
            Factory.LOGGER.error("Use_CmdHeartbeatListen.hearHearbeat has problems with with the delivered String", e);
        } 
    }
    */
    
    //  work with a Json Object as return
    public void hearHeartbeat(JSONObject backJson) {        
        logger.info("delivered Json to Use_CmdHeartbeatListen ist: {}", backJson);        
    }    

}
