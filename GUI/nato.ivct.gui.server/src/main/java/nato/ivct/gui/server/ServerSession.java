package nato.ivct.gui.server;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.server.AbstractServerSession;
import org.eclipse.scout.rt.server.session.ServerSessionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.Factory;

/**
 * <h3>{@link ServerSession}</h3>
 *
 * @author hzg
 */
public class ServerSession extends AbstractServerSession {

	private IFuture<CmdListSuT> loadSuTJob = null;
	private IFuture<CmdListBadges> loadBadgesJob = null;
	Factory ivctCmdFactory = new Factory();

	public class LoadSuTdescriptions implements Callable<CmdListSuT> {

		@Override
		public CmdListSuT call() throws Exception {
			CmdListSuT sut;
			sut = ivctCmdFactory.createCmdListSut();
			sut.execute();
			return sut;
		}
	}

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
	}

	public IFuture<CmdListSuT> getCmdJobs() {
		return loadSuTJob;
	}

	public IFuture<CmdListBadges> getLoadBadgesJob() {
		return loadBadgesJob;
	}

}
