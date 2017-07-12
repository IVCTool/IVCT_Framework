package nato.ivct.gui.client.cb;

import java.io.File;
import java.io.InputStream;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.tree.AbstractTree;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.treefield.AbstractTreeField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.IOUtility;
import org.eclipse.scout.rt.shared.TEXTS;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.client.cb.CbForm.MainBox.CancelButton;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbDependenciesField;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbDescriptionField;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbImageField;
import nato.ivct.gui.client.cb.CbForm.MainBox.GeneralBox.CbNameField;
import nato.ivct.gui.client.cb.CbForm.MainBox.IncludedCbBox;
import nato.ivct.gui.client.cb.CbForm.MainBox.IncludedCbBox.IncludedCbField;
import nato.ivct.gui.client.cb.CbForm.MainBox.OkButton;
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

	public IncludedCbField getIncludedCbField() {
		return getFieldByClass(IncludedCbField.class);
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

	public CbDependenciesField getCbDependenciesField() {
		return getFieldByClass(CbDependenciesField.class);
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
					// TODO Auto-generated method stub
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
					// TODO Auto-generated method stub
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
					// TODO Auto-generated method stub
					return true;
				}
			}

			@Order(2500)
			public class CbDependenciesField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Dependencies");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}

				@Override
				protected int getConfiguredGridW() {
					return 3;
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
					return 4;
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
				return TEXTS.get("InclCb");
			}

			@Order(1000)
			public class IncludedCbField extends AbstractTreeField {
				public class Tree extends AbstractTree {
				}

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Capabilities");
				}

				@Override
				protected int getConfiguredGridH() {
					return 6;
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
					.getResourceAsStream("icons" + File.separator + formData.getCbId() + ".png")) {
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
