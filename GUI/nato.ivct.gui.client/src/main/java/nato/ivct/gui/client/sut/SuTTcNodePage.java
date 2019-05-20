package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class SuTTcNodePage extends AbstractPageWithNodes {

	private String sutId = null;
	private String badgeId = null;
	private String requirementId = null;
	private String cbTestCase = null;

	@Override
	protected String getConfiguredTitle() {
		// TODO [the] verify translation
		return TEXTS.get("SuTCbNodePage");
	}
	
	@Override
	protected boolean getConfiguredLeaf() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getCbTestCase() {
		return cbTestCase;
	}	
	public void setCbTestCase(String tc) {
		this.cbTestCase = tc;
	}

	public String getRequirementId() {
		return requirementId;
	}	
	public void setRequirementId(String Id) {
		this.requirementId = Id;
	}

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}

	public String getSutId() {
		return sutId;
	}

	public void setSutId(String sutId) {
		this.sutId = sutId;
	}

	@Override
	protected void execPageActivated() throws ProcessingException {
	  if (getDetailForm() == null) {
	    // TODO use constructor with title argument?
		SuTTcRequirementForm form = new SuTTcRequirementForm();
	    form.setSutId(getSutId());
		form.setBadgeId(getBadgeId());
	    form.setRequirementId(getRequirementId());
	    form.setTestCaseId(getCbTestCase());
	    setDetailForm(form);
	    form.startView();
	  }
	}
	
	@Override
	protected void execPageDeactivated() throws ProcessingException {
	  if (getDetailForm() != null) {
	    getDetailForm().doClose();
	    setDetailForm(null);
	  }
	}

}
