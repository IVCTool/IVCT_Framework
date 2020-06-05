/* Copyright 2020, Reinhard Herzog, Michael Theis, Felix Schöppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

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
        final String localeString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.PREF_USER_LOCALE, "");
        getLocaleField().setValue(LocaleUtility.parse(localeString));

        // set the log level
        final String logLevelString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.CUR_LOG_LEVEL, "");
        getLogLevelField().setValue(logLevelString);
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
                return AvailableLocaleLookupCall.class;
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
                return LogLevelLookupCall.class;
            }
        }

        @Order(100000)
        public class OkButton extends AbstractOkButton {
            @Override
            protected void execClickAction() {
                // publish log level
                final String level = getLogLevelField().getValue();
                final Locale language = getLocaleField().getValue();

                if (level != null) {
                    storeLogLevel(level);
                }

                if (language != null) {
                    storeLanguageOptions();
                }
            }
        }

        @Order(101000)
        public class CancelButton extends AbstractCancelButton {}
    }

    protected void storeLogLevel(String logLevel) {
        final boolean logLevelChanged = ClientUIPreferences.getClientPreferences(ClientSession.get()).put(ClientSession.CUR_LOG_LEVEL, getLogLevelField().getValue());
        if (logLevelChanged) {
            //Required for multiuser support: ClientUIPreferences.getClientPreferences(ClientSession.get()).flush();
            BEANS.get(IOptionsService.class).setLogLevel(logLevel);
        }
    }


    protected void storeLanguageOptions() {
        // not inside form handler, because the form is used in a FormToolButton without a handler
        final boolean localeChanged = ClientUIPreferences.getClientPreferences(ClientSession.get()).put(ClientSession.PREF_USER_LOCALE, getLocaleField().getValue().toString());
        if (localeChanged) {
            ClientUIPreferences.getClientPreferences(ClientSession.get()).flush();
            MessageBoxes.createOk().withBody(TEXTS.get("ChangeOfLanguageApplicationOnNextLogin")).show();
        }
    }
}
