package nato.ivct.gui.client.sut;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

public class SuTCbNodePage extends AbstractPageWithNodes {

	private String sutCbBadgeId;

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

//	@Override
//	protected void execCreateChildPages(List<IPage<?>> pageList) {
//		// TODO [the] Auto-generated method stub.
//		super.execCreateChildPages(pageList);
//	}

	public String getSuTCbBadgeId() {
		return sutCbBadgeId;
	}	
	public void setSuTCbBadgeId(String Id) {
		this.sutCbBadgeId = Id;
	}

}
