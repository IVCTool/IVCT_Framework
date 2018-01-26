package nato.ivct.gui.client.cb;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.Spliterators;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.IValueField;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.client.ui.form.fields.treebox.AbstractTreeBox;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;
import org.slf4j.LoggerFactory;

import com.google.api.client.repackaged.com.google.common.base.Splitter;

import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.cb.CbForm.MainBox.CancelButton;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbDependenciesTreeBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbNameField;
import nato.ivct.gui.client.cb.CbForm.MainBox.IncludedCbBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.IncludedCbBox.CbRequirementsTableField;
import nato.ivct.gui.client.cb.CbForm.MainBox.OkButton;
import nato.ivct.gui.shared.cb.CbDependenciesLookupCall;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.CbRequirementsLookupCall;
import nato.ivct.gui.shared.cb.CreateCbPermission;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.UpdateCbPermission;

@FormData(value = CbFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class CbForm extends AbstractForm {

	private String cbId;
	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	protected String getConfiguredTitle() {
		// TODO [hzg] verify translation
		return TEXTS.get("Capability");
	}

	@Override
	protected int getConfiguredDisplayHint() {
		// TODO Auto-generated method stub
		return IForm.DISPLAY_HINT_VIEW;
	}

	@Override
	public Object computeExclusiveKey() {
		// TODO Auto-generated method stub
		return getCbId();
	}

	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

	public void startNew() {
		startInternal(new NewHandler());
	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

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

		@Order(1000)
		public class GeneralBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("GeneralCapabilityInformation");
			}

			@Order(1000)
			public class CbNameField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("CapabilityName");
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
					return TEXTS.get("CapabilityDescription");
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

			@Order(2500)
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
			
				
			@Order(3000)
			public class CbImageField extends AbstractImageField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("CbImage");
				}

				@Override
				protected boolean getConfiguredAutoFit() {
					return false;
				}

				@Override
				protected int getConfiguredGridH() {
					return 6;
				}

				@Override
				protected int getConfiguredGridW() {
					// TODO Auto-generated method stub
					return 2;
				}

				@Override
				protected boolean getConfiguredLabelVisible() {
					return false;
				}
			}
	}

		@Order(2000)
		public class IncludedCbBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("InclRequirements");
			}

			@Order(1000)
			public class CbRequirementsTableField extends AbstractTableField<CbRequirementsTableField.CbRequirementsTable> {
				public class CbRequirementsTable extends AbstractTable {
					@Order(1000)
					public class RequirementIdColumn extends AbstractStringColumn {
						public RequirementIdColumn() {
							setInitialSortAscending(true);
						}
						
						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("Requirement");
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
							return 500;
						}
					}

					@Order(3000)
					public class AbstractTCColumn extends AbstractStringColumn {
						@Override
						protected String getConfiguredHeaderText() {
							return TEXTS.get("ATC");
						}

						@Override
						protected int getConfiguredWidth() {
							return 100;
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

//				@Override
//				protected String getConfiguredLabel() {
//					return TEXTS.get("Requirements");
//				}

				@Override
				protected int getConfiguredGridH() {
					return 6;
				}
				@Override
				protected Class<? extends IValueField> getConfiguredMasterField() {
					return CbForm.MainBox.GeneralBox.CbDependenciesTreeBox.class;
				}
		
				@Override
				protected void execChangedMasterValue(Object newMasterValue) {
					Set<String> selectedBadges = getCbDependenciesTreeField().getCheckedKeys();
					CbRequirementsTable table = getTable();
					
					// cleanup table
					table.deleteAllRows();
					
					// Execute the LookupCall (using DataByKey) to fill table
					LookupCall<String> call = new CbRequirementsLookupCall();
					for (String selBadge:selectedBadges) {
						call.setKey(selBadge);
					    List<? extends ILookupRow<String>> rows = call.getDataByKey();

					    //Get the textual requirement (with a null check)
					    if (rows != null && !rows.isEmpty()) {
					    	for (ILookupRow<String> row:rows) {
						    	ITableRow r = table.addRow(getTable().createRow());
								table.getRequirementIdColumn().setValue(r, row.getKey());
								List<String> reqText = Splitter.on(";;").splitToList(row.getText());
								table.getRequirementDescColumn().setValue(r, reqText.get(0));
								table.getAbstractTCColumn().setValue(r, reqText.get(1));
					    	}
					    }
					}
					super.execChangedMasterValue(newMasterValue);
				}
			}
		}

		@Order(100000)
		public class OkButton extends AbstractOkButton {
		}

		@Order(101000)
		public class CancelButton extends AbstractCancelButton {
		}
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
