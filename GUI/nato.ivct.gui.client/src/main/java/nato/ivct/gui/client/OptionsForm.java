package nato.ivct.gui.client;

import java.util.Locale;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.nls.LocaleUtility;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import nato.ivct.gui.client.OptionsForm.MainBox.CancelButton;
import nato.ivct.gui.client.OptionsForm.MainBox.LocaleField;
import nato.ivct.gui.client.OptionsForm.MainBox.LogLevelField;
import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.shared.AvailableLocaleLookupCall;
import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.LogLevelLookupCall;
import nato.ivct.gui.shared.OptionsFormData;

@FormData(value = OptionsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class OptionsForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Options");
	}

	@Override
	protected void execInitForm() {
		// set the language
		String localeString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.PREF_USER_LOCALE, null);
		getLocaleField().setValue(LocaleUtility.parse(Strings.nullToEmpty(localeString)));
		
		// set the log level
		String logLevelString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.CUR_LOG_LEVEL, null);
		getLogLevelField().setValue(Strings.nullToEmpty(logLevelString));
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}
	
	public LocaleField getLocaleField() {
		return getFieldByClass(LocaleField.class);
	}

	public LogLevelField getLogLevelField() {
		return getFieldByClass(LogLevelField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {
		
		@Override
		protected int getConfiguredGridColumnCount() {
			return 1;
		}
		
		@Order(1000)
		public class LocaleField extends AbstractSmartField<Locale> {
			
			@Override
			protected int getConfiguredGridW() {
				return 1;
			}
			
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Language");
			}
			
			@Override
			protected boolean getConfiguredStatusVisible() {
				return false;
			}
			
			@Override
			protected Class<? extends ILookupCall<Locale>> getConfiguredLookupCall() {
				return (Class<? extends ILookupCall<Locale>>) AvailableLocaleLookupCall.class;
			}
		}

		@Order(1100)
		public class LogLevelField extends AbstractSmartField<String> {
			
			@Override
			protected int getConfiguredGridW() {
				return 1;
			}
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("LogLevel");
			}

			@Override
			protected boolean getConfiguredStatusVisible() {
				return false;
			}
			
			@Override
			protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
				return (Class<? extends ILookupCall<String>>) LogLevelLookupCall.class;
			}
		}


		@Order(100000)
		public class OkButton extends AbstractOkButton {
			@Override
			protected void execClickAction() {
				// publish log level
				IOptionsService service = BEANS.get(IOptionsService.class);
				String level = getLogLevelField().getValue();
				service.setLogLevel(level);
				// store log level
				storeLogLevel(level);
				// store gui language
				storeLanguageOptions();
			}
		}

		@Order(101000)
		public class CancelButton extends AbstractCancelButton {
		}
	}
	
	protected void storeLogLevel(String logLevel) {
	    boolean logLevelChanged = ClientUIPreferences.getClientPreferences(ClientSession.get()).put(ClientSession.CUR_LOG_LEVEL, getLogLevelField().getValue().toString());
	    if (logLevelChanged) {
	    	ClientUIPreferences.getClientPreferences(ClientSession.get()).flush();
	    }
	}
	
	protected void storeLanguageOptions() {
		// not inside form handler, because the form is used in a FormToolButton without a handler
	    boolean localeChanged = ClientUIPreferences.getClientPreferences(ClientSession.get()).put(ClientSession.PREF_USER_LOCALE, getLocaleField().getValue().toString());
	    if (localeChanged) {
	    	ClientUIPreferences.getClientPreferences(ClientSession.get()).flush();
	    	
	    	MessageBoxes.createOk()
	    		.withBody(TEXTS.get("ChangeOfLanguageApplicationOnNextLogin"))
	    		.show();
	    }
	}

// Not used by this form
//	public class ModifyHandler extends AbstractFormHandler {
//
//		@Override
//		protected void execLoad() {
//			IOptionsService service = BEANS.get(IOptionsService.class);
//			OptionsFormData formData = new OptionsFormData();
//			exportFormData(formData);
//			formData = service.load(formData);
//			importFormData(formData);
//
//			setEnabledPermission(new UpdateOptionsPermission());
//		}
//
//		@Override
//		protected void execStore() {
//			IOptionsService service = BEANS.get(IOptionsService.class);
//			OptionsFormData formData = new OptionsFormData();
//			exportFormData(formData);
//			service.store(formData);
//		}
//	}
//
//	public class NewHandler extends AbstractFormHandler {
//
//		@Override
//		protected void execLoad() {
//			IOptionsService service = BEANS.get(IOptionsService.class);
//			OptionsFormData formData = new OptionsFormData();
//			exportFormData(formData);
//			formData = service.prepareCreate(formData);
//			importFormData(formData);
//
//			setEnabledPermission(new CreateOptionsPermission());
//		}
//
//		@Override
//		protected void execStore() {
//			IOptionsService service = BEANS.get(IOptionsService.class);
//			OptionsFormData formData = new OptionsFormData();
//			exportFormData(formData);
//			service.create(formData);
//		}
//	}
}
