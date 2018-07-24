package nato.ivct.gui.client.sut;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ColumnSet;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.TEXTS;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbNameField;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbFormData;

@FormData(value = SuTCbFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTCbForm extends AbstractForm {

	private String sutId = null;
	private String cbId = null;
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	// unique table row ID generator
	private AtomicLong m_nextRowId = new AtomicLong();

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
	
	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

	public void startNew() {
		startInternal(new NewHandler());
	}

//	public CancelButton getCancelButton() {
//		return getFieldByClass(CancelButton.class);
//	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public GeneralBox getGeneralBox() {
		return getFieldByClass(GeneralBox.class);
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

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@FormData
	public String getCbId() {
		return cbId;
	}

	@FormData
	public void setCbId(final String _cbId) {
		this.cbId = _cbId;
	}

	@FormData
	public String getSutId() {
		return sutId;
	}

	@FormData
	public void setSutId(final String _sutId) {
		this.sutId = _sutId;
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
					return TEXTS.get("GeneralCapabilityInformation");
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
						return TEXTS.get("CbName");
					}

					@Override
					protected int getConfiguredGridW() {
						return 3;
					}
				}

				@Order(2000)
				public class CbDescriptionField extends AbstractStringField {
					@Override
					protected String getConfiguredLabel() {
						return TEXTS.get("CbDescription");
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

				@Order(3000)
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
						return 3;
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
			public class SuTCbDetailsBox extends AbstractGroupBox {

				@Order(1000)
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
					public class SuTCbParameterTableField extends AbstractTableField<SuTCbParameterTableField.SuTCbParameterTable> {
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
							return TEXTS.get("BadgeParameterSettings"); 
						}

				        @Override
				        protected void execInitField() {
				          getTable().replaceRows(createInitialRows());
//				          getTable()BORDER_DECORATION_AUTO.expandAll(null);
				        }

				        private List<ITableRow> createInitialRows() {
				          List<ITableRow> rows = new ArrayList<>();
				          ITableRow javaRow = createRow(null, "JavaKey", "JavaValue");
				          rows.add(javaRow);

				          ITableRow eclipseRow = createRow(null, "EclipseKey", null);
				          rows.add(eclipseRow);
				          ITableRow usaRow = createRow(eclipseRow, "USAKey", "USAValue");
				          rows.add(usaRow);
				          ITableRow jsRow = createRow(null, "JavaScriptKey", "JavaScriptValue");
				          rows.add(jsRow);
				          return rows;
				        }
						
				        private ITableRow createRow(ITableRow parentRow, String key, String value) {
				            SuTCbParameterTable table = getTable();
				            ITableRow row = table.createRow();
				            table.getIdColumn().setValue(row, m_nextRowId.getAndIncrement());
				            table.getParentIdColumn().setValue(row, Optional.ofNullable(parentRow).map(r -> table.getIdColumn().getValue(parentRow)).orElse(null));
				            table.getParameterNameColumn().setValue(row, key);
				            table.getParameterValueColumn().setValue(row, value);
//				            table.getNameColumn().setValue(row, name);
//				            table.getLocationColumn().setValue(row, location);
//				            table.getDateColumn().setValue(row, date);
//				            table.getStartColumn().setValue(row, startTime);
//				            table.getEndDateTimeColumn().setValue(row, endDateTime);
//				            table.getIndustryColumn().setValue(row, industery);
				            return row;
				          }

				          private void newRow() {
				            newRowWithParent(null);
				          }

				          private void newRowWithParent(ITableRow parent) {
				            SuTCbParameterTable table = getTable();
				            ColumnSet cols = table.getColumnSet();
				            ITableRow row = new TableRow(cols);

				            row.getCellForUpdate(table.getIdColumn()).setValue(m_nextRowId.getAndIncrement());
				            row.getCellForUpdate(table.getParentIdColumn()).setValue(Optional.ofNullable(table.getIdColumn().getValue(parent)).orElse(null));
//				            row.getCellForUpdate(table.getNameColumn()).setValue("New Row");
//				            row.getCellForUpdate(table.getTrendColumn()).setValue(AbstractIcons.LongArrowUp);
//				            ExampleBean bean = new ExampleBean();
//				            bean.setHeader("header property");
//				            row.getCellForUpdate(table.getCustomColumn()).setValue(bean);

				            table.addRow(row, true);
				          }
				          
				          public class SuTCbParameterTable extends AbstractTable {
				        	  
						  public ParentIdColumn getParentIdColumn() {
						      return getColumnSet().getColumnByClass(ParentIdColumn.class);
						  }

						  public IdColumn getIdColumn() {
							    return getColumnSet().getColumnByClass(IdColumn.class);
						  }
							
						  public ParameterNameColumn getParameterNameColumn() {
						    return getColumnSet().getColumnByClass(ParameterNameColumn.class);
						  }
							
						  public ParameterValueColumn getParameterValueColumn() {
						    return getColumnSet().getColumnByClass(ParameterValueColumn.class);
						  }

				          @Override
				          protected void execDecorateRow(ITableRow row) {
				            if (getParameterValueColumn().getValue(row) == null) {
				              row.setIconId("font:\uE001");
				            }
				          }

						  @Order(10)
						  public class IdColumn extends AbstractLongColumn {
						
						    @Override
						    protected boolean getConfiguredDisplayable() {
						      return false;
						    }
						
						    @Override
						    protected boolean getConfiguredPrimaryKey() {
						      return true;
						    }
						
						  }
						
					  	  @Order(15)
						  public class ParentIdColumn extends AbstractLongColumn {
						
							    @Override
							    protected boolean getConfiguredDisplayable() {
							      return false;
							    }
						  	}

							
							@Order(1000)
							public class ParameterNameColumn extends AbstractStringColumn {

								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("ParameterName");
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}

							@Order(2000)
							public class ParameterValueColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("ParameterValue");
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}
						}
					}

					@Order(2000)
					public class CbRequirementsTableField extends AbstractTableField<CbRequirementsTableField.CbRequirementsTable> {
						@Override
						protected int getConfiguredGridW() {
							return 3;
						}
						
						@Override
						protected String getConfiguredLabel() {
							return TEXTS.get("Requirements");
						}

						@Override
						protected int getConfiguredGridH() {
							return 6;
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

							@Order(3000)
							public class AbstractTCColumn extends AbstractStringColumn {
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("TC");
								}

								@Override
								protected int getConfiguredWidth() {
									return 200;
								}
							}
							
							public RequirementIdColumn getRequirementIdColumn() {
								return getColumnSet().getColumnByClass(RequirementIdColumn.class);
							}

							public RequirementDescColumn getRequirementDescColumn() {
								return getColumnSet().getColumnByClass(RequirementDescColumn.class);
							}

							public AbstractTCColumn getAbstractTCColumn() {
								return getColumnSet().getColumnByClass(AbstractTCColumn.class);
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

//		@Order(101000)
//		public class CancelButton extends AbstractCancelButton {
//
//		}
	}

	protected abstract class AbstractCbFormHandler extends AbstractFormHandler {
		@Override
		protected void execLoad() {
			ISuTCbService service = BEANS.get(ISuTCbService.class);
			SuTCbFormData formData = new SuTCbFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
			// load badge image
			try (InputStream in = ResourceBase.class.getResourceAsStream("icons/" + formData.getCbId() + ".png")) {
				getCbImageField().setImage(IOUtility.readBytes(in));
				getCbImageField().setImageId(formData.getCbId());
			} catch (Exception e) {
				logger.warn("Could not load image file: " + formData.getCbId() + ".png");
			}
			getForm().setSubTitle(formData.getCbName().getValue());
//			setEnabledPermission(new UpdateCbPermission());
		}
	}
	
	public class ViewHandler extends AbstractCbFormHandler {

		@Override
		protected void execLoad() {
			super.execLoad();
			getForm().getFieldByClass(MainBox.CloseButton.class).setVisible(false);
		}
	}

	public class ModifyHandler extends AbstractCbFormHandler {

		@Override
		protected void execLoad() {
			super.execLoad();
		}

		@Override
		protected void execStore() {
			ISuTCbService service = BEANS.get(ISuTCbService.class);
			SuTCbFormData formData = new SuTCbFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}

	public class NewHandler extends AbstractCbFormHandler {

		@Override
		protected void execLoad() {
			ISuTCbService service = BEANS.get(ISuTCbService.class);
			SuTCbFormData formData = new SuTCbFormData();
			exportFormData(formData);
			formData = service.prepareCreate(formData);
			importFormData(formData);

			setEnabledPermission(new CreateSuTPermission());
		}

		@Override
		protected void execStore() {
			ISuTCbService service = BEANS.get(ISuTCbService.class);
			SuTCbFormData formData = new SuTCbFormData();
			exportFormData(formData);
			service.create(formData);
		}
	}
}
