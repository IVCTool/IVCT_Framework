package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.cb.CbForm;

public class SuTBadgeDetailsNodePage extends AbstractPageWithNodes {

	private String sutId = null;
	
	private String badgeId = null;

	@Override
	protected String getConfiguredTitle() {
		// TODO [the] verify translation
		return TEXTS.get("SuTBadgeDetailsNodePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		// do not show children if any
		return true;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		// TODO [the] Auto-generated method stub.
		super.execCreateChildPages(pageList);
	}
	
	@Override
	protected void execPageActivated() throws ProcessingException {
	  if (getDetailForm() == null) {
	    CbForm form = new CbForm();
	    form.setCbId(getBadgeId());
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

	public void setBadgeId(String _badgeId) {
		badgeId = _badgeId;
	}

	public String getBadgeId() {
		return badgeId;
	}

	public String getSutId() {
		return sutId;
	}

	public void setSutId(String _sutId) {
		this.sutId = _sutId;
	}
}
