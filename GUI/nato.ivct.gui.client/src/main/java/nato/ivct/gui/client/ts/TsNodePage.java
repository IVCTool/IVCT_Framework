package nato.ivct.gui.client.ts;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class TsNodePage extends AbstractPageWithNodes {
	private String m_tsId = null;
	private String m_pageTitle = null;

	public TsNodePage() {
		m_pageTitle = TEXTS.get("TsNodePage");
	}
	
	public TsNodePage(final String badgeId) {
		m_pageTitle = m_tsId = badgeId;
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
	    TsForm form = new TsForm();
	    form.setTsId(getTsId());
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

	public String getTsId() {
		return m_tsId;
	}

	public void setTsId(String tsId) {
		this.m_tsId = tsId;
	}
}
