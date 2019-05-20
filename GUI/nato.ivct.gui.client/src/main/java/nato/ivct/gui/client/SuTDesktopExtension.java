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
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;

import nato.ivct.gui.client.SuTDesktopExtension.AlterSuTMenu.EditSutMenu;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTEditForm;
import nato.ivct.gui.client.sut.SuTForm;

public class SuTDesktopExtension extends AbstractDesktopExtension {

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
    public class AlterSuTMenu extends AbstractMenu {
        @Override
        protected String getConfiguredText() {
            return TEXTS.get("AlterSutList");
        }
        
        @Override
        protected Set<? extends IMenuType> getConfiguredMenuTypes() {
            return CollectionUtility.hashSet();
        }
//    
//        @Override
//        public boolean isVisible() {
//            return super.isVisible();
//        }

		@Order(110)
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
			protected void execAction() {
	            SuTEditForm form = new SuTEditForm("");
	            form.startNew();
			}
		}



		@Order(120)
		public class EditSutMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("EditSUT");
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet();
			}
			
			@Override
			protected boolean getConfiguredVisible() {
				// set to invisible by default until a SUT is selected
				return false;
			}
			
			@Override
			protected void execAction() {
	            SuTEditForm form = new SuTEditForm("");
	            form.startModify();
			}
		}
		
		
    }
    
    @Override
    protected ContributionCommand execPageDetailFormChanged(IForm oldForm, IForm newForm) {
    	 if (newForm instanceof SuTForm)
             getCoreDesktop().getMenuByClass(EditSutMenu.class).setVisible(true);
    	 else
    		 getCoreDesktop().getMenuByClass(EditSutMenu.class).setVisible(false);
    	
    	return ContributionCommand.Stop; //super.execPageDetailFormChanged(oldForm, newForm);
    }
    
    @Override
    protected ContributionCommand execOutlineChanged(IOutline oldOutline, IOutline newOutline) {
        if (newOutline instanceof SuTOutline)
            getCoreDesktop().getMenuByClass(AlterSuTMenu.class).setVisible(true);
        else
            getCoreDesktop().getMenuByClass(AlterSuTMenu.class).setVisible(false);
        
        return ContributionCommand.Stop; //super.execOutlineChanged(oldOutline, newOutline);
    }
}
