/* Copyright 2020, Michael Theis (Fraunhofer IOSB)

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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.CloseButton;
import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.HbLastSeenField;
import nato.ivct.gui.client.HeartBeatInfoForm.MainBox.StatusField;
import nato.ivct.gui.shared.HeartBeatNotification;
import nato.ivct.gui.shared.HeartBeatNotification.HbNotificationState;
import nato.ivct.gui.shared.OptionsFormData;


@FormData(value = OptionsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class HeartBeatInfoForm extends AbstractForm {

    @Override
    protected void execInitForm() {
        final SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ");
        final Timestamp now = new Timestamp(System.currentTimeMillis());

        final long minuteInSeconds = 60;
        final long hourInSeconds = minuteInSeconds * 60;
        final long dayInSeconds = hourInSeconds * 24;
        final long weekInSeconds = dayInSeconds * 7;
        final long monthInSeconds = dayInSeconds * 30;
        final long yearInSeconds = dayInSeconds * 365;

        HeartBeatNotification hbn = HeartBeatNotificationHandler.hbLastReceivedMap.get(getTitle());
        if (hbn != null) {
            getFieldByClass(StatusField.class).setValue(hbn.notifyState.name() + "  (" + getTitle() + ")");

            long timedif;
            String timeElapsed = "";
            String timeUnit = "";

            try {
                timedif = (now.getTime() - df.parse(hbn.lastSendingTime).getTime()) / 1000L;
                if (timedif < minuteInSeconds) {
                    timeElapsed = Long.toString(timedif);
                    timeUnit = "second(s)";
                }
                else if (timedif < hourInSeconds) {
                    timeElapsed = Long.toString(timedif / minuteInSeconds);
                    timeUnit = "minute(s)";
                }
                else if (timedif < weekInSeconds) {
                    timeElapsed = Long.toString(timedif / hourInSeconds);
                    timeUnit = "hour(s)";
                }
                else if (timedif < monthInSeconds) {
                    timeElapsed = Long.toString(timedif / weekInSeconds);
                    timeUnit = "week(s)";
                }
                else if (timedif < yearInSeconds) {
                    timeElapsed = Long.toString(timedif / monthInSeconds);
                    timeUnit = "month(s)";
                }
                else {
                    timeElapsed = Long.toString(timedif / yearInSeconds);
                    timeUnit = "year(s)";
                }
            }
            catch (ParseException exc) {
                timeElapsed = "???";
                timeUnit = "";
            }

            getFieldByClass(HbLastSeenField.class).setValue(timeElapsed + " " + timeUnit + " ago");
        }
        else
            getFieldByClass(StatusField.class).setValue(HbNotificationState.UNKNOWN.name());
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
