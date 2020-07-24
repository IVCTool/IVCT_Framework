/* Copyright 2020, Michael Theis, Felix Schoeppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.client.sut;

import java.util.Objects;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.html.HTML;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.TcVerdictNotification;


public class TcVerdictNotificationHandler implements INotificationHandler<TcVerdictNotification> {

    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    // verdicts
    public static final String PASSED_VERDICT       = "PASSED";
    public static final String INCONCLUSIVE_VERDICT = "INCONCLUSIVE";
    public static final String FAILED_VERDICT       = "FAILED";
    public static final String NOT_RUN_VERDICT      = "NOT_RUN";

    @Override
    public void handleNotification(TcVerdictNotification notification) {
        // inform client about test case verdict
        ModelJobs.schedule(new IRunnable() {
            @Override
            public void run() throws Exception {
                // set TC verdict in TC execution form
                final IDesktop desktop = ClientSessionProvider.currentSession().getDesktop();
                final IOutline outline = desktop.getOutline();
                if (outline instanceof SuTOutline) {
                    desktop.findForms(SuTTcExecutionForm.class).stream().filter(form -> form.getSutId().equalsIgnoreCase(notification.getSutId()) && form.getTestCaseId().equalsIgnoreCase(notification.getTcId())).forEach(form -> {
                        logger.trace("Test Case Notification {}", notification.getVerdict() + " received for TC " + notification.getTcId() + "and SuT " + notification.getSutId());

                        // set verdict
                        form.setTestCaseVerdict(notification.getVerdict());
                        form.getTcExecutionStatus().setValue(notification.getVerdict());

                        // show the TC execution status in a message box and set the color of the test result in TC Status
                        switch (Objects.toString(notification.getVerdict(), "")) {
                            case PASSED_VERDICT:
                                form.getTcExecutionStatus().setForegroundColor("0DAF66");
                                MessageBoxes.createOk().withHeader(TEXTS.get("TCExecutionStatus") + "\n\n" + notification.getVerdict()).withHtml(HTML.fragment(HTML.italic(TEXTS.get("TestcaseId") + ": " + notification.getTcId()))).withAutoCloseMillis(30000).show();
                                break;
                            case INCONCLUSIVE_VERDICT:
                                form.getTcExecutionStatus().setForegroundColor("997bb7");
                                MessageBoxes.createOk().withHeader(TEXTS.get("TCExecutionStatus") + "\n\n" + notification.getVerdict()).withHtml(HTML.fragment(HTML.italic(TEXTS.get("TestcaseId") + ": " + notification.getTcId()))).withAutoCloseMillis(30000).show();
                                break;
                            case FAILED_VERDICT:
                                form.getTcExecutionStatus().setForegroundColor("db3d57");
                                MessageBoxes.createOk().withHeader(TEXTS.get("TCExecutionStatus") + "\n\n" + notification.getVerdict()).withHtml(HTML.fragment(HTML.italic(TEXTS.get("TestcaseId") + ": " + notification.getTcId()))).withAutoCloseMillis(30000).show();
                                break;
                        }

                        //update log file table
                        final ISuTTcService service = BEANS.get(ISuTTcService.class);
                        SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
                        form.exportFormData(formData);
                        formData = service.updateLogFileTable(formData);
                        form.importFormData(formData);

                        // set result color in the execution history table
                        form.setTestResultColor();

                        // mark the first line of the TC History Table to focus the currently executed test case
                        form.getTcExecutionHistoryTableField().getTable().selectFirstRow();

                        // set execution status color of the tc tile
                        setTcVerdictColor(form.getSutId(), form.getBadgeId(), form.getTestsuiteId(), form.getTestCaseId(), notification.getVerdict());

                        // show TC Execution History Table after TC execution
                        form.getTcExecutionHistoryTableField().setVisible(true);

                        // show TC execution button
                        form.getTcExecutionButton().setVisible(true);
                        
                        // show Close button
                        form.getCloseButton().setVisible(true);
                        
                        // hide Abort button
                        form.getTcAbortButton().setVisible(false);

                        // reset progress bar
                        form.setTestCaseProgress(0);
                    });
                }
            }
        }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
    }


    private void setTcVerdictColor(String sutId, String badgeId, String tsId, String tcId, String tcVerdict) {
        final SuTCbForm form = (SuTCbForm) Desktop.CURRENT.get().getPageDetailForm();
        form.setTcTilecolor(tsId, tcId, tcVerdict);
    }
}
