package nato.ivct.gui.client.outlines;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.ts.TsNodePage;
import nato.ivct.gui.shared.Icons;
import nato.ivct.gui.shared.cb.ITsService;

/**
 * <h3>{@link TsOutline}</h3>
 *
 * @author hzg
 */
@Order(1000)
public class TsOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		Set<String> testsuites = BEANS.get(ITsService.class).loadTestSuites();
		testsuites.forEach(s -> {TsNodePage np = new TsNodePage(s); pageList.add(np);});
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Testsuites");
	}

	@Override
	protected String getConfiguredIconId() {
		return Icons.CategoryBold;
	}
}
