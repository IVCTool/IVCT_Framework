package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCTVersionCheck;
import de.fraunhofer.iosb.tc_lib.IVCTVersionCheckException;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdHeartbeatSend.OnCmdHeartbeatSend;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.commander.CmdOperatorConfirmationListener;
import nato.ivct.commander.CmdOperatorConfirmationListener.OnOperatorConfirmationListener;
import nato.ivct.commander.CmdOperatorConfirmationListener.OperatorConfirmationInfo;
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
import nato.ivct.commander.TcLoggerData;

/**
 * Testrunner that listens for certain commands to start and stop test cases.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public class TestEngine extends TestRunner implements OnSetLogLevelListener, OnQuitListener, OnStartTestCaseListener,
		OnCmdHeartbeatSend, OnOperatorConfirmationListener {

	public String logLevelId = Level.INFO.toString();
	public String testCaseId = "no test case is running";
	private AbstractTestCase testCase = null;

	private CmdListTestSuites testSuites;
	private Map<String, URLClassLoader> classLoaders = new HashMap<String, URLClassLoader>();

	/**
	 * Main entry point from the command line.
	 *
	 * @param args The command line arguments
	 */
	public static void main(final String[] args) {
		@SuppressWarnings("unused")
		final TestEngine runner = new TestEngine();
	}

	/**
	 * public constructor.
	 */
	public TestEngine() {

		// set heartbeat identifier
		myClassName = this.getClass().getSimpleName();

		// initialize the IVCT Commander Factory
		Factory.initialize();

		// Configure the logger
		LogConfigurationHelper.configureLogging();

		// start command listeners
		(new CmdSetLogLevelListener(this)).execute();
		(new CmdStartTcListener(this)).execute();
		(new CmdQuitListener(this)).execute();
		try {
			(new CmdHeartbeatSend(this)).execute();
		} catch (Exception e1) {
			Set<Logger> loggers = TcLoggerData.getLoggers();
			for (Logger entry : loggers) {
				entry.error("Could not start HeartbeatSend: " + e1.toString());
			}
			if (loggers.size() == 0) {
				System.out.println("Could not start HeartbeatSend: " + e1.toString());
			}
		}
		(new CmdOperatorConfirmationListener(this)).execute();

		// get the test suite descriptions
		testSuites = new CmdListTestSuites();
		try {
			testSuites.execute();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @author hzg
	 * 
	 *         TestScheduleRunner executes a sequence of test cases
	 *
	 */
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

		private void extendThreadClassLoader(final TestSuiteDescription testSuiteDescription) {
			URLClassLoader classLoader = classLoaders.get(testSuiteDescription.id);
			if (classLoader == null) {
				String ts_path = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID);
				String lib_path = ts_path + "/" + testSuiteDescription.tsLibTimeFolder;
				File dir = new File(lib_path);
				File[] filesList = dir.listFiles();
				if (filesList == null) {
					Set<Logger> loggers = TcLoggerData.getLoggers();
					for (Logger entry : loggers) {
						entry.info("No files found in folder {}", dir.getPath());
					}
					if (loggers.size() == 0) {
						System.out.println("No files found in folder {}" + dir.getPath());
					}
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
				classLoaders.put(testSuiteDescription.id, classLoader);
			}
			Thread.currentThread().setContextClassLoader(classLoader);
		}

		public void run() {
			Logger tcLogger = LoggerFactory.getLogger(info.testCaseId);
			TcLoggerData.addLoggerData(tcLogger, tcLogger.getName(), info.sutName, info.testSuiteId, info.testCaseId);
			tcLogger.info("TestEngine:onMessageConsumer:run: " + info.testCaseId);

			TestSuiteDescription tsd = testSuites.getTestSuiteForTc(info.testCaseId);
			if (tsd == null) {
				tcLogger.error("TestEngine:onMessageConsumer:run: unknown testsuite for testcase: " + info.testCaseId);
				return;
			}
			String runFolder = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID) + '/' + tsd.tsRunTimeFolder;

			tcLogger.info("TestEngine:onMessageConsumer:run: tsRunFolder is " + runFolder);
			if (setCurrentDirectory(runFolder)) {
				tcLogger.info("TestEngine:onMessageConsumer:run: setCurrentDirectory true");
			}

			File f = getCwd();
			String tcDir = f.getAbsolutePath();
			tcLogger.info("TestEngine:onMessageConsumer:run: TC DIR is " + tcDir);

			tcLogger.info("TestEngine:onMessageConsumer:run: The test case class is: " + testCaseId);
			String[] testcases = info.testCaseId.split("\\s");
			IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

			extendThreadClassLoader(tsd);

			int i = 0;
			for (final String classname : testcases) {
				try {
					testCase = (AbstractTestCase) Thread.currentThread().getContextClassLoader().loadClass(classname)
							.newInstance();
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
					tcLogger.error("Could not instantiate " + classname + " !", ex);
				}
				if (testCase == null) {
					verdicts[i] = new IVCT_Verdict();
					verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
					verdicts[i].text = "Could not instantiate " + classname;
					i++;
					continue;
				}
				testCase.setDefaultLogger(tcLogger);
				testCase.setSutName(info.sutName);
				testCase.setTsName(info.testSuiteId);
				testCase.setTcName(classname);
				testCase.setSettingsDesignator(info.settingsDesignator);
				testCase.setFederationName(info.federationName);
				testCase.setSutFederateName(info.sutFederateName);

				/**
				 * Check the compability of IVCT-Version which had this testCase at
				 * building-time against the IVCT-Version at Runtime
				 */

				try {
					tcLogger.debug("TestEngine.run.compabilityCheck: the IVCTVersion of testcase " + testCase + " is: "
							+ testCase.getIVCTVersion()); // Debug

					new IVCTVersionCheck(testCase.getIVCTVersion()).compare();

				} catch (IVCTVersionCheckException cf) {
					tcLogger.error("TestEngine: IVCTVersionCheck shows problems with IVCTVersion-Check ");
					verdicts[i] = new IVCT_Verdict();
					verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
					verdicts[i].text = "Could not instantiate because of IVCTVersionCheckError " + classname;
					i++;
					cf.printStackTrace();
					continue;
				}

				verdicts[i] = testCase.execute(info.testCaseParam, tcLogger);
				tcLogger.info("Test Case Ended");
				new CmdSendTcVerdict(info.sutName, info.sutDir, info.testSuiteId, testcases[i],
						verdicts[i].verdict.name(), verdicts[i].text).execute();
				i++;
			}

			TcLoggerData.removeLogger(tcLogger.getName());
		}

	}

	@Override
	public void onSetLogLevel(LogLevel level) {
		TcLoggerData.setLogLevel(level.name());
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

	@Override
	public void onOperatorConfirmation(OperatorConfirmationInfo operatorConfirmationInfo) {
		testCase.onOperatorConfirmation(operatorConfirmationInfo);
	}

	/*
	 * implement a heartbeat , brf 05.07.2019 (Fraunhofer IOSB) CmdHeartbeatSend
	 * will fetch all 5 Seconds the health state from 'here' and send all 5 Seconds
	 * a message to ActiveMQ So if the value for health is changed here, this will
	 * change the tenor of the message CmdHeartbeatSend sends to ActiveMQ if this
	 * thread is stopped, CmdHeardbeatListen will give out an Alert-Status
	 */

	@Override
	public String getMyClassName() {
		return myClassName;
	}

	@Override
	public boolean getMyHealth() {
		return health;
	}

}
