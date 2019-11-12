package nato.ivct.gui.client.outlines;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.cb.CbNodePage;
import nato.ivct.gui.shared.Icons;
import nato.ivct.gui.shared.cb.ICbService;

/**
 * <h3>{@link BadgeOutline}</h3>
 *
 * @author hzg
 */
@Order(1000)
public class BadgeOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		Set<String> badges = BEANS.get(ICbService.class).loadBadges();
		badges.forEach(s -> {CbNodePage np = new CbNodePage(s); np.setOverviewIconId(getConfiguredIconId());pageList.add(np);});
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Badges");
	}

	@Override
	protected String getConfiguredIconId() {
		return Icons.FolderBold;
	}
}
