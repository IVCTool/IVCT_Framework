package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractIntegerColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.ReqDescrField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TestCaseExecutionStatusTableField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.GeneralBox.TestCaseNameField;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox;
import nato.ivct.gui.client.sut.SuTTcExecutionForm.MainBox.TcExecutionDetailsBox.DetailsHorizontalSplitBox.TcExecutionLogField;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.SuTTcExecutionFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;

@FormData(value = SuTTcExecutionFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTTcExecutionForm extends AbstractForm {
	
	private String sutId = null;
	private String badgeId = null;
	private String requirementId = null;
	private String testCaseId = null;

	@FormData
	public String getTestCaseId() {
		return testCaseId;
	}
	public void setTestCaseId(String testCaseId) {
		this.testCaseId = testCaseId;
	}
	
	@FormData
	public String getRequirementId() {
		return requirementId;
	}	
	@FormData
	public void setRequirementId(final String _sutCapabilityId) {
		this.requirementId = _sutCapabilityId;
	}

	@FormData
	public String getBadgeId() {
		return badgeId;
	}

	@FormData
	public void setBadgeId(final String _badgeId) {
		this.badgeId = _badgeId;
	}

	@FormData
	public String getSutId() {
		return sutId;
	}

	@FormData
	public void setSutId(final String _sutId) {
		this.sutId = _sutId;
	}
	
	@Override
	protected String getConfiguredTitle() {
		// TODO [the] verify translation
		return TEXTS.get("SuTTcExecution");
	}

	public void startView() {
		startInternal(new ViewHandler());
	}
	
//	public void startModify() {
//		startInternalExclusive(new ModifyHandler());
//	}
//
//	public void startNew() {
//		startInternal(new NewHandler());
//	}

//	public CancelButton getCancelButton() {
//		return getFieldByClass(CancelButton.class);
//	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public GeneralBox getGeneralBox() {
		return getFieldByClass(GeneralBox.class);
	}

	public ReqDescrField getDescrField() {
		return getFieldByClass(ReqDescrField.class);
	}

	public TestCaseNameField getTestCaseNameField() {
		return getFieldByClass(TestCaseNameField.class);
	}
	
	public TestCaseExecutionStatusTableField getTestCaseExecutionStatusTableField() {
		return getFieldByClass(TestCaseExecutionStatusTableField.class);
	}
	public TcExecutionDetailsBox getTcExecutionDetailsBox() {
		return getFieldByClass(TcExecutionDetailsBox.class);
	}
	
	public TcExecutionLogField getTcExecutionLogField() {
		return getFieldByClass(TcExecutionLogField.class);
	}

//	public OkButton getOkButton() {
//		return getFieldByClass(OkButton.class);
//	}

	@Order(10000)
	public class MainBox extends AbstractGroupBox {
		
		@Order(1000)
		public class GeneralBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Requirement");
			}
			
			@Override
			public boolean isEnabled() {
				// set all fields to read-only
				return false;
			}
			
			@Order(1010)
			public class ReqDescrField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("RequirementDescription");
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

				@Override
				protected int getConfiguredMaxLength() {
					return 256;
				}
			}

			@Order(1020)
			public class TestCaseNameField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("TC");
				}
				
				@Override
				protected int getConfiguredGridW() {
					return 3;
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(1030)
			public class TestCaseExecutionStatusTableField extends AbstractTableField<TestCaseExecutionStatusTableField.Table> {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("TCAusfhrung");
				}

				@Override
				protected int getConfiguredGridH() {
					return 1;
				}
				
				@Override
				protected int getConfiguredGridW() {
					return 2;
				}
				

				public class Table extends AbstractTable {
					
					@Override
					protected boolean getConfiguredHeaderVisible() {
						// do not show headline
						return true;
					}
					
					public ProgressColumn getProgressColumn() {
						return getColumnSet().getColumnByClass(ProgressColumn.class);
					}

					public TcStatusColumn getTcStatusColumn() {
						return getColumnSet().getColumnByClass(TcStatusColumn.class);
					}

					@Order(1000)
					public class TcStatusColumn extends AbstractStringColumn {
						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("TCStatus");
						}

						@Override
						protected int getConfiguredWidth() {
							return 200;
						}
					}

					@Order(2000)
					public class ProgressColumn extends AbstractIntegerColumn {
						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("Progress");
						}
						@Override
						protected int getConfiguredHorizontalAlignment() {
							// left alignment
							return -1;
						}

						@Override
						protected int getConfiguredWidth() {
							return 200;
						}
						
						@Override
						protected String getConfiguredBackgroundEffect() {
							return BackgroundEffect.BAR_CHART;
						}
						
					}
				}
			}
		}
		
		@Order(2000)
		public class TcExecutionDetailsBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("TCExecutionDetails");
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
				return 0.4;
				}

				@Order(1000)
				public class TcExecutionHistoryTableField extends AbstractTableField<TcExecutionHistoryTableField.TcExecutionHistoryTable> {

					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("TcExecutionHistory");
					}
					
					@Override
					protected int getConfiguredGridH() {
						return 3;
					}
					
					@Override
					protected int getConfiguredGridW() {
						return 3;
					}
					
					public class TcExecutionHistoryTable extends AbstractTable {
						
						@Override
						protected boolean getConfiguredMultiSelect() {
							// only a single row can be selected
							return false;
						}
						
						@Override
						protected void execRowsSelected(List<? extends ITableRow> rows) {
							// TODO Auto-generated method stub
							if (getSelectedRowCount() > 0) {
								String tcName = getTable().getFileNameColumn().getValue(getSelectedRow());
								// load log file content
								ISuTTcService service = BEANS.get(ISuTTcService.class);
								SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
								exportFormData(formData);
								formData = service.loadLogFile(formData, tcName);
								importFormData(formData);
							}
							else {
								getTcExecutionLogField().setValue(null);
							}
						}
						
						@Order(1000)
						public class FileNameColumn extends AbstractStringColumn {

							@Override
							protected String getConfiguredHeaderText() {
								return TEXTS.get("FileName");
							}

							@Override
							protected int getConfiguredWidth() {
								return 200;
							}
						}

						@Order(2000)
						public class TcVerdictColumn extends AbstractStringColumn {
							@Override
							protected String getConfiguredHeaderText() {
								return TEXTS.get("TCResult");
							}

							@Override
							protected int getConfiguredWidth() {
								return 200;
							}
						}
						
						public FileNameColumn getFileNameColumn() {
							return this.getColumnSet().getColumnByClass(FileNameColumn.class);
						}

						public TcVerdictColumn getTcVerdictColumn() {
							return getColumnSet().getColumnByClass(TcVerdictColumn.class);
						}
					}
				}
				
				@Order(2000)
				public class TcExecutionLogField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("TcExecutionLog");
					}
					
					@Override
					protected int getConfiguredGridH() {
						// TODO Auto-generated method stub
						return 5;
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
						// set to read-only
						return false;
					}
				}
			}
		}

//		@Order(100000)
//		public class OkButton extends AbstractOkButton {
//		}
//
//		@Order(101000)
//		public class CancelButton extends AbstractCancelButton {
//		}
	}

	public class ViewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ISuTTcService service = BEANS.get(ISuTTcService.class);
			SuTTcExecutionFormData formData = new SuTTcExecutionFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
//			getForm().setSubTitle(formData.getName().getValue());

			setEnabledPermission(new UpdateSuTPermission());
		}
	}

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
		}

		@Override
		protected void execStore() {
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
		}

		@Override
		protected void execStore() {
		}
	}
}
