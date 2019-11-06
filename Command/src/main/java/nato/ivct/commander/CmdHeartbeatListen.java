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



package nato.ivct.commander;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.HeartBeatMsgStatus.HbMsgState;

import java.util.Optional;
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
    public interface OnCmdHeartbeatListen { 
          //public void hearHeartbeat(String backInfo);
          public void hearHeartbeat(JSONObject backJson);
      } 

    
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CmdHeartbeatListen.class);
    
    public static final String HB_MSG_TOPIC = "HeartBeat";
    
    private JSONParser jsonParser = new JSONParser();
    
    private JSONObject jsonObject = new JSONObject();
    
    private HbMsgState messageState  = HbMsgState.UNKNOWN;
    private String desiredHeartBeatSenderClass;
    
    private Timestamp last;
    private Timestamp first;
    private long sendingPeriod;
   
 
    // the referenz to the caller 
    private  OnCmdHeartbeatListen querryClient;  
    
        
    public CmdHeartbeatListen(OnCmdHeartbeatListen caller) {
    	this(caller, null);
        //System.out.println ("client is delivered to the constructor CmdHeartbeatListen : " +client);   // Debug
    }
    
    // the client can use this with a special HeartbeatSender to observe
    public CmdHeartbeatListen(OnCmdHeartbeatListen  caller, String _desiredHeartBeatSenderClass) {
        this.querryClient = caller;
        this.desiredHeartBeatSenderClass= _desiredHeartBeatSenderClass;
    }
    
    //@SuppressWarnings("unchecked")
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            
            try {
                final String content = textMessage.getText();
                // logger.info("CmdHeartbeatListener gets from ActiveMQ: " + content ); // Debug
                JSONObject jMessage = (JSONObject) jsonParser.parse(content);
                if (desiredHeartBeatSenderClass!=null && !Optional.ofNullable(jMessage.get(CmdHeartbeatSend.HB_SENDER)).orElse("").equals(desiredHeartBeatSenderClass))
                	// discard message
                	return;
                
                // put the contents of the message in a jsonObject               
                this.jsonObject = jMessage;
                
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
    
   
    @Override
    public void execute() {
        Factory.initialize();
        Factory.LOGGER.trace("subscribing the Heartbeat listener");
        Factory.jmsHelper.setupTopicListener(CmdHeartbeatSend.HB_MSG_TOPIC, this);
        monitor();
    }
    
    
    // we need a method to monitor if there even are messages and which, and give back the necessary Information
    @SuppressWarnings("unchecked")  
    public void monitor() {
       
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {

                Timestamp now = new Timestamp(System.currentTimeMillis());
                
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
                String alerttime = df.format(now);

                Timestamp myFirst = getFirst();
                Timestamp myLast = getLast();                
                
                JSONObject myJsonObject = getJsonObject();                
                JSONObject failJsonObject = new JSONObject();

                if  (myFirst != null && myLast != null) {  

                     if (myJsonObject != null) {                                             
                         long mySendingPeriod = getSendingPeriod();
                        
                         if  (now.getTime() - myLast.getTime()  <= (mySendingPeriod + 1000) ) {        // <= 6000 ms
                            setMessageState(HbMsgState.INTIME);
                         }
                         else if (now.getTime() - myLast.getTime() <= (mySendingPeriod + 6000 ) ) {  // <= 11 000 ms
                             setMessageState(HbMsgState.WAITING);
                          }
                         else if (now.getTime() - myLast.getTime() <= (mySendingPeriod + 16000) ) {  // <= 21 000 ms
                             setMessageState(HbMsgState.ALERT);    
                         }
                         else if (now.getTime() - myLast.getTime() > ((mySendingPeriod * 4) +1000 )) {   // ca > 21 000 ms
                            setMessageState(HbMsgState.DEAD);
                            myJsonObject.put(CmdHeartbeatSend.HB_ALLERTTIME, alerttime);                            
                            myJsonObject.put(CmdHeartbeatSend.HB_LASTSENDINGPERIOD, 0L);                            
                            myJsonObject.put(CmdHeartbeatSend.HB_SENDERHEALTHSTATE, false);                            
                         } 
                         else {
                            setMessageState(HbMsgState.UNKNOWN);
                         }
                       
                         myJsonObject.put(CmdHeartbeatSend.HB_MESSAGESTATE, messageState);    
                         
                         // give the enhanced json-object back to the caller  
                         sendbackToQuerryClient(myJsonObject );
                    }
                }
                else if (myLast==null) {                    
                    //  if we have not got any message with onMessage there is nothing to monitor!                
                	setMessageState(HbMsgState.UNKNOWN);
                    failJsonObject.put(CmdHeartbeatSend.HB_SENDER, desiredHeartBeatSenderClass);
                    failJsonObject.put(CmdHeartbeatSend.HB_MESSAGESTATE, messageState); 
                    failJsonObject.put(CmdHeartbeatSend.HB_COMMENT, "there is'nt any HeartBeat yet");
                    
                    sendbackToQuerryClient(failJsonObject );
                }
            }
        }, 0, 5000);

    }
    
    
    // give the enhanced json-object back to the caller
    public void sendbackToQuerryClient(JSONObject _myJsonObject) {
        if (querryClient != null) {
            // querryClient.hearHeartbeat(content);                  // the simple Text Message
            // querryClient.hearHeartbeat(myJsonObject.toString());  // the json object as a String
            querryClient.hearHeartbeat(_myJsonObject);               // giving back a jsonObject

        } else {
            logger.warn("In CmdHeartbeatListener Monitor  client is null !!!!");
        }

    }


    public HbMsgState getMessageState() {
        return messageState;
    }


    public void setMessageState(HbMsgState messageState) {
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
