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
import java.io.FileReader;
import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

public class CmdStartTc implements Command {
	private MessageProducer producer;
	private String sut;
	private String badge;
	private String tc;
	
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(CmdStartTc.class);


	public CmdStartTc(MessageProducer p) {
		producer = p;
	}

	@Override
	public void execute() {
		// build the start parameter
		try {
			String sutHome = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
			JSONObject startCmd = new JSONObject();
			startCmd.put("sutId", sut);
			startCmd.put("testScheduleName", badge);
			startCmd.put("testCaseId", tc);
			startCmd.put("tsRunFolder", sutHome);
			startCmd.put("testCaseId", tc);
			startCmd.put("testCaseId", tc);
			JSONParser parser = new JSONParser();
			String paramFileName = sutHome + "\\" + sut + "\\" + badge+ "\\TcParam.json";
			JSONObject jsonParam = (JSONObject) parser.parse(new FileReader(paramFileName));
			startCmd.put("tcParam", jsonParam);

			// send the start message
			Message m = Factory.jmsHelper.createTextMessage (startCmd.toString());
			producer.send(m);

		} catch (IOException | ParseException | JMSException e) {
			// TODO Auto-generated catch block
			LOGGER.error("error in starting test case <" + badge + "/" + tc + ">");
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

}
