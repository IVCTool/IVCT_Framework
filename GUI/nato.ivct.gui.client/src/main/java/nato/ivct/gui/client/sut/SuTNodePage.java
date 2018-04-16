package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.sut.SuTCbTablePage;

public class SuTNodePage extends AbstractPageWithNodes {

	private String suId;
	
	@Override
	protected String getConfiguredTitle() {
		// TODO [hzg] verify translation
		return TEXTS.get("SuTNodePage");
	}
	
	@Override
	protected boolean getConfiguredLeaf() {
		// TODO Auto-generated method stub
		return false;
	}
/*
	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		SuTCbTablePage cbTablePage = new SuTCbTablePage();
		cbTablePage.setSutId(getSutId());
		pageList.add(cbTablePage);
	}
*/
	public String getSutId() {
		return suId;
	}	
	public void setSutId(String Id) {
		this.suId = Id;
	}
}
