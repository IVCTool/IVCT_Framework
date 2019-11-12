package nato.ivct.gui.server;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.IPlatform.State;
import org.eclipse.scout.rt.platform.IPlatformListener;
import org.eclipse.scout.rt.platform.PlatformEvent;
import org.eclipse.scout.rt.server.clientnotification.ClientNotificationRegistry;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdHeartbeatListen;
import nato.ivct.commander.CmdHeartbeatListen.OnCmdHeartbeatListen;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdLogMsgListener;
import nato.ivct.commander.CmdLogMsgListener.LogMsg;
import nato.ivct.commander.CmdLogMsgListener.OnLogMsgListener;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.CmdTcStatusListener;
import nato.ivct.commander.CmdTcStatusListener.OnTcStatusListener;
import nato.ivct.commander.CmdTcStatusListener.TcStatus;
import nato.ivct.commander.Factory;
import nato.ivct.commander.HeartBeatMsgStatus.HbMsgState;
import nato.ivct.gui.shared.HeartBeatNotification;
import nato.ivct.gui.shared.HeartBeatNotification.HbNotificationState;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;
import nato.ivct.gui.shared.sut.TcStatusNotification;
import nato.ivct.gui.shared.sut.TcVerdictNotification;


public class ServerStartup implements IPlatformListener {
    private static final Logger LOG = LoggerFactory.getLogger(ServerStartup.class);

    private CmdStartTestResultListener tcResultListener;
    private CmdTcStatusListener        tcStatusListener;
    private CmdLogMsgListener          logMsgListener;

    public class ResultListener implements OnResultListener {

        @Override
        public void onResult(TcResult result) {
            final TcVerdictNotification notification = new TcVerdictNotification();
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
            final TcStatusNotification notification = new TcStatusNotification();
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
            final TcLogMsgNotification notification = new TcLogMsgNotification();
            notification.setSutId(logMsg.sut);
            notification.setTcId(logMsg.tc);
            notification.setBadgeId(logMsg.badge);
            notification.setLogMsg(logMsg.txt);
            notification.setLogLevel(logMsg.level);
            notification.setTimeStamp(logMsg.time);

            BEANS.get(ClientNotificationRegistry.class).putForAllSessions(notification);
        }
    }

    public class IvctHeartBeatListener implements OnCmdHeartbeatListen {

        @Override
        public void hearHeartbeat(JSONObject heartBeat) {
            final HeartBeatNotification hbn = new HeartBeatNotification();
            LOG.trace("heartbeat received: {}", heartBeat.toJSONString());

            try {
                final HbMsgState hbMsgState = (HbMsgState) heartBeat.getOrDefault(CmdHeartbeatSend.HB_MESSAGESTATE, HbMsgState.UNKNOWN);
                switch (hbMsgState) {
                    case INTIME:
                        hbn.notifyState = HbNotificationState.OK;
                        break;
                    case TIMEOUT:
                    case WAITING:
                    case ALERT:
                        hbn.notifyState = HbNotificationState.WARNING;
                        break;
                    case FAIL:
                        hbn.notifyState = HbNotificationState.CRITICAL;
                        break;
                    case DEAD:
                        hbn.notifyState = HbNotificationState.DEAD;
                        break;
                    case UNKNOWN:
                    default:
                        hbn.notifyState = HbNotificationState.UNKNOWN;
                }

                hbn.lastSendingPeriod = (long) heartBeat.getOrDefault(CmdHeartbeatSend.HB_LASTSENDINGPERIOD, 0L);
                hbn.alertTime = (String) heartBeat.getOrDefault(CmdHeartbeatSend.HB_ALLERTTIME, "");
                hbn.heartBeatSender = (String) heartBeat.getOrDefault(CmdHeartbeatSend.HB_SENDER, "");
                hbn.lastSendingTime = (String) heartBeat.getOrDefault(CmdHeartbeatSend.HB_LASTSENDINGTIME, "");
                hbn.senderHealthState = (boolean) heartBeat.getOrDefault(CmdHeartbeatSend.HB_SENDERHEALTHSTATE, false);
                hbn.comment = (String) heartBeat.getOrDefault(CmdHeartbeatSend.HB_COMMENT, "");
            }
            catch (final Exception exc) {
                exc.printStackTrace();
            }
            finally {
                // forward the heartbeat info to all registered notification handlers
                BEANS.get(ClientNotificationRegistry.class).putForAllSessions(hbn);
            }
        }
    }


    @Override
    public void stateChanged(PlatformEvent event) {
        if (event.getState() == State.PlatformStarted) {
            Factory.initialize();

            LOG.info("start test case Result Listener");
            (tcResultListener = Factory.createCmdStartTestResultListener(new ResultListener())).execute();

            LOG.info("start test case Status Listener");
            (tcStatusListener = Factory.createCmdTcStatusListener(new StatusListener())).execute();

            LOG.info("start Log Message Listener");
            (logMsgListener = Factory.createCmdLogMsgListener(new LogMsgListener())).execute();

            LOG.info("start heartbeat Listener");
            //      new CmdHeartbeatListen(new IvctHeartBeatListener(), "Use_CmdHeartbeatSend").execute(); // for testing purpose
            //        new CmdHeartbeatListen(new IvctHeartBeatListener(), "TestRunner").execute();
            new CmdHeartbeatListen(new IvctHeartBeatListener(), "TestEngine").execute();
            new CmdHeartbeatListen(new IvctHeartBeatListener(), "LogSink").execute();
        }
    }
}
