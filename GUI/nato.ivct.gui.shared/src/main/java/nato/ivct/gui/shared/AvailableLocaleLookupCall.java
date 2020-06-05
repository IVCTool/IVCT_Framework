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

package nato.ivct.gui.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.scout.rt.platform.nls.NlsLocale;
import org.eclipse.scout.rt.platform.util.StringUtility;
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

public class AvailableLocaleLookupCall extends LocalLookupCall<Locale> {
	
	private static final Locale[] AVAILABLE_LOCALES = new Locale[]{Locale.ENGLISH, Locale.GERMAN};
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
		
		List<LookupRow<Locale>> rows = new ArrayList<>();
		for (Locale locale : sort(AVAILABLE_LOCALES)) {
			String displayName = locale.getDisplayName(NlsLocale.get());
			if (StringUtility.hasText(displayName)) {
				rows.add(new LookupRow<Locale>(locale, displayName));
			}
		}
		return rows;
	}
}
