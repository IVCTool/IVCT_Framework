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
import nato.ivct.gui.shared.sut.SuTTcRequirementFormData;
import nato.ivct.gui.shared.sut.TcVerdictNotification;

public class TcVerdictNotificationHandler implements INotificationHandler<TcVerdictNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TcVerdictNotification notification) {
		// inform client about test case verdict
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Test Case Notification " + notification.getVerdict() + " received for "
						+ notification.getTcId());

				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof SuTOutline) {
						// set TC verdict in table
						SuTTcNodePage tcNP = (SuTTcNodePage) outline.getSelectedNode();
						SuTCbTablePage sutCbNode = (SuTCbTablePage) tcNP.getParentNode();
						for (ITableRow tr : sutCbNode.getTable().getRows()) {
							// find row with test case name
							if (sutCbNode.getTable().getAbstractTCColumn().getValue(tr).equals(notification.getTcId())) {

								// set verdict in node page table
								sutCbNode.getTable().getTCresultColumn().setValue(tr,notification.getVerdict());
								
								// update log table and set verdict in detailed form 
								SuTTcRequirementForm detailedForm = (SuTTcRequirementForm) tcNP.getDetailForm();
								if (detailedForm != null
									&& detailedForm.isFormStarted()
									&& detailedForm.getTestCaseId().equals(notification.getTcId())) {
									
									//update log file table
									ISuTTcService service = BEANS.get(ISuTTcService.class);
									SuTTcRequirementFormData formData = new SuTTcRequirementFormData();
									detailedForm.exportFormData(formData);
									formData = service.updateLogFileTable(formData);
									detailedForm.importFormData(formData);
									
									// set verdict
									detailedForm.getTestCaseExecutionStatusTableField().setValue(notification.getVerdict());
								}
							}
						}
						
						// set TC verdict in TC execution form
						Desktop.CURRENT.get().findForms(SuTTcExecutionForm.class).forEach(form->{
							// set TC execution notification in detail form
							if (form.getSutId().equalsIgnoreCase(notification.getSutId()) 
								&& form.getTestCaseId().equalsIgnoreCase(notification.getTcId())) {
								ITable tbl = ((SuTTcExecutionForm) form).getTestCaseExecutionStatusTableField().getTable();
								tbl.discardAllRows();

								// set verdict
								ITableRow row = tbl.addRow();
								((Table) tbl).getTcStatusColumn().setValue(row, notification.getVerdict());
								
								// set execution progress
								((Table) tbl).getProgressColumn().setValue(row, null);
								
								//record status and progress in the form
								((SuTTcExecutionForm) form).setTestCaseVerdict(notification.getVerdict());
								((SuTTcExecutionForm) form).setTestCaseProgress(null);
							}
						});
						
						// set TC verdict in detail form
//						((SuTTcExecutionForm) tcNP.getDetailForm()).getTestCaseExecutionStatusField().setValue(notification.getVerdict());

//						ITable tbl = ((SuTTcRequirementForm) tcNP.getDetailForm()).getTestCaseExecutionStatusTableField().getTable();
//						tbl.discardAllRows();
//						ITableRow row = tbl.addRow();
//						// set verdict
//						((Table) tbl).getTcStatusColumn().setValue(row, notification.getVerdict());
					}
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
