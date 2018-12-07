package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.sut.SuTBdParamForm.MainBox.CancelButton;
import nato.ivct.gui.client.sut.SuTBdParamForm.MainBox.OkButton;
import nato.ivct.gui.shared.sut.ISuTBdParamEditService;
import nato.ivct.gui.shared.sut.SuTTcParamFormData;
import nato.ivct.gui.shared.sut.UpdateSuTBdParamPermission;

@FormData(value = SuTTcParamFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class SuTBdParamForm extends AbstractForm {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("SuTBdParamEdit");
	}

	public void startModify() {
		startInternalExclusive(new ModifyHandler());
	}

//	public void startNew() {
//		startInternal(new NewHandler());
//	}

	public CancelButton getCancelButton() {
		return getFieldByClass(CancelButton.class);
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	public OkButton getOkButton() {
		return getFieldByClass(OkButton.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {

		public SuTBdParamTableField getSuTBdParamTableField() {
			return getFieldByClass(SuTBdParamTableField.class);
		}
	
		@Order(1010)
		public class SuTBdParamTableField extends AbstractTableField<SuTBdParamTableField.Table> {
			
//			@Override
//			protected String getConfiguredLabel() {
//				return TEXTS.get("BadgeParameterSettings");
//			}

			@Override
			protected int getConfiguredGridH() {
				return 6;
			}
			
			@Override
			protected double getConfiguredGridWeightY() {
				return 3;
			}

			// derive table structure from SutCbForm
			public class Table extends AbstractTable {

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
			ISuTBdParamEditService service = BEANS.get(ISuTBdParamEditService.class);
			SuTTcParamFormData formData = new SuTTcParamFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);

			setEnabledPermission(new UpdateSuTBdParamPermission());
		}

		@Override
		protected void execStore() {
			ISuTBdParamEditService service = BEANS.get(ISuTBdParamEditService.class);
			SuTTcParamFormData formData = new SuTTcParamFormData();
			exportFormData(formData);
			service.store(formData);
		}
	}

//	public class NewHandler extends AbstractFormHandler {
//
//		@Override
//		protected void execLoad() {
//			ISuTBdParamEditService service = BEANS.get(ISuTBdParamEditService.class);
//			SuTBdParamFormData formData = new SuTBdParamFormData();
//			exportFormData(formData);
//			formData = service.prepareCreate(formData);
//			importFormData(formData);
//
//			setEnabledPermission(new CreateSuTBdParamPermission());
//		}
//
//		@Override
//		protected void execStore() {
//			ISuTBdParamEditService service = BEANS.get(ISuTBdParamEditService.class);
//			SuTBdParamFormData formData = new SuTBdParamFormData();
//			exportFormData(formData);
//			service.create(formData);
//		}
//	}
}
