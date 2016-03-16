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
import java.util.Map;

/*
 * This class gets and displays the test schedules for a test suite.
 */
public class ListTestSchedules implements Command {
	final IVCTcommander ivctCommander;
	RuntimeParameters rtp;
	String testsuite;
	boolean displayList;

	ListTestSchedules(IVCTcommander ivctCommander, RuntimeParameters rtp, final String testsuite, final boolean displayList) {
		this.ivctCommander = ivctCommander;
		this.rtp = rtp;
		this.testsuite = testsuite;
		this.displayList = displayList;
	}

	public void execute() {
		this.rtp.testsuiteTestcases = IVCTcommander.readTestSuiteFiles(this.testsuite);
		if (displayList) {
	        for (Map.Entry<String, List<String>> entry : this.rtp.testsuiteTestcases.entrySet()) {
	            String schedule = entry.getKey();
                System.out.println(schedule);
	        }			
		}
	}
}
