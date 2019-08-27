package nato.ivct.gui.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.server.AbstractServerSession;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.eclipse.scout.rt.server.session.ServerSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdLogMsgListener.LogMsg;
import nato.ivct.commander.CmdLogMsgListener.OnLogMsgListener;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.CmdTcStatusListener.OnTcStatusListener;
import nato.ivct.commander.CmdTcStatusListener.TcStatus;
import nato.ivct.commander.Factory;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;
import nato.ivct.gui.shared.sut.TcStatusNotification;
import nato.ivct.gui.shared.sut.TcVerdictNotification;

/**
 * <h3>{@link ServerSession}</h3>
 *
 * @author hzg
 */
public class ServerSession extends AbstractServerSession {
	
	private static final Pattern RESULT_EXP = Pattern.compile("^.*?:\\s+(.*?)\\s+(.*?)\\s.*?([^()\\s]*?/[^()\\s]*?\\.log)\\)?\\s*$");   // (".*?:\\s+(.*?)\\s+(.*?)\\s.*\\(([^(]*?)\\)\\s*");
	private static final Pattern VERDICT_LINE = Pattern.compile("^\\s*?VERDICT:\\s.*", Pattern.CASE_INSENSITIVE);

	private IFuture<CmdListSuT> loadSuTJob = null;
	private IFuture<SutTcResultDescription> loadTcResultsJob = null;
	private IFuture<CmdListBadges> loadBadgesJob = null;
	private IFuture<CmdStartTc> startTcJobs = null;
	private IFuture<CmdStartTestResultListener> testResultListener = null;
	private ResultListener sessionResultListener;
	private StatusListener statusListener;
	private LogMsgListener logMsgListener;

	public class SutTcResultDescription {
		
		/*
		 * Result map of the form (sutId, (badgeId, (logFile, verdict))). All elements are from type String
		 */
		public HashMap<String, HashMap<String, HashMap<String, String>>> sutResultMap = new HashMap<String, HashMap<String, HashMap<String, String>>>();
	}
	
	
	/*
	 * Load SuT descriptions job
	 */
	public class LoadSuTdescriptions implements Callable<CmdListSuT> {

		@Override
		public CmdListSuT call() throws Exception {
			CmdListSuT sut;
			sut = Factory.createCmdListSut();
			sut.execute();
			return sut;
		}
	}

	/*
	 * Load Badge descriptions job
	 */
	public class LoadBadgeDescriptions implements Callable<CmdListBadges> {

		@Override
		public CmdListBadges call() throws Exception {
			CmdListBadges badges;
			badges = Factory.createCmdListBadges();
			badges.execute();
			return badges;
		}

	}
	
	/*
	 * Load TC execution results
	 */
	public class LoadTcResults implements Callable<SutTcResultDescription> {
		
		@Override
		public SutTcResultDescription call() throws Exception {
			SutTcResultDescription sutTcResults = new SutTcResultDescription();
			
			// get SuT list
			final List<String> sutList = Factory.getSutPathsFiles().getSuts();
			// iterate over all SuTs to get its report files
			sutList.forEach(sutId -> {
				final List<String> reportFiles = Factory.getSutPathsFiles().getSutReportFileNames(sutId, true);
				//parse each report file to get the verdict and the corresponding log file name
				reportFiles.forEach(reportFile -> {
					LOG.info("parse report file: {}\n", reportFile);
					try {
						Files.lines(Paths.get(reportFile))
						.filter(line -> VERDICT_LINE.matcher(line).matches())
						.forEach(verdictLine -> {
							LOG.info("\tparse verdict line: {}\n", verdictLine);
							// extract the required elements from the verdict line
							final String[] result = parseVerdictLine(verdictLine);
							if (result.length > 0) {
								final List<String> logFiles = Factory.getSutPathsFiles().getSutLogFileNames(sutId, result[2]);
								// get the matching log file from the list
								final Optional<String> matchingFileName = logFiles.stream().filter(fileName -> fileName.contains(result[3])).findFirst();
								if (matchingFileName.isPresent()) {
									// add the match to the result map
									sutTcResults.sutResultMap.computeIfAbsent(sutId, k -> new HashMap<>()).computeIfAbsent(result[2], k -> new HashMap<>()).put(matchingFileName.get(), result[1]);
								}
							}
						});
					} catch (NoSuchFileException exc) {
			            LOG.info("report file not found: {}", reportFile);
					} catch (IOException exc) {
						exc.printStackTrace();
					}
				});
			});
			
			return sutTcResults;
		}
		
		/*
		 * Parse the verdict string of a report file
		 * @param line the line to parse
		 * @return a String array with 4 element with:
		 *         [0]: extended test case name
		 *         [1]: verdict
		 *         [2]: badge id
		 *         [3]: log file name
		 */
		private  String[] parseVerdictLine(final String line) {
			Matcher matcher = RESULT_EXP.matcher(line);
			
			return matcher.matches() ? matcher.replaceAll("$1 $2 $3").replace('/', ' ').split(" ") : new String[0];
		}
	}
	
	public void updateSutResultMap(final String sutId, final String badgeId, final String tcFullName) {
		LOG.info("reload test results for all SuTs");
		loadTcResultsJob = Jobs.schedule(new LoadTcResults(), Jobs.newInput());
	}

	public class ResultListener implements OnResultListener {

		@Override
		public void onResult(TcResult result) {
			TcVerdictNotification notification = new TcVerdictNotification();
			notification.setSutId(result.sutName);
			notification.setTcId(result.testcase);
			notification.setVerdict(result.verdict);
			notification.setText(result.verdictText);

			BEANS.get(ClientNotificationRegistry.class).putForAllSessions(notification);
		}

	}

	public class StatusListener implements OnTcStatusListener {

		@Override
		public void onTcStatus(TcStatus status) {
			TcStatusNotification notification = new TcStatusNotification();
			notification.setSutId(status.sutName);
			notification.setTcId(status.tcName);
			notification.setPercent(status.percentFinshed);
			notification.setStatus(status.status);

			BEANS.get(ClientNotificationRegistry.class).putForAllSessions(notification);
		}

	}
	
	public class LogMsgListener implements OnLogMsgListener {

		@Override
		public void onLogMsg(LogMsg logMsg) {
			TcLogMsgNotification notification = new TcLogMsgNotification();
			notification.setSutId(logMsg.sut);
			notification.setTcId(logMsg.tc);
			notification.setBadgeId(logMsg.badge);
			notification.setLogMsg(logMsg.txt);
			notification.setLogLevel(logMsg.level);
			notification.setTimeStamp(logMsg.time);
			
			BEANS.get(ClientNotificationRegistry.class).putForAllSessions(notification);
		}
		
	}

	/*
	 * Wait for test case results job
	 */
	public class TestResultListener implements Callable<CmdStartTestResultListener> {

		private CmdStartTestResultListener resultCmd;
		private ResultListener resultListener;

		public TestResultListener(ResultListener listener) {
			resultListener = listener;
		}

		@Override
		public CmdStartTestResultListener call() throws Exception {
			resultCmd = Factory.createCmdStartTestResultListener(resultListener);
			resultCmd.execute();
			return resultCmd;
		}
	}

	/*
	 * Execute test case job
	 */
	public class ExecuteTestCase implements Callable<CmdStartTc> {
		private String sut;
		private String tc;
		private String badge;
		private String settingsDesignator;
		private String federationName;
		private String federateName;

		public ExecuteTestCase(String _sut, String _tc, String _badge, String _settingsDesignator, String _federationName, String _federateName) {
			sut = _sut;
			tc = _tc;
			badge = _badge;
			settingsDesignator = _settingsDesignator;
			federationName = _federationName;
			federateName = _federateName;
		}

		@Override
		public CmdStartTc call() throws Exception {
			CmdStartTc tcCmd = Factory.createCmdStartTc(sut, badge, tc, settingsDesignator, federationName, federateName);
			tcCmd.execute();
			return null;
		}

	}

	public class ExecuteSetLogLevel implements Callable<CmdSetLogLevel> {

		private LogLevel logLevel;

		public ExecuteSetLogLevel(String level) {
			switch (level==null ? "" : level) {
			case "debug":
				logLevel = LogLevel.DEBUG;
				break;
			case "info":
				logLevel = LogLevel.INFO;
				break;
			case "warn":
				logLevel = LogLevel.WARNING;
				break;
			case "error":
				logLevel = LogLevel.ERROR;
				break;
			case "trace":
			default:
				logLevel = LogLevel.TRACE;
				break;
			}
		}

		@Override
		public CmdSetLogLevel call() throws Exception {
			CmdSetLogLevel setCmd = Factory.createCmdSetLogLevel(logLevel);
			setCmd.execute();
			return null;
		}

	}

	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	public ServerSession() {
		super(true);
	}

	/**
	 * @return The {@link ServerSession} which is associated with the current
	 *         thread, or {@code null} if not found.
	 */
	public static ServerSession get() {
		return ServerSessionProvider.currentSession(ServerSession.class);
	}

	@Override
	protected void execLoadSession() {
		LOG.info("created a new session for {}", getUserId());
		Factory.initialize();

		LOG.info("load SuT Information");
		loadSuTJob = Jobs.schedule(new LoadSuTdescriptions(), Jobs.newInput());
		
		LOG.info("load test results for all SuTs");
		loadTcResultsJob = Jobs.schedule(new LoadTcResults(), Jobs.newInput());

		LOG.info("load Badge Descriptions");
		loadBadgesJob = Jobs.schedule(new LoadBadgeDescriptions(), Jobs.newInput());

		LOG.info("start test case Result Listener");
		sessionResultListener = new ResultListener();
		testResultListener = Jobs.schedule(new TestResultListener(sessionResultListener), Jobs.newInput());

		LOG.info("start test case Status Listener");
		statusListener = new StatusListener();
		(Factory.createCmdTcStatusListener(statusListener)).execute();
		
		LOG.info("start Log Message Listener");
		logMsgListener = new LogMsgListener();
		(Factory.createCmdLogMsgListener(logMsgListener)).execute();

	}

	public IFuture<CmdListSuT> getLoadSuTJob() {
		return loadSuTJob;
	}

	public IFuture<CmdListBadges> getLoadBadgesJob() {
		return loadBadgesJob;
	}
	
	public IFuture<SutTcResultDescription> getLoadTcResultsJob() {
		return loadTcResultsJob;
	}

	public void execStartTc(String sut, String tc, String badge, String settingsDesignator, String federationName, String federateName) {
		LOG.info("starting test case");
		startTcJobs = Jobs.schedule(new ExecuteTestCase(sut, tc, badge, settingsDesignator, federationName, federateName), Jobs.newInput());
	}

	public void setLogLevel(String level) {
		LOG.info("set log level");
		Jobs.schedule(new ExecuteSetLogLevel(level), Jobs.newInput());
	}
}
