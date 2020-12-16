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
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.HeartBeatMsgStatus.HbMsgState;

import java.util.HashMap;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * CmdHeartbeatListen receive json-objects from ActiveMQ topic "HeartBeat",  
 * examine these, and either, if there are no Problems
 * enhance it only with one Key "MessageState" 
 * or in case of problems, change more Keys  
 * and give it back to the caller of this class
 */



public class CmdHeartbeatListen implements MessageListener, Command {

  // Organize return mechanism (the client has to implement this interface.)
  public interface OnCmdHeartbeatListen {
    public void hearHeartbeat(JSONObject backJson);
  }

  private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CmdHeartbeatListen.class);

  public static final String HB_MSG_TOPIC = "HeartBeat";

  private JSONParser jsonParser = new JSONParser();

  private HbMsgState messageState = HbMsgState.UNKNOWN;
  
  private String desiredHeartBeatSenderClass;
  
  // we have to organize timestamps and messages from different HB_Sender
  // for this we use 2 maps :  lastTimestampsMap and jsonObjectsMap

  // a data structure to insert different timestamps
  HashMap<String, Timestamp> lastTimestampsMap = new HashMap<>();

  // a data structure to insert different JsonObjects
  HashMap<String, JSONObject> jsonObjectsMap = new HashMap<>();

  // the referenz to the caller
  private OnCmdHeartbeatListen querryClient;

  public CmdHeartbeatListen(OnCmdHeartbeatListen caller) {
    this(caller, null);
  }

  // the client can use this with a special HeartbeatSender to observe
  public CmdHeartbeatListen(OnCmdHeartbeatListen caller, String desiredHeartBeatSenderClass) {
    this.querryClient = caller;
    this.desiredHeartBeatSenderClass = desiredHeartBeatSenderClass;
  }
    
  @Override
  public void execute() {
    Factory.initialize();
    Factory.LOGGER.trace("subscribing the Heartbeat listener");
    Factory.jmsHelper.setupTopicListener(CmdHeartbeatSend.HB_MSG_TOPIC, this);
    monitor();
  }
    

  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      final TextMessage textMessage = (TextMessage) message;

      try {
        final String content = textMessage.getText();
        // logger.DEBUG("####### CmdHeartbeatListener gets from ActiveMQ: " + content ); // Debug

        // put the contents of the Message in a JsonObject
        JSONObject jMessage = (JSONObject) jsonParser.parse(content);
                
        // get the HB_Sender-name from the JsonObject
        //String senderName = (String) jMessage.get(CmdHeartbeatSend.HB_SENDER);
        String senderName = (String) jMessage.get(CmdHeartbeatSend.HB_SENDER) + "_" + jMessage.get(CmdHeartbeatSend.HB_TESTENGINELABEL);

        /* if we got a desiredHeartBeatSenderClass but in the Message of ActiveMQ in
         * "HeartbeatSender" this name is not found, we discard this message
         */
        if (desiredHeartBeatSenderClass != null && !Optional.ofNullable(jMessage.get(CmdHeartbeatSend.HB_SENDER)).orElse("").equals(desiredHeartBeatSenderClass)) {
          // logger.debug("a "+CmdHeartbeatSend.HB_SENDER+": "+desiredHeartBeatSenderClass+" is not found in HeartbeatMessages but: "+jMessage.get(CmdHeartbeatSend.HB_SENDER));
          // discard message ( there will be no Entries in the maps )
          jMessage = null;
          return;
        }
        
        // put the JsonObject with the message in the jsonObject-Map
        jsonObjectsMap.put(senderName, jMessage);

        // we need some kind of timestamp
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // put the timestamp in the timestamp-Map
        lastTimestampsMap.put(senderName, now);

      } catch (final Exception e) {
        Factory.LOGGER.error("onMessage: problems with getText", e);
      }
    }
  }   
   
        
  /*
   * a method to monitor if there even are messages, observe the
   * frequency of incomming messages, draw conclusions of this and give back the
   * necessary Information
   */
  @SuppressWarnings("unchecked")
  public void monitor() {

    Timer timer = new Timer();
    timer.schedule(new TimerTask() {
      public void run() {

        Timestamp now = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        String alerttime = df.format(now);

        // if there are Timestamps and jsonObjekte in our maps
        if (!lastTimestampsMap.isEmpty() && (!jsonObjectsMap.isEmpty())) {

          // for every Entry in lastTimestampsMap
          for (HashMap.Entry<String, Timestamp> timestampEntry : getLastTimestampsMap().entrySet()) {
            //logger.debug("in Monitor HB_Sender is now : " + timestampEintrag.getKey()); // Debug

            // get the HB_Sender for this message
            String tempHBSender = timestampEntry.getKey();

            // get the incomming timestamp of this message
            Timestamp myLast = timestampEntry.getValue();

            // get the JsonObject with the message for this HB_Sender from the jsonObjectsMap
            JSONObject myJsonObject = getJsonObjectsMap().get(tempHBSender);

            // get the intended Sending-Period of the Message-Sender
            long mySendingPeriod = (long) myJsonObject.get(CmdHeartbeatSend.HB_LASTSENDINGPERIOD);          

            if (now.getTime() - myLast.getTime() <= (mySendingPeriod + 1000)) { // <= 6000 ms
              setMessageState(HbMsgState.INTIME);
            } else if (now.getTime() - myLast.getTime() <= (mySendingPeriod + 6000)) { // <= 11 000 ms
              setMessageState(HbMsgState.WAITING);
            } else if (now.getTime() - myLast.getTime() <= (mySendingPeriod + 16000)) { // <= 21 000 ms
              setMessageState(HbMsgState.ALERT);
            } else if (now.getTime() - myLast.getTime() > ((mySendingPeriod * 4) + 1000)) { // ca > 21 000 ms
              setMessageState(HbMsgState.DEAD);
              myJsonObject.put(CmdHeartbeatSend.HB_ALLERTTIME, alerttime);
              //myJsonObject.put(CmdHeartbeatSend.HB_LASTSENDINGPERIOD, 0L);
              myJsonObject.put(CmdHeartbeatSend.HB_LASTSENDINGPERIOD, mySendingPeriod);
              myJsonObject.put(CmdHeartbeatSend.HB_SENDERHEALTHSTATE, false);
            } else {
              setMessageState(HbMsgState.UNKNOWN);
            }
      
            myJsonObject.put(CmdHeartbeatSend.HB_MESSAGESTATE, getMessageState().state());

            // give the enhanced json-object back to the caller
            sendbackToQuerryClient(myJsonObject);

          }
          
        } else {
          JSONObject failJsonObject = new JSONObject();
          // if we have not got any message with onMessage there is nothing to monitor!
          setMessageState(HbMsgState.UNKNOWN);
          failJsonObject.put(CmdHeartbeatSend.HB_SENDER, desiredHeartBeatSenderClass);
          failJsonObject.put(CmdHeartbeatSend.HB_MESSAGESTATE, messageState.state());
          failJsonObject.put(CmdHeartbeatSend.HB_COMMENT, "there is'nt any HeartBeat yet");

          sendbackToQuerryClient(failJsonObject);
        }      
      }
    }, 0, 5000);
  }
    
    
  // give the enhanced json-object back to the caller
  public void sendbackToQuerryClient(JSONObject myJsonObject) {
    if (querryClient != null) {
      // querryClient.hearHeartbeat(content);                  // the simple Text Message
      // querryClient.hearHeartbeat(myJsonObject.toString());  // the json object as a String
      querryClient.hearHeartbeat(myJsonObject);               // giving back a jsonObject

      } else {
        logger.warn("In CmdHeartbeatListener Monitor  client is null !!!!");
      }
  }


    
  //  getter und Setter
    
  private HashMap<String,Timestamp> getLastTimestampsMap() {
    return lastTimestampsMap;
  }
    
  private HashMap<String,JSONObject> getJsonObjectsMap() {
      return jsonObjectsMap;
  }
   
    
  public HbMsgState getMessageState() {
    return messageState;
  }

  public void setMessageState(HbMsgState messageState) {
    this.messageState = messageState;
  }
    
}
