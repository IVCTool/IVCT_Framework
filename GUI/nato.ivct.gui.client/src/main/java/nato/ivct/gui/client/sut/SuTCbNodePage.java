/* Copyright 2020, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

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


    public void setBadgeId(String badgeId) {
        this.badgeId = badgeId;
    }


    public String getBadgeId() {
        return badgeId;
    }


    public String getSutId() {
        return sutId;
    }


    public void setSutId(String sutId) {
        this.sutId = sutId;
    }
}
