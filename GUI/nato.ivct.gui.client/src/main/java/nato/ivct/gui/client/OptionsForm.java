package nato.ivct.gui.client;

import java.util.Locale;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.nls.LocaleUtility;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import nato.ivct.gui.client.OptionsForm.MainBox.CancelButton;
import nato.ivct.gui.client.OptionsForm.MainBox.LocaleField;
import nato.ivct.gui.client.OptionsForm.MainBox.LogLevelField;
import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.OptionsForm.MainBox.PropertiesBox;
import nato.ivct.gui.client.OptionsForm.MainBox.PropertiesBox.BadgeHomeField;
import nato.ivct.gui.client.OptionsForm.MainBox.PropertiesBox.SutHomeField;
import nato.ivct.gui.client.OptionsForm.MainBox.PropertiesBox.TsHomeField;
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
		String localeString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.PREF_USER_LOCALE, null);
		getLocaleField().setValue(LocaleUtility.parse(localeString));
	}

// Both handlers are not used by this form
//	public void startModify() {
//		startInternalExclusive(new ModifyHandler());
//	}
//
//	public void startNew() {
//		startInternal(new NewHandler());
//	}

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

	public TsHomeField getTsHomeField() {
		return getFieldByClass(TsHomeField.class);
	}

	public SutHomeField getSutHomeField() {
		return getFieldByClass(SutHomeField.class);
	}

	public BadgeHomeField getBadgeHomeField() {
		return getFieldByClass(BadgeHomeField.class);
	}

	public PropertiesBox getPropertiesBox() {
		return getFieldByClass(PropertiesBox.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		@Order(1000)
		public class LogLevelField extends AbstractSmartField<String> {
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
		
		@Order(1100)
		public class LocaleField extends AbstractSmartField<Locale> {
			
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

		@Order(1500)
		public class PropertiesBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Properties");
			}

			@Order(2000)
			public class TsHomeField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("TS_HOME");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(3000)
			public class SutHomeField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("SutHome");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(4000)
			public class BadgeHomeField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("BadgeHome");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

		}

		@Order(100000)
		public class OkButton extends AbstractOkButton {
			@Override
			protected void execClickAction() {
				IOptionsService service = BEANS.get(IOptionsService.class);
				String level = getLogLevelField().getValue();
				service.setLogLevel(level);
				storeLanguageOptions();
			}
		}

		@Order(101000)
		public class CancelButton extends AbstractCancelButton {
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
