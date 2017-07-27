package nato.ivct.gui.client;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.OptionsForm.MainBox.CancelButton;
import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.shared.CreateOptionsPermission;
import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.OptionsFormData;
import nato.ivct.gui.shared.UpdateOptionsPermission;
import nato.ivct.gui.client.OptionsForm.MainBox.LogLevelField;

@FormData(value = OptionsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class OptionsForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Options");
	}
	
	@Override
	protected void execInitForm() {
		// TODO Auto-generated method stub
		super.execInitForm();
	}

	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

	public void startNew() {
		startInternal(new NewHandler());
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public LogLevelField getLogLevelField() {
		return getFieldByClass(LogLevelField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		
		@Order(1000)
		public class LogLevelField extends AbstractStringField {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("LogLevel");
			}

			@Override
			protected int getConfiguredMaxLength() {
				return 128;
			}
		}

		@Order(100000)
		public class OkButton extends AbstractOkButton {
			@Override
			protected void execClickAction() {
				IOptionsService service = BEANS.get(IOptionsService.class);
				String level = getLogLevelField().getValue();
				service.setLogLevel(level);
//				super.execClickAction();
			}
		}

		@Order(101000)
		public class CancelButton extends AbstractCancelButton {
		}
	}

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			IOptionsService service = BEANS.get(IOptionsService.class);
			OptionsFormData formData = new OptionsFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);

			setEnabledPermission(new UpdateOptionsPermission());
		}

		@Override
		protected void execStore() {
			IOptionsService service = BEANS.get(IOptionsService.class);
			OptionsFormData formData = new OptionsFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			IOptionsService service = BEANS.get(IOptionsService.class);
			OptionsFormData formData = new OptionsFormData();
			exportFormData(formData);
			formData = service.prepareCreate(formData);
			importFormData(formData);

			setEnabledPermission(new CreateOptionsPermission());
		}

		@Override
		protected void execStore() {
			IOptionsService service = BEANS.get(IOptionsService.class);
			OptionsFormData formData = new OptionsFormData();
			exportFormData(formData);
			service.create(formData);
		}
	}
}
