/* Copyright 2020, Michael Theis, Felix Schoeppenthau (Fraunhofer IOSB)

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
import java.util.stream.Stream;

import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;


public class LogLevelLookupCall extends LocalLookupCall<String> {
	
	private static final long serialVersionUID = 1L;
	
	public enum LogLevels {
		TRACE("trace"), DEBUG("debug"), INFO("info"), WARN("warn"), ERROR("error");
		private String key;
		
		LogLevels(String key) {
			this.key = key;
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
		List<LookupRow<String>> rows = new ArrayList<>();
			
		Stream.of(LogLevels.values()).forEachOrdered(logLevel->{
			rows.add(new LookupRow<String>(logLevel.key, logLevel.name()));
		});
		return rows;
	}

}
