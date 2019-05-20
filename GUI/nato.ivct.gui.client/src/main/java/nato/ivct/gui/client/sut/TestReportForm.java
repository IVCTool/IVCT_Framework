package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.FormData;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.form.AbstractForm;
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractButton;
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox;
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.TestReportFormData;

@FormData(value = TestReportFormData.class, sdkCommand = FormData.SdkCommand.CREATE)
public class TestReportForm extends AbstractForm {
	private String sutId = null;
	private String reportFileName = null;
	
	@FormData
	public String getSutId() {
		return sutId;
	}

	@FormData
	public void setSutId(final String _sutId) {
		this.sutId = _sutId;
	}

	@FormData
	public String getReportFileName () {
		return reportFileName;
	}
	
	@FormData
	public void setReportFileName (final String _reportFileName) {
		reportFileName = _reportFileName;
	}

	@Override
	protected int getConfiguredDisplayHint() {
		return IForm.DISPLAY_HINT_VIEW;
	}

	public void startView() {
		startInternal(new ViewHandler());
	}

	public MainBox getMainBox() {
		return getFieldByClass(MainBox.class);
	}

	@Order(1000)
	public class MainBox extends AbstractGroupBox {
		
		@Override
		public boolean isEnabled() {
			// set all fields to read-only
			return false;
		}
		
		@Order(1100)
		public class TestReportField extends AbstractStringField {
			
			// hide the label; the width must be > 0
			@Override
			protected int getConfiguredLabelWidthInPixel() {
				return 1;
			}
			
			@Override
			protected int getConfiguredGridH() {
				return 5;
			}
			
			@Override
			protected boolean getConfiguredMultilineText() {
				return true;
			}
			
			@Override
			protected int getConfiguredMaxLength() {
				return Integer.MAX_VALUE;
			}
			
//			@Override
//			public boolean isEnabled() {
//				// set to r/w to activate the scrollbars
//				return true;
//			}
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
	}

	public class ViewHandler extends AbstractFormHandler {

		@Override
		protected void execLoad() {
			ISuTService service = BEANS.get(ISuTService.class);
			TestReportFormData formData = new TestReportFormData();
			exportFormData(formData);
			formData = service.load(formData);
			importFormData(formData);
			// set the title for this form
			setTitle(getReportFileName());
		}
	}
}
