package nato.ivct.gui.client.sut;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktopExtension;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.Order;

public class DesktopExtension extends AbstractDesktopExtension {

	public DesktopExtension() {
		
	}

	@Override
	protected List<Class<? extends IOutline>> getConfiguredOutlines() {
		List<Class<? extends IOutline>> outlines = new ArrayList<>();
		outlines.add(SuTOutline.class);
		return outlines;
	}

	  @Order(2000)
	  public class SuTOutlineViewButton extends AbstractOutlineViewButton {

	    public SuTOutlineViewButton() {
	      super(getCoreDesktop(), SuTOutline.class);
	    }

	    @Override
	    protected DisplayStyle getConfiguredDisplayStyle() {
	      return DisplayStyle.MENU;
	    }
//
//	    @Override
//	    protected String getConfiguredKeyStroke() {
//	      return "ctrl-shift-e";
//	    }
	  }
}
