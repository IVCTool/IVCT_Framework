package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.badges.BadgeOutline;
import nato.ivct.gui.shared.sut.CapabilityTablePageData.CapabilityTableRowData;
import nato.ivct.gui.shared.sut.TestCaseNotification;

public class TestCaseNotificationHandler implements INotificationHandler<TestCaseNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TestCaseNotification notification) {
		// inform client about test case verdict
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.info("Test Case Notification " + notification.getVerdict() + " received for "
						+ notification.getTc());
				// CapabilityTablePage cTP =
				// BEANS.get(CapabilityTablePage.class);
				// for (ITableRow tr : cTP.getTableRowsFor(null)) {
				// Object value0 = tr.getCellValue(0);
				// }

				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof BadgeOutline) {
						if (outline.getActivePage() instanceof CapabilityTablePage) {
							CapabilityTablePage cTP = (CapabilityTablePage) outline.getActivePage();
							for (ITableRow tr : cTP.getTable().getRows()) {
								tr.touch();
//								if (tr instanceof CapabilityTableRowData) {
//									CapabilityTableRowData cTR = (CapabilityTableRowData) tr;
//									String bid = cTR.getBadgeId();
//								}
							}
						}
					}
				}

				for (ITableRow tr : CapabilityTablePage.currentTcArray) {
					tr.setCellValue(4, notification.getVerdict());
					tr.setBackgroundColor("00FF00");
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));

	}

}
