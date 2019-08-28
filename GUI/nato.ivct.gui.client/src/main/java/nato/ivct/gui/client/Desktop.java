package nato.ivct.gui.client;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractFormMenu;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.Desktop.AlterSuTMenu.EditSutMenu;
import nato.ivct.gui.client.outlines.BadgeOutline;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.sut.SuTEditForm;
import nato.ivct.gui.client.sut.SuTForm;
import nato.ivct.gui.shared.Icons;

/**
 * <h3>{@link Desktop}</h3>
 *
 * @author hzg
 */
public class Desktop extends AbstractDesktop {
	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("ApplicationTitle");
	}

	@Override
	protected String getConfiguredLogoId() {
		return Icons.AppLogo;
	}

	@Override
	protected List<Class<? extends IOutline>> getConfiguredOutlines() {
		return CollectionUtility.<Class<? extends IOutline>>arrayList(BadgeOutline.class, SuTOutline.class);
	}

	@Override
	protected void execDefaultView() {
		setOutline(SuTOutline.class);
	}
	
//	@Order(90)
//	public class CmdHeartbeatSend extends AbstractFormMenu<HeartBeatInfoForm> {
//		
//		@Override
//		protected String getConfiguredText() {
//			return TEXTS.get("CmdHeartbeatSend");
//		}
//		
//		@Override
//		protected String getConfiguredIconId() {
//			return Icons.WhiteBullet_32x32;//AbstractIcons.CircleSolid;
//		}
//		
//	    @Override
//	    protected Class<HeartBeatInfoForm> getConfiguredForm() {
//	      return HeartBeatInfoForm.class;
//	    }
//	    
//	    @Override
//	    protected void execInitAction() {
//	    	setProperty("hbSender", "Use_CmdHeartbeatSend");
//	    }
//	    
//		@Override
//		protected void execInitForm(IForm form) {
//			form.setTitle(getProperty("hbSender").toString());
//		}
//	}

	@Order(91)
	public class TcRunnerStatus extends AbstractFormMenu<HeartBeatInfoForm> {
		@Override
		protected String getConfiguredText() {
			return TEXTS.get("TcRunnerStatus");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return Icons.WhiteBullet_32x32;//AbstractIcons.CircleSolid;
		}
		
	    @Override
	    protected Class<HeartBeatInfoForm> getConfiguredForm() {
	      return HeartBeatInfoForm.class;
	    }
	    
	    @Override
	    protected void execInitAction() {
	    	setProperty("hbSender", "TestRunner");
	    }
	    
		@Override
		protected void execInitForm(IForm form) {
			form.setTitle(getProperty("hbSender").toString());
		}
	}
	
	@Order(92)
	public class LogSinkStatus extends AbstractFormMenu<HeartBeatInfoForm> {
		@Override
		protected String getConfiguredText() {
			return TEXTS.get("LogSinkStatus");
		}
		
		@Override
		protected String getConfiguredIconId() {
			return Icons.WhiteBullet_32x32;//AbstractIcons.CircleSolid;
		}
		
	    @Override
	    protected Class<HeartBeatInfoForm> getConfiguredForm() {
	      return HeartBeatInfoForm.class;
	    }
	    
	    @Override
	    protected void execInitAction() {
	    	setProperty("hbSender", "LogSink");
	    }
	    
		@Override
		protected void execInitForm(IForm form) {
			form.setTitle(getProperty("hbSender").toString());
		}
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

	@Order(1000)
	public class QuickAccessMenu extends AbstractMenu {

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("QuickAccess");
		}

		@Order(1000)
		public class ExitMenu extends AbstractMenu {

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Exit");
			}

			@Override
			protected void execAction() {
				ClientSessionProvider.currentSession(ClientSession.class).stop();
			}
		}
	}
	
	@Order(1500)
	public class OptionsMenu extends AbstractFormMenu<OptionsForm> {
		@Override
		protected String getConfiguredText() {
			return TEXTS.get("Options");
		}

		@Override
		protected Set<? extends IMenuType> getConfiguredMenuTypes() {
			return CollectionUtility.hashSet();
		}

		@Override
		protected String getConfiguredIconId() {
			return AbstractIcons.Gear;
		}
		
	    @Override
	    protected Class<OptionsForm> getConfiguredForm() {
	      return OptionsForm.class;
	    }
	    
//		@Override
//		protected void execInitForm(IForm form) {
//			super.execInitForm(form);
//		}
	}

	@Order(2000)
	public class UserMenu extends AbstractMenu {

		@Override
		protected String getConfiguredIconId() {
			return AbstractIcons.PersonSolid;
		}
		
		@Order(1000)
		public class AboutMenu extends AbstractMenu {

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("About");
			}

			@Override
			protected void execAction() {
//				ScoutInfoForm form = new ScoutInfoForm();
				IvctInfoForm form = new IvctInfoForm();
				form.startModify();
			}
		}
	}

	@Order(1000)
	public class BadgeOutlineViewButton extends AbstractOutlineViewButton {

		public BadgeOutlineViewButton() {
			this(BadgeOutline.class);
		}

		protected BadgeOutlineViewButton(Class<? extends BadgeOutline> outlineClass) {
			super(Desktop.this, outlineClass);
		}

		@Override
		protected String getConfiguredKeyStroke() {
//			return IKeyStroke.F2;
			return "ctrl-shift-b";
		}
	}
	
    @Order(2000)
    public class SuTOutlineViewButton extends AbstractOutlineViewButton {
    
        public SuTOutlineViewButton() {
            super(Desktop.this, SuTOutline.class);
        }
        
        @Override
        protected DisplayStyle getConfiguredDisplayStyle() {
            return DisplayStyle.TAB;
        }
    
        @Override
        protected String getConfiguredKeyStroke() {
          return "ctrl-shift-s";
        }
    }
    
    @Override
    protected void execPageDetailFormChanged(IForm oldForm, IForm newForm) {
    	 if (newForm instanceof SuTForm)
             this.getMenuByClass(EditSutMenu.class).setVisible(true);
    	 else
    		 this.getMenuByClass(EditSutMenu.class).setVisible(false);
    }
    
    @Override
    protected void execOutlineChanged(IOutline oldOutline, IOutline newOutline) {
        if (newOutline instanceof SuTOutline)
            this.getMenuByClass(AlterSuTMenu.class).setVisible(true);
        else
            this.getMenuByClass(AlterSuTMenu.class).setVisible(false);
    }

}
