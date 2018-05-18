package nato.ivct.gui.client.outlines;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.sut.SuTTablePage;
import nato.ivct.gui.shared.Icons;

public class SuTOutline extends AbstractOutline {

	  @Override
	  protected String getConfiguredTitle() {
	    return TEXTS.get("SuT");
	  }

	  @Override
	  protected void execCreateChildPages(List<IPage<?>> pageList) {
		  SuTTablePage sutTablePage = new SuTTablePage();
	    pageList.add(sutTablePage);
	  }

	  @Override
	  protected String getConfiguredIconId() {
	    return Icons.Gear;
	  }
}
