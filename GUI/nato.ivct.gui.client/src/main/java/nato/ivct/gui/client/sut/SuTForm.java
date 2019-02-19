package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.sut.SuTForm.MainBox.CloseButton;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.DetailsHorizontalSplitterBox.CapabilityStatusBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.DescrField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.NameField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.SutVendorField;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;

@FormData(value = SuTFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTForm extends AbstractForm {

	private String sutId = null;
	private String title = null;

	public SuTForm(String formTitle) {
		title = formTitle;
	}

	@Override
	protected String getConfiguredTitle() {
		if (title != null)
			return title;
		else
			return TEXTS.get("SuT");
	}

	public void startView() {
		startInternal/*Exclusive*/(new ViewHandler());
	}

	public CloseButton getCloseButton() {
		return getFieldByClass(CloseButton.class);
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

	public CapabilityStatusBox getDetailsBox() {
		return getFieldByClass(CapabilityStatusBox.class);
	}

	public NameField getNameField() {
		return getFieldByClass(NameField.class);
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
	public Object computeExclusiveKey() {
		return getSutId();
	}

	@Override
	protected int getConfiguredDisplayHint() {
		// TODO Auto-generated method stub
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
					return TEXTS.get("GeneralSuTInformation");
				}
				
				// set all fields of this box to read-only
				@Override
				public boolean isEnabled() {
					return false;
				}
	
				@Order(1000)
				public class NameField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("SuTName");
					}
	
					@Override
					protected int getConfiguredMaxLength() {
						return 128;
					}
				}
				
				@Order(1500)
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
	
				@Order(2000)
				public class SutVendorField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("SuTVendor");
					}
	
					@Override
					protected int getConfiguredMaxLength() {
						return 128;
					}
				}
	
				@Order(3000)
				public class DescrField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("SuTDescription");
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
			}

			@Order(2000)
			public class DetailsHorizontalSplitterBox extends AbstractSplitBox {
				
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
				public class CapabilityStatusBox extends AbstractGroupBox {
					
					@Order(1000)
					public class SutCapabilityStatusTableField extends AbstractTableField<SutCapabilityStatusTableField.SutCapabilityStatusTable> {
						@Override
						protected int getConfiguredGridW() {
							return 3;
						}
						
						@Override
						protected String getConfiguredLabel() {
							return TEXTS.get("CapabilityStatus");
						}

						public class SutCapabilityStatusTable extends AbstractTable {
							@Order(1000)
							public class CbBadgeID extends AbstractColumn<String> {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("BadgeId");
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}
							
							@Order(2000)
							public class CbBadgeStatus extends AbstractColumn<String> {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("BadgeConformanceStatus");
								}

								@Override
								protected int getConfiguredWidth() {
									return 100;
								}
							}
						}
					}
				}

				@Order(2000)
				public class TestReportBox extends AbstractGroupBox {

					@Order(2000)
					public class TestReportTableField extends AbstractTableField<TestReportTableField.TestReportTable> {
						
						@Override
						protected String getConfiguredLabel() {
							return TEXTS.get("TestReports");
						}

						@Override
						protected int getConfiguredGridW() {
							return 3;
						}
						
						@Order(1000)
						public class TestReportTable extends AbstractTable {
							@Order(1000)
							public class TestReport extends AbstractColumn<String> {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("FileName");
								}

								@Override
								protected int getConfiguredWidth() {
									return 400;
								}

							}
						}
					}
				}
			}
		}
		
		@Order(100000)
		public class CloseButton extends AbstractButton {
			
			public CloseButton() {
				super();
				// set button invisible by default
				setVisible(false);
			}
			
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

	public class ViewHandler extends AbstractFormHandler {
		
		@Override
		protected void execLoad() {
			ISuTService service = BEANS.get(ISuTService.class);
			SuTFormData formData = new SuTFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);

			setEnabledPermission(new UpdateSuTPermission());
		}
	}
}
