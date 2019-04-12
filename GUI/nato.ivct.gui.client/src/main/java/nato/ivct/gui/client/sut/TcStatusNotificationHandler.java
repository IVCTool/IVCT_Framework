/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

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
import org.eclipse.scout.rt.client.ui.basic.table.ITable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TestCaseExecutionStatusTableField.Table;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.TcStatusNotification;

public class TcStatusNotificationHandler implements INotificationHandler<TcStatusNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TcStatusNotification notification) {
		// inform client about test case progress
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Test Case Status Notification " + notification.getTc() + " is at "
						+ notification.getPercent() + "%");
				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof SuTOutline) {
						// set TC execution status in table
						SuTTcNodePage tcNP = (SuTTcNodePage) outline.getSelectedNode();
						SuTCbTablePage cbNode = (SuTCbTablePage) tcNP.getParentNode();
						for (ITableRow tr : cbNode.getTable().getRows()) {
							// find row with test case name
							if (cbNode.getTable().getAbstractTCColumn().getValue(tr).equals(notification.getTc())) {
								cbNode.getTable().getTCresultColumn().setValue(tr, notification.getStatus() + ": " + notification.getPercent() + "%");
							}
						}

						// set TC execution status in TC execution form
						Desktop.CURRENT.get().findForms(SuTTcExecutionForm.class).forEach(form->{
							// set TC execution status in detail form
							if (notification.getTc().endsWith(((SuTTcExecutionForm) form).getTestCaseId())) {
								ITable tbl = ((SuTTcExecutionForm) form).getTestCaseExecutionStatusTableField().getTable();
								tbl.discardAllRows();
								ITableRow row = tbl.addRow();
								// set verdict
								((Table) tbl).getTcStatusColumn().setValue(row, notification.getStatus());
								
								// set execution progress
								((Table) tbl).getProgressColumn().setValue(row, notification.getPercent());
								
								// update the TC log
								SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
								((SuTTcExecutionForm) form).exportFormData(formData);
								formData = BEANS.get(ISuTTcService.class).loadLogFileContent(formData, ((SuTTcExecutionForm) form).getTestCaseId());
								((SuTTcExecutionForm) form).importFormData(formData);
								
								//record status and progress in the form
								((SuTTcExecutionForm) form).setTestCaseStatus(notification.getStatus());
								((SuTTcExecutionForm) form).setTestCaseProgress(Integer.toString(notification.getPercent()));
							}
						});
					}
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
