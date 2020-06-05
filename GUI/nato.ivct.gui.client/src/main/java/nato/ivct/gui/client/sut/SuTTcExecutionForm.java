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

package nato.ivct.gui.client.sut;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.basic.cell.Cell;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.htmlfield.AbstractHtmlField;
import org.eclipse.scout.rt.client.ui.form.fields.labelfield.AbstractLabelField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.html.HTML;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;

import nato.ivct.commander.CmdOperatorConfirmation;
import nato.ivct.commander.Factory;
import nato.ivct.gui.client.ClientSession;
import nato.ivct.gui.client.HeartBeatNotificationHandler;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.ContentForm.MainBox.FieldsBox.CommentField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcDescrField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcExecutionStatus;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcProgressField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionButton;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcExecutionHistoryTableField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcLogField;
import nato.ivct.gui.shared.HeartBeatNotification;
import nato.ivct.gui.shared.HeartBeatNotification.HbNotificationState;
import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.LogLevelLookupCall;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;


@FormData(value = SuTTcExecutionFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTTcExecutionForm extends AbstractForm {

    private String sutId       = null;
    private String badgeId     = null;
    private String testsuiteId = null;
    private String testCaseId  = null;

    private String testCaseStatus   = null;
    private String testCaseVerdict  = null;
    private int    testCaseProgress = 0;

    private String defaultTcStatusForegroundColor;

    // test result verdicts
    public static final String PASSED_VERDICT       = "PASSED";
    public static final String INCONCLUSIVE_VERDICT = "INCONCLUSIVE";
    public static final String FAILED_VERDICT       = "FAILED";
    public static final String NOT_RUN_VERDICT      = "NOT_RUN";

    @FormData
    public String getSutId() {
        return sutId;
    }


    @FormData
    public void setSutId(final String sutId) {
        this.sutId = sutId;
    }


    @FormData
    public String getBadgeId() {
        return badgeId;
    }


    @FormData
    public void setBadgeId(final String badgeId) {
        this.badgeId = badgeId;
    }


    @FormData
    public String getTestsuiteId() {
        return testsuiteId;
    }


    @FormData
    public void setTestsuiteId(final String testsuiteId) {
        this.testsuiteId = testsuiteId;
    }


    @FormData
    public String getTestCaseId() {
        return testCaseId;
    }


    @FormData
    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }


    @FormData
    public String getTestCaseStatus() {
        return testCaseStatus;
    }


    @FormData
    public void setTestCaseStatus(String testCaseStatus) {
        this.testCaseStatus = testCaseStatus;
    }


    @FormData
    public int getTestCaseProgress() {
        return testCaseProgress;
    }


    @FormData
    public void setTestCaseProgress(int tcProgress) {
        testCaseProgress = tcProgress;

        getTcProgressField().setValue(getTcProgressField().createHtmlContent(testCaseProgress));

    }


    @FormData
    public String getTestCaseVerdict() {
        return testCaseVerdict;
    }


    @FormData
    public void setTestCaseVerdict(String testCaseVerdict) {
        this.testCaseVerdict = testCaseVerdict;
    }


    @FormData
    public void setDefaultTcStatusForegroundColor(String color) {
        defaultTcStatusForegroundColor = color;
    }


    @FormData
    public String getDefaultTcStatusForegroundColor() {
        return defaultTcStatusForegroundColor;
    }


    @Override
    protected int getConfiguredDisplayHint() {
        return IForm.DISPLAY_HINT_VIEW;
    }


    public void startView() {
        startInternal(new ViewHandler());
    }


    public MainBox getMainBox() {
        return getFieldByClass(MainBox.class);
    }


    public GeneralBox getGeneralBox() {
        return getFieldByClass(GeneralBox.class);
    }


    public TcDescrField getDescrField() {
        return getFieldByClass(TcDescrField.class);
    }


    public TcExecutionStatus getTcExecutionStatus() {
        return getFieldByClass(TcExecutionStatus.class);
    }


    public TcProgressField getTcProgressField() {
        return getFieldByClass(TcProgressField.class);
    }


    public TcExecutionDetailsBox getTcExecutionDetailsBox() {
        return getFieldByClass(TcExecutionDetailsBox.class);
    }


    public TcExecutionHistoryTableField getTcExecutionHistoryTableField() {
        return getFieldByClass(TcExecutionHistoryTableField.class);
    }


    public TcLogField getTcLogField() {
        return getFieldByClass(TcLogField.class);
    }


    public TcExecutionButton getTcExecutionButton() {
        return getFieldByClass(TcExecutionButton.class);
    }


    protected void openPopup(String sutName, String testSuiteId, String testCaseId, String operatorMessage) {
        IForm form = new ContentForm(sutName, testSuiteId, testCaseId, operatorMessage);
        form.start();
        form.waitFor();
    }

    public class ContentForm extends AbstractForm {

        private String sutName;
        private String testSuiteId;
        private String testCaseId;
        private String operatorMessage;

        public ContentForm(String sutName, String testSuiteId, String testCaseId, String operatorMessage) {
            this.sutName = sutName;
            this.testSuiteId = testSuiteId;
            this.testCaseId = testCaseId;
            this.operatorMessage = operatorMessage;
        }


        @Override
        public boolean isModal() {
            return true;
        }


        @Override
        public int getDisplayHint() {
            return DISPLAY_HINT_DIALOG;
        }

        @Order(10)
        public class MainBox extends AbstractGroupBox {

            @Order(10)
            public class FieldsBox extends AbstractGroupBox {

                @Override
                protected int getConfiguredGridW() {
                    return 5;
                }

                @Order(10)
                public class SutNameField extends AbstractLabelField {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("SuT");
                    }


                    @Override
                    protected void execInitField() {
                        setValue(sutName);
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 5;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 1;
                    }

                }

                @Order(20)
                public class SutIdField extends AbstractLabelField {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("TestsuiteId");
                    }


                    @Override
                    protected void execInitField() {
                        setValue(testSuiteId);
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 5;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 1;
                    }

                }

                @Order(30)
                public class TestCaseIdField extends AbstractLabelField {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("TestcaseId");
                    }


                    @Override
                    protected void execInitField() {
                        setValue(testCaseId);
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 5;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 1;
                    }

                }

                @Order(40)
                public class MessageField extends AbstractStringField {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("OperatorMessage");
                    }


                    @Override
                    protected void execInitField() {
                        setValue(operatorMessage);
                    }

                    @Override
                    protected boolean getConfiguredMultilineText() {
                        return true;
                    }


                    @Override
                    protected String getConfiguredForegroundColor() {
                        return "db3d57";
                    }


                    @Override
                    protected boolean getConfiguredEnabled() {
                        return false;
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 5;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 4;
                    }

                }

                @Order(50)
                public class CommentField extends AbstractStringField {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Comment");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 250;
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 5;
                    }
                }
            }

            @Order(60)
            public class ConfirmButton extends AbstractButton {

                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("Confirm");
                }


                @Override
                protected void execClickAction() {
                    ContentForm.this.doClose();

                    // Confirmation Message for the TestEngine            
                    CmdOperatorConfirmation operatorConfirmationCmd = Factory.createCmdOperatorConfirmation(sutName, testSuiteId, testCaseId, true, getCommentField().getValue());
                    operatorConfirmationCmd.execute();

                }


                public CommentField getCommentField() {
                    return getFieldByClass(CommentField.class);
                }
            }

            @Order(70)
            public class CancelButton extends AbstractButton {

                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("Reject");
                }


                @Override
                protected void execClickAction() {
                    ContentForm.this.doClose();

                    // Reject Message for the TestEngine              
                    CmdOperatorConfirmation operatorConfirmationCmd = Factory.createCmdOperatorConfirmation(sutName, testSuiteId, testCaseId, false, getCommentField().getValue());
                    operatorConfirmationCmd.execute();

                }


                public CommentField getCommentField() {
                    return getFieldByClass(CommentField.class);
                }
            }
        }
    }

    @Order(10000)
    public class MainBox extends AbstractGroupBox {

        @Override
        protected int getConfiguredGridColumnCount() {
            return 3;
        }

        @Order(1000)
        public class GeneralBox extends AbstractGroupBox {

            @Override
            public boolean isEnabled() {
                // set all fields to read-only
                return false;
            }

            @Order(1010)
            public class TcDescrField extends AbstractStringField {

                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("Description");
                }


                @Override
                protected int getConfiguredGridW() {
                    return 3;
                }


                @Override
                protected int getConfiguredHeightInPixel() {
                    return 80;
                }


                @Override
                protected boolean getConfiguredMultilineText() {
                    return true;
                }


                @Override
                protected boolean getConfiguredWrapText() {
                    return true;
                }


                @Override
                protected int getConfiguredMaxLength() {
                    return 2000;
                }
            }

            @Order(1031)
            public class TcExecutionStatus extends AbstractStringField {
                @Override
                protected int getConfiguredGridW() {
                    return 2;
                }


                @Override
                protected double getConfiguredGridWeightY() {
                    return 0;
                }


                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("TCStatus");
                }


                @Override
                public boolean isLabelVisible() {
                    return true;
                }

            }

            @Order(1100)
            public class TcProgressField extends AbstractHtmlField {
                @Override
                protected int getConfiguredGridW() {
                    return 1;
                }


                @Override
                protected int getConfiguredHeightInPixel() {
                    return 20;
                }


                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("TcProgress");
                }


                @Override
                protected String getConfiguredBackgroundColor() {
                    return "AABBCC";
                }


                @Override
                protected boolean getConfiguredLabelVisible() {
                    return false;
                }


                @Override
                protected boolean getConfiguredVisible() {
                    return false;
                }


                @Override
                protected void execInitField() {
                    setTestCaseProgress(0);
                }


                protected String createHtmlContent(int progress) {
                    return HTML.fragment(HTML.body("<div class='progress'><div class='progress-bar' role='progressbar' aria-valuenow='40' aria-valuemin='0' aria-valuemax='100' style='width:" + Integer.toString(progress) + "%'>" + Integer.toString(progress) + "%</div></div>")).toPlainText();

                }
            }
        }

        @Order(2000)
        public class TcExecutionDetailsBox extends AbstractGroupBox {

            @Override
            protected double getConfiguredGridWeightY() {
                return 1;
            }


            @Override
            protected int getConfiguredHeightInPixel() {
                return 700;
            }

            @Order(1000)
            public class DetailsHorizontalSplitBox extends AbstractSplitBox {
                @Override
                protected boolean getConfiguredSplitHorizontal() {
                    // split horizontal
                    return false;
                }


                @Override
                protected double getConfiguredSplitterPosition() {
                    return 0.3;
                }

                @Order(1100)
                public class TcExecutionHistoryTableField extends AbstractTableField<TcExecutionHistoryTableField.TcExecutionHistoryTable> {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("TcExecutionHistory");
                    }

                    public class TcExecutionHistoryTable extends AbstractTable {

                        @Override
                        protected boolean getConfiguredMultiSelect() {
                            // only a single row can be selected
                            return false;
                        }


                        public FileNameColumn getFileNameColumn() {
                            return getColumnSet().getColumnByClass(FileNameColumn.class);
                        }


                        public TcVerdictColumn getTcVerdictColumn() {
                            return getColumnSet().getColumnByClass(TcVerdictColumn.class);
                        }

                        @Order(1000)
                        public class FileNameColumn extends AbstractStringColumn {

                            @Override
                            protected String getConfiguredHeaderText() {
                                return TEXTS.get("FileName");
                            }

                            @Override
                            protected int getConfiguredWidth() {
                                return 700;
                            }

                        }

                        @Order(2000)
                        public class TcVerdictColumn extends AbstractStringColumn {
                            @Override
                            protected String getConfiguredHeaderText() {
                                return TEXTS.get("TcResult");
                            }


                            @Override
                            protected int getConfiguredWidth() {
                                return 200;
                            }
                        }

                        @Override
                        protected void execRowsSelected(List<? extends ITableRow> rows) {
                            // clear TC Execution Log table
                            getTcLogField().getTable().discardAllRows();
                            if (!rows.isEmpty()) {
                                // row is selected
                                final String logFileName = getTable().getFileNameColumn().getValue(getSelectedRow());
                                // load log file content
                                final ISuTTcService service = BEANS.get(ISuTTcService.class);
                                SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
                                formData = service.loadJSONLogFileContent(getSutId(), getTestsuiteId(), logFileName, formData);
                                importFormData(formData);

                                // set the log level color of the TC Execution Log table
                                getTcLogField().getTable().getRows().forEach(row -> {
                                    Cell cell = row.getCellForUpdate(getTcLogField().getTable().getLogLevelColumn());
                                    switch (Objects.toString(cell.getValue(), "")) {
                                        case "ERROR":
                                            row.setBackgroundColor("efa9b5");
                                            break;
                                        case "WARN":
                                            row.setBackgroundColor("FFDB9D");
                                            break;
                                        default:
                                            break;
                                    }
                                });

                            }
                        }
                    }
                }

                @Order(1200)
                public class TcLogField extends AbstractTableField<TcLogField.TcLogFieldTable> {

                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("TcExecutionLog");
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 2;
                    }


                    public void addLine(String logLevel, String timeStamp, String logMsg) {
                        TcLogFieldTable tbl = getTcLogField().getTable();
                        ITableRow row = tbl.addRow(tbl.createRow());
                        tbl.getLogLevelColumn().setValue(row, logLevel);
                        tbl.getTimeStampColumn().setValue(row, timeStamp);
                        tbl.getLogMsgColumn().setValue(row, logMsg);
                        if (LogLevelLookupCall.LogLevels.ERROR.name().equalsIgnoreCase(logLevel)) {
                            row.setBackgroundColor("efa9b5");
                        }
                        if (LogLevelLookupCall.LogLevels.WARN.name().equalsIgnoreCase(logLevel)) {
                            row.setBackgroundColor("FFDB9D");
                        }

                        tbl.selectLastRow();
                        tbl.scrollToSelection();

                    }

                    @Order(2200)
                    public class TcLogFieldTable extends AbstractTable {

                        public LogLevelColumn getLogLevelColumn() {
                            return getColumnSet().getColumnByClass(LogLevelColumn.class);
                        }


                        public TimeStampColumn getTimeStampColumn() {
                            return getColumnSet().getColumnByClass(TimeStampColumn.class);
                        }


                        public LogMsgColumn getLogMsgColumn() {
                            return getColumnSet().getColumnByClass(LogMsgColumn.class);
                        }

                        @Order(1000)
                        public class LogLevelColumn extends AbstractStringColumn {
                            @Override
                            protected String getConfiguredHeaderText() {
                                return TEXTS.get("LogLevel");
                            }


                            @Override
                            protected int getConfiguredWidth() {
                                return 100;
                            }

                        }

                        @Order(1200)
                        public class TimeStampColumn extends AbstractStringColumn {
                            @Override
                            protected String getConfiguredHeaderText() {
                                return TEXTS.get("TimeStamp");
                            }


                            @Override
                            protected int getConfiguredWidth() {
                                return 220;
                            }

                        }

                        @Order(1400)
                        public class LogMsgColumn extends AbstractStringColumn {
                            @Override
                            protected String getConfiguredHeaderText() {
                                return TEXTS.get("LogMsg");
                            }


                            @Override
                            protected int getConfiguredWidth() {
                                return 1200;
                            }


                            @Override
                            protected boolean getConfiguredTextWrap() {
                                return true;
                            }
                        }

                    }
                }
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

        @Order(110000)
        public class TcExecutionButton extends AbstractButton {

            @Override
            protected String getConfiguredLabel() {
                return TEXTS.get("TCexec");
            }


            @Override
            protected void execClickAction() {

                // check the status of the TestEngine
                HeartBeatNotification hbn = HeartBeatNotificationHandler.lastReceivedFromSender("TestEngine");
                if (hbn.notifyState != HbNotificationState.OK) {
                    MessageBoxes.createOk().withHeader(TEXTS.get("TeExecMsgBoxHeader")).withBody(TEXTS.get("TeExecMsgBoxBody")).show();
                    return;
                }

                // hide TC execute button if the same test case is already executed
                final IDesktop desktop = ClientSessionProvider.currentSession().getDesktop();
                List<SuTTcExecutionForm> forms = desktop.findForms(SuTTcExecutionForm.class);
                if (forms.stream().filter(form -> form.getSutId().equalsIgnoreCase(getSutId()) && form.getTestCaseId().equalsIgnoreCase(getTestCaseId())).anyMatch(form -> !form.getTcExecutionButton().isVisible())) {
                    MessageBoxes.createOk().withHeader(TEXTS.get("TcExecMsgBoxHeader")).withBody(TEXTS.get("TcExecMsgBoxBody")).show();
                    return;
                }

                // show progress bar
                getTcProgressField().setVisible(true);
                // reset tc status foreground color
                getTcExecutionStatus().setForegroundColor(getDefaultTcStatusForegroundColor());
                //hide tc execution button
                this.setVisible(false);

                // hide TC Execution History Table during TC execution
                getTcExecutionHistoryTableField().setVisible(false);

                // clear TC status and progress bar
                getTcExecutionStatus().setValue("");
                setTestCaseProgress(0);

                // clear the TC log field
                getTcLogField().getTable().discardAllRows();

                // use ModelJobs to asynchronously start test case execution sequence
                ModelJobs.schedule(new IRunnable() {

                    @Override
                    public void run() throws Exception {
                        // before starting the TC, communicate this user's last used log level
                        final String logLevel = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(ClientSession.CUR_LOG_LEVEL, null);
                        final IOptionsService service = BEANS.get(IOptionsService.class);
                        service.setLogLevel(logLevel);
                        // now start the TC
                        final ISuTTcService sutCbService = BEANS.get(ISuTTcService.class);
                        sutCbService.executeTestCase(getSutId(), getTestCaseId(), getTestsuiteId());
                    }
                }, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
            }
        }
    }

    public class ViewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            final ISuTTcService service = BEANS.get(ISuTTcService.class);
            SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
            exportFormData(formData);
            formData = service.load(formData);
            importFormData(formData);

            // set result color in the execution history table
            setTestResultColor();

            setDefaultTcStatusForegroundColor(getTcExecutionStatus().getForegroundColor());

            Stream.of(getTestCaseId().split(Pattern.quote("."))).reduce((a, b) -> b).ifPresent(result -> getForm().setTitle(result));
            
            setEnabledPermission(new UpdateSuTPermission());
        }
    }

    public void setTestResultColor() {
        // set the color of the test results in TC Execution History Table Field
        getTcExecutionHistoryTableField().getTable().getRows().forEach(row -> {
            Cell cell = row.getCellForUpdate(getTcExecutionHistoryTableField().getTable().getTcVerdictColumn());
            switch (Objects.toString(cell.getValue(), "")) {
                case PASSED_VERDICT:
                    cell.setCssClass("passed-text");
                    break;
                case INCONCLUSIVE_VERDICT:
                    cell.setCssClass("inconclusive-text");
                    break;
                case FAILED_VERDICT:
                    cell.setCssClass("failed-text");
                    break;
                default:
                    break;
            }
        });
    }
}
