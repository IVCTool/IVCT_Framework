/* Copyright 2020, Michael Theis, Felix Schöppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.client.ts;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.ts.TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.TcRequirementHorizontalSplitterBox.TsRequirementsTableField;
import nato.ivct.gui.client.ts.TsForm.MainBox.TsHorizontalSplitBox.TsDetailedBox.TcRequirementHorizontalSplitterBox.TsRequirementsTableField.CbRequirementsTable;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.cb.UpdateCbPermission;
import nato.ivct.gui.shared.ts.TsFormData;

@FormData(value = TsFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class TsForm extends AbstractForm {

	private String tsId;
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@FormData
	public String getTsId() {
		return tsId;
	}

	@FormData
	public void setTsId(String tsId) {
		this.tsId = tsId;
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

	public TsRequirementsTableField getTsRequirementsTableField() {
		return getFieldByClass(TsRequirementsTableField.class);
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
					return TEXTS.get("GeneralInformation");
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
				public class TsDescriptionField extends AbstractStringField {
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
				public class TcRequirementHorizontalSplitterBox extends AbstractSplitBox {
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
					public class TcTableField extends AbstractTableField<TcTableField.TcTable> {
                       @Override
                        protected String getConfiguredLabel() {
                            return TEXTS.get("TestCases");
                        }

                        @Override
                        protected int getConfiguredGridH() {
                            return 1;
                        }

                        @Override
						protected int getConfiguredGridW() {
							return 3;
						}

                        public class TcTable extends AbstractTable {
                        	
							@Order(999)
							public class TcIdColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("TcId");
								}
								@Override
								protected boolean getConfiguredDisplayable() {
									// column shall not be displayed
									return false;
								}
							}
							
							@Order(1000)
							public class TcNameColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("TcName");
								}

								@Override
								protected int getConfiguredWidth() {
									return 250;
								}
							}

							@Order(2000)
							public class TcDescColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("Description");
								}

								@Override
								protected int getConfiguredWidth() {
									return 1000;
								}
								
	                            @Override
	                            protected boolean getConfiguredTextWrap() {
	                                return true;
	                            }
							}
							
							public TcIdColumn getTcIdColumn() {
								return getColumnSet().getColumnByClass(TcIdColumn.class);
							}
							
							public TcNameColumn getTcNameColumn() {
								return getColumnSet().getColumnByClass(TcNameColumn.class);
							}

							public TcDescColumn getTcDescColumn() {
								return getColumnSet().getColumnByClass(TcDescColumn.class);
							}
							
							@Override
							protected boolean getConfiguredSortEnabled() {
								return false;
							}
							
							@Override
							public void sort() {
								super.sort();
							}
							
							@Override
							protected void execRowsSelected(List<? extends ITableRow> rows) {
								// first, clear the requirement table
								CbRequirementsTable requirementsTable = getTsRequirementsTableField().getTable();
								requirementsTable.deleteAllRows();
								
								if (getSelectedRowCount() > 0) {
									// load the associated requirements into the requirement table
									ITsService TsService = BEANS.get(ITsService.class);
									Set<String> selTc = new HashSet<>();
									rows.forEach(row -> {
										selTc.add(row.getCell(getTcIdColumn()).getValue().toString());
									});
									AbstractTableFieldBeanData requirementTableRows = TsService.loadRequirementsForTc(selTc);
									// add requirements to table
									requirementsTable.importFromTableBeanData(requirementTableRows);
								}
							}
                        }
					}

					@Order(2000)
					public class TsRequirementsTableField extends AbstractTableField<TsRequirementsTableField.CbRequirementsTable> {

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
							
							@Override
							protected boolean getConfiguredSortEnabled() {
								return false;
							}
						}
					}
				}
			}
		}
	}

	public class ViewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ITsService tsSservice = BEANS.get(ITsService.class);
			TsFormData formData = new TsFormData();
			exportFormData(formData);
			formData = tsSservice.load(formData);
			importFormData(formData);
			setEnabledPermission(new UpdateCbPermission());
		}
	}
}
