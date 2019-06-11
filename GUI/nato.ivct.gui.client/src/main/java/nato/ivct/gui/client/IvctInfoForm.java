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
		final IHtmlElement html = HTML.div(
// 			1 single column
//	        createLogoHtml(),
//	        createTitleHtml(),
//	        createHtmlTable(getProperties())
			HTML.table(HTML.tr(HTML.td(createLogoHtml(),
					           HTML.td(createTitleHtml(), createHtmlTable(getProperties())).style("vertical-align:top; padding-left:10px"))
			)));
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
//      Scout logo used
//		IconSpec logo = IconLocator.instance().getIconSpec(AbstractIcons.ApplicationLogo);
//		if (logo != null) {
//		  return HTML.p(HTML.imgByIconId(AbstractIcons.ApplicationLogo).cssClass("scout-info-form-logo"));
//		}
//		return null;
		
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
	  for (Entry<String, ?> p : properties.entrySet()) {
	    rows.add(createHtmlRow(p.getKey(), p.getValue()));
	  }
	  return HTML.table(rows);
	}
	  
	@Override
	protected String getProductVersion() {
//	    return CONFIG.getPropertyValue(ApplicationVersionProperty.class);
		return  BEANS.get(IOptionsService.class).getIvctVersion();
	}
}
