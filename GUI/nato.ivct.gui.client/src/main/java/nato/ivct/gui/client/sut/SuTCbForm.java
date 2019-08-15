package nato.ivct.gui.client.sut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.filechooser.FileChooser;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ColumnSet;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.TableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.OpenUriAction;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonWriter;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.CbNameField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SuTCbDetailsBox.DetailsHorizontalSplitterBox.SutParameterBox.ParameterHorizontalSplitterBox.SuTTcParameterTableField.SuTTcParameterTable.SaveMenu;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SuTCbDetailsBox.DetailsHorizontalSplitterBox.SutParameterBox.ParameterHorizontalSplitterBox.SutTcExtraParameterTableField;
import nato.ivct.gui.shared.cb.ICbService;
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

	public SutTcExtraParameterTableField getSutExtraParameterTableField() {
		return getFieldByClass(SutTcExtraParameterTableField.class);
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
			return 0.25;
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
					return 0.8;
					}
					
					@Order (1000)
					public class SutParameterBox extends AbstractGroupBox {
						@Order(1000)
						public class ParameterHorizontalSplitterBox extends AbstractSplitBox {
							@Override
							protected boolean getConfiguredSplitHorizontal() {
								// split horizontal
								return false;
							}
							
							@Override
							protected double getConfiguredSplitterPosition() {
							return 0.6;
							}

							public SuTTcParameterTableField getSuTTcParameterTableField() {
								return getFieldByClass(SuTTcParameterTableField.class);
							}

							@Order(1000)
							public class SuTTcParameterTableField extends AbstractTableField<SuTTcParameterTableField.SuTTcParameterTable> {
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
									return TEXTS.get("TcParameterSettings"); 
								}
	
						        @Override
						        protected void execInitField() {
					        		mRows.clear();
									getTable().addRows(loadAllRows());
									getTable().expandAll(null);
						        }
						        
						        @Override
						        protected void execReloadTableData() {
						        	getTable().discardAllRows();
						        	getTable().addRows(mRows);
						        	getTable().expandAll(null);
						        	
						        	// clear modified status and hide save menu
						        	getSuTTcParameterTableField().execMarkSaved();
						        	getSuTTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(false);
						        }
						        
						        // Save the TC parameters from the SuTCbParameterTable
						        @Override
						        public void doSave() {
						        	SuTTcParameterTable tbl = getSuTTcParameterTableField().getTable();
						        	List<ITableRow> rows = tbl.getRows();
	
			                        // map to record all JSONObject and all JSONArray
		                            HashMap<ITableRow, Object> jsonElements = new HashMap<>();
	
						        	// root JSON object
						        	JSONObject jsonObjects = new JSONObject();
						        	//record root JSON object
						        	jsonElements.put(null, jsonObjects);
						        	
						        	// interate over all children and grand-children of the root object
						        	for(ITableRow row : rows) {
						        		ITableRow parentRow = row.getParentRow();
						        		
						        		String key = row.getCell(tbl.getParameterNameColumn()).getText();
						        		String value = row.getCell(tbl.getParameterValueColumn()).getText();
						        		
						        		// if the parent object is already known, then use it. Otherwise create it
						        		Object jsonParentElement = jsonElements.getOrDefault(parentRow, jsonObjects);
						        		Object jsonElement = null;
						        		
						        		if ("[".equals(value)) {
						        		    jsonElement = new JSONArray();
						        		    jsonElements.put(row, jsonElement);
						        		} else if ("{".equals(value)) {
						        		    jsonElement = new JSONObject();
						        		    jsonElements.put(row, jsonElement);
						        		} else {
						        		    if (jsonParentElement instanceof JSONArray && key != null) {
						        		        jsonElement = new JSONObject();
						        		        ((JSONObject) jsonElement).put(key, value);
						        		    }
						        		    else
						        		        jsonElement = value;
						        		}
						        		
						        		if (jsonParentElement instanceof JSONArray)
						        		    ((JSONArray) jsonParentElement).add(jsonElement);
						        		else
						        		    ((JSONObject) jsonParentElement).put(key != null ? key : "", jsonElement);
						        	}
						        	
							        ISuTCbService service = BEANS.get(ISuTCbService.class);
							        Map<String, Object> options = new HashMap<>();
							        options.put(JsonWriter.PRETTY_PRINT ,true);
							        options.put(JsonWriter.TYPE, false);
							        service.storeTcParams(getSutId(), getCbId(), JsonWriter.objectToJson(jsonObjects, options));
						        	
						        	// call super
						        	super.doSave();
						        }
						        
								private List<ITableRow> loadAllRows() {
						          ISuTCbService service = BEANS.get(ISuTCbService.class);
						          String sParams = service.loadTcParams(getSutId(), getCbId());
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
						        				ITableRow newRow = addElementToTable(parentRow, key.toString(), value instanceof JSONObject ? "{" : "[");
							        			addJsonObjectToTable(newRow, value);
						        			} else {
						        				// value is a simple object
						        				addElementToTable(parentRow, key.toString(), Objects.toString(value, null));
						        			}
						        		});
						        	} else if (jObject instanceof JSONArray) {
						        		// handle as JSONArray
						        		((JSONArray) jObject).forEach(value -> addJsonObjectToTable(parentRow, value));
						        	} else {
						        		// handle as element without having a key
						        		addElementToTable(parentRow, null, jObject.toString());
						        	}
						        }
						        
						        private ITableRow addElementToTable(final ITableRow parentRow, final String key, final String value) {
						            SuTTcParameterTable table = getTable();
						            ITableRow row = table.createRow();
						            table.getIdColumn().setValue(row, m_nextRowId.getAndIncrement());
						            table.getParentIdColumn().setValue(row, Optional.ofNullable(parentRow).map(r -> table.getIdColumn().getValue(parentRow)).orElse(null));
						            table.getParameterNameColumn().setValue(row, key);
						            table.getParameterValueColumn().setValue(row, value);
						            
						            //add row to table
						            mRows.add(row);
						            
						            return row;
						        }
	
								private void newRowWithParent(final ITableRow parent) {
								    SuTTcParameterTable table = getTable();
								    ColumnSet cols = table.getColumnSet();
								    ITableRow row = new TableRow(cols);
								
								    row.getCellForUpdate(table.getIdColumn()).setValue(m_nextRowId.getAndIncrement());
								    row.getCellForUpdate(table.getParentIdColumn()).setValue(Optional.ofNullable(table.getIdColumn().getValue(parent)).orElse(null));
								
								    table.addRow(row, true);
								}
	
								public class SuTTcParameterTable extends AbstractTable {
						        	
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
							        
									@Override
									protected void execContentChanged() {
										// show save menu if a table value was changed
										if (execIsSaveNeeded()) {
											getSuTTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
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
	
							        @Order(3000)
							        public class EditMenu extends AbstractMenu {
	
							        	@Override
							            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
							           		return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
							           	}
	
							            @Override
							            protected String getConfiguredText() {
							            	return TEXTS.get("Edit");
							            }
							            
							            @Override
							            protected void execAction() {
							            	SuTTcParameterTable tbl = getSuTTcParameterTableField().getTable();
							            	tbl.getParameterValueColumn().setEditable(true);
						            		tbl.getMenuByClass(EditMenu.class).setEnabled(false);
						            		
						            		// enable for use of adding/deleting parameters
						            		// N.B.: excluded from usage for the moment until functionality is completely implemented
						            		if (false) {
							            		tbl.getMenuByClass(NewMenu.class).setVisible(true);
							            		tbl.getMenuByClass(DeleteMenu.class).setVisible(true);
						            		} 
						            		else {
						            			tbl.getRows().forEach(row -> {
						            				if (row.getCellValue(tbl.getParameterValueColumn().getColumnIndex()).toString().equals("[")
						            						|| row.getCellValue(tbl.getParameterValueColumn().getColumnIndex()).toString().equals("{")) {
						            					row.setEnabled(false);
						            					tbl.getMenuByClass(AbortMenu.class).setVisible(true);
						            					row.   getCell(getParameterNameColumn());
						            				}
						            			});
						            		}
						            		
						            		tbl.getMenuByClass(AbortMenu.class).setVisible(true);
							            }
							        }
								
									@Order(3100)
									public class NewMenu extends AbstractMenu {
									
									    @Override
									    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
									    	return CollectionUtility.<IMenuType> hashSet(TableMenuType.EmptySpace);
									    }
									
									    @Override
									    protected String getConfiguredText() {
									    	return TEXTS.get("New");
									    }
									    
									    @Override
									    protected boolean getConfiguredVisible() {
									    	return false;
									    }
									
									    @Override
									    protected void execAction() {
									    	newRowWithParent(getTable().getSelectedRow());
									    	//set parameter column editable
									    	getSuTTcParameterTableField().getTable().getParameterNameColumn().setEditable(true);
									    	// set save botton visible
									    	getSuTTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
									    }
	
		                                @Order(3110)
		                                public class NewJsonObjectMenu extends AbstractMenu {
		                                    @Override
		                                    protected String getConfiguredText() {
		                                        return TEXTS.get("JSONObject");
		                                    }
	
		                                    @Override
		                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
		                                    }
		                                }
	
		                                @Order(3120)
		                                public class NewJsonArrayMenu extends AbstractMenu {
		                                    @Override
		                                    protected String getConfiguredText() {
		                                        return TEXTS.get("JSONArray");
		                                    }
	
		                                    @Override
		                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
		                                    }
		                                }
	
		                                @Order(3130)
		                                public class NewJsonElementMenu extends AbstractMenu {
		                                    @Override
		                                    protected String getConfiguredText() {
		                                        return TEXTS.get("JSONElement");
		                                    }
	
		                                    @Override
		                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
		                                    }
		                                }
	
		                                @Order(3140)
		                                public class NewJsonValueMenu extends AbstractMenu {
		                                    @Override
		                                    protected String getConfiguredText() {
		                                        return TEXTS.get("JSONValue");
		                                    }
	
		                                    @Override
		                                    protected Set<? extends IMenuType> getConfiguredMenuTypes() {
		                                        return CollectionUtility.hashSet(TableMenuType.EmptySpace);
		                                    }
		                                }
									}
									
							        @Order(3200)
							        public class DeleteMenu extends AbstractMenu {
	
							        	@Override
							            protected Set<? extends IMenuType> getConfiguredMenuTypes() {
							           		return CollectionUtility.<IMenuType> hashSet(TableMenuType.MultiSelection, TableMenuType.SingleSelection);
							           	}
	
							            @Override
							            protected String getConfiguredText() {
							            	return TEXTS.get("DeleteMenu");
							            }
									    
									    @Override
									    protected boolean getConfiguredVisible() {
									    	return false;
									    }
	
									    @Override
							            protected void execAction() {
							            	List<ITableRow> rows = getSelectedRows();
							            	deleteRows(rows);
							            	//set save button visible
							            	getSuTTcParameterTableField().getTable().getMenuByClass(SaveMenu.class).setVisible(true);
							            }
							        }
	
									@Order(3900)
									public class AbortMenu extends AbstractMenu {
										@Override
										protected String getConfiguredText() {
											return TEXTS.get("Abort");
										}
	
										@Override
										protected Set<? extends IMenuType> getConfiguredMenuTypes() {
											return CollectionUtility.hashSet(TableMenuType.EmptySpace);
										}
									    
									    @Override
									    protected boolean getConfiguredVisible() {
									    	return false;
									    }
									    
									    @Override
									    public byte getHorizontalAlignment() {
									    	return 1;
									    }
	
										@Override
										protected void execAction() {
							            	SuTTcParameterTable tbl = getSuTTcParameterTableField().getTable();
							            	
	//						            	if (tbl.getParameterValueColumn().isEditable()) {
							            		tbl.getParameterNameColumn().setEditable(false);
							            		tbl.getParameterValueColumn().setEditable(false);
							            		
							            		tbl.getMenuByClass(AbortMenu.class).setVisible(false);
							            		tbl.getMenuByClass(SaveMenu.class).setVisible(false);
							            		tbl.getMenuByClass(NewMenu.class).setVisible(false);
							            		tbl.getMenuByClass(DeleteMenu.class).setVisible(false);
	
							            		tbl.getMenuByClass(EditMenu.class).setEnabled(true);
							            		
							            		// reload values if altered
							            		if (execIsSaveNeeded()) {
							            			getSuTTcParameterTableField().reloadTableData();
							            		}
	//						            	}
										}
									}
	
									@Order(3910)
									public class SaveMenu extends AbstractMenu {
										@Override
										protected String getConfiguredText() {
											return TEXTS.get("SaveChanges");
										}
	
										@Override
										protected Set<? extends IMenuType> getConfiguredMenuTypes() {
											return CollectionUtility.hashSet(TableMenuType.EmptySpace);
										}
									    
									    @Override
									    protected boolean getConfiguredVisible() {
									    	return false;
									    }
									    
									    @Override
									    public byte getHorizontalAlignment() {
									    	return 1;
									    }
	
										@Override
										protected void execAction() {
							            	SuTTcParameterTable tbl = getSuTTcParameterTableField().getTable();
							            	
						            		tbl.getParameterNameColumn().setEditable(false);
						            		tbl.getParameterValueColumn().setEditable(false);
						            		
						            		tbl.getMenuByClass(AbortMenu.class).setVisible(false);
						            		tbl.getMenuByClass(SaveMenu.class).setVisible(false);
						            		tbl.getMenuByClass(NewMenu.class).setVisible(false);
						            		tbl.getMenuByClass(DeleteMenu.class).setVisible(false);
	
						            		tbl.getMenuByClass(EditMenu.class).setEnabled(true);
							            	
											// ToDo: Save changes
							            	doSave();
							            	markSaved();
										}
									}
								}
							}
	
							@Order(2000)
							public class SutTcExtraParameterTableField extends AbstractTableField<SutTcExtraParameterTableField.SutTcExtraParameterTable> {
								@Override
								protected String getConfiguredLabel() {
									return TEXTS.get("TcExtraParameterFiles");
								}
	
								@Override
								protected int getConfiguredGridH() {
									return 1;
								}
								
								@Override
								protected int getConfiguredGridW() {
									return 3;
								}

								@Order (2100)
								public class SutTcExtraParameterTable extends AbstractTable {

									@Override
									protected boolean getConfiguredMultiSelect() {
										// only a single row can be selected
										return false;
									}

									public FileNameColumn getFileNameColumn() {
										return getColumnSet().getColumnByClass(FileNameColumn.class);
									}
									
									@Override
									protected void execRowsSelected(List<? extends ITableRow> rows) {
										if (rows.size() == 1) {
											// set downlowad menu visible
											getMenuByClass(FileDownloadMenu.class).setVisible(true);
										} else {
											// hide download menu if no row is selected
											getMenuByClass(FileDownloadMenu.class).setVisible(false);
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
											return 300;
										}
										
										@Override
										protected int getConfiguredSortIndex() {
											// sort table by this column
											return 10;
										}
									}

									@Order(2000)
									public class FileUploadMenuMenu extends AbstractMenu {
										@Override
										protected String getConfiguredText() {
											return TEXTS.get("FileUpload");
										}

										@Override
										protected Set<? extends IMenuType> getConfiguredMenuTypes() {
											return CollectionUtility.hashSet(TableMenuType.EmptySpace);
										}

										@Override
										protected void execAction() {
											FileChooser fileChooser = new FileChooser();
											List<BinaryResource> files = fileChooser.startChooser();
											ISuTCbService service = BEANS.get(ISuTCbService.class);
											files.forEach(file -> {
												if (service.copyUploadedTcExtraParameterFile(getSutId(), getCbId(), file)) {
													ITableRow row = getTable().addRow(getTable().createRow());
													getTable().getFileNameColumn().setValue(row, file.getFilename());
												}
											});
											getTable().sort();
										}
									}

									@Order(3000)
									public class FileDownloadMenu extends AbstractMenu {
										@Override
										protected String getConfiguredText() {
											return TEXTS.get("FileDownload");
										}

										@Override
										protected Set<? extends IMenuType> getConfiguredMenuTypes() {
											return CollectionUtility.hashSet(TableMenuType.EmptySpace);
										}
										
										@Override
										protected boolean getConfiguredVisible() {
											return false;
										}

										@Override
										protected void execAction() {
											// get the selected file name from the table
											ITableRow row = getTable().getSelectedRow();
											if (row == null)
												// no row selected - nothing to do
												return;
											// get the content of the selected file
											BinaryResource downloadFileResource = BEANS.get(ISuTCbService.class).getFileContent(getSutId(), getCbId(), getTable().getFileNameColumn().getValue(row));
											if (downloadFileResource.getContentLength() != -1) {
												getDesktop().openUri(downloadFileResource, OpenUriAction.DOWNLOAD);
											}
										}

									}
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


//		@Order(100000)
//		public class CloseButton extends AbstractButton {
//			
//			  @Override
//			  protected int getConfiguredSystemType() {
//			    return SYSTEM_TYPE_CLOSE;
//			  }
//
//			  @Override
//			  protected String getConfiguredLabel() {
//			    return TEXTS.get("CloseButton");
//			  }
//
//			  @Override
//			  protected String getConfiguredKeyStroke() {
//			    return IKeyStroke.ESCAPE;
//			  }
//		}

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
			byte[] badgeIcon = BEANS.get(ICbService.class).loadBadgeIcon(formData.getCbId());
			if (badgeIcon != null) {
				getCbImageField().setImage(badgeIcon);
				getForm().setSubTitle(formData.getCbName().getValue());
			}
			else {
				logger.warn("Could not load image file for badge ID " + formData.getCbId());
			}
//			setEnabledPermission(new UpdateCbPermission());
		}
	}
	
	public class ViewHandler extends AbstractCbFormHandler {

		@Override
		protected void execLoad() {
			super.execLoad();
//			getForm().getFieldByClass(MainBox.CloseButton.class).setVisible(false);
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
