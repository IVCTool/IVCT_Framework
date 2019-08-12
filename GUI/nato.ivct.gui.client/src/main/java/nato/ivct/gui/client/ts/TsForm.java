package nato.ivct.gui.client.ts;

import java.util.Set;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.cb.CbForm;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbDependenciesTreeBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField.CbRequirementsTable;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField.CbRequirementsTable.RequirementDescColumn;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField.CbRequirementsTable.RequirementIdColumn;
import nato.ivct.gui.client.ts.TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.DependenciesHorizontalSplitterBox.TcListBox;
import nato.ivct.gui.client.ts.TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.DependenciesHorizontalSplitterBox.TsRequirementsTableField;
import nato.ivct.gui.client.ts.TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.DependenciesHorizontalSplitterBox.TsRequirementsTableField.TsRequirementsTable;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.ts.TsFormData;
import nato.ivct.gui.shared.ts.TsTestcaseLookupCall;

@FormData(value = TsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class TsForm extends AbstractForm {

	private String TsId;
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@FormData
	public String getTsId() {
		return TsId;
	}

	@FormData
	public void setTsId(String TsId) {
		this.TsId = TsId;
	}

	public TcListBox getTcListBox() {
		return getFieldByClass(TcListBox.class);
	}

//	public TsRequirementsTableField getTsRequirementsTableField() {
//		return getFieldByClass(TsRequirementsTableField.class);
//	}
//
	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
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
		return getTsId();
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Ts");
	}

	public void startView() {
		startInternal(new ViewHandler());
	}
	
	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		@Override
		protected int getConfiguredGridColumnCount() {
			return 2;
		}
		
		// main box shall not be scrollable to keep it in its size
		@Override
		protected TriState getConfiguredScrollable() {
			return TriState.FALSE;
		}
		
		
		@Order(10000)
		public class TsHorizontalSplitBox extends AbstractSplitBox {
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
					return TEXTS.get("GeneralTestSuiteInformation");
				}
				
				// set all fields of this box to read-only
				@Override
				public boolean isEnabled() {
					return false;
				}
				
				@Order(1100)
				public class TsNameField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Name");
					}

					@Override
					protected int getConfiguredGridW() {
						return 1;
					}
				}
				
				@Order(1200)
				public class TsVersionField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("Version");
					}

					@Override
					protected int getConfiguredGridW() {
						return 1;
					}
				}

				@Order(1300)
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
						return 2;
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
			}

			@Order(2000)
			public class TsDetailedBox extends AbstractGroupBox {
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
					public class TcListBox extends AbstractListBox<String> {
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
							return TEXTS.get("TestCases"); 
						}

				        @Override
				        protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
				        	return TsTestcaseLookupCall.class;
				        }
				        @Override
				        protected void execPrepareLookup(ILookupCall<String> call) {
							TsFormData formData = new TsFormData();
							exportFormData(formData);
							TsTestcaseLookupCall c = (TsTestcaseLookupCall) call;
							c.setTsId(formData.getTsId());
				        }
					}

					@Order(2000)
					public class TsRequirementsTableField extends AbstractTableField<TsRequirementsTableField.TsRequirementsTable> {

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
						
						public class TsRequirementsTable extends AbstractTable {

							@Order(1000)
							public class RequirementIdColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("RequirementId");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
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
									return 400;
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
							return TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.DependenciesHorizontalSplitterBox.TcListBox.class;
						}
				
						@Override
						protected void execChangedMasterValue(Object newMasterValue) {
							// get the selected testcases
							Set<String> testcases = getTcListBox().getCheckedKeys();
							
							// get the requirements for the selected badges
							ITsService TsService = BEANS.get(ITsService.class);
							AbstractTableFieldBeanData requirementTableRows = TsService.loadRequirementsForTc(testcases);
							
							TsRequirementsTable requirementsTable = getTable();
							
							// cleanup table
							requirementsTable.deleteAllRows();
							
							// add requirements to table
							requirementsTable.importFromTableBeanData(requirementTableRows);
							// sort the table
							boolean tableSortEnable = requirementsTable.isSortEnabled();
							requirementsTable.setSortEnabled(true);
							requirementsTable.getColumnSet().addSortColumn(requirementsTable.getRequirementIdColumn(), true);
							requirementsTable.sort();
							requirementsTable.setSortEnabled(tableSortEnable);
							
							super.execChangedMasterValue(newMasterValue);
						}
						
						@Override
						protected void execInitField() {
							Set<String> requirements = CollectionUtility.hashSet(getTsId());
							
							// get the requirements for the selected badges
							ITsService TsService = BEANS.get(ITsService.class);
							AbstractTableFieldBeanData requirementTableRows = TsService.loadRequirements(requirements);
							
							TsRequirementsTable requirementsTable = getTable();
							
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
		}
	}
}
