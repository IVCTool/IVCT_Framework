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

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

public class CmdStartTc implements Command {
    
    public static final String COMMAND_ID          = "commandType";
    public static final String COMMAND             = "startTestCase";
    public static final String SEQ                 = "sequence";
    public static final String SUT_NAME            = "sutName";
    public static final String SUT_DIR             = "sutDir";
    public static final String TS_ID               = "testSuiteId";
    public static final String TC_ID               = "testCaseId";
    public static final String TC_PARAM            = "tcParam";
    public static final String SETTINGS_DESIGNATOR = "settingsDesignator";
    public static final String FEDERATION          = "federationName";
	public static final String FEDERATE            = "sutFederateName";
	// for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
	public static final String TESTENGINE_LABEL    = "testEngineLabel";
            
	// private MessageProducer producer;
	private String sut;
	private String testSuiteName;
    private String tc;
    private String settingsDesignator;
    
    private String federationName;
	private String sutFederateName;
	// for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
	private String testEngineLabel ;
	
	

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTc.class);

	// for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
	public CmdStartTc(String sut, String testSuiteName, String tc, String settingsDesignator, String federationName, String sutFederateName) {
		this(sut, testSuiteName,tc ,settingsDesignator, federationName, sutFederateName, Factory.TESTENGINE_LABEL_DEFLT );		
	}
	
	public CmdStartTc(String sut, String testSuiteName, String tc, String settingsDesignator, String federationName, String sutFederateName, String _testEngineLabel) {
		this.sut = sut;
		this.tc = tc;
		this.testSuiteName = testSuiteName;
		this.settingsDesignator = settingsDesignator;
		this.federationName = federationName;
		this.sutFederateName = sutFederateName;
		// for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
	    this.testEngineLabel = _testEngineLabel;		
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * The Structure of start test case command message looks like the following:
	 *
	 * { 
	 *   "commandType":"startTestCase",
	 *   "sequence":"0",
	 *   "testScheduleName":"HelloWorld",
	 *   "sutName":"hw_iosb",
	 *   "sutDir":"C:/projects/IVCT_Runtime/IVCTsut/hw_iosb",
	 *   "testSuiteId":"TS_HelloWorld-2019",
	 *   "testCaseId":"TC0002",
	 *   "settingsDesignator":"crcAddress=localhost:8989",
	 *   "federationName":"TheWorld",
	 *   "tcParam":{
         *     "sutFederateName":"A" 
         *     "testDuration":"120" 
	 *     }
	 *   }
	 *
	 * @see nato.ivct.commander.Command#execute()
	 */
	public void execute() {
		// build the start parameter
		try {
			JSONParser parser = new JSONParser();
			JSONObject startCmd = new JSONObject();
			String sutHome = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
			String paramFileName = sutHome + '/' + sut + '/' + testSuiteName + "/TcParam.json";
			startCmd.put(COMMAND_ID, COMMAND);
			startCmd.put(SEQ, Integer.toString(Factory.newCmdCount()));
			startCmd.put(SUT_NAME, sut);
			startCmd.put(SUT_DIR, sutHome + '/' + sut);
			startCmd.put(TS_ID, testSuiteName);
            startCmd.put(TC_ID, tc);
            startCmd.put(SETTINGS_DESIGNATOR, settingsDesignator);
            startCmd.put(FEDERATION, federationName);
			startCmd.put(FEDERATE, sutFederateName);
			// for enhanced heartbeat with RTI-Type-Information brf 06.11.2020
			startCmd.put(TESTENGINE_LABEL, testEngineLabel);
			LOGGER.debug("StartTc Command: {}", startCmd);

			String paramFileContentString = Factory.readWholeFile(paramFileName);
			if (paramFileContentString != null) {
				JSONObject jsonParam = (JSONObject) parser.parse(paramFileContentString);
				startCmd.put(TC_PARAM, jsonParam);
			} else {
				LOGGER.error("File not found: {}. No test case start command sent!", paramFileName);
				return;
			}

			// send the start message
			Factory.sendToJms(startCmd.toString());
		} catch (ParseException exc) {
			LOGGER.error("error in starting test case <" + testSuiteName + '/' + tc + ">", exc);
		}

	}

	public String getSut() {
		return sut;
	}

	public void setSut(String sut) {
		this.sut = sut;
	}

	public String getTc() {
		return tc;
	}

	public void setTc(String tc) {
		this.tc = tc;
	}

	public String getSuiteName() {
		return testSuiteName;
	}

	public void setSuiteName(String testSuiteName) {
		this.testSuiteName = testSuiteName;
	}

	public String getSettingsDesignator() {
		return settingsDesignator;
	}

	public void setSettingsDesignator(String settingsDesignator) {
		this.settingsDesignator = settingsDesignator;
	}

	public String getFederationName() {
		return federationName;
	}

	public void setFederationName(String federationName) {
		this.federationName = federationName;
	}

}
