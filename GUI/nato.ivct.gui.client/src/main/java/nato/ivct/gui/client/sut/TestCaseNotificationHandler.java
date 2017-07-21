package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.shared.sut.TestCaseNotification;

public class TestCaseNotificationHandler implements INotificationHandler<TestCaseNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TestCaseNotification notification) {
		if (notification.getTc() == null) {
			return;
		} else {
			logger.info("Test Case Notification " + notification.getVerdict() + " received for " + notification.getTc());
			// Trigger client ui update

			// according to the scout manual this should be done, but
			// copyCurrent does not provide valid ModelJob!
			// ModelJobs.schedule(new IRunnable() {
			// @Override
			// public void run() throws Exception {
			// for (ITableRow tr : CapabilityTablePage.currentTcArray) {
			//
			// tr.setCellValue(4, notification.getVerdict());
			//
			// }
			// }
			// }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));

		}
	}

}
