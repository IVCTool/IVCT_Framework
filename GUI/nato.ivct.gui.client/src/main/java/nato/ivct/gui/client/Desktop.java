package nato.ivct.gui.client;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.form.AbstractFormMenu;
import org.eclipse.scout.rt.client.ui.form.ScoutInfoForm;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.AbstractIcons;
import org.eclipse.scout.rt.shared.TEXTS;

import nato.ivct.gui.client.outlines.BadgeOutline;
import nato.ivct.gui.client.search.SearchOutline;
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
		return CollectionUtility.<Class<? extends IOutline>>arrayList(BadgeOutline.class, SearchOutline.class);
	}

	@Override
	protected void execDefaultView() {
		selectFirstVisibleOutline();
	}

	protected void selectFirstVisibleOutline() {
		for (IOutline outline : getAvailableOutlines()) {
			if (outline.isEnabled() && outline.isVisible()) {
				setOutline(outline.getClass());
				return;
			}
		}
	}

	@Order(1000)
	public class QuickAccessMenu extends AbstractMenu {

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("QuickAccess");
		}


		@Order(0)
		public class NewSuTMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("NewSuT");
			}

			@Override
			protected void execAction() {
				new SuTForm(TEXTS.get("SuT")).startNew();
			}
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

	}

	@Order(2000)
	public class UserMenu extends AbstractMenu {

		@Override
		protected String getConfiguredIconId() {
			return AbstractIcons.Person;
		}
		
		@Order(1000)
		public class AboutMenu extends AbstractMenu {

			@Override
			protected String getConfiguredText() {
				return TEXTS.get("About");
			}

			@Override
			protected void execAction() {
				ScoutInfoForm form = new ScoutInfoForm();
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
			return IKeyStroke.F2;
		}
	}

	@Order(2000)
	public class SearchOutlineViewButton extends AbstractOutlineViewButton {

		public SearchOutlineViewButton() {
			this(SearchOutline.class);
		}

		protected SearchOutlineViewButton(Class<? extends SearchOutline> outlineClass) {
			super(Desktop.this, outlineClass);
		}

		@Override
		protected DisplayStyle getConfiguredDisplayStyle() {
			return DisplayStyle.TAB;
		}

		@Override
		protected String getConfiguredKeyStroke() {
			return IKeyStroke.F3;
		}
	}

}
