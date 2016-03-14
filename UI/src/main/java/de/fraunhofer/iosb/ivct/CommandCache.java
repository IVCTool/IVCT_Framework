/*
Copyright 2016, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.ivct;

import java.util.List;

public class CommandCache {
	private String testschedule;
	private List<String> testcases;
	private int i;

	CommandCache(final String testschedule, final List<String> testcases) {
		this.testschedule = testschedule;
		this.testcases = testcases;
		i = 0;
	}
	
	public String getNextTestCase() {

		if (i < testcases.size()) {
		return testcases.get(i++);
		} else {
			return null;
		}
	}
	
	public String getTestschedule() {
		return this.testschedule;
	}
}
