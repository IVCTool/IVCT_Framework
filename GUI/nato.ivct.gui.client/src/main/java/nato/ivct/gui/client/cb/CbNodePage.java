package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.sut.CapabilityTablePage;

public class CbNodePage extends AbstractPageWithNodes {

	private String suId;
	
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
		CapabilityTablePage cbTablePage = new CapabilityTablePage();
		cbTablePage.setSutId(getSutId());
		pageList.add(cbTablePage);
	}

	public String getSutId() {
		return suId;
	}	
	public void setSutId(String Id) {
		this.suId = Id;
	}
}
