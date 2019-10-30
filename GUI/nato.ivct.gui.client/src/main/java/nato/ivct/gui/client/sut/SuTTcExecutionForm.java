package nato.ivct.gui.client.sut;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
// import
// org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
// import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.htmlfield.AbstractHtmlField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.html.HTML;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;

import nato.ivct.gui.client.ClientSession;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcDescrField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcExecutionStatus;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TcProgressField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionButton;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcExecutionHistoryTableField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcLogField;
import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;


@FormData(value = SuTTcExecutionFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTTcExecutionForm extends AbstractForm {

    private String sutId       = null;
    private String badgeId     = null;
    private String testsuiteId = null; // TODO: TcStatusNotification and TcVerdictNotification need adjustments
    private String testCaseId  = null;

    private String testCaseStatus   = null;
    private String testCaseVerdict  = null;
    private int    testCaseProgress = 0;


    @FormData
    public String getSutId() {
        return sutId;
    }


    @FormData
    public void setSutId(final String _sutId) {
        sutId = _sutId;
    }


    @FormData
    public String getBadgeId() {
        return badgeId;
    }


    @FormData
    public void setBadgeId(final String _badgeId) {
        badgeId = _badgeId;
    }


    @FormData
    public String getTestsuiteId() {
        return testsuiteId;
    }


    @FormData
    public void setTestsuiteId(final String _testsuiteId) {
        testsuiteId = _testsuiteId;
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
                            protected int getConfiguredMinWidth() {
                                return 600;
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
                                return 100;
                            }
                        }


                        @Override
                        protected void execRowsSelected(List<? extends ITableRow> rows) {

                            if (!rows.isEmpty()) {
                                final String tcName = getTable().getFileNameColumn().getValue(getSelectedRow());
                                // load log file content
                                final ISuTTcService service = BEANS.get(ISuTTcService.class);
                                getTcLogField().setValue(service.loadLogFileContent(getSutId(), getTestsuiteId(), tcName));
                            }
                            else {
                                getTcLogField().setValue(null);
                            }
                        }

                    }
                }

                @Order(1200)
                public class TcLogField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("TcExecutionLog");
                    }


                    @Override
                    protected boolean getConfiguredMultilineText() {
                        return true;
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return Integer.MAX_VALUE;
                    }


                    @Override
                    public boolean isEnabled() {
                        // set to r/w to activate the scrollbars
                        return true;
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
                // show progress bar
                getTcProgressField().setVisible(true);
                //hide tc execution button
                this.setVisible(false);
                // clear TC status and progress bar
                getTcExecutionStatus().setValue("");
                setTestCaseProgress(0);

                // clear the TC log field
                if (getTcLogField().getValue() != null) {
                    getTcLogField().resetValue();
                }

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

            getForm().setTitle(Stream.of(getTestCaseId().split(Pattern.quote("."))).reduce((a, b) -> b).get());

            setEnabledPermission(new UpdateSuTPermission());
        }
    }
}
