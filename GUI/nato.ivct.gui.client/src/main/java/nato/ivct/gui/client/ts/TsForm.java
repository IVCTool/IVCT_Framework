package nato.ivct.gui.client.ts;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.form.fields.treebox.AbstractTreeBox;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.slf4j.LoggerFactory;

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
