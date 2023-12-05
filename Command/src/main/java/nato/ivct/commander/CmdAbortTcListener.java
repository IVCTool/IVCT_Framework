/* Copyright 2020, Felix Schoeppenthau (Fraunhofer IOSB)

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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class CmdAbortTcListener implements MessageListener, Command {

    private OnAbortTestCaseListener listener;

    public class TcAbortInfo {
        public String sutName;
        public String testCaseId;
        public String testEngineLabel;
    }

    public interface OnAbortTestCaseListener {
        public void onAbortTestCase(TcAbortInfo info);
    }

    public CmdAbortTcListener(OnAbortTestCaseListener listener) {
        this.listener = listener;
    }

    @Override
    public void execute() {
        Factory.LOGGER.trace("subscribing the commands listener");
        Factory.jmsHelper.setupTopicListener(Factory.props.getProperty(Factory.PROPERTY_IVCTCOMMANDER_QUEUE, "commands"), this);
    }

    @Override
    public void onMessage(Message message) {
        if (!(message instanceof TextMessage))
            return;

        final TextMessage textMessage = (TextMessage) message;
        try {
            final String content = textMessage.getText();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(content);
            String commandTypeName = (String) jsonObject.get(CmdAbortTc.COMMAND_ID);

            if (!commandTypeName.equals(CmdAbortTc.COMMAND))
                return;

            Factory.LOGGER.trace("JMS Message received: {}", content);
            TcAbortInfo info = new TcAbortInfo();

            info.sutName = (String) jsonObject.get(CmdAbortTc.SUT_NAME);
            info.testCaseId = (String) jsonObject.get(CmdAbortTc.TC_ID);
            info.testEngineLabel = (String) jsonObject.get(CmdAbortTc.TEST_ENGINE);

            Factory.LOGGER.info("AbortTcListener Command received: {}", jsonObject);

            // check for missing values
            if (info.sutName == null)
                Factory.LOGGER.error("sutName is missing");
            if (info.testCaseId == null)
                Factory.LOGGER.error("testCaseId is missing");

            listener.onAbortTestCase(info);
        }
        catch (ParseException exc) {
            Factory.LOGGER.error("onMessage: ", exc);
        }
        catch (final JMSException exc) {
            Factory.LOGGER.error("onMessage: problems with getText", exc);
        }
    }
}
