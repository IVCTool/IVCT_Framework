package nato.ivct.gui.client.sut;

import java.util.Optional;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.Desktop;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcExecutionLogField;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;

public class TcLogMsgNotificationHandler implements INotificationHandler<TcLogMsgNotification> {
	
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public void handleNotification(TcLogMsgNotification tcLogMsgNotification) {
		// inform client about test case log message
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Test Case log message received for " + tcLogMsgNotification.getTcId());
				for (IOutline outline : Desktop.CURRENT.get().getAvailableOutlines()) {
					if (outline instanceof SuTOutline) {
						// add log message in TC execution form
						Desktop.CURRENT.get().findForms(SuTTcExecutionForm.class).forEach(form->{
							// show only with a log message that is for this SuT and the currently executed test case
							if (tcLogMsgNotification.getLogMsg().length()>0 
								&& form.getSutId().equalsIgnoreCase(tcLogMsgNotification.getSut() ) 
								&& form.getTestCaseId().equalsIgnoreCase(tcLogMsgNotification.getTcId())) {

								// message including level and time stamp
//								String logMsg = String.format("[%s] %s: %s", tcLogMsgNotification.getLogLevel(), tcLogMsgNotification.getTimeStamp(), tcLogMsgNotification.getLogMsg()); 

								// message including only level
								String logMsg = String.format("[%s]: %s", tcLogMsgNotification.getLogLevel(), tcLogMsgNotification.getLogMsg());
								
								SuTTcExecutionFormData formData = (SuTTcExecutionFormData) form.createFormData();
								((SuTTcExecutionForm) form).exportFormData(formData);
								
								// check if sufficient memory is available
								String tcExecLog = Optional.ofNullable(formData.getTcExecutionLog().getValue()).orElse("");
								int configuredLogMsgLength  = ((SuTTcExecutionForm) form).getFieldByClass(TcExecutionLogField.class).getConfiguredMaxLength();
								if (configuredLogMsgLength < Integer.MAX_VALUE) {
									// increase the configured data space if required
									int requiredMsgLength = configuredLogMsgLength + logMsg.length();
									if (configuredLogMsgLength < requiredMsgLength) {
										// increase the memory
										form.getFieldByClass(TcExecutionLogField.class).setMaxLength(requiredMsgLength < Integer.MAX_VALUE ? requiredMsgLength : Integer.MAX_VALUE);
										form.exportFormData(formData);
									}
								}
								
								formData.getTcExecutionLog().setValue((tcExecLog.length()>0?tcExecLog + "\n" : tcExecLog) + logMsg);
								form.importFormData(formData);
							}
						});
					}
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
