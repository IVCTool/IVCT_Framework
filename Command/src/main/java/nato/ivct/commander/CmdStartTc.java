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

import java.io.File;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

public class CmdStartTc implements Command {
	// private MessageProducer producer;
	private String sut;
	private String badge;
	private String tc;
	private String runFolder;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTc.class);

	public CmdStartTc(String _sut, String _badge, String _tc, String _runFolder) {
		sut = _sut;
		tc = _tc;
		badge = _badge;
		runFolder = _runFolder;
	}

	@SuppressWarnings("unchecked")
	@Override
	/*
	 * The Structure of start test case command message looks like the
	 * following:
	 * 
	 * { "sequence":"0", "commandType":"startTestCase",
	 * "testScheduleName":"HelloWorld", "sutName":"hw_iosb",
	 * "sutDir":"C:\\projects\\MSG134\\IVCT_Runtime\\IVCTsut\\hw_iosb",
	 * "tsRunFolder":"C:\\projects\\MSG134\\IVCT_Runtime\\Badges\\HelloWorld",
	 * "tcParam":{ "rtiHostName":"localhost", "federationName":"HelloWorld",
	 * "sutFederateName":"A" }, "testCaseId":"TC0002"}
	 * 
	 * @see nato.ivct.commander.Command#execute()
	 */
	public void execute() {
		// build the start parameter
		try {
			JSONParser parser = new JSONParser();
			JSONObject startCmd = new JSONObject();
			String sutHome = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
			String paramFileName = sutHome + '/' + sut + '/' + badge + "/TcParam.json";
			startCmd.put("commandType", "startTestCase");
			startCmd.put("sequence", Integer.toString(Factory.newCmdCount()));
			startCmd.put("sutName", sut);
			startCmd.put("sutDir", sutHome + '/' + sut);
			startCmd.put("testScheduleName", badge);
			startCmd.put("testCaseId", tc);
			startCmd.put("tsRunFolder",
					Factory.props.getProperty(Factory.IVCT_TS_HOME_ID) + '/' + runFolder);
			
			String paramFileContentString = Factory.readWholeFile(paramFileName);
			String tmpString = Factory.replaceMacro(paramFileContentString);
			JSONObject jsonParam = (JSONObject) parser.parse(tmpString);
			startCmd.put("tcParam", jsonParam);

			// send the start message
			Factory.sendToJms(startCmd.toString());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error in starting test case <" + badge + '/' + tc + ">");
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

	public String getBadge() {
		return badge;
	}

	public void setBadge(String _badge) {
		this.badge = _badge;
	}

	public String getRunFolder() {
		return runFolder;
	}

	public void setRunFolder(String runFolder) {
		this.runFolder = runFolder;
	}

}
