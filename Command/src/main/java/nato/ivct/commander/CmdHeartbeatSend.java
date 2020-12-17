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

import javax.jms.Message;

import javax.jms.MessageProducer;

import org.json.simple.JSONObject;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class CmdHeartbeatSend  implements Command {
    
    // ----  Organize communication  mechanism (the client has to implement this interface.)
    public interface OnCmdHeartbeatSend { 
          public boolean getMyHealth ();
          public String getMyClassName(); 
          public String getMyTestEngineLabel();
      }
    
   
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CmdHeartbeatSend.class);

    public static MessageProducer logProducer;
    
    //  definition of all  keys  for our  json-object    
    public static final String HB_MSG_TOPIC = "HeartBeat";    
    public static final String HB_SENDER="HeartbeatSender";    
    public static final String HB_LASTSENDINGPERIOD= "LastSendingPeriod";    
    public static final String HB_LASTSENDINGTIME= "LastSendingTime";    
    public static final String HB_SENDERHEALTHSTATE= "SenderHealthState";    
    public static final String HB_MESSAGESTATE= "MessageState";    
    public static final String HB_ALLERTTIME= "Alert-Time";
    public static final String HB_COMMENT= "Comment";
    public static final String HB_IVCTVERSION= "IVCTVersion";
    public static final String HB_TESTENGINELABEL= "TESTENGINE_LABEL";
        
    private boolean health;    
       
    private String heartbeatSenderName;
    
    private OnCmdHeartbeatSend sender;

    private String testEngineLabel;
    
      
    public CmdHeartbeatSend(OnCmdHeartbeatSend sender) {
        Factory.initialize();
        logProducer = Factory.createTopicProducer(HB_MSG_TOPIC);
        this.sender=sender;
    }
        

    /*
     *  this execute method is started by a application which instanciate this class  
     *  we fetch all xy seconds some variables from that application,
     *  build a json-object with this Informations and other Values 
     *  and send this to ActiveMQ
     *  
     *  if the application change the variables, the values in the json will be changed
     */
    
    @SuppressWarnings("unchecked")
    public void execute()  throws Exception   {    
        
        // we try to get Informations about our  Client        
        if (sender != null) {
            this.health = sender.getMyHealth();
            this.heartbeatSenderName= sender.getMyClassName();

            this.testEngineLabel = sender.getMyTestEngineLabel();
            
            } else {
            logger.warn("In CmdHeartbeatSend sender  is null !!!!");
        }
        
        // Info:   for the Message we put some Keys/values in a json object        
        JSONObject heartbeatjson = new JSONObject();              
        heartbeatjson.put(HB_SENDER, this.heartbeatSenderName);
        heartbeatjson.put(HB_LASTSENDINGPERIOD, 5000L);         
        heartbeatjson.put(HB_IVCTVERSION, Factory.getVersion() );
        
        heartbeatjson.put(HB_TESTENGINELABEL, testEngineLabel );
       
        // Scheduler run all 5 Seconds  till the parent-thread ist stopped
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
          public void run() {
              
                   health=sender.getMyHealth();                                
              
                try {

                    Date datum = new java.util.Date();

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                    String dateString = df.format(datum);

                    heartbeatjson.put(HB_LASTSENDINGTIME,dateString);                    
                    heartbeatjson.put(HB_SENDERHEALTHSTATE, health);                    
                    sendMessage(heartbeatjson.toString());
                    
                    //logger.info("### CmdHeartbeatSend.execute is sending: "+heartbeatjson.toString()); // Debug                    
                    
                } catch (Exception exc) {
                    logger.error("could not send command: ", exc);
                }
            }
        }, 0, 5000);

    }
    
    public void sendMessage(String healthtxt ) throws Exception{
         Message message = Factory.jmsHelper.createTextMessage(healthtxt);                
        //logger.info("heartbeatClient Test message ist: " + message);  // Debug          
         logProducer.send(message);        
    }
    
}
