package de.fraunhofer.iosb.testrunner;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import de.fraunhofer.iosb.tc_lib_if.AbstractTestCaseIf;
import de.fraunhofer.iosb.tc_lib_if.IVCTVersionCheck;
import de.fraunhofer.iosb.tc_lib_if.IVCTVersionCheckException;
import de.fraunhofer.iosb.tc_lib_if.IVCT_Verdict;
import de.fraunhofer.iosb.tc_lib_if.TestSuite;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdHeartbeatSend.OnCmdHeartbeatSend;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.commander.CmdQuitListener;
import nato.ivct.commander.CmdQuitListener.OnQuitListener;
import nato.ivct.commander.CmdSendTcVerdict;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdSetLogLevelListener;
import nato.ivct.commander.CmdSetLogLevelListener.OnSetLogLevelListener;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.CmdStartTcListener.OnStartTestCaseListener;
import nato.ivct.commander.CmdStartTcListener.TcInfo;
import nato.ivct.commander.CmdAbortTcListener;
import nato.ivct.commander.CmdAbortTcListener.OnAbortTestCaseListener;
import nato.ivct.commander.CmdAbortTcListener.TcAbortInfo;
import nato.ivct.commander.Factory;
import nato.ivct.commander.TcLoggerData;

/**
 * Test runner that listens for certain commands to start and stop test cases.
 *
 * @author Manfred Schenk (Fraunhofer IOSB)
 * @author Reinhard Herzog (Fraunhofer IOSB)
 */
public class TestEngine extends TestRunner implements 
	OnSetLogLevelListener, 
	OnQuitListener, 
	OnStartTestCaseListener, 
	OnAbortTestCaseListener, 
	OnCmdHeartbeatSend {

	private AbstractTestCaseIf testCase = null;
	private CmdListTestSuites testSuites;
	private Map<String, URLClassLoader> classLoaders = new HashMap<>();
	// test engine identification label
    private String testEngineLabel;
	// the number of threads in the fixed thread pool
	private static final int MAX_THREADS = 10;
	private ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
	public Map<String,SoftReference<Future<?>>> threadCache = new HashMap<>();

	
	/**
	 * Main entry point from the command line.
	 *
	 * @param args The command line arguments
	 */ 
	public static void main(final String[] args) {
	    startTestEngine();
	}	
	
	
	/**
	 * public constructor.
	 */
	public TestEngine() {
		super();		
	}

	public static void startTestEngine() {
	    new TestEngine().startUp();
	}	

	public void startUp() {
		// initialize the IVCT Commander Factory
		Factory.initialize();		
		
		// Configure the logger
		LogConfigurationHelper.configureLogging();

		// for enhanced heartbeat with RTI-Type-Information brf 22.10.2020
		testEngineLabel = Factory.props.getProperty("TESTENGINE_LABEL") ;		

		// start command listeners
		new CmdSetLogLevelListener(this).execute();
		new CmdStartTcListener(this).execute();
		new CmdQuitListener(this).execute();
		new CmdAbortTcListener(this).execute();
		try {
			(new CmdHeartbeatSend(this)).execute();
		} catch (Exception e1) {
			Set<Logger> loggers = TcLoggerData.getLoggers();
			for (Logger entry : loggers) {
				entry.error("Could not start HeartbeatSend: ",e1);
			}	
			if (loggers.size() == 0) {
				System.out.println("Could not start HeartbeatSend: " + e1.toString());
			}	
		}	
		// get the test suite descriptions
		try {
			testSuites = new CmdListTestSuites();
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

		TestScheduleRunner(final TcInfo info) {
			this.info = info;
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
		 * @param directoryName name of directory to be the current directory
		 * @return true if successful
		 */
		private boolean setCurrentDirectory(String directoryName) {
			boolean result = false; // Boolean indicating whether directory was
									// set
			File directory; // Desired current working directory

			directory = new File(directoryName).getAbsoluteFile();
			if (directory.exists()) {
				directory.mkdirs();
				result = (System.setProperty("user.dir", directory.getAbsolutePath()) != null);
			}

			return result;
		}

		private void extendThreadClassLoader(final TestSuiteDescription testSuiteDescription) {
			URLClassLoader classLoader = classLoaders.get(testSuiteDescription.id);
			if (classLoader == null) {
				String tsPath = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID);
				String libPath = tsPath + "/" + testSuiteDescription.tsLibTimeFolder;
				File dir = new File(libPath);
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
			tcLogger.info("TestEngine:onMessageConsumer:run: {}", info.testCaseId);

			// find test suite description for requested test case
			TestSuiteDescription tsd = testSuites.getTestSuiteForTc(info.testCaseId);
			if (tsd == null) {
				tcLogger.error("TestEngine:onMessageConsumer:run: unknown testsuite for testcase: {}", info.testCaseId);
				return;
			}
			String runFolder = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID) + '/' + tsd.tsRunTimeFolder;

			tcLogger.info("TestEngine:onMessageConsumer:run: tsRunFolder is {}", runFolder);
			if (setCurrentDirectory(runFolder)) {
				tcLogger.info("TestEngine:onMessageConsumer:run: setCurrentDirectory true");
			}

			File f = getCwd();
			String tcDir = f.getAbsolutePath();
			tcLogger.info("TestEngine:onMessageConsumer:run: TC DIR is {}", tcDir);

			tcLogger.info("TestEngine:onMessageConsumer:run: The test case class is: {}", info.testCaseId);
			String[] testcases = info.testCaseId.split("\\s");
			
			// run the test cases
			for (final String testCaseName : testcases) {
				IVCT_Verdict verdict = null;
				testCase = null;
				
				// try to find test case from known service loaders
				TestSuite tsLoader = testSuites.tsServiceLoaders.get(info.testSuiteId);
				if (tsLoader != null) {
					tcLogger.debug("test suite {} with class {} found", tsLoader.getId(), tsLoader.getClass());
					testCase = tsLoader.getTestCase(info.testCaseId);
				}
				// if not found by service loaders, try to extend the classpath
				if (testCase == null) {
					extendThreadClassLoader(tsd);
					try {
						testCase = (AbstractTestCaseIf) Thread.currentThread().getContextClassLoader().loadClass(testCaseName)
								.getDeclaredConstructor().newInstance();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException
							| ClassNotFoundException e) {
						tcLogger.error("Error loading class {}. Reason: {}", testCaseName, e.getMessage());
						continue;
					}
				}
				// if test case is still not found, 
				if (testCase == null) {
					verdict = new IVCT_Verdict();
					verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
					verdict.text = "Could not instantiate " + testCaseName;
				} else {
					testCase.setDefaultLogger(tcLogger);
					testCase.setSutName(info.sutName);
					testCase.setTsName(info.testSuiteId);
					testCase.setTcName(testCaseName);
					testCase.setSettingsDesignator(info.settingsDesignator);
					testCase.setFederationName(info.federationName);
					testCase.setSutFederateName(info.sutFederateName);
					testCase.setTcParam(info.testCaseParam);
					testCase.setOperatorService(new OperatorServiceImpl().initialize(info.sutName, info.testCaseId, testCaseName, testEngineLabel));

					/*
					* Check the compatibility of IVCT-Version which had this testCase at
					* building-time against the IVCT-Version at Runtime
					*/
					try {
						tcLogger.debug("TestEngine.run.compabilityCheck: the IVCTVersion of test case {} is: {}", testCase, testCase.getIVCTVersion());
						new IVCTVersionCheck(testCase.getIVCTVersion(),Factory.getVersion()).compare();
					} catch (IVCTVersionCheckException cf) {
						tcLogger.error("TestEngine: IVCTVersionCheck shows problems with IVCTVersion-Check ", cf);
						verdict = new IVCT_Verdict();
						verdict.verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
						verdict.text = "Could not instantiate because of IVCTVersionCheckError " + testCaseName;
					}
					verdict = testCase.execute(tcLogger);
				}
				// send test case verdict 
				tcLogger.info("Test Case Ended");
				new CmdSendTcVerdict(
					info.sutName, 
					info.sutDir, 
					info.testSuiteId, 
					testCaseName,
					verdict.verdict.name(), 
					verdict.text
				).execute();
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
	    executorService.shutdown();
		System.exit(0);
	}
	

	@Override
	public void onStartTestCase(TcInfo info) {
		Logger tcLogger = LoggerFactory.getLogger(info.testCaseId);
		
		// for enhanced RTI-Type-Information brf 06.11.2020
		tcLogger.info("TestEngine.onStartTestCase get TCInfo.testEngineLabel  \"" + info.testEngineLabel  +"\"" );
		tcLogger.info("TestEngine.onStartTestCase get TCInfo.settingsDesignator \""+ info.settingsDesignator +"\"");
		if ( ! verifyTestEngineLabel("onStartTestCase", tcLogger, info.testEngineLabel ) ) {
			return;
		}
		 
		// if the TestEngineLabel is like makRti4.6* but the settingsDesignator is not for MAK RtI, stop here
		if ( info.testEngineLabel.toLowerCase().contains("mak".toLowerCase()) &&  !info.settingsDesignator.toLowerCase().contains("setqb".toLowerCase()) ) {
			tcLogger.warn("TestEngine is started for MAK RTI but possibly got wrong settingsDesignator  ");
		}
     
		Runnable th1 = new TestScheduleRunner(info);
		Future<?> startedThread = executorService.submit(th1);
		threadCache.put(info.testCaseId, new SoftReference<>(startedThread));
		tcLogger.info("Test Case Started: {}", info.testCaseId);
	}
	
   @Override
    public void onAbortTestCase(TcAbortInfo info) {
		Logger tcLogger = LoggerFactory.getLogger(info.testCaseId);
       
		// for  RTI-Type-Information brf 07.12.2020 
		tcLogger.info("TestEngine.onAbortTestCase get TcAbortInfo.testEngineLabel  \"" + info.testEngineLabel  +"\"" );           
		if ( ! verifyTestEngineLabel("onAbortTestCase", tcLogger, info.testEngineLabel ) ) {
			return;
		}
          
		tcLogger.warn("Aborting the test case: {}", info.testCaseId);
		Future<?> threadToAbort = threadCache.get(info.testCaseId).get();
		if (threadToAbort != null && !threadToAbort.isDone() && !threadToAbort.isCancelled()) {
			threadToAbort.cancel(true);
			tcLogger.warn("Test Case Aborted: {}", info.testCaseId);
		} else {
			tcLogger.warn("Test case could not be aborted: {} {}", info.testCaseId, threadToAbort);
			if (threadToAbort != null)
				tcLogger.warn("Thread isDone: {}, Thread is already canceled: {}", threadToAbort.isDone(), threadToAbort.isCancelled());
		}
    }


    /*
	 * Methode to verify if the received test engine label is the own label 
	 */
	private boolean verifyTestEngineLabel(String requestingMethod, Logger tcLogger, String testEngineLabel_) {
        boolean competence = true;
        if (!(testEngineLabel_.equals(testEngineLabel) || testEngineLabel_.equals(Factory.TESTENGINE_LABEL_DEFLT))) {
            tcLogger.info("TestEngine." + requestingMethod + ": This Job is not for this TestEngine - do not perfom\"  ");
            competence = false;
        }
        return competence;
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
	
	// for enhanced heartbeat with RTI-Type-Information brf 22.10.2020
	public String getMyTestEngineLabel() {
		return testEngineLabel;
	}


	
	

}
