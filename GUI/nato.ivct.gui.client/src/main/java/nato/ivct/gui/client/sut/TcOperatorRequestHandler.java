/* Copyright 2020, Felix Schoeppenthau (Fraunhofer IOSB)

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

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.shared.sut.TcOperatorRequestNotification;


public class TcOperatorRequestHandler implements INotificationHandler<TcOperatorRequestNotification> {
    
    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

    public void handleNotification(TcOperatorRequestNotification notification) {
        final IDesktop desktop = ClientSessionProvider.currentSession().getDesktop();
        final IOutline outline = desktop.getOutline();
        if (!(outline instanceof SuTOutline))
            return;

        ModelJobs.schedule(() -> {
            Desktop.CURRENT.get().findForms(SuTTcExecutionForm.class).forEach(form -> {
                if (form.getTestCaseId().equalsIgnoreCase(notification.getTestCaseId())) {
    
                    // show the TC operator messages
                    form.openPopup(notification.getSutName(), notification.getTestSuiteId(), notification.getTestCaseId(), notification.getOperatorMessage());
    
                }
            });
        }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
    }
}
