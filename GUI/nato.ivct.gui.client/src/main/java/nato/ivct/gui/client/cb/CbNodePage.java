package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class CbNodePage extends AbstractPageWithNodes {
	private String m_badgeId = null;
	private String m_pageTitle = null;

	public CbNodePage() {
		m_pageTitle = TEXTS.get("CbNodePage");
	}
	
	public CbNodePage(final String badgeId) {
		m_pageTitle = m_badgeId = badgeId;
	}
	
	@Override
	protected String getConfiguredTitle() {
		return m_pageTitle;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		super.execCreateChildPages(pageList);
	}

	@Override
	protected boolean getConfiguredLeaf() {
		// do not show child notes
		return true;
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

	public String getBadgeId() {
		return m_badgeId;
	}

	public void setBadgeId(String badgeId) {
		this.m_badgeId = badgeId;
	}
}
