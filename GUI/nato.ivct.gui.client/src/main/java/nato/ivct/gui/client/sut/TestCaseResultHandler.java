package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.shared.notification.INotificationHandler;

import nato.ivct.gui.shared.sut.TestCaseNotification;

public class TestCaseResultHandler implements INotificationHandler<TestCaseNotification> {

	@Override
	public void handleNotification(TestCaseNotification notification) {
		if (notification.getTc() == null) {
			return;
		} else {
			// model job needs to be created

		}
	}
}
