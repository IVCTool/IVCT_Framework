package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import ch.qos.logback.classic.Level;
import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import de.fraunhofer.iosb.messaginghelpers.PropertyBasedClientSetup;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSendTcVerdict;
import nato.ivct.commander.CmdStartChangeLogLevelListener;
import nato.ivct.commander.CmdStartChangeLogLevelListener.OnChangeLogLevelListener;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.Factory;

/**
 * Testrunner that listens on a certain JMS queue for commands to start a
 * certain test case.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 */
public class JMSTestRunner extends TestRunner
		implements OnChangeLogLevelListener, OnQuitListener, OnStartTestCaseListener {

	private static Logger logger = LoggerFactory.getLogger(JMSTestRunner.class);
	private PropertyBasedClientSetup jmshelper;

	public String logLevelId = Level.INFO.toString();
	public String testCaseId = "no test case is running";

	private Factory cmdFactory;

	/**
	 * disconnect from JMS
	 */
	public void disconnect() {
		jmshelper.disconnect();
	}

	/**
	 * Main entry point from the command line.
	 *
	 * @param args
	 *            The command line arguments
	 */
	public static void main(final String[] args) {
		// LogConfigurationHelper.configureLogging(JMSTestRunner.class);
		LogConfigurationHelper.configureLogging();
		try {
			final JMSTestRunner runner = new JMSTestRunner();
		} catch (final IOException ex) {
			logger.error(ex.getMessage(), ex);
		}
	}

	/**
	 * public constructor.
	 *
	 * @throws IOException
	 *             problems with loading properties
	 */
	public JMSTestRunner() throws IOException {

		// initialize the IVCT Commander Factory
		cmdFactory = new Factory();
		cmdFactory.initialize();

		// start command listeners
		(new CmdStartChangeLogLevelListener(this)).execute();
		(new CmdStartTcListener(this)).execute();
		(new CmdQuitListener(this)).execute();
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
		 * This method provides a way to set the current working directory which
		 * is not available as such in java.
		 * 
		 * N.B. This method uses a trick to get the desired result
		 *
		 * @param directory_name
		 *            name of directory to be the current directory
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

		public void run() {
			logger.info("JMSTestRunner:onMessageConsumer:run: " + info.testCaseId);
			MDC.put("sutName", info.sutName);
			MDC.put("sutDir", info.sutDir);

			logger.info("JMSTestRunner:onMessageConsumer:run: tsRunFolder is " + info.tsRunFolder);
			if (setCurrentDirectory(info.tsRunFolder)) {
				logger.info("JMSTestRunner:onMessageConsumer:run: setCurrentDirectory true");
			}

			File f = getCwd();
			String tcDir = f.getAbsolutePath();
			logger.info("JMSTestRunner:onMessageConsumer:run: TC DIR is " + tcDir);

			MDC.put("testScheduleName", info.testScheduleName);

			logger.info("JMSTestRunner:onMessageConsumer:run: The test case class is: " + testCaseId);
			String[] testcases = info.testCaseId.split("\\s");
			IVCT_Verdict verdicts[] = new IVCT_Verdict[testcases.length];

			this.testRunner.executeTests(logger, info.testCaseId.split("\\s"), info.testCaseParam.toString(), verdicts);
			for (int i = 0; i < testcases.length; i++) {
				new CmdSendTcVerdict(info.sutName, info.sutDir, info.testScheduleName, testcases[i],
						verdicts[i].toString(), verdicts[i].text).execute();
			}
			logger.debug("JMSTestRunner:onMessageConsumer:run: after");
		}

	}

	@Override
	public void onChangeLogLevel(String level) {
		if (logger instanceof ch.qos.logback.classic.Logger) {
			ch.qos.logback.classic.Logger lo = (ch.qos.logback.classic.Logger) logger;
			switch (level) {
			case "error":
				logger.trace("JMSTestRunner:onMessageConsumer:run: error");
				lo.setLevel(Level.ERROR);
				break;
			case "warning":
				logger.trace("JMSTestRunner:onMessageConsumer:run: warning");
				lo.setLevel(Level.WARN);
				break;
			case "info":
				logger.trace("JMSTestRunner:onMessageConsumer:run: info");
				lo.setLevel(Level.INFO);
				break;
			case "debug":
				logger.trace("JMSTestRunner:onMessageConsumer:run: debug");
				lo.setLevel(Level.INFO);
				break;
			case "trace":
				logger.trace("JMSTestRunner:onMessageConsumer:run: trace");
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
		Thread th1 = new Thread(new TestScheduleRunner(info, this));
		th1.start();
	}
}
