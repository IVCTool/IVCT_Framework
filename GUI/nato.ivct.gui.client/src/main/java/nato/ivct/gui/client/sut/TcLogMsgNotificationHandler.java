package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;


public class TcLogMsgNotificationHandler implements INotificationHandler<TcLogMsgNotification> {

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void handleNotification(TcLogMsgNotification tcLogMsgNotification) {
        // inform client about test case log message
        ModelJobs.schedule(new IRunnable() {
            @Override
            public void run() throws Exception {
                logger.trace("Test Case log message received for " + tcLogMsgNotification.getTcId());
                final IDesktop desktop = ClientSessionProvider.currentSession().getDesktop();
                final IOutline outline = desktop.getOutline();
                if (outline instanceof SuTOutline) {
                    // add log message in TC execution form
                    desktop.findForms(SuTTcExecutionForm.class).forEach(form -> {
                        // show only with a log message that is for this SuT and the currently executed test case
                        if (tcLogMsgNotification.getLogMsg().length() > 0 && form.getSutId().equalsIgnoreCase(tcLogMsgNotification.getSut()) && form.getTestCaseId().equalsIgnoreCase(tcLogMsgNotification.getTcId())) {

                            final String logLevel = String.format(tcLogMsgNotification.getLogLevel());
                            final String timeStamp = String.format(tcLogMsgNotification.getTimeStamp());
                            final String logMsg = String.format(tcLogMsgNotification.getLogMsg());

                            form.getTcLogField().addLine(logLevel, timeStamp, logMsg);

                        }
                    });
                }
            }
        }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
    }
}
