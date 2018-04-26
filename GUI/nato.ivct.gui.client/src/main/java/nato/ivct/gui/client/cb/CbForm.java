package nato.ivct.gui.client.cb;

import java.io.InputStream;
import java.util.Set;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.form.fields.treebox.AbstractTreeBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.platform.util.TriState;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.OptionsForm.MainBox.OkButton;
import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.GeneralBox.CbNameField;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbDependenciesTreeBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbRequirementsTableField;
//import nato.ivct.gui.client.cb.CbForm.MainBox.CancelButton;
import nato.ivct.gui.shared.cb.CbDependenciesLookupCall;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.CreateCbPermission;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.UpdateCbPermission;

@FormData(value = CbFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class CbForm extends AbstractForm {

	private String cbId;
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
	public Object computeExclusiveKey() {
		return getCbId();
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
//
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

	public CbDependenciesTreeBox getCbDependenciesTreeField() {
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
						// allign to left
						return -1;
					}
					
					@Override
					protected int getConfiguredVerticalAlignment() {
						// allign to top
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

//				@Override
//				protected int getConfiguredGridColumnCount() {
//					return 5;
//				}
				
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
							CbFormData formData = new CbFormData();
							exportFormData(formData);
							CbDependenciesLookupCall c = (CbDependenciesLookupCall) call;
							c.setCbId(formData.getCbId());
							super.execPrepareLookup(call, parent);
				        }
				        
				        // do not expand all nodes initially
				        @Override
				        protected boolean getConfiguredAutoExpandAll() {
				        	return false;
				        }
					}

					@Order(2000)
					public class CbRequirementsTableField extends AbstractTableField<CbRequirementsTableField.CbRequirementsTable> {
						@Override
						protected int getConfiguredGridW() {
							return 3;
						}
						
						public class CbRequirementsTable extends AbstractTable {
							@Order(1000)
							public class RequirementIdColumn extends AbstractStringColumn {
								// set this column to sorted ascending
								public RequirementIdColumn() {
									setInitialSortAscending(true);
								}
								
								@Override
								protected String getConfiguredHeaderText() {
									return TEXTS.get("ID");
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
									return TEXTS.get("Description");
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

						@Override
						protected String getConfiguredLabel() {
							return TEXTS.get("Requirements");
						}

						@Override
						protected int getConfiguredGridH() {
							return 6;
						}
						
						@Override
						protected Class<? extends IValueField> getConfiguredMasterField() {
							return CbForm.MainBox.BadgeHorizontalSplitBox.IncludedCbBox.DependenciesHorizontalSplitterBox.CbDependenciesTreeBox.class;
						}
				
						@Override
						protected void execChangedMasterValue(Object newMasterValue) {
							// get dependencies badges from the dependencies badge tree
							Set<String> badges = getCbDependenciesTreeField().getCheckedKeys();
							// add the selected badge
							badges.add(getCbId());
							
							// get the requirements for the selected badges
							ICbService CbService = BEANS.get(ICbService.class);
							AbstractTableFieldBeanData requirementTableRows = CbService.loadRequirements(badges);
							
							CbRequirementsTable requirementsTable = getTable();
							
							// cleanup table
							requirementsTable.deleteAllRows();
							
							// add requirements to table
							requirementsTable.importFromTableBeanData(requirementTableRows);
							
							super.execChangedMasterValue(newMasterValue);
						}
						
						@Override
						protected void execInitField() {
							Set<String> badges = CollectionUtility.hashSet(getCbId());
							
							// get the requirements for the selected badges
							ICbService CbService = BEANS.get(ICbService.class);
							AbstractTableFieldBeanData requirementTableRows = CbService.loadRequirements(badges);
							
							CbRequirementsTable requirementsTable = getTable();
							
							// add requirements to table
							requirementsTable.importFromTableBeanData(requirementTableRows);
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
			    return IKeyStroke.ENTER;
			  }
		}

//		@Order(101000)
//		public class CancelButton extends AbstractCancelButton {
//
//		}
	}

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ICbService service = BEANS.get(ICbService.class);
			CbFormData formData = new CbFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
			// load badge image
			try (InputStream in = ResourceBase.class
					.getResourceAsStream("icons/" + formData.getCbId() + ".png")) {
				getCbImageField().setImage(IOUtility.readBytes(in));
				getCbImageField().setImageId(formData.getCbId());
			} catch (Exception e) {
				logger.warn("Could not load image file: " + formData.getCbId() + ".png");
			}
			getForm().setSubTitle(formData.getCbName().getValue());
			setEnabledPermission(new UpdateCbPermission());
		}

		@Override
		protected void execStore() {
			ICbService service = BEANS.get(ICbService.class);
			CbFormData formData = new CbFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ICbService service = BEANS.get(ICbService.class);
			CbFormData formData = new CbFormData();
			exportFormData(formData);
			formData = service.prepareCreate(formData);
			importFormData(formData);

			setEnabledPermission(new CreateCbPermission());
		}

		@Override
		protected void execStore() {
			ICbService service = BEANS.get(ICbService.class);
			CbFormData formData = new CbFormData();
			exportFormData(formData);
			service.create(formData);
		}
	}
}
