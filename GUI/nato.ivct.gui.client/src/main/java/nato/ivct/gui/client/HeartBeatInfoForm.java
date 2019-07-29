package nato.ivct.gui.client;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.notification.NotificationHandlerRegistry;

import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.CloseButton;
import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.HbLastSeenField;
import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.StatusField;
import nato.ivct.gui.shared.HeartBeatNotification;
import nato.ivct.gui.shared.OptionsFormData;

@FormData(value = OptionsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class HeartBeatInfoForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("HeartBeatInformation");
	}

	@Override
	protected void execInitForm() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        final Timestamp now = new Timestamp(System.currentTimeMillis());
        
//        ClientSession.CURRENT.get().getData(key)
        HeartBeatNotification hbn = HeartBeatNotificationHandler.hbLastReceivedMap.get("Use_CmdHeartbeatSend");
        if (hbn != null) {
			getFieldByClass(StatusField.class).setValue(hbn.notifyState.name());
			getFieldByClass(HbLastSeenField.class).setValue(hbn.lastSendingTime);
        }
	}

	public CloseButton getCloseButton() {
		return getFieldByClass(CloseButton.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}
	
	@Order(1000)
	public class MainBox extends AbstractGroupBox {
		
		@Override
		protected int getConfiguredGridColumnCount() {
			return 1;
		}
		
		@Order(1000)
		public class StatusField extends AbstractStringField {
			
			@Override
			protected int getConfiguredGridW() {
				return 1;
			}
			
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Status");
			}
			
			@Override
			protected boolean getConfiguredEnabled() {
				return false;
			}
		}

		@Order(1100)
		public class HbLastSeenField extends AbstractStringField {
			
			@Override
			protected int getConfiguredGridW() {
				return 1;
			}
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("LastSeen");
			}
			
			@Override
			protected boolean getConfiguredEnabled() {
				return false;
			}
		}


		@Order(100000)
		public class CloseButton extends AbstractButton {
		
			@Override
			protected int getConfiguredSystemType() {
				return SYSTEM_TYPE_CLOSE;
			}
			
			@Override
			protected String getConfiguredLabel() {
			    return TEXTS.get("CloseButton");
			}
			
			@Override
			protected String getConfiguredKeyStroke() {
			    return IKeyStroke.ESCAPE;
			}
		}
	}
}
