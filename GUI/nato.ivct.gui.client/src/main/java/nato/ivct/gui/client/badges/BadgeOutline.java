package nato.ivct.gui.client.badges;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.cb.CbTablePage;
import nato.ivct.gui.client.sut.SuTTablePage;
import nato.ivct.gui.shared.Icons;

/**
 * <h3>{@link BadgeOutline}</h3>
 *
 * @author hzg
 */
@Order(1000)
public class BadgeOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		pageList.add(new CbTablePage());
//		pageList.add(new SuTTablePage());
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Badges");
	}

	@Override
	protected String getConfiguredIconId() {
		return Icons.Category;
	}
}
