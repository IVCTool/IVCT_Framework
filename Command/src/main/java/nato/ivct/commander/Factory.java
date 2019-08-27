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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;

import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import nato.ivct.commander.CmdLogMsgListener.OnLogMsgListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdTcStatusListener.OnTcStatusListener;

/*
 * The Factory is used to create Command objects to be executed by a user interface.
 * Before any Command objects can be created, the factory object need to be initialized.
 * The Factory is also the container for all properties and JMS elements. The life cycle is managed by the
 * caller of the factory
 */
public class Factory {

	public static Properties props = null;
	public static final String IVCT_CONF = "IVCT_CONF";
	public static final String IVCT_CONF_DEFLT = "/root/conf/IVCT.properties";

	public static final String IVCT_TS_HOME_ID = "IVCT_TS_HOME_ID";
	public static final String IVCT_TS_HOME_ID_DEFLT = "/root/conf/TestSuites";
	public static final String IVCT_SUT_HOME_ID = "IVCT_SUT_HOME_ID";
	public static final String IVCT_SUT_HOME_ID_DEFLT = "/root/conf/IVCTsut";
    public static final String IVCT_BADGE_HOME_ID = "IVCT_BADGE_HOME_ID";
    public static final String IVCT_BADGE_HOME_ID_DEFLT = "/root/conf/Badges";
    public static final String IVCT_BADGE_ICONS_ID = "IVCT_BADGE_ICONS";
    public static final String IVCT_BADGE_ICONS_ID_DEFLT = "/root/conf/Badges";

	public static final String RTI_ID = "RTI_ID";
	public static final String RTI_ID_DEFLT = "pRTI";
	public static final String PROPERTY_IVCTCOMMANDER_QUEUE = "ivctcommander.queue";
	public static final String MESSAGING_USER_ID = PropertyBasedClientSetup.PROPERTY_USER;
	public static final String MESSAGING_USER_DEFLT = "admin";
	public static final String MESSAGING_PASSWORD_ID = PropertyBasedClientSetup.PROPERTY_PASSWORD;
	public static final String MESSAGING_PASSWORD_DEFLT = "password";
	public static final String MESSAGING_HOST_ID = PropertyBasedClientSetup.PROPERTY_HOST;
	public static final String MESSAGING_HOST_DEFLT = "localhost";
	public static final String MESSAGING_PORT_ID = PropertyBasedClientSetup.PROPERTY_PORT;
	public static final String MESSAGING_PORT_DEFLT = "61616";
	public static final String JMS_QUEUE_ID = "jmstestrunner.queue";
	public static final String JMS_QUEUE_DEFLT = "commands";

	public static final String JAVA_NAMING_FACTORY_ID = "java.naming.factory.initial";
	public static final String JAVA_NAMING_FACTORY_DEFLT = "org.apache.activemq.jndi.ActiveMQInitialContextFactory";
	public static final String JAVA_NAMING_PROVIDER_ID = "java.naming.provider.url";
	public static final String JAVA_NAMING_PROVIDER_DEFLT = "tcp://localhost:61616";

	public static final String LOGSINK_TCF_BINDINGNAME_ID = "logsink.tcf.bindingname";
	public static final String LOGSINK_TCF_BINDINGNAME_DEFLT = "ConnectionFactory";
	public static final String LOGSINK_TOPIC_BINDINGNAME_ID = "logsink.topic.bindingname";
	public static final String LOGSINK_TOPIC_BINDINGNAME_DEFLT = "dynamicTopics/LogTopic.jms";
	public static final String LOGSINK_USER_ID = "logsink.user";
	public static final String LOGSINK_USER_DEFLT = "";
	public static final String LOGSINK_PASSWORD_ID = "logsink.password";
	public static final String LOGSINK_PASSWORD_DEFLT = "";

    public static final String SETTINGS_DESIGNATOR = "SETTINGS_DESIGNATOR";
    public static final String SETTINGS_DESIGNATOR_DEFLT = "crcAddress=localhost:8989";
    public static final String FEDERATION_NAME = "FEDERATION_NAME";
    public static final String FEDERATION_NAME_DEFLT = "TheWorld";
    public static final String FEDERATE_NAME_DEFLT = "sut";

	private static MessageProducer producer = null;
	private static int cmdCounter = 0;
    private static String version = null;
    private static String build = null;


	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);

	static public PropertyBasedClientSetup jmsHelper = null;

	/*
	 * read string environment variable if defined, otherwise return default
	 */
    public static String getEnv(String key) {
        String value = System.getenv(key);
        return value;
    }

    /*
     * read environment variable and provide default if not found
     */
    public static String getEnv(String key, String deflt) {
		String value = System.getenv(key);
		if (value == null) {
			return deflt;
		}
		LOGGER.info("Environment Variable {} = {} found", key, value);
		return value;
	}

	/*
	 * overwrite properties list if given key is found in system environment
	 */
	private static void overwriteWithEnv(String key) {
		String value = System.getenv(key);
		if (value != null) {
			LOGGER.info("Environment Variable {} = {} found", key, value);
			props.setProperty(key, value);
		}
	}


	public static void readVersion() {
	    Properties versionProperties = new Properties();
	    try {
            versionProperties.load(Command.class.getResourceAsStream("/dev.properties"));
            setVersion(versionProperties.getProperty("version"));
            setBuild(versionProperties.getProperty("build"));
            } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}

	/*
	 * Factory has to be initialized before any commands are being created.
	 */
	public static void initialize() {

		if (props == null) {
		    readVersion();
		    LOGGER.info("IVCT Version " + getVersion() + ", build " + getBuild());

			Properties fallback = new Properties();
			fallback.put(IVCT_CONF, IVCT_CONF_DEFLT);
			fallback.put(IVCT_TS_HOME_ID, IVCT_TS_HOME_ID_DEFLT);
			fallback.put(IVCT_SUT_HOME_ID, IVCT_SUT_HOME_ID_DEFLT);
            fallback.put(IVCT_BADGE_HOME_ID, IVCT_BADGE_HOME_ID_DEFLT);
            fallback.put(IVCT_BADGE_ICONS_ID, IVCT_BADGE_ICONS_ID_DEFLT);
			fallback.put(RTI_ID, RTI_ID_DEFLT);
			fallback.put(MESSAGING_USER_ID, MESSAGING_USER_DEFLT);
			fallback.put(MESSAGING_PASSWORD_ID, MESSAGING_PASSWORD_DEFLT);
			fallback.put(MESSAGING_HOST_ID, MESSAGING_HOST_DEFLT);
			fallback.put(MESSAGING_PORT_ID, MESSAGING_PORT_DEFLT);
			fallback.put(JMS_QUEUE_ID, JMS_QUEUE_DEFLT);
			fallback.put(JAVA_NAMING_FACTORY_ID, JAVA_NAMING_FACTORY_DEFLT);
			fallback.put(JAVA_NAMING_PROVIDER_ID, JAVA_NAMING_PROVIDER_DEFLT);
			fallback.put(LOGSINK_TCF_BINDINGNAME_ID, LOGSINK_TCF_BINDINGNAME_DEFLT);
			fallback.put(LOGSINK_TOPIC_BINDINGNAME_ID, LOGSINK_TOPIC_BINDINGNAME_DEFLT);
			fallback.put(LOGSINK_USER_ID, LOGSINK_USER_DEFLT);
			fallback.put(LOGSINK_PASSWORD_ID, LOGSINK_PASSWORD_DEFLT);
			fallback.put(SETTINGS_DESIGNATOR, SETTINGS_DESIGNATOR_DEFLT);

			props = new Properties(fallback);

			String home = System.getenv(IVCT_CONF);

			if (home == null) {
			    LOGGER.debug("using IVCT_CONF default ("+ IVCT_CONF_DEFLT + ")");
			    home = props.getProperty(IVCT_CONF);
			}
			try {
				File f = new File(home);
				// test if IVCT_CONF is already a filename
				if (f.exists()) {
					LOGGER.debug(home + " exists");
				}
				if (f.isDirectory()) {
					LOGGER.debug(home + " is directory");
				}
				if (f.exists() && !f.isDirectory()) {
					props.load(new FileInputStream(f));
				} else {
					// if not, just try to read the properties file with the default name
					props.load(new FileInputStream(home + "/IVCT.properties"));
					LOGGER.debug("Properties {} file loaded", home + "/IVCT.properties");
				}
			} catch (final Exception e) {
				LOGGER.error("Unable to read IVCT_CONF = {}  creating default values", home);
				try {
					fallback.store(new FileOutputStream(home + "/IVCT.properties"), "IVCT Properties File");
					LOGGER.warn(
							"New IVCT.properties file has been created with default values. Please verify settings!");
					LOGGER.warn(props.toString());
				} catch (IOException e1) {
					LOGGER.error("Unable to write " + home + "/IVCT.properties file.");
					e1.printStackTrace();
				}
			}

			// overwrite with environment settings
			overwriteWithEnv(IVCT_TS_HOME_ID);
			overwriteWithEnv(IVCT_SUT_HOME_ID);
            overwriteWithEnv(IVCT_BADGE_HOME_ID);
            overwriteWithEnv(IVCT_BADGE_ICONS_ID);
			overwriteWithEnv(RTI_ID);
			overwriteWithEnv(MESSAGING_USER_ID);
			overwriteWithEnv(MESSAGING_PASSWORD_ID);
			overwriteWithEnv(MESSAGING_HOST_ID);
			overwriteWithEnv(MESSAGING_PORT_ID);
			overwriteWithEnv(JMS_QUEUE_ID);
			overwriteWithEnv(JAVA_NAMING_FACTORY_ID);
			overwriteWithEnv(LOGSINK_TCF_BINDINGNAME_ID);
			overwriteWithEnv(LOGSINK_TOPIC_BINDINGNAME_ID);
			overwriteWithEnv(LOGSINK_USER_ID);
			overwriteWithEnv(LOGSINK_PASSWORD_ID);
            overwriteWithEnv(SETTINGS_DESIGNATOR);
            overwriteWithEnv(FEDERATION_NAME);

			LOGGER.debug("Properties used: {}", props);

			jmsHelper = new PropertyBasedClientSetup(props);
			jmsHelper.parseProperties();
			jmsHelper.initConnection();
			jmsHelper.initSession();
			producer = jmsHelper.setupTopicProducer(props.getProperty(PROPERTY_IVCTCOMMANDER_QUEUE, JMS_QUEUE_DEFLT));
		} // otherwise consider to be already initialized
	}

	public static MessageProducer createTopicProducer (String topic) {
        return jmsHelper.setupTopicProducer(topic);
	}

	public static String readWholeFile(final String filename) {
		BufferedReader br = null;
		String everything = null;

		File myFile = new File(filename);
		if (myFile.isFile() == false) {
			return everything;
		}

		try {
			br = new BufferedReader(new FileReader(filename));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			everything = sb.toString();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}

		return everything;
	}

	public static String replaceMacro(String inString) {
		StringBuilder env = new StringBuilder();
		StringBuilder out = new StringBuilder();
		int len;

		env.setLength(512);
		out.setLength(512);
		out.setLength(0);
		len = inString.length();

		// Loop through inString character by character
		for (int i = 0, l = 0; i < len; i++, l++) {
			boolean gotClosing = false;
			boolean gotEnv = false;
			if (i < len - 1) {
				// Check if we have "$(" in inString
				if (inString.charAt(i) == '$' && inString.charAt(i + 1) == '(') {
					gotClosing = false;
					for (int j = i + 2, k = 0; j < len; j++, k++) {
						if (inString.charAt(j) == ')') {
							String b = null;
							gotClosing = true;
							gotEnv = true;
							env.setLength(k);
							if (env.length() > 0) {
								b = Factory.props.getProperty(env.toString());
							} else {
								LOGGER.error("LineUtil:replaceMacro: Missing environment variable ");
							}
							if (b != null) {
								out.append(b);
								l += b.length() - 1;
								i = j;
							} else {
								LOGGER.error(
										"LineUtil:replaceMacro: Environment variable not found: " + env.toString());
								String s = inString.subSequence(i, i + k + 3).toString();
								out.append(s);
								l += j - i;
								i = j;
							}
							break;
						}
						env.setCharAt(k, inString.charAt(j));
					}
					if (gotClosing == false) {
						LOGGER.error("LineUtil:replaceMacro: Missing closing bracket");
					}
				}
			}
			if (gotEnv) {
				gotEnv = false;
				continue;
			}
			out.insert(l, inString.charAt(i));
			out.setLength(l + 1);
		}

		return out.toString();
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

	// factory methods for commands

	public static CmdListSuT createCmdListSut() {
		initialize();
		return new CmdListSuT();
	}

	public static CmdListBadges createCmdListBadges() {
		initialize();
		return new CmdListBadges();
	}

	public static CmdListTestSuites createCmdListTestSuites() {
		initialize();
		return new CmdListTestSuites();
	}

	public static CmdStartTc createCmdStartTc(String _sut, String _testSuiteName, String _tc, String _settingsDesignator, String _federationName, String _sutFederateName) {
		initialize();
		return new CmdStartTc(_sut, _testSuiteName, _tc, _settingsDesignator, _federationName, _sutFederateName);
	}

	public static CmdSetLogLevel createCmdSetLogLevel(LogLevel level) {
		initialize();
		return new CmdSetLogLevel(level);
	}

	public static CmdQuit createCmdQuit() {
		initialize();
		return new CmdQuit();
	}

	public static CmdQuitListener createCmdQuitListener(OnQuitListener listener) {
		initialize();
		return new CmdQuitListener(listener);
	}

	public static CmdStartTestResultListener createCmdStartTestResultListener(OnResultListener listener) {
		initialize();
		return new CmdStartTestResultListener(listener);
	}

	public static CmdTcStatusListener createCmdTcStatusListener(OnTcStatusListener listener) {
		initialize();
		return new CmdTcStatusListener(listener);
	}

	public static CmdLogMsgListener createCmdLogMsgListener(OnLogMsgListener listener) {
		initialize();
		return new CmdLogMsgListener(listener);
	}

	public static CmdSendTcStatus createCmdSendTcStatus() {
		initialize();
		return new CmdSendTcStatus();
	}

	public static CmdSendTcVerdict createCmdSendTcVerdict(String sutName, String sutDir, String testScheduleName,
			String testcase, String verdict, String verdictText) {
		initialize();
		return new CmdSendTcVerdict(sutName, sutDir, testScheduleName, testcase, verdict, verdictText);
	}

	public static CmdUpdateSUT createCmdUpdateSUT(final SutDescription sutDescription) {
		initialize();
		return new CmdUpdateSUT(sutDescription);
	}

	public static SutPathsFiles getSutPathsFiles() {
		initialize();
		return new SutPathsFiles();
	}

	public static CmdListSuT createCmdListSuT() {
	    initialize();
	    return new CmdListSuT();
	}
	public static int getCmdCounter() {
		return cmdCounter;
	}

	public static int newCmdCount() {
		return ++cmdCounter;
	}

    public static String getVersion() {
        return version;
    }

    private static void setVersion(String version) {
        Factory.version = version;
    }

    public static String getBuild() {
        return build;
    }

    private static void setBuild(String build) {
        Factory.build = build;
    }

}
