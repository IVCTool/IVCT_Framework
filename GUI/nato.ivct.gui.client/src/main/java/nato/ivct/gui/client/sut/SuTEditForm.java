package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractSaveButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.splitbox.AbstractSplitBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;

import java.util.TreeSet;

import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.CancelButton;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.DescrField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.NameField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.MainBoxHorizontalSplitBox.GeneralBox.SutVendorField;
import nato.ivct.gui.client.sut.SuTEditForm.MainBox.SaveButton;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTCbLookupCall;
import nato.ivct.gui.shared.sut.SuTEditFormData;

@FormData(value = SuTEditFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTEditForm extends AbstractForm {

	private String sutId = null;
	private String title = null;

	public SuTEditForm(String formTitle) {
		title = formTitle;
	}

	@Override
	protected String getConfiguredTitle() {
		if (title != null)
			return title;
		else
			return TEXTS.get("NewSuT");
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

	public SaveButton getSaveButton() {
		return getFieldByClass(SaveButton.class);
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
        
                // set all fields of this box to read/write
                @Override
                public boolean isEnabled() {
                	return true;
                }
                
                @Order(1100)
                public class NameField extends AbstractStringField {
                	@Override
                	protected String getConfiguredLabel() {
                		return TEXTS.get("SuTName");
                	}
                
                	@Override
                	protected int getConfiguredMaxLength() {
                		return 128;
                	}
					
					// the ID field must have a value
					@Override
					public boolean isMandatory() {
						return true;
					}
                }
        
                @Order(1200)
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
                
                @Order(1300)
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
    		public class SuTCapabilityBox extends AbstractListBox<String> {
                @Override
                protected String getConfiguredLabel() {
                    return TEXTS.get("SuTCapabilities");
                }
    			
                @Override
                protected int getConfiguredGridH() {
                    return 1;
                }
                
                @Override
                protected int getConfiguredGridW() {
                    return 3;
                }

                @Override
                protected Class<? extends ILookupCall<String>> getConfiguredLookupCall() {
                    return SuTCbLookupCall.class;
                }
    		}
        }

        @Order(100000)
        public class SaveButton extends AbstractSaveButton {
			@Override
			protected String getConfiguredKeyStroke() {
				return "ctrl-s";
			}
			
			@Override
			protected boolean getConfiguredEnabled() {
				return false;
			}	
        }
        
        @Order(101000)
        public class CancelButton extends AbstractCancelButton {
            @Override
            public boolean isVisible() {
                return true;
            }
            
            @Override
            protected void execClickAction() {
            	// TODO Auto-generated method stub
            }
        }
	}
	
	@Override
	protected boolean execIsSaveNeeded() {
		// show save button if save is required
		final boolean saveNeeded = super.execIsSaveNeeded();
			getSaveButton().setEnabled(saveNeeded);
		return saveNeeded;
	}
	
	@Override
	public boolean isSaveNeededVisible() {
		return true;
	}
	
    public class NewHandler extends AbstractFormHandler {

        @Override
        protected void execLoad() {
            if (isSaveNeeded()) {
	            ISuTService service = BEANS.get(ISuTService.class);
	            SuTEditFormData formData = new SuTEditFormData();
	            exportFormData(formData);
	            formData = service.prepareCreate(formData);
	            importFormData(formData);
            }
//            setEnabledPermission(new CreateSuTPermission());
        }

        @Override
        protected void execStore() {
        	if (isSaveNeeded()) {
	            ISuTService service = BEANS.get(ISuTService.class);
	            SuTEditFormData formData = new SuTEditFormData();
	            exportFormData(formData);
	            service.create(formData);
        	}

        	// close the form
			doClose();
			
			// reset outline to reload all SuT
			IOutline sutOutline = getDesktop().getOutline();
			sutOutline.resetOutline();
			sutOutline.deselectNodes(sutOutline.getSelectedNodes());
        }
    }

	public class ModifyHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			super.execLoad();
		}

		@Override
		protected void execStore() {
			ISuTService service = BEANS.get(ISuTService.class);
			SuTEditFormData formData = new SuTEditFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}
}
