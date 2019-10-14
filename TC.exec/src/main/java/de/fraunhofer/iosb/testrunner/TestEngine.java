package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCTVersionCheck;
import de.fraunhofer.iosb.tc_lib.IVCTVersionCheckFailed;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSendTcVerdict;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdSetLogLevelListener;
import nato.ivct.commander.CmdSetLogLevelListener.OnSetLogLevelListener;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.Factory;

/**
 * Testrunner that listens for certain commands to start and stop test cases.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public class TestEngine extends TestRunner implements OnSetLogLevelListener, OnQuitListener, OnStartTestCaseListener {

	private static Logger logger = LoggerFactory.getLogger(TestEngine.class);

	public String logLevelId = Level.INFO.toString();
	public String testCaseId = "no test case is running";

	private CmdListBadges badges;
	private HashMap<String, URLClassLoader> classLoaders = new HashMap<String, URLClassLoader>();


	/**
	 * Main entry point from the command line.
	 *
	 * @param args The command line arguments
	 */
	public static void main(final String[] args) {
		try {
			@SuppressWarnings("unused")
			final TestEngine runner = new TestEngine();
		} catch (final IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * public constructor.
	 *
	 * @throws IOException problems with loading properties
	 */
	public TestEngine() throws IOException {
		
		// set heartbeat identificer
    	myClassName = this.getClass().getSimpleName();

		// initialize the IVCT Commander Factory
		Factory.initialize();

		// Configure the logger
		LogConfigurationHelper.configureLogging();

		// start command listeners
		(new CmdSetLogLevelListener(this)).execute();
		(new CmdStartTcListener(this)).execute();
		(new CmdQuitListener(this)).execute();

		// start the heartbeat sender
		try {
			this.health = true;
			(new CmdHeartbeatSend(this)).execute();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("could not start  sendHeartbeat ");
		}

		// get the badge descriptions
		badges = new CmdListBadges();
		badges.execute();
	}

	private class TestScheduleRunner implements Runnable {
		TcInfo info;
		private TestRunner testRunner;

		TestScheduleRunner(final TcInfo info, final TestRunner testRunner) {
			this.info = info;
			this.testRunner = testRunner;
		}

		private File getCwd() {
			return new File("").getAbsoluteFile();
		}

		/**
		 * This method provides a way to set the current working directory which is not
		 * available as such in java.
		 * 
		 * N.B. This method uses a trick to get the desired result
		 *
		 * @param directory_name name of directory to be the current directory
		 * @return true if successful
		 */
		private boolean setCurrentDirectory(String directory_name) {
			boolean result = false; // Boolean indicating whether directory was
									// set
			File directory; // Desired current working directory

			directory = new File(directory_name).getAbsoluteFile();
			if (directory.exists()) {
				directory.mkdirs();
				result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
			}

			return result;
		}

		private void extendThreadClassLoader(final String badge) {
			URLClassLoader classLoader = classLoaders.get(badge);
			if (classLoader == null) {
				BadgeDescription bd = badges.badgeMap.get(badge);
				if (bd != null) {
					String ts_path = Factory.props.getProperty(Factory.IVCT_TS_HOME_ID);
					String lib_path = ts_path + "/" + bd.tsLibTimeFolder;
					File dir = new File(lib_path);
					File[] filesList = dir.listFiles();
					if (filesList == null) {
						logger.info("No files found in folder {}", dir.getPath());
						return;
					}

					URL[] urls = new URL[filesList.length];
					for (int i = 0; i < filesList.length; i++) {
						try {
							urls[i] = filesList[i].toURI().toURL();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					classLoader = new URLClassLoader(urls, TestRunner.class.getClassLoader());
					classLoaders.put(badge, classLoader);
				} else {
					logger.error("unknown badge " + badge);
				}
			}
			Thread.currentThread().setContextClassLoader(classLoader);
		}

		public void run() {
			logger.info("TestEngine:onMessageConsumer:run: " + info.testCaseId);
			MDC.put("sutName", info.sutName);
			MDC.put("sutDir", info.sutDir);
			MDC.put("badge", info.badge);
			MDC.put("testcase", info.testCaseId);

			BadgeDescription b = badges.badgeMap.get(info.badge);
			if (b == null) {
				logger.error("TestEngine:onMessageConsumer:run: unknown badge " + info.badge);
				return;
			}
			String runFolder = Factory.props.getProperty(Factory.IVCT_TS_HOME_ID) + '/' + b.tsRunTimeFolder;

			logger.info("TestEngine:onMessageConsumer:run: tsRunFolder is " + runFolder);
			if (setCurrentDirectory(runFolder)) {
				logger.info("TestEngine:onMessageConsumer:run: setCurrentDirectory true");
			}

			File f = getCwd();
			String tcDir = f.getAbsolutePath();
			logger.info("TestEngine:onMessageConsumer:run: TC DIR is " + tcDir);

			logger.info("TestEngine:onMessageConsumer:run: The test case class is: " + testCaseId);
			String[] testcases = info.testCaseId.split("\\s");
			IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

			extendThreadClassLoader(info.badge);

//			this.testRunner.executeTests(logger, info.sutName, testcases, info.testCaseParam.toString(), verdicts);

			int i = 0;
			for (final String classname : testcases) {
				AbstractTestCase testCase = null;
				try {
					testCase = (AbstractTestCase) Thread.currentThread().getContextClassLoader().loadClass(classname)
							.newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
					logger.error("Could not instantiate " + classname + " !", ex);
				}
				if (testCase == null) {
					verdicts[i] = new IVCT_Verdict();
					verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
					verdicts[i].text = "Could not instantiate " + classname;
					i++;
					continue;
				}
				
        /* Check the compatibility of the IVCT Version of TC.exec an TC.lib in a dedicated class */
        logger.info("check the IVCT-Version with which testcase is build - against IVct-version of Tc.exec");
        //logger.info("### testCase.getIVCTVersion:"+testCase.getIVCTVersion()+"  Factory.getVersion: " +Factory.getVersion()); // Debug

        try {
          new IVCTVersionCheck(testCase.getIVCTVersion(), Factory.getVersion() ).compare();;
        } catch (IVCTVersionCheckFailed cf) {
          logger.error("IVCTVersionCheck shows incompability of Version ");
          verdicts[i] = new IVCT_Verdict();
          verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
          verdicts[i].text = "Could not instantiate because of IVCTVersionCheckError " + classname;
          i++;
          cf.printStackTrace();
          continue;
        }
        
				
				testCase.setSutName(info.sutName);
				testCase.setTcName(classname);
				testCase.setSettingsDesignator(info.settingsDesignator);
				testCase.setFederationName(info.federationName);
				testCase.setSutFederateName(info.sutFederateName);

				verdicts[i++] = testCase.execute(info.testCaseParam.toString(), logger);
			}

			// The JMSLogSink waits on this message!
			// The following pair of lines will cause the JMSLogSink to close the log file!
			MDC.put("tcStatus", "ended");
			logger.info("Test Case Ended");

			for (i = 0; i < testcases.length; i++) {
				new CmdSendTcVerdict(info.sutName, info.sutDir, info.badge, testcases[i], verdicts[i].verdict.name(),
						verdicts[i].text).execute();
			}
			MDC.put("tcStatus", "inactive");
		}

	}

	@Override
	public void onSetLogLevel(LogLevel level) {
		this.logLevelId = level.name();
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger lo = (ch.qos.logback.classic.Logger) logger;
			switch (level) {
			case ERROR:
				logger.trace("TestEngine:onMessageConsumer:run: error");
				lo.setLevel(Level.ERROR);
				break;
			case WARNING:
				logger.trace("TestEngine:onMessageConsumer:run: warning");
				lo.setLevel(Level.WARN);
				break;
			case INFO:
				logger.trace("TestEngine:onMessageConsumer:run: info");
				lo.setLevel(Level.INFO);
				break;
			case DEBUG:
				logger.trace("TestEngine:onMessageConsumer:run: debug");
				lo.setLevel(Level.INFO);
				break;
			case TRACE:
				logger.trace("TestEngine:onMessageConsumer:run: trace");
				lo.setLevel(Level.TRACE);
				break;
			}
		}

	}

	@Override
	public void onQuit() {
		System.exit(0);
	}

	@Override
	public void onStartTestCase(TcInfo info) {
		this.testCaseId = new String(info.testCaseId);
		Thread th1 = new Thread(new TestScheduleRunner(info, this));
		th1.start();
	}
}
