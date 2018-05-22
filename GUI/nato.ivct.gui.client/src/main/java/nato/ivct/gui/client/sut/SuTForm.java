package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.tree.AbstractTree;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.listbox.AbstractListBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.client.ui.form.fields.treefield.AbstractTreeField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;

import nato.ivct.gui.client.sut.SuTForm.MainBox.CancelButton;
import nato.ivct.gui.client.sut.SuTForm.MainBox.DetailsBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.DetailsBox.CapabilitiesBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.DetailsBox.TestResultsField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.ExecutionBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.ExecutionBox.EventsField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.ExecutionBox.ExecuteButton;
import nato.ivct.gui.client.sut.SuTForm.MainBox.ExecutionBox.TerminateButton;
import nato.ivct.gui.client.sut.SuTForm.MainBox.GeneralBox;
import nato.ivct.gui.client.sut.SuTForm.MainBox.GeneralBox.CapabilitiesField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.GeneralBox.DescrField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.GeneralBox.NameField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.GeneralBox.SutVendorField;
import nato.ivct.gui.client.sut.SuTForm.MainBox.OkButton;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
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

	public DescrField getDescrField() {
		return getFieldByClass(DescrField.class);
	}

	public SutVendorField getSutVendorField() {
		return getFieldByClass(SutVendorField.class);
	}

	public CapabilitiesBox getCapabilitiesBox() {
		return getFieldByClass(CapabilitiesBox.class);
	}

	public TestResultsField getTestResultsField() {
		return getFieldByClass(TestResultsField.class);
	}

	public DetailsBox getDetailsBox() {
		return getFieldByClass(DetailsBox.class);
	}

	public ExecutionBox getExecutionBox() {
		return getFieldByClass(ExecutionBox.class);
	}

	public ExecuteButton getExecuteButton() {
		return getFieldByClass(ExecuteButton.class);
	}

	public TerminateButton getTerminateButton() {
		return getFieldByClass(TerminateButton.class);
	}

	public EventsField getEventsField() {
		return getFieldByClass(EventsField.class);
	}

	public CapabilitiesField getCapabilitiesField() {
		return getFieldByClass(CapabilitiesField.class);
	}

	public NameField getNameField() {
		return getFieldByClass(NameField.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@FormData
	public String getSutId() {
		return sutId;
	}

	@FormData
	public void setSutId(String sutId) {
		this.sutId = sutId;
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
		public class GeneralBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("GeneralSuTInformation");
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

			@Order(2000)
			public class DescrField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("SuTDescription");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

			@Order(3000)
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

			@Order(4000)
			public class CapabilitiesField extends AbstractStringField {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Capabilities");
				}

				@Override
				protected int getConfiguredMaxLength() {
					return 128;
				}
			}

		}

		@Order(2000)
		public class DetailsBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Details");
			}

			@Order(4000)
			public class CapabilitiesBox extends AbstractListBox<String> {

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Capabilities");
				}

				@Override
				protected int getConfiguredGridH() {
					return 6;
				}

				@Override
				protected void execInitField() {
					// TODO Auto-generated method stub
					super.execInitField();
				}
				@Override
				protected List<? extends ILookupRow<String>> execLoadTableData() {
					// TODO Auto-generated method stub
					return super.execLoadTableData();
				}
				
			}

			@Order(5000)
			public class TestResultsField extends AbstractTreeField {
				public class Tree extends AbstractTree {
				}

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("SuTResults");
				}

				@Override
				protected int getConfiguredGridH() {
					return 6;
				}
			}
		}

		@Order(3000)
		public class ExecutionBox extends AbstractGroupBox {
			@Override
			protected String getConfiguredLabel() {
				return TEXTS.get("Execution");
			}

			@Order(1000)
			public class ExecuteButton extends AbstractButton {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Execute");
				}

				@Override
				protected void execClickAction() {
				}
			}

			@Order(2000)
			public class TerminateButton extends AbstractButton {
				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Terminate");
				}

				@Override
				protected void execClickAction() {
				}
			}

			@Order(3000)
			public class EventsField extends AbstractTreeField {
				public class Tree extends AbstractTree {
				}

				@Override
				protected String getConfiguredLabel() {
					return TEXTS.get("Logging");
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
			ISuTService service = BEANS.get(ISuTService.class);
			SuTFormData formData = new SuTFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
			getForm().setSubTitle(formData.getName().getValue());

			setEnabledPermission(new UpdateSuTPermission());
		}

		@Override
		protected void execStore() {
			ISuTService service = BEANS.get(ISuTService.class);
			SuTFormData formData = new SuTFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}

	public class NewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ISuTService service = BEANS.get(ISuTService.class);
			SuTFormData formData = new SuTFormData();
			exportFormData(formData);
			formData = service.prepareCreate(formData);
			importFormData(formData);

			setEnabledPermission(new CreateSuTPermission());
		}

		@Override
		protected void execStore() {
			ISuTService service = BEANS.get(ISuTService.class);
			SuTFormData formData = new SuTFormData();
			exportFormData(formData);
			service.create(formData);
		}
	}
}
