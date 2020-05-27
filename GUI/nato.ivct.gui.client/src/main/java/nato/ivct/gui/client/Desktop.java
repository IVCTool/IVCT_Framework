package nato.ivct.gui.client;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.basic.tree.ITreeNode;
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton;
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.form.AbstractFormMenu;
import org.eclipse.scout.rt.client.ui.form.IForm;
import org.eclipse.scout.rt.client.ui.messagebox.IMessageBox;
import org.eclipse.scout.rt.client.ui.messagebox.MessageBoxes;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.AbstractIcons;
import nato.ivct.gui.client.Desktop.AlterSuTMenu.DeleteSutMenu;
import nato.ivct.gui.client.Desktop.AlterSuTMenu.EditSutMenu;
import nato.ivct.gui.client.outlines.BadgeOutline;
import nato.ivct.gui.client.outlines.SuTOutline;
import nato.ivct.gui.client.outlines.TsOutline;
import nato.ivct.gui.client.sut.SuTEditForm;
import nato.ivct.gui.client.sut.SuTForm;
import nato.ivct.gui.shared.Icons;
import nato.ivct.gui.shared.sut.ISuTService;


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
    protected List<Class<? extends IOutline>> getConfiguredOutlines() {
        return CollectionUtility.<Class<? extends IOutline>> arrayList(BadgeOutline.class, TsOutline.class, SuTOutline.class);
    }


    @Override
    protected void execDefaultView() {
        setOutline(SuTOutline.class);
    }

    @Order(91)
    public class TestEngineStatus extends AbstractFormMenu<HeartBeatInfoForm> {

        @Override
        protected String getConfiguredText() {
            return TEXTS.get("TestEngine");
        }


        @Override
        protected String getConfiguredIconId() {
            return Icons.WhiteBullet_32x32;
        }


        @Override
        protected Class<HeartBeatInfoForm> getConfiguredForm() {
            return HeartBeatInfoForm.class;
        }


        @Override
        protected void execInitAction() {
            setProperty("hbSender", "TestEngine");
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
            return TEXTS.get("LogSink");
        }


        @Override
        protected String getConfiguredIconId() {
            return Icons.WhiteBullet_32x32;
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
            return TEXTS.get("SuTSettings");
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
                final SuTEditForm form = new SuTEditForm("");
                form.startNew();
            }
        }

        @Order(120)
        public class EditSutMenu extends AbstractMenu {
            @Override
            protected String getConfiguredText() {
                return TEXTS.get("EditSuT");
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
                final SuTEditForm form = new SuTEditForm("");
                form.startModify();
            }
        }

        @Order(130)
        public class DeleteSutMenu extends AbstractMenu {
            @Override
            protected String getConfiguredText() {
                return TEXTS.get("DeleteSuT");
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
                final IDesktop desktop = ClientSessionProvider.currentSession().getDesktop();
                final IOutline outline = desktop.getOutline();
                if (outline instanceof SuTOutline) {
                    final IForm form = desktop.getPageDetailForm();
                    if (form instanceof SuTForm) {
                        int result = MessageBoxes.createYesNo().withHeader(TEXTS.get("DeleteSuTMsgBoxHeader")).withBody("SuT: "+((SuTForm) form).getSutId()).show();
                        if (result == IMessageBox.YES_OPTION) {
                            if (BEANS.get(ISuTService.class).deleteSut(((SuTForm) form).getSutId())) {
                                //Success message
                                MessageBoxes.createOk().withHeader(TEXTS.get("DeleteSuTSuccessMsgBoxHeader")).show();

                                final AbstractPageWithNodes pageWithNode = (AbstractPageWithNodes) outline.getRootNode();
                                final ITreeNode sutNode = pageWithNode.getTree().findNode(((SuTForm) form).getSutId());

                                if (sutNode != null) {
                                    pageWithNode.getTree().removeNode(sutNode);
                                    // close form
                                    form.doClose();
                                    return;
                                }
                            }
                            //Error message
                            MessageBoxes.createOk().withHeader(TEXTS.get("DeleteSuTErrorMsgBoxHeader")).show();
                        }
                    }
                }
            }
        }
    }

    @Order(1500)
    public class OptionsMenu extends AbstractFormMenu<OptionsForm> {
        //        @Override
        //        protected String getConfiguredText() {
        //            return TEXTS.get("Options");
        //        }

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
            return Icons.AppLogo;
        }

        @Order(1000)
        public class AboutMenu extends AbstractMenu {

            @Override
            protected String getConfiguredText() {
                return TEXTS.get("About");
            }


            @Override
            protected void execAction() {
                final IvctInfoForm form = new IvctInfoForm();
                form.startModify();
            }
        }

        @Order(1200)
        public class LogoutMenu extends AbstractMenu {

            @Override
            protected String getConfiguredText() {
                return TEXTS.get("Logout");
            }


            @Override
            protected void execAction() {
                ClientSessionProvider.currentSession(ClientSession.class).stop();
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
        protected DisplayStyle getConfiguredDisplayStyle() {
            return DisplayStyle.TAB;
        }


        @Override
        protected String getConfiguredKeyStroke() {
            //			return IKeyStroke.F2;
            return "ctrl-shift-b";
        }
    }

    @Order(2000)
    public class TsOutlineViewButton extends AbstractOutlineViewButton {

        public TsOutlineViewButton() {
            this(TsOutline.class);
        }


        protected TsOutlineViewButton(Class<? extends TsOutline> outlineClass) {
            super(Desktop.this, outlineClass);
        }


        @Override
        protected DisplayStyle getConfiguredDisplayStyle() {
            return DisplayStyle.TAB;
        }


        @Override
        protected String getConfiguredKeyStroke() {
            return "ctrl-shift-t";
        }
    }

    @Order(3000)
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
        if (newForm instanceof SuTForm) {
            this.getMenuByClass(EditSutMenu.class).setVisible(true);
            this.getMenuByClass(DeleteSutMenu.class).setVisible(true);
        }
        else {
            this.getMenuByClass(EditSutMenu.class).setVisible(false);
            this.getMenuByClass(DeleteSutMenu.class).setVisible(false);
        }
    }


    @Override
    protected void execOutlineChanged(IOutline oldOutline, IOutline newOutline) {
        if (newOutline instanceof SuTOutline) {
            this.getMenuByClass(AlterSuTMenu.class).setVisible(true);
            this.getMenuByClass(DeleteSutMenu.class).setVisible(true);
        }
        else {
            this.getMenuByClass(AlterSuTMenu.class).setVisible(false);
            this.getMenuByClass(DeleteSutMenu.class).setVisible(false);
        }
    }

}
