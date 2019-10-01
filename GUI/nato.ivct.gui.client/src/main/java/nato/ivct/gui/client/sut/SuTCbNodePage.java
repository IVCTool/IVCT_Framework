package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.shared.sut.SuTCbNodePageData;


@Data(SuTCbNodePageData.class)
public class SuTCbNodePage extends AbstractPageWithNodes {

    private String sutId = null;

    private String badgeId = null;


    @Override
    protected String getConfiguredTitle() {
        // TODO [hzg] verify translation
        return TEXTS.get("SuTCbNodePage");
    }


    @Override
    protected boolean getConfiguredLeaf() {
        // do not show child notes
        return true;
    }


    @Override
    protected void execPageActivated() throws ProcessingException {
        if (getDetailForm() == null) {
            final SuTCbForm form = new SuTCbForm();
            form.setSutId(getSutId());
            form.setCbId(getBadgeId());
            setDetailForm(form);
            form.startView();
        }
    }


    @Override
    protected void execPageDeactivated() throws ProcessingException {
        if (getDetailForm() != null) {
            getDetailForm().doClose();
            setDetailForm(null);
        }
    }


    public void setBadgeId(String _badgeId) {
        badgeId = _badgeId;
    }


    public String getBadgeId() {
        return badgeId;
    }


    public String getSutId() {
        return sutId;
    }


    public void setSutId(String _sutId) {
        sutId = _sutId;
    }
}
