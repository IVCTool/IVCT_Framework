package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.shared.TEXTS;

public class SuTNodePage extends AbstractPageWithNodes {

	private String sutId = null;

	@Override
	protected String getConfiguredTitle() {
		// TODO [the] verify translation
		return TEXTS.get("SuTBadgeTablePage");
	}

	@Override
	protected boolean getConfiguredLeaf() {
		// show children if any
		return false;
	}

//	@Override
//	protected void execCreateChildPages(List<IPage<?>> pageList) {
//		SuTBadgeTablePage sutBadgeTablePage = new SuTBadgeTablePage();
//		pageList.add(sutBadgeTablePage);
//	}
	
	@Override
	protected void execPageActivated() throws ProcessingException {
	  if (getDetailForm() == null) {
	    SuTForm form = new SuTForm("");
	    form.setSutId(getSutId());
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

	public void setSutId(String _sutId) {
		sutId = _sutId;
	}

	public String getSutId() {
		return sutId;
	}
}
