package nato.ivct.gui.client.sut;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ColumnSet;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
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
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.eclipse.scout.rt.platform.text.TEXTS;
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
					return 0.5;
					}
					
					public SuTCbParameterTableField getSuTCbParameterTableField() {
						return getFieldByClass(SuTCbParameterTableField.class);
					}

					@Order(1000)
					public class SuTCbParameterTableField extends AbstractTableField<SuTCbParameterTableField.SuTCbParameterTable> {
				        List<ITableRow> mRows = new ArrayList<>();

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
							  getTable().expandAll(null);
				        }

				        private List<ITableRow> createInitialRows() {
				          
				          ISuTCbService service = BEANS.get(ISuTCbService.class);
				          String sParams = service.loadBadgeParams(getSutId(), getCbId());
				          if (sParams != null)
				          {
				        	  // param file does not exist
					          JSONParser parser = new JSONParser();
					          Object jParams = null;
					          try {
					        	  jParams = parser.parse(sParams);
					          } catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
					          }
	
					          if (jParams != null) {
					        	 addJsonObjectToTable(null, jParams);
					          }
				          }
				          
				          return mRows;
				        }
				        
				        private void addJsonObjectToTable(final ITableRow parentRow, final Object jObject) {
				        	if (jObject instanceof JSONObject) {
				        		if (((JSONObject) jObject).isEmpty())
				        			return;
				        		// handle JSONObject of the form (key:value)
				        		((JSONObject) jObject).forEach((key, value) -> {
				        			if (value instanceof JSONObject || value instanceof JSONArray) {
				        				// value is itself a JSONObject or JSONArray
				        				ITableRow newRow = addElementToTable(parentRow, key.toString(), null);
					        			addJsonObjectToTable(newRow, value);
				        			} else {
				        				// value is a simple object
				        				addElementToTable(parentRow, key.toString(), value.toString());
				        			}
				        		});
				        	} else if (jObject instanceof JSONArray) {
				        		// handle as JSONArray
				        		((JSONArray) jObject).forEach(value -> {
				        			addJsonObjectToTable(parentRow, value);
				        		});
				        	} else {
				        		// handle as element without having a key
				        		addElementToTable(parentRow, null, jObject.toString());
				        	}
				        	
				        	return;
				        }
				        
				        private ITableRow addElementToTable(final ITableRow parentRow, final String key, final String value) {
				            SuTCbParameterTable table = getTable();
				            ITableRow row = table.createRow();
				            table.getIdColumn().setValue(row, m_nextRowId.getAndIncrement());
				            table.getParentIdColumn().setValue(row, Optional.ofNullable(parentRow).map(r -> table.getIdColumn().getValue(parentRow)).orElse(null));
				            table.getParameterNameColumn().setValue(row, key);
				            table.getParameterValueColumn().setValue(row, value);
				            
				            //add row to table
				           mRows.add(row);
				            
				            return row;
				          }

				          private void newRow() {
				            newRowWithParent(null);
				          }

				          private void newRowWithParent(final ITableRow parent) {
				            SuTCbParameterTable table = getTable();
				            ColumnSet cols = table.getColumnSet();
				            ITableRow row = new TableRow(cols);

				            row.getCellForUpdate(table.getIdColumn()).setValue(m_nextRowId.getAndIncrement());
				            row.getCellForUpdate(table.getParentIdColumn()).setValue(Optional.ofNullable(table.getIdColumn().getValue(parent)).orElse(null));

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
					            protected boolean getConfiguredParentKey() {
					              return true;
					            }
						
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
							
//							@Order(110)
//							public class NewMenu extends AbstractMenu {
//							
//							    @Override
//							    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
//							    	return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
//							    }
//							
//							    @Override
//							    protected String getConfiguredText() {
//							    	return TEXTS.get("New");
//							    }
//							
//							    @Override
//							    protected void execAction() {
//							    	newRowWithParent(getTable().getSelectedRow());
//							    }
//							}
							
					        @Order(120)
					        public class EditMenu extends AbstractMenu {

					        	@Override
					            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
//					           		return CollectionUtility.<IMenuType> hashSet(TableMenuType.SingleSelection);
					           		return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
					           	}

					            @Override
					            protected String getConfiguredText() {
					            	return TEXTS.get("Edit");
					            }

					            @Override
					            protected void execAction() {
//					            	if (getTable().getSelectedRowCount()==1) {
//					            		getTable().getParameterValueColumn().setEditable(true);
			            			    List<IColumn<?>> columns = getSuTCbParameterTableField().getTable().getColumns();
					            		SuTBdParamForm badgeParamForm = new SuTBdParamForm(columns);
					            		badgeParamForm.setDisplayHint(DISPLAY_HINT_DIALOG);
//					            		try {
						            		badgeParamForm.startNew();
						            		badgeParamForm.waitFor();
//										} catch (CloneNotSupportedException exc) {
//											// TODO Auto-generated catch block
//											exc.printStackTrace();
//										}
//					            	}
					            }
					        }
							
//					        @Order(130)
//					        public class DeleteMenu extends AbstractMenu {
//
//					        	@Override
//					            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
//					           		return CollectionUtility.<IMenuType> hashSet(TableMenuType.MultiSelection, TableMenuType.SingleSelection);
//					           	}
//
//					            @Override
//					            protected String getConfiguredText() {
//					            	return TEXTS.get("DeleteMenu");
//					            }
//
//					            @Override
//					            protected void execAction() {
//					            	List<ITableRow> rows = getSelectedRows();
//					            	deleteRows(rows);
//					            }
//					        }
					        
					        @Override
					        protected void execRowsSelected(List<? extends ITableRow> rows) {
					        	// Set table to read-only if no row is selected
					        	if (rows.isEmpty())
					        		getTable().getParameterValueColumn().setEditable(false);
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
