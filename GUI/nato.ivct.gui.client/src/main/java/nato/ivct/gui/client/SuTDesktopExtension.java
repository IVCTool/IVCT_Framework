package nato.ivct.gui.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktopExtension;
import org.eclipse.scout.rt.client.ui.desktop.ContributionCommand;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;

import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTEditForm;

public class SuTDesktopExtension extends AbstractDesktopExtension {

    public SuTDesktopExtension() {
    	
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
    
//        @Override
//        protected String getConfiguredKeyStroke() {
//          return "ctrl-shift-e";
//        }
    }

    @Order(100)
    public class NewSuTMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
            return TEXTS.get("NewSuT");
        }
        
        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
            return CollectionUtility.hashSet();
        }
    
        @Override
        public boolean isVisible() {
            // TODO Auto-generated method stub
            return super.isVisible();
        }
    
        @Override
        protected void execAction() {
            SuTEditForm form = new SuTEditForm("");
//            setDetailForm(form);
            form.startNew();

        }
    }
    
    
    @Override
    protected ContributionCommand execOutlineChanged(IOutline oldOutline, IOutline newOutline) {
        // TODO Auto-generated method stub
        if (newOutline instanceof SuTOutline)
            getCoreDesktop().getMenuByClass(NewSuTMenu.class).setVisible(true);
        else
            getCoreDesktop().getMenuByClass(NewSuTMenu.class).setVisible(false);
        
        return super.execOutlineChanged(oldOutline, newOutline);
    }
}
