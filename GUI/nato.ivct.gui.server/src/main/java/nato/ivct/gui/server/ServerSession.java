package nato.ivct.gui.server;

import java.io.IOException;
import java.util.concurrent.Callable;

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
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.Factory;
import nato.ivct.gui.shared.sut.TestCaseNotification;

/**
 * <h3>{@link ServerSession}</h3>
 *
 * @author hzg
 */
public class ServerSession extends AbstractServerSession {

	private IFuture<CmdListSuT> loadSuTJob = null;
	private IFuture<CmdListBadges> loadBadgesJob = null;
	private IFuture<CmdStartTc> startTcJobs = null;
	private IFuture<CmdStartTestResultListener> testResultListener = null;
	private ResultListener sessionResultListener;
	Factory ivctCmdFactory = new Factory();

	/*
	 * Load SuT descriptions job
	 */
	public class LoadSuTdescriptions implements Callable<CmdListSuT> {

		@Override
		public CmdListSuT call() throws Exception {
			CmdListSuT sut;
			sut = ivctCmdFactory.createCmdListSut();
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
			// TODO Auto-generated method stub
			CmdListBadges badges;
			badges = ivctCmdFactory.createCmdListBadges();
			badges.execute();
			return badges;
		}

	}

	public class ResultListener implements OnResultListener {

		@Override
		public void OnResult(TcResult result) {
			// TODO Auto-generated method stub
			TestCaseNotification notification = new TestCaseNotification();
			notification.setTc(result.tc);
			notification.setVerdict(result.verdict);
			notification.setText(result.text);
			BEANS.get(ClientNotificationRegistry.class).putForAllNodes(notification);
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
			resultCmd = ivctCmdFactory.createCmdStartTestResultListener(resultListener);
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
		private String runFolder;

		public ExecuteTestCase(String _sut, String _tc, String _badge, String _runFolder) {
			sut = _sut;
			tc = _tc;
			badge = _badge;
			runFolder = _runFolder;
		}

		@Override
		public CmdStartTc call() throws Exception {
			CmdStartTc tcCmd = ivctCmdFactory.createCmdStartTc(sut, badge, tc, runFolder);
			tcCmd.execute();
			return null;
		}

	}

	public class ExecuteSetLogLevel implements Callable<CmdSetLogLevel> {

		private String logLevel;

		public ExecuteSetLogLevel(String level) {
			logLevel = level;
		}

		@Override
		public CmdSetLogLevel call() throws Exception {
			CmdSetLogLevel setCmd = new CmdSetLogLevel(logLevel);
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
		ivctCmdFactory = new Factory();
		try {
			ivctCmdFactory.initialize();
		} catch (IOException e) {
			e.printStackTrace();
		}

		LOG.info("load SuT Information");
		loadSuTJob = Jobs.schedule(new LoadSuTdescriptions(), Jobs.newInput());

		LOG.info("load Badge Descriptions");
		loadBadgesJob = Jobs.schedule(new LoadBadgeDescriptions(), Jobs.newInput());

		LOG.info("start test case Result Listener");
		sessionResultListener = new ResultListener();
		testResultListener = Jobs.schedule(new TestResultListener(sessionResultListener), Jobs.newInput());
	}

	public IFuture<CmdListSuT> getCmdJobs() {
		return loadSuTJob;
	}

	public IFuture<CmdListBadges> getLoadBadgesJob() {
		return loadBadgesJob;
	}

	public void execStartTc(String sut, String tc, String badge, String runFolder) {
		LOG.info("starting test case");
		startTcJobs = Jobs.schedule(new ExecuteTestCase(sut, tc, badge, runFolder), Jobs.newInput());

	}

	public void setLogLevel (String level) {
		LOG.info("set log level");
		Jobs.schedule(new ExecuteSetLogLevel(level), Jobs.newInput());

	}

}