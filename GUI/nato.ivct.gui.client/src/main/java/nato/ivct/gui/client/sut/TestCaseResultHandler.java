package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;

import nato.ivct.gui.shared.sut.TestCaseNotification;


public class TestCaseResultHandler implements INotificationHandler<TestCaseNotification> {

	@Override
	public void handleNotification(TestCaseNotification notification) {
		// TODO Auto-generated method stub
		ModelJobs.schedule(new IRunnable() {

			@Override
			public void run() throws Exception {

				String dosomething = "empty";
				
//				List<ITableRow> tcArray = getSelectedRows();
//				for (ITableRow tr : tcArray) {
//					List<Object> tc = tr.getKeyValues();
//					tr.setCellValue(4, "starting");
//					tr.setBackgroundColor("FFA500"); // depending on TC result
//					for (Object o : tc) {
//						String s = o.toString();
//					}
//				}
				
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));	
	}

}
