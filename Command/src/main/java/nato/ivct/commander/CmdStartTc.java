/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

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
    public static final String BADGE               = "badge";
    public static final String TC_ID               = "testCaseId";
    public static final String TC_PARAM            = "tcParam";
    public static final String SETTINGS_DESIGNATOR = "settingsDesignator";
    public static final String FEDERATION          = "federationName";
            
	// private MessageProducer producer;
	private String sut;
	private String testSuiteName;
    private String tc;
    private String settingsDesignator;
    private String federationName;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTc.class);

	public CmdStartTc(String _sut, String _testSuiteName, String _tc, String _settingsDesignator, String _federationName) {
		sut = _sut;
		tc = _tc;
		testSuiteName = _testSuiteName;
		settingsDesignator = _settingsDesignator;
		federationName = _federationName;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * The Structure of start test case command message looks like the
	 * following:
	 *
	 * { 
	 *   "commandType":"startTestCase",
	 *   "sequence":"0",
	 *   "testScheduleName":"HelloWorld",
	 *   "sutName":"hw_iosb",
	 *   "sutDir":"C:/projects/IVCT_Runtime/IVCTsut/hw_iosb",
	 *   "badge":"C:/projects/IVCT_Runtime/Badges/HelloWorld-1.0.0",
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
			startCmd.put(BADGE, testSuiteName);
            startCmd.put(TC_ID, tc);
            startCmd.put(SETTINGS_DESIGNATOR, settingsDesignator);
            startCmd.put(FEDERATION, federationName);
			
			String paramFileContentString = Factory.readWholeFile(paramFileName);
			if (paramFileContentString != null) {
    			String tmpString = Factory.replaceMacro(paramFileContentString);
    			JSONObject jsonParam = (JSONObject) parser.parse(tmpString);
    			startCmd.put(TC_PARAM, jsonParam);
			} else {
				LOGGER.error("File not found: " + paramFileName);
				return;
			}

			// send the start message
			Factory.sendToJms(startCmd.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error in starting test case <" + testSuiteName + '/' + tc + ">");
			e.printStackTrace();
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

	public void setSuiteName(String _testSuiteName) {
		this.testSuiteName = _testSuiteName;
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
