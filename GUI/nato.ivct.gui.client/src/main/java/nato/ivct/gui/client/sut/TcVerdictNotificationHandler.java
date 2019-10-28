package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcExecutionHistoryTableField.TcExecutionHistoryTable;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.TcVerdictNotification;


public class TcVerdictNotificationHandler implements INotificationHandler<TcVerdictNotification> {

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    public void handleNotification(TcVerdictNotification notification) {
        // inform client about test case verdict
        ModelJobs.schedule(new IRunnable() {
            @Override
            public void run() throws Exception {
                // set TC verdict in TC execution form
                Desktop.CURRENT.get().findForms(SuTTcExecutionForm.class).stream().filter(form -> form.getSutId().equalsIgnoreCase(notification.getSutId()) && form.getTestCaseId().equalsIgnoreCase(notification.getTcId())).forEach(form -> {
                    logger.trace("Test Case Notification " + notification.getVerdict() + " received for TC " + notification.getTcId() + "and SuT " + notification.getSutId());

                    //                                // set TC execution notification in detail form
                    //                                final ITable tbl = form.getTestCaseExecutionStatusTableField().getTable();
                    //                                tbl.discardAllRows();
                    //
                    // set verdict
                    form.setTestCaseVerdict(notification.getVerdict());
                    form.getTcExecutionStatus().setValue(notification.getVerdict());

                    //update log file table
                    final ISuTTcService service = BEANS.get(ISuTTcService.class);
                    SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
                    form.exportFormData(formData);
                    formData = service.updateLogFileTable(formData);
                    form.importFormData(formData);

                    // remove filter to show all log files
                    final TcExecutionHistoryTable tbl = form.getTcExecutionHistoryTableField().getTable();
                    tbl.getUserFilterManager().removeFilterByKey(tbl.getTcVerdictColumn().getColumnId());

                    // show Execute TC button
                    form.getTcExecutionButton().setVisible(true);

                    // reset progress bar
                    form.setTestCaseProgress(0);
                });

            }
        }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
    }
}
