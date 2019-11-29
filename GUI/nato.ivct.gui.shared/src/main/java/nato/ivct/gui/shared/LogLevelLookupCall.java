package nato.ivct.gui.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;


public class LogLevelLookupCall extends LocalLookupCall<String> {
	
	private static final long serialVersionUID = 1L;
	
	public static enum LogLevels {
		TRACE("trace"), DEBUG("debug"), INFO("info"), WARNING("warn"), ERROR("error");
		private String key;
		
		LogLevels(String key_) {
			this.key = key_;
		}
	}
	
	protected String[] sort(String[] logLevels) {
		Arrays.sort(logLevels, new Comparator<String>() {
			@Override
			public int compare(String level1, String level2) {
				return level1.compareTo(level2);
			}
		});
		
		return logLevels;
	}
	
	@Override
	protected List<LookupRow<String>> execCreateLookupRows() {
		List<LookupRow<String>> rows = new ArrayList<LookupRow<String>>();
//		for (String logLevel : sort(AVAILABLE_LOGLEVELS)) {
//			String displayName = locale.getDisplayName(NlsLocale.get());
//			if (StringUtility.hasText(displayName)) {
//				rows.add(new LookupRow<String>(locale, displayName));
//			}
//	}
			
		Stream.of(LogLevels.values()).forEachOrdered(logLevel->{
			rows.add(new LookupRow<String>(logLevel.key, logLevel.name()));
		});
		return rows;
	}

}
