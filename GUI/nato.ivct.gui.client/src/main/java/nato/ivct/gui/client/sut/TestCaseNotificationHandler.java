package nato.ivct.gui.client.sut;

import javax.swing.text.StyleConstants.ColorConstants;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.shared.sut.TestCaseNotification;

public class TestCaseNotificationHandler implements INotificationHandler<TestCaseNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TestCaseNotification notification) {
		// inform client about test case verdict
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Test Case Notification " + notification.getVerdict() + " received for "
						+ notification.getTc());

				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof SuTOutline) {
						SuTTcNodePage tcNP = (SuTTcNodePage) outline.getSelectedNode();
						SuTCbTablePage sutCbNode = (SuTCbTablePage) tcNP.getParentNode();
						// set TC execution status in table
						for (ITableRow tr : sutCbNode.getTable().getRows()) {
							// find row with test case name
							if (sutCbNode.getTable().getAbstractTCColumn().getValue(tr).equals(notification.getTc())) {
								sutCbNode.getTable().getTCresultColumn().setValue(tr,notification.getVerdict());
								tr.setBackgroundColor(ResourceBase.getVerdictColor(notification.getVerdict()));
								// set font color to dark black if background color changed due to TC status
								tr.setForegroundColor("000000");
							}
						}
						
						// set TC execution status in detail form
						((SuTTcExecutionForm) tcNP.getDetailForm()).getTestCaseExecutionStatusField().setValue(notification.getVerdict());
					}
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
