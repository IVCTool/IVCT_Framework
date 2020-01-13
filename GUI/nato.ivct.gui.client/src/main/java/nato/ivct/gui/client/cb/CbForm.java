package nato.ivct.gui.client.cb;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.form.fields.treebox.AbstractTreeBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbNameField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbDependenciesTreeBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField;
import nato.ivct.gui.shared.cb.CbDependenciesLookupCall;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.UpdateCbPermission;


@FormData(value = CbFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class CbForm extends AbstractForm {

    private String   cbId;
    org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());


    @Override
    protected String getConfiguredTitle() {
        return TEXTS.get("Badge");
    }


    @Override
    protected int getConfiguredDisplayHint() {
        return IForm.DISPLAY_HINT_VIEW;
    }


    @Override
    protected String getConfiguredDisplayViewId() {
        return IForm.VIEW_ID_CENTER;
    }


    @Override
    public Object computeExclusiveKey() {
        return getCbId();
    }


    public void startView() throws ProcessingException {
        startInternal(new ViewHandler());
    }


    public MainBox getMainBox() {
        return getFieldByClass(MainBox.class);
    }


    public GeneralBox getGeneralBox() {
        return getFieldByClass(GeneralBox.class);
    }


    public IncludedCbBox getIncludedCbBox() {
        return getFieldByClass(IncludedCbBox.class);
    }


    public CbNameField getCbNameField() {
        return getFieldByClass(CbNameField.class);
    }


    public CbDescriptionField getCbDescriptionField() {
        return getFieldByClass(CbDescriptionField.class);
    }


    public CbImageField getCbImageField() {
        return getFieldByClass(CbImageField.class);
    }


    public CbDependenciesTreeBox getCbDependenciesTreeBox() {
        return getFieldByClass(CbDependenciesTreeBox.class);
    }


    public CbRequirementsTableField getCbRequirementsTableField() {
        return getFieldByClass(CbRequirementsTableField.class);
    }


    public OkButton getOkButton() {
        return getFieldByClass(OkButton.class);
    }


    @FormData
    public String getCbId() {
        return cbId;
    }


    @FormData
    public void setCbId(String cbId) {
        this.cbId = cbId;
    }

    @Order(1000)
    public class MainBox extends AbstractGroupBox {

        @Override
        protected int getConfiguredGridColumnCount() {
            return 5;
        }


        // main box shall not be scrollable to keep it in its size
        @Override
        protected TriState getConfiguredScrollable() {
            return TriState.FALSE;
        }

        @Order(1000)
        public class BadgeHorizontalSplitBox extends AbstractSplitBox {
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


                // set all fields of this box to read-only
                @Override
                public boolean isEnabled() {
                    return false;
                }

                @Order(1000)
                public class CbNameField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Name");
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 3;
                    }
                }

                @Order(2000)
                public class CbVersion extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Version");
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 3;
                    }


                    @Override
                    protected int getConfiguredHorizontalAlignment() {
                        return -1;
                    }

                }

                @Order(3000)
                public class CbDescriptionField extends AbstractStringField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("Description");
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 2;
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 3;
                    }


                    @Override
                    protected boolean getConfiguredMultilineText() {
                        return true;
                    }


                    @Override
                    protected boolean getConfiguredWrapText() {
                        return true;
                    }
                }

                @Order(4000)
                public class CbImageField extends AbstractImageField {
                    @Override
                    protected String getConfiguredLabel() {
                        return TEXTS.get("CbImage");
                    }


                    @Override
                    protected boolean getConfiguredLabelVisible() {
                        return false;
                    }


                    @Override
                    protected boolean getConfiguredAutoFit() {
                        return false;
                    }


                    @Override
                    protected int getConfiguredGridH() {
                        return 4;
                    }


                    @Override
                    protected int getConfiguredGridW() {
                        return 2;
                    }


                    @Override
                    protected int getConfiguredHorizontalAlignment() {
                        // align to left
                        return -1;
                    }


                    @Override
                    protected int getConfiguredVerticalAlignment() {
                        // align to top
                        return -1;
                    }


                    // fit the image into the field size
                    @Override
                    public boolean isAutoFit() {
                        return true;
                    }
                }
            }

            @Order(2000)
            public class IncludedCbBox extends AbstractGroupBox {
                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("InclRequirements");
                }

                @Order(1000)
                public class DependenciesHorizontalSplitterBox extends AbstractSplitBox {
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
                    public class CbDependenciesTreeBox extends AbstractTreeBox<String> {
                        @Override
                        protected int getConfiguredGridH() {
                            return 3;
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 3;
                        }


                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("BadgeDependencies");
                        }


                        @Override
                        protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
                            return CbDependenciesLookupCall.class;
                        }


                        @Override
                        protected void execPrepareLookup(ILookupCall<String> call, ITreeNode parent) {
                            final CbFormData formData = new CbFormData();
                            exportFormData(formData);
                            final CbDependenciesLookupCall c = (CbDependenciesLookupCall) call;
                            c.setCbId(formData.getCbId());
                        }


                        // do not expand all nodes initially
                        @Override
                        protected boolean getConfiguredAutoExpandAll() {
                            return false;
                        }


                        // check all child notes if their parent is checked
                        @Override
                        protected boolean getConfiguredAutoCheckChildNodes() {
                            return true;
                        }
                    }

                    @Order(2000)
                    public class CbRequirementsTableField extends AbstractTableField<CbRequirementsTableField.CbRequirementsTable> {

                        @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("Requirements");
                        }


                        @Override
                        protected int getConfiguredGridH() {
                            return 1;
                        }


                        @Override
                        protected int getConfiguredGridW() {
                            return 3;
                        }

                        public class CbRequirementsTable extends AbstractTable {

                            @Order(1000)
                            public class RequirementIdColumn extends AbstractStringColumn {

                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("RequirementId");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 200;
                                }
                            }

                            @Order(2000)
                            public class RequirementDescColumn extends AbstractStringColumn {
                                @Override
                                protected String getConfiguredHeaderText() {
                                    return TEXTS.get("RequirementDescription");
                                }


                                @Override
                                protected int getConfiguredWidth() {
                                    return 1000;
                                }
                            }


                            public RequirementIdColumn getRequirementIdColumn() {
                                return getColumnSet().getColumnByClass(RequirementIdColumn.class);
                            }


                            public RequirementDescColumn getRequirementDescColumn() {
                                return getColumnSet().getColumnByClass(RequirementDescColumn.class);
                            }
                        }


                        @Override
                        protected Class<? extends IValueField<Set<String>>> getConfiguredMasterField() {
                            return CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbDependenciesTreeBox.class;
                        }


                        @Override
                        protected void execChangedMasterValue(Object newMasterValue) {
                            // get dependencies badges from the dependencies badge tree
                            final Set<String> badges = getCbDependenciesTreeBox().getCheckedKeys();
                            // add the selected badge
                            badges.add(getCbId());

                            // get the requirements for the selected badges
                            final ICbService CbService = BEANS.get(ICbService.class);
                            final AbstractTableFieldBeanData requirementTableRows = CbService.loadRequirementTable(badges);

                            final CbRequirementsTable requirementsTable = getTable();

                            // cleanup table
                            requirementsTable.deleteAllRows();

                            // add requirements to table
                            requirementsTable.importFromTableBeanData(requirementTableRows);
                            // sort the table
                            final boolean tableSortEnable = requirementsTable.isSortEnabled();
                            requirementsTable.setSortEnabled(true);
                            requirementsTable.getColumnSet().addSortColumn(requirementsTable.getRequirementIdColumn(), true);
                            requirementsTable.sort();
                            requirementsTable.setSortEnabled(tableSortEnable);

                            super.execChangedMasterValue(newMasterValue);
                        }


                        @Override
                        protected void execInitField() {
                            final Set<String> badges = CollectionUtility.hashSet(getCbId());

                            // get the requirements for the selected badges
                            final ICbService CbService = BEANS.get(ICbService.class);
                            final AbstractTableFieldBeanData requirementTableRows = CbService.loadRequirementTable(badges);

                            final CbRequirementsTable requirementsTable = getTable();

                            // add requirements to table
                            requirementsTable.importFromTableBeanData(requirementTableRows);
                        }
                    }
                }
            }
        }
    }

    public class ViewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            final ICbService service = BEANS.get(ICbService.class);
            CbFormData formData = new CbFormData();
            exportFormData(formData);
            formData = service.load(formData);
            importFormData(formData);
            // load badge image
            final byte[] badgeIcon = service.loadBadgeIcon(formData.getCbId());
            if (badgeIcon != null) {
                getCbImageField().setImage(badgeIcon);
                getForm().setSubTitle(formData.getCbName().getValue());
            }
            else {
                logger.warn("Could not load image file for badge ID " + formData.getCbId());
            }
            setEnabledPermission(new UpdateCbPermission());
        }
    }
}
