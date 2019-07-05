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

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;


/*
 * we get a json - object from ActiveMQ topic "HeartBeat",  
 * examine this, and either, if there are no Problems
 * enhance it only with one Key "MessageState" 
 * or in case of problems, change more Keys  
 * and give it to the caller of this class
 */

public class CmdHeartbeatListen implements MessageListener, Command {
    
    // ----  Organize return mechanism (the client has to implement this interface.)
    public interface OnHeartbeatListener { 
          public void hearHeartbeat(String backInfo);
      } 

    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CmdHeartbeatListen.class);
    
    JSONParser jsonParser = new JSONParser();
    private JSONObject jsonObject;
    
    // Status Variable  ( may be:   unknown  inTime  waiting  alert  dead )
    String messageState  = "unknown";
    
    private Timestamp last;
    private Timestamp first;
    private long sendingPeriod;
   
    // the referenz to the caller 
    private  OnHeartbeatListener client;  
        
    //public CmdHeartbeatListen() {
    //}

    public CmdHeartbeatListen(OnHeartbeatListener  caller) {
        this.client = caller;
        System.out.println ("client ist im Konstruktor : " +client);     // Debug
    }
  

    //@SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            
            try {
                final String content = textMessage.getText();
                // logger.info("CmdHeartbeatListener gets from ActiveMQ: " + content ); // Debug

                // take the contents of the json-object in the message               
                this.jsonObject = (JSONObject) jsonParser.parse(content);
                
                // we need some kind of timestamp history   
                Timestamp now = new Timestamp(System.currentTimeMillis());                
                this.first = last;
                this.last = now;
                this.sendingPeriod = (Long) jsonObject.get("LastSendingPeriod");                
 
            } catch (final Exception e) {
                Factory.LOGGER.error("onMessage: problems with getText", e);
            }
        }
    }    
   
   @SuppressWarnings("unchecked")
    // we need a method to monitor if there even are messages and which, and give back the necessary Information
    public void monitor() {

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                Timestamp now = new Timestamp(System.currentTimeMillis());
                
                SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
                String alerttime = df.format(now);

                //logger.info("### monitor ist runnuning - timestamp: " + now); // Debug

                Timestamp myFirst = getFirst();
                Timestamp myLast = getLast();

                if ((myFirst != null) && (myLast != null)) {

                    JSONObject myJsonObject = getJsonObject();
                    if (myJsonObject != null) {                                             
                        
                        long mySendingPeriod = getSendingPeriod();  
                        
                        if (now.getTime() - myFirst.getTime() >  ((mySendingPeriod *1000 *2)+1000) ) { // > 1100 Sec
                            setMessageState("alert");
                        } else if (now.getTime() - myLast.getTime() > ((mySendingPeriod *1000 +100)) ) { //  > 5100
                            setMessageState("waiting");
                        } else if (now.getTime() - myLast.getTime() <= ((mySendingPeriod *1000 +100)) ) {// <= 5100
                            setMessageState("inTime");
                        } else {
                            setMessageState("unknown");
                        }

                        myJsonObject.put("MessageState", messageState);
                        
                        // adapt the json - object for our purpose
                         if (now.getTime() - myFirst.getTime() >  ((mySendingPeriod *1000 *6)+1000) ) {  // ca > 30000
                            setMessageState("dead");
                            myJsonObject.put("MessageState", messageState);
                            myJsonObject.put("Alert-Time", alerttime);
                            myJsonObject.put("HeartbeatSender",myJsonObject.get("HeartbeatSender") );
                            myJsonObject.put("LastSendingPeriod", 0);
                            myJsonObject.put("LastSendingTime", myJsonObject.get("LastSendingTime") );                  
                            myJsonObject.put("SenderHealthState", false);
                        }

                        // give the enhanced json-object back to the caller
                        if (client != null) {
                            // client.hearHeartbeat(content); // the simple Text Message
                            client.hearHeartbeat(myJsonObject.toString()); // the json object
                        } else {
                            logger.warn("In CmdHeartbeatListener Monitor  client is null !!!!");
                        }
                    }
                }

            }
        }, 0, 5000);

    }
    
    
    @Override
    public void execute() {
        Factory.initialize();
        Factory.LOGGER.trace("subsribing the Heartbeat listener");
        Factory.jmsHelper.setupTopicListener("HeartBeat", this);
        monitor();
    }


    public String getMessageState() {
        return messageState;
    }


    public void setMessageState(String messageState) {
        this.messageState = messageState;
    }


    public JSONObject getJsonObject() {
        return jsonObject;
    }


    public Timestamp getLast() {
        return last;
    }


    public Timestamp getFirst() {
        return first;
    }
    
    public long getSendingPeriod() {
        return sendingPeriod;
    }
    
    
    
    
    
}
