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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;

/*
 * The Factory is used to create Command objects to be executed by a user interface.
 */
public class Factory {

	public static Properties props = null;
	public static final String IVCT_HOME_ID = "IVCT_HOME_ID";
	public static final String IVCT_TS_HOME_ID = "IVCT_TS_HOME_ID";
	public static final String IVCT_SUT_HOME_ID = "IVCT_SUT_HOME_ID";
	public static final String RTI_ID = "RTI_ID";
	public static final String PROPERTY_IVCTCOMMANDER_QUEUE = "ivctcommander.queue";
	private static MessageProducer producer = null;
	private static int cmdCounter = 0;

	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);

	static public PropertyBasedClientSetup jmsHelper = null;

	/*
	 * Factory has to be initialized before any commands are being created.
	 */
	public void initialize() throws FileNotFoundException, IOException {
		props = new Properties();
		try {
			props.load(new FileInputStream("IVCT.properties"));
			jmsHelper = new PropertyBasedClientSetup(props);
			jmsHelper.parseProperties();
			jmsHelper.initConnection();
			jmsHelper.initSession();
			producer = jmsHelper.setupTopicProducer(props.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, "commands"));

		} catch (final FileNotFoundException e) {
			LOGGER.warn("no properties file IVCT.properties found");
			props.setProperty(IVCT_HOME_ID, "C:/ProjekteLokal/MSG134/IVCT_Framework");
			props.setProperty(IVCT_TS_HOME_ID, "C:/ProjekteLokal/MSG134/DemoFolders/IVCTtestSuites");
			props.setProperty(IVCT_SUT_HOME_ID, "C:/ProjekteLokal/MSG134/DemoFolders/IVCTsut");
			props.setProperty(RTI_ID, "pRTI");
			props.store(new FileOutputStream("IVCT.properties"), "IVCT Properties File");
			LOGGER.warn("New IVCT.properties file has been created with default values. Please verify settings!");
			LOGGER.warn(props.toString());
		}
	}

	public static void sendToJms(final String userCommand) {
		Message message = jmsHelper.createTextMessage(userCommand);
		try {
			producer.send(message);
		} catch (JMSException e) {
			LOGGER.error("could not send command: " + userCommand);
			e.printStackTrace();
		}
	}

	public CmdListSuT createCmdListSut() {
		return new CmdListSuT();
	}

	public CmdListBadges createCmdListBadges() {
		return new CmdListBadges();
	}

	public CmdStartTc createCmdStartTc(String _sut, String _badge, String _tc, String _runFolder) {
		return new CmdStartTc(_sut, _badge, _tc, _runFolder);
	}

	// public CmdSetLogLevel createCmdSetLogLevel () {
	// return new CmdSetLogLevel();
	// }
	//
	public CmdStartTestResultListener createCmdStartTestResultListener(OnResultListener listener) {
		return new CmdStartTestResultListener(listener);
	}

	public static int getCmdCounter() {
		return cmdCounter;
	}

	public static int newCmdCount() {
		return ++cmdCounter;
	}

}
