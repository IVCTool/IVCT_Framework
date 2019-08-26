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
import org.eclipse.scout.rt.client.ui.form.fields.GridData;
import org.eclipse.scout.rt.client.ui.form.fields.accordionfield.AbstractAccordionField;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.group.AbstractGroup;
import org.eclipse.scout.rt.client.ui.tile.AbstractHtmlTile;
import org.eclipse.scout.rt.client.ui.tile.AbstractTileAccordion;
import org.eclipse.scout.rt.client.ui.tile.AbstractTileGrid;
import org.eclipse.scout.rt.client.ui.tile.IHtmlTile;
import org.eclipse.scout.rt.client.ui.tile.ITile;
import org.eclipse.scout.rt.client.ui.tile.TileGridLayoutConfig;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.annotations.ConfigProperty;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.security.SecurityUtility;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.data.tile.ITileColorScheme;
import org.eclipse.scout.rt.shared.data.tile.TileColorScheme;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonWriter;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SutParameterBox.ParameterHorizontalSplitterBox.SuTTcParameterTableField.SuTTcParameterTable.SaveMenu;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.SutParameterBox.ParameterHorizontalSplitterBox.SutTcExtraParameterTableField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField.Accordion;
import nato.ivct.gui.client.sut.SuTCbForm.MainBox.MainBoxHorizontalSplitBox.AccordionField.Accordion.TileGroup;
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

	public AccordionField getAccordionField() {
		return getFieldByClass(AccordionField.class) ;
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
			public class AccordionField extends AbstractAccordionField<AccordionField.Accordion> {
				
				@Override
				protected String getConfiguredLabel() {
					// TODO Auto-generated method stub
					return TEXTS.get("Testsuites");
				}
				@Override
				protected int getConfiguredGridW() {
					return FULL_WIDTH;
				}
				
				public class Accordion extends AbstractTileAccordion<ITile> {
					
					@Override
					protected boolean getConfiguredExclusiveExpand() {
						return true;
					}

//					@Override
//					protected String getConfiguredCssClass() {
//						return "has-custom-tiles";
//					}
					
					public class TileGroup extends AbstractGroup {

					    @Override
					    public TileGrid getBody() {
					    	return (TileGrid) super.getBody();
					    }
					
					    public class TileGrid extends AbstractTileGrid<ITile> {
							@Override
							protected boolean getConfiguredSelectable() {
								return true;
							}
							
							@Override
							protected int getConfiguredGridColumnCount() {
								return 6;
							}
							
							@Override
							protected TileGridLayoutConfig getConfiguredLayoutConfig() {
							    return super.getConfiguredLayoutConfig()
							        .withColumnWidth(100)
							        .withRowHeight(100);
							}
							
							@Override
							protected boolean getConfiguredScrollable() {
							    return false;
							}
						}
					}
					
				}
			
			}
			
			@Order (2000)
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
		}
	}

	public class ViewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ISuTCbService service = BEANS.get(ISuTCbService.class);
			SuTCbFormData formData = new SuTCbFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
			
			loadTestSuites();
//			// load badge image
//			byte[] badgeIcon = BEANS.get(ICbService.class).loadBadgeIcon(formData.getCbId());
//			if (badgeIcon != null) {
//				getCbImageField().setImage(badgeIcon);
//				getForm().setSubTitle(formData.getCbName().getValue());
//			}
//			else {
//				logger.warn("Could not load image file for badge ID " + formData.getCbId());
//			}
//			setEnabledPermission(new UpdateCbPermission());
		}
		
		private void loadTestSuites() {
//			addGroupWithTiles("A");
//			addGroupWithTiles("B");
//			addGroupWithTiles("C");
		}
	
	}

	
	public class CustomTile extends AbstractHtmlTile {

		@Override
		protected String getConfiguredCssClass() {
			// TODO Auto-generated method stub
			return super.getConfiguredCssClass();
		}
//		  String PROP_LABEL = "label";
//
//		  public String getLabel() {
//		    return propertySupport.getPropertyString(PROP_LABEL);
//		  }
//
//		  public void setLabel(String label) {
//		    propertySupport.setProperty(PROP_LABEL, label);
//		  }
	}
	
	protected void addGroupWithTiles(String grId) {
	    Accordion accordion = getAccordionField().getAccordion();
	    List<ITile> tiles = new ArrayList<>();
	    int maxTiles = SecurityUtility.createSecureRandom().nextInt(10)+1;
	    for (int i = 0; i < maxTiles; i++) {
	      CustomTile tile = new CustomTile();
	      
	      
	      
//	      tile.setLabel("Tile " + i);
	      tile.setContent("Testcase " + i);
	      GridData gridDataHints = tile.getGridDataHints();
	      gridDataHints.weightX = 0;
	      tile.setGridDataHints(gridDataHints);
	      tile.setColorScheme(TileColorScheme.DEFAULT);
//	      tile.setColorScheme(TileColorScheme.RAINBOW);
	      tiles.add(tile);
	    }
	    TileGroup group = accordion.new TileGroup();
	    group.setTitle("Testsuite " + grId);
	    group.getBody().setTiles(tiles);
	    accordion.addGroup(group);
	 }
	
	@Override
	protected void execInitForm() {
		super.execInitForm();
		addGroupWithTiles("A");
		addGroupWithTiles("B");
		addGroupWithTiles("C");

	}
}