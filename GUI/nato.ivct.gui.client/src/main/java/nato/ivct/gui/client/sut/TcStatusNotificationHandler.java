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

import java.io.InputStream;
import java.time.LocalTime;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTTcExecutionForm;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.ProgressImageField;
import nato.ivct.gui.shared.sut.TcStatusNotification;

public class TcStatusNotificationHandler implements INotificationHandler<TcStatusNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TcStatusNotification notification) {
		// inform client about test case verdict
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Test Case Status Notification " + notification.getTc() + " is at "
						+ notification.getPercent() + "%");
//				System.out.println("Test Case Status Notification " + notification.getTc() + " is at " + LocalTime.now().getSecond() + "." + LocalTime.now().getNano()/1000000+") : " + notification.getPercent() + "%");
				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof SuTOutline) {
						SuTTcNodePage tcNP = (SuTTcNodePage) outline.getSelectedNode();
						SuTCbTablePage cbNode = (SuTCbTablePage) tcNP.getParentNode();
						// set TC execution notification in table
						for (ITableRow tr : cbNode.getTable().getRows()) {
							// find row with test case name
							if (cbNode.getTable().getAbstractTCColumn().getValue(tr).equals(notification.getTc())) {
								cbNode.getTable().getTCresultColumn().setValue(tr, notification.getStatus() + ": " + notification.getPercent() + "%");
							}
						}
						// set TC execution notification in detail form
//						((SuTTcExecutionForm) tcNP.getDetailForm()).getTestCaseExecutionStatusField().setValue(notification.getStatus() + " (" + LocalTime.now().getSecond() + "." + LocalTime.now().getNano()/1000000+") : " + notification.getPercent() + "%");
						((SuTTcExecutionForm) tcNP.getDetailForm()).getTestCaseExecutionStatusField().setValue(notification.getStatus() + " : " + notification.getPercent() + "%");
						
						// Test for a progress bar
//						try (InputStream in = ResourceBase.class
//								.getResourceAsStream("icons/" + "NATO_logo" + ".png")) {
//							SuTTcExecutionForm df = (SuTTcExecutionForm) tcNP.getDetailForm();
//							ProgressImageField pif = ((SuTTcExecutionForm) tcNP.getDetailForm()).getProgressImageField();
//							pif.setImage(IOUtility.readBytes(in));
//							pif.setImageId("NATO_logo");
//							pif.doRotate(notification.getPercent());
////							pif.doZoom(notification.getPercent(), notification.getPercent());
////						getCbImageField().setImageId(formData.getCbId());
//						} catch (Exception e) {
//							logger.warn("Could not load progress image file");
//						}

					}
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
