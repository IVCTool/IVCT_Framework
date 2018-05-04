package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TEXTS;

public class CbNodePage extends AbstractPageWithNodes {
	private String badgeId = null;
	
	@Override
	protected String getConfiguredTitle() {
		// TODO [the] verify translation
		return TEXTS.get("CbNodePage");
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		// TODO [the] Auto-generated method stub.
		super.execCreateChildPages(pageList);
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

//	@Override
//	public void setDetailForm(IForm form) {
//		// TODO Auto-generated method stub
//		super.setDetailForm(form);
//	}
//	@Override
//	protected IForm createDetailForm() {
//		// TODO Auto-generated method stub
//		return super.createDetailForm();
//	}
	
	@Override
	protected void execPageActivated() throws ProcessingException {
	  if (getDetailForm() == null) {
	    CbForm form = new CbForm();
	    form.setCbId(getBadgeId());
	    setDetailForm(form);
//	    form.startView();
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

	public String getBadgeId() {
		return badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.badgeId = badgeId;
	}
}
