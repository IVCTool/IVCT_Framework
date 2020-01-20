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

import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.SutDescription;

public class StartTestSchedule implements nato.ivct.commander.Command {
	final String sutName;
	final SutDescription sutDescription;
	final CommandCache commandCache;
	final IVCTcommander ivctCommander;
	final CmdListTestSuites cmdListTestSuites;

	StartTestSchedule (final String sutName, final SutDescription sutDescription, final CmdListTestSuites cmdListTestSuites, final CommandCache commandCache, IVCTcommander ivctCommander) {
		this.sutName = sutName;
		this.sutDescription = sutDescription;
		this.cmdListTestSuites = cmdListTestSuites;
		this.commandCache = commandCache;
		this.ivctCommander = ivctCommander;
		ivctCommander.rtp.setTestScheduleRunningBool(true);
	}
	
	public void execute() {
		for (String tc = commandCache.getNextTestCase(); tc != null; tc = commandCache.getNextTestCase()) {
			if (RuntimeParameters.getAbortTestScheduleBool()) {
				break;
			}
			ivctCommander.rtp.startTestCase(this.sutName, this.sutDescription, cmdListTestSuites.getTestSuiteForTc(tc).id, tc);

			this.ivctCommander.rtp.acquireSemaphore();
		}
		ivctCommander.addTestSessionSeparator();
		RuntimeParameters.setAbortTestScheduleBool(false);
		ivctCommander.rtp.setTestScheduleRunningBool(false);
		System.out.println("Test badge finished: " + ivctCommander.rtp.getTestScheduleName());
	}
}
