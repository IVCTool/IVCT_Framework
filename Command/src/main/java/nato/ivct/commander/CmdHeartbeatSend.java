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

import javax.jms.Message;

import javax.jms.MessageProducer;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;


public class CmdHeartbeatSend  implements Command {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CmdHeartbeatSend.class);

    public static MessageProducer logProducer;
    
    public static final String LOG_MSG_TOPIC = "HeartBeat";
    
    private boolean health;
    
    private String healthStatus = "true";
    
    private String heartbeatSender;
    
    
    
    public CmdHeartbeatSend() {
        Factory.initialize();
        logProducer = Factory.createTopicProducer(LOG_MSG_TOPIC);
        this.health=true;
    }
    
    
    /*
     *  this execute method is started by a application which instanciate this class     
     *  the application set some variable in this instance,
     *  which give a part of the values of a json object
     *  execute send all 5 seconds this json - object to AcitveMQ 
     *  
     *  if the application change the variables, the values in the json will be changed
     */
    
    @SuppressWarnings("unchecked")
    public void execute()  throws Exception   {        
                 
        //logger.info(" CmdHeartbeatSend.execute wurde gestartet");    // Debug        
        
        // Info:   for the Message we put  following Keys in a json object
        //HeartbeatSender   LastSendingPeriod  LastSendingTime   SenderHealthState  (later MessageState)
         
        JSONObject heartbeatjson = new JSONObject();              
        heartbeatjson.put("HeartbeatSender", heartbeatSender);
        heartbeatjson.put("LastSendingPeriod", 5);
        
       
        // Scheduler run all 5 Seconds  till the parent-thread ist stopped
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          public void run() {
              
                try {

                    Date datum = new java.util.Date();

                    SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                    String dateString = df.format(datum);

                    heartbeatjson.put("LastSendingTime",dateString);

                    if (health) {
                        //heartbeatjson.put("HealthState", healthStatus);                        
                        heartbeatjson.put("SenderHealthState", health); 
                        sendMessage(heartbeatjson.toString());                        
                        logger.info("### CmdHeartbeatSend.execute sendet: "+heartbeatjson.toString()); // Debug

                    } else {
                        healthStatus = "false";
                        //heartbeatjson.put("HealthState", healthStatus);
                        heartbeatjson.put("SenderHealthState", health);
                        sendMessage(heartbeatjson.toString());
                    }
                } catch (Exception ex) {
                    logger.error("could not send command: " + ex);
                }
            }
        }, 0, 5000);

    }
    
    public void sendMessage(String healthtxt ) throws Exception{
         Message message = Factory.jmsHelper.createTextMessage(healthtxt);                
        //logger.info("heartbeatClient Test message ist: " + message);  // Debug          
         logProducer.send(message);        
    }
    
    
    // The client - application  set the Status of health
    public boolean getHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }


    public void setHeartbeatSender(String heartbeatSender) {
        this.heartbeatSender = heartbeatSender;
    } 
    
    
    
    
    
    

}
