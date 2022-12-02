/* Copyright 2020, Reinhard Herzog (Fraunhofer IOSB)

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


public class CmdStartTcListener implements MessageListener, Command {

    private OnStartTestCaseListener listener;

    public static class TcInfo {
        public String sutName;
        public String sutDir;
        public String testSuiteId;
        public String testCaseId;
        public String testCaseParam;
        public String settingsDesignator;
        public String federationName;
        public String sutFederateName;
        public String testEngineLabel;
    }

    public interface OnStartTestCaseListener {
        public void onStartTestCase(TcInfo info);
    }


    public CmdStartTcListener(OnStartTestCaseListener listener) {
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
            String commandTypeName = (String) jsonObject.get(CmdStartTc.COMMAND_ID);

            if (!commandTypeName.equals(CmdStartTc.COMMAND))
                return;

            Factory.LOGGER.trace("JMS Message received: {}", content);
            TcInfo info = new TcInfo();

            info.sutName = (String) jsonObject.get(CmdStartTc.SUT_NAME);
            info.sutDir = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID) + '/' + info.sutName;
            info.testSuiteId = (String) jsonObject.get(CmdStartTc.TS_ID);
            info.testCaseId = (String) jsonObject.get(CmdStartTc.TC_ID);
            info.settingsDesignator = (String) jsonObject.get(CmdStartTc.SETTINGS_DESIGNATOR);
            info.federationName = (String) jsonObject.get(CmdStartTc.FEDERATION);
            info.sutFederateName = (String) jsonObject.get(CmdStartTc.FEDERATE);

            // add context info into properties
            Factory.props.setProperty("IVCT_SUT_ID", info.sutName);
            Factory.props.setProperty("IVCT_TESTSUITE_ID", info.testSuiteId);
            info.testCaseParam = Factory.replaceMacro(jsonObject.get(CmdStartTc.TC_PARAM).toString());
            
            // for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
            if ( (jsonObject.get(CmdStartTc.TESTENGINE_LABEL).toString() == null) ||
                    (jsonObject.get(CmdStartTc.TESTENGINE_LABEL).toString().isEmpty() ) )  {
            	info.testEngineLabel= "TestEngine_Label not given to CmdStartTCListen" ;
            } else {
            info.testEngineLabel = jsonObject.get(CmdStartTc.TESTENGINE_LABEL).toString();
            }            
   
            Factory.LOGGER.info("StartTcListener Command received: {}", jsonObject);

            // check for missing values
            if (info.sutName == null)
                Factory.LOGGER.error("sutName is missing");
            if (info.sutDir == null)
                Factory.LOGGER.error("sutDir is missing");
            if (info.testSuiteId == null)
                Factory.LOGGER.error("testSuiteId is missing");
            if (info.testCaseId == null)
                Factory.LOGGER.error("testCaseId is missing");
            if (info.settingsDesignator == null)
                Factory.LOGGER.error("settingsDesignator is missing");
            if (info.federationName == null)
                Factory.LOGGER.error("federationName is missing");
            if (info.sutFederateName == null)
                Factory.LOGGER.error("sutFederateName is missing");
            if (info.testCaseParam == null)
                Factory.LOGGER.error("testCaseParam is missing");
            // for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
            if (info.testEngineLabel == null)
                Factory.LOGGER.error("testEngineLabel is missing");

            listener.onStartTestCase(info);
        }
        catch (ParseException e) {
            Factory.LOGGER.error("onMessage: ", e);
        }
        catch (final JMSException e) {
            Factory.LOGGER.error("onMessage: problems with getText", e);
        }
    }

}
