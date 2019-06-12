package nato.ivct.gui.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.scout.rt.platform.nls.LocaleUtility;
import org.eclipse.scout.rt.platform.nls.NlsLocale;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

public class AvailableLocaleLookupCall extends LocalLookupCall<Locale> {
	
	private static final Locale[] AVAILABLE_LOCALES = new Locale[]{Locale.US, LocaleUtility.parse("de_DE")};
	private static final long serialVersionUID = 1L;
	
	protected Locale[] sort(Locale[] locales) {
		Arrays.sort(locales, new Comparator<Locale>() {
			@Override
			public int compare(Locale locale1, Locale locale2) {
				String name1 = locale1.getDisplayName(NlsLocale.get());
				String name2 = locale2.getDisplayName(NlsLocale.get());
				return name1.compareTo(name2);
			}
		});
		
		return locales;
	}
	
	@Override
	protected List<LookupRow<Locale>> execCreateLookupRows() {
		
		List<LookupRow<Locale>> rows = new ArrayList<LookupRow<Locale>>();
		for (Locale locale : sort(AVAILABLE_LOCALES)) {
			String displayName = locale.getDisplayName(NlsLocale.get());
			if (StringUtility.hasText(displayName)) {
				rows.add(new LookupRow<Locale>(locale, displayName));
			}
		}
		return rows;
	}
}
