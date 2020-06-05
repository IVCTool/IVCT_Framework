/* Copyright 2020, Michael Theis, Felix Schöppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.scout.rt.client.services.common.icon.IconLocator;
import org.eclipse.scout.rt.client.services.common.icon.IconSpec;
import org.eclipse.scout.rt.client.ui.form.ScoutInfoForm;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.html.HTML;
import org.eclipse.scout.rt.platform.html.IHtmlElement;
import org.eclipse.scout.rt.platform.html.IHtmlTable;
import org.eclipse.scout.rt.platform.html.IHtmlTableRow;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.platform.util.StringUtility;

import nato.ivct.gui.shared.IOptionsService;
import nato.ivct.gui.shared.Icons;


public class IvctInfoForm extends ScoutInfoForm {

    public IvctInfoForm() throws ProcessingException {
        super();
    }

    public class IVCTMainBox extends MainBox {

        @Override
        protected int getConfiguredWidthInPixel() {
            // estimated size in px
            return 500;
        }
    }

    @Override
    protected String createHtmlBody() {
        final IHtmlElement html = HTML.div(HTML.table(HTML.tr(HTML.td(createLogoHtml(), HTML.td(createTitleHtml(), createHtmlTable(getProperties())).style("vertical-align:top; padding-left:10px")))));
        return html.toHtml();
    }


    @Override
    protected IHtmlElement createTitleHtml() {
        String title = StringUtility.join(" ", getProductName(), getProductVersion());
        if (StringUtility.hasText(title)) {
            return HTML.h2(title);
        }
        return null;
    }


    @Override
    protected IHtmlElement createLogoHtml() {

        IconSpec logo = IconLocator.instance().getIconSpec(Icons.IvctLogo);
        if (logo != null) {
            return HTML.p(HTML.imgByIconId(Icons.IvctLogo).cssClass("scout-info-form-logo"));
        }
        return null;
    }


    @Override
    protected IHtmlTable createHtmlTable(Map<String, ?> properties) {
        List<IHtmlTableRow> rows = new ArrayList<>();

        // add build id
        rows.add(createHtmlRow(TEXTS.get("Build"), BEANS.get(IOptionsService.class).getIvctBuild()));

        // add Scout properties
        for (Entry<String, ?> p: properties.entrySet()) {
            rows.add(createHtmlRow(p.getKey(), p.getValue()));
        }
        return HTML.table(rows);
    }


    @Override
    protected String getProductVersion() {
        return BEANS.get(IOptionsService.class).getIvctVersion();
    }
}
