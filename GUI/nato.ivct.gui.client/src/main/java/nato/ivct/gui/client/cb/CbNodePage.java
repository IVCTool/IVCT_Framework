package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.sut.SuTTablePage;

public class CbNodePage extends AbstractPageWithNodes {

	private String cbId;
	
	@Override
	protected String getConfiguredTitle() {
		// TODO [hzg] verify translation
		return TEXTS.get("CbNodePage");
	}
	
	@Override
	protected boolean getConfiguredLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		SuTTablePage sutTablePage = new SuTTablePage();
		sutTablePage.setCbId(getCbId());
		// TODO set the content
		pageList.add(sutTablePage);
	}

	public String getCbId() {
		return cbId;
	}

	public void setCbId(String cbId) {
		this.cbId = cbId;
	}
}
