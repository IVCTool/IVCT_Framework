package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractSaveButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.CancelButton;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.DescrField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.NameField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.SutVendorField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.SuTCapabilityBox;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.SaveButton;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTCbLookupCall;
import nato.ivct.gui.shared.sut.SuTEditFormData;


@FormData(value = SuTEditFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTEditForm extends AbstractForm {

    private String sutId = null;
    private String title = null;

    public SuTEditForm(String formTitle) {
        title = formTitle;
    }


    @Override
    protected String getConfiguredTitle() {
        if (title != null)
            return title;
        else
            return TEXTS.get("SuT");
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


    public SaveButton getSaveButton() {
        return getFieldByClass(SaveButton.class);
    }


    public MainBox getMainBox() {
        return getFieldByClass(MainBox.class);
    }


    public GeneralBox getGeneralBox() {
        return getFieldByClass(GeneralBox.class);
    }


    public DescrField getDescrField() {
        return getFieldByClass(DescrField.class);
    }


    public SutVendorField getSutVendorField() {
        return getFieldByClass(SutVendorField.class);
    }


    public NameField getNameField() {
        return getFieldByClass(NameField.class);
    }


    public SuTCapabilityBox getSuTCapabilityBox() {
        return getFieldByClass(SuTCapabilityBox.class);
    }


    @FormData
    public String getSutId() {
        return sutId;
    }


    @FormData
    public void setSutId(final String _sutId) {
        sutId = _sutId;
    }


    @Override
    public Object computeExclusiveKey() {
        return getSutId();
    }


    @Override
    protected int getConfiguredDisplayHint() {
        return IForm.DISPLAY_HINT_VIEW;
    }

    @Order(1000)
    public class MainBox extends AbstractGroupBox {

        @Order(1000)
        public class MainBoxHorizontalSplitBox extends AbstractSplitBox {
            @Override
            protected boolean getConfiguredSplitHorizontal() {
                // split horizontal
                return false;
            }


            @Override
            protected double getConfiguredSplitterPosition() {
                return 0.35;
            }

            @Order(1000)
            public class GeneralBox extends AbstractGroupBox {
                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("GeneralInformation");
                }


                // set all fields of this box to read/write
                @Override
                public boolean isEnabled() {
                    return true;
                }

                @Order(1100)
                public class NameField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Name");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 128;
                    }


                    // the ID field must have a value
                    @Override
                    public boolean isMandatory() {
                        return true;
                    }
                }

                @Order(1150)
                public class VersionField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Version");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 64;
                    }
                }

                @Order(1200)
                public class SutVendorField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Vendor");
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 128;
                    }
                }

                @Order(1300)
                public class DescrField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Description");
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 3;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 2;
                    }


                    @Override
                    protected int getConfiguredMaxLength() {
                        return 256;
                    }


                    // set to multi-line
                    @Override
                    protected boolean getConfiguredMultilineText() {
                        return true;
                    }
                }

                @Order(1400)
                public class FederationSettingsBox extends AbstractGroupBox {
                    @Override
                    protected int getConfiguredGridColumnCount() {
                        return 2;
                    }


                    // don't show border
                    @Override
                    protected boolean getConfiguredBorderVisible() {
                        return false;
                    }

                    @Order(1410)
                    public class RtiSettingDesignator extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("RTIConnection");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }

                    @Order(1420)
                    public class FederationName extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("FederationName");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }

                    @Order(1430)
                    public class FederateName extends AbstractStringField {
                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("FederateName");
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 1;
                        }
                    }
                }

            }

            @Order(2000)
            public class SuTCapabilityBox extends AbstractListBox<String> {
                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("Capabilities");
                }


                @Override
                protected int getConfiguredGridH() {
                    return 1;
                }


                @Override
                protected int getConfiguredGridW() {
                    return 3;
                }


                @Override
                protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
                    return SuTCbLookupCall.class;
                }
            }
        }

        @Order(100000)
        public class SaveButton extends AbstractSaveButton {
            @Override
            protected String getConfiguredKeyStroke() {
                return "ctrl-s";
            }


            @Override
            protected boolean getConfiguredEnabled() {
                return false;
            }
        }

        @Order(101000)
        public class CancelButton extends AbstractCancelButton {
            @Override
            public boolean isVisible() {
                return true;
            }


            @Override
            protected void execClickAction() {
                // TODO Auto-generated method stub
            }
        }
    }

    @Override
    protected boolean execIsSaveNeeded() {
        // show save button if save is required
        final boolean saveNeeded = super.execIsSaveNeeded();
        getSaveButton().setEnabled(saveNeeded);
        return saveNeeded;
    }


    @Override
    public boolean isSaveNeededVisible() {
        return true;
    }


    @Override
    protected void execStored() {
        // select the newly created/modified node
        final SuTOutline sutOutline = (SuTOutline) getDesktop().getOutline();

        final AbstractPageWithNodes pageWithNode = (AbstractPageWithNodes) sutOutline.getRootNode();
        // add only a newly created node
        if (pageWithNode.getTree().findNode(getSutId()) == null) {
            final SuTBadgeTablePage newPage = sutOutline.createChildPage(getSutId());
            pageWithNode.getTree().addChildNode(pageWithNode, newPage);
            //			List<ITreeNode> notesToAdd = new ArrayList<>();
            //			notesToAdd.add(newPage);
            //			pageWithNode.getTree().updateChildNodeOrder(pageWithNode, pageWithNode.getChildPages());
            sutOutline.selectNode(newPage);
        }
        else {
            // toggle selection to reload to form content
            final SuTBadgeTablePage selNode = (SuTBadgeTablePage) sutOutline.getSelectedNode();
            sutOutline.deselectNode(selNode);
            sutOutline.selectNode(selNode);

            // reload capabilities
            if (getFieldByClass(SuTCapabilityBox.class).getTable().getUpdatedRowCount() > 0)
                selNode.execLoadData(new SearchFilter());
        }

        // close the edit form
        doClose();
    }

    public class NewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            final ISuTService service = BEANS.get(ISuTService.class);
            SuTEditFormData formData = new SuTEditFormData();
            exportFormData(formData);
            formData = service.prepareCreate(formData);
            importFormData(formData);
            //          setEnabledPermission(new CreateSuTPermission());
        }


        @Override
        protected void execStore() {
            final ISuTService service = BEANS.get(ISuTService.class);
            final SuTEditFormData formData = new SuTEditFormData();
            exportFormData(formData);
            final String sutId = service.create(formData).getSutId();
            // set the id of this form which is derived from the SUT name
            setSutId(sutId);
        }
    }

    public class ModifyHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            final ISuTService service = BEANS.get(ISuTService.class);
            SuTEditFormData formData = new SuTEditFormData();
            exportFormData(formData);

            // set the SutId
            final SuTOutline sutOutline = (SuTOutline) getDesktop().getOutline();
            final String SutId = (String) sutOutline.getActivePage().getPrimaryKey();
            formData.setSutId(SutId);

            //load the data into the form
            formData = service.load(formData);
            importFormData(formData);

            // mark the already implemented capabilities
            sutOutline.getSelectedNode().getChildNodes().forEach(node -> {
                final List<String> keys = CollectionUtility.emptyArrayList();
                keys.add(((SuTCbNodePage) node).getBadgeId());
                getSuTCapabilityBox().getTable().checkRow(getSuTCapabilityBox().getTable().getRowByKey(keys), true);
            });

            //            setEnabledPermission(new CreateSuTPermission());
        }


        @Override
        protected void execPostLoad() {
            // set all rows of the table to unchanged after loading to avoid updating the table without being explicitly altered (checked/unchecked)
            getSuTCapabilityBox().getTable().getRows().forEach(row -> row.setStatus(ITableRow.STATUS_NON_CHANGED));
        }


        @Override
        protected void execStore() {
            final ISuTService service = BEANS.get(ISuTService.class);
            final SuTEditFormData formData = new SuTEditFormData();
            exportFormData(formData);
            service.store(formData);
        }
    }
}
