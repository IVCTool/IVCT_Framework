/*
 * Copyright 2017, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package nato.ivct.commander;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;


public class CmdTcStatusListener implements MessageListener, Command {
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdTcStatusListener.class);
    private final OnTcStatusListener      listener;

    public class TcStatus {
        public String status;
        public int    percentFinshed;
        public String tcName;
        public String sutName;
    }

    public interface OnTcStatusListener {
        public void onTcStatus(TcStatus status);
    }


    public CmdTcStatusListener(OnTcStatusListener listener) {
        this.listener = listener;
    }


    @Override
    public void execute() {
        LOGGER.trace("subsribing the CmdTcStatusListener");
        Factory.jmsHelper.setupTopicListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE, "commands"), this);
    }


    public void terminate() {
        LOGGER.trace("terminate the CmdTcStatusListener");
        Factory.jmsHelper.disconnect();
    }


    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            final TextMessage textMessage = (TextMessage) message;
            try {
                final String content = textMessage.getText();
                try {
                    final JSONParser jsonParser = new JSONParser();
                    final JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
                    final String commandTypeName = (String) jsonObject.get("commandType");

                    if (commandTypeName.equals("TcStatus")) {
                        LOGGER.trace("JMS Message received: " + content);
                        final TcStatus status = new TcStatus();
                        status.status = (String) jsonObject.get("status");
                        status.percentFinshed = ((Long) jsonObject.get("percentFinshed")).intValue();
                        status.tcName = (String) jsonObject.get("tcName");
                        status.sutName = (String) jsonObject.get("sutName");
                        listener.onTcStatus(status);
                    }
                }
                catch (final ParseException e) {
                    e.printStackTrace();
                    LOGGER.error("onMessage: ", e);
                }
            }
            catch (final JMSException e) {
                LOGGER.error("onMessage: problems with getText", e);
            }
        }
    }

}
