package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

public class CbNodePage extends AbstractPageWithNodes {

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
}
