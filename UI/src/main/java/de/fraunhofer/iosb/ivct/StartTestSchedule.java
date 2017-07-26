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

public class StartTestSchedule implements Command {
	final CommandCache commandCache;
	final IVCTcommander ivctCommander;

	StartTestSchedule (final CommandCache commandCache, IVCTcommander ivctCommander) {
		this.commandCache = commandCache;
		this.ivctCommander = ivctCommander;
		ivctCommander.rtp.setTestScheduleRunningBool(true);
	}
	
	public void execute() {
		for (String tc = commandCache.getNextTestCase(); tc != null; tc = commandCache.getNextTestCase()) {
			if (RuntimeParameters.getAbortTestScheduleBool()) {
				break;
			}
			ivctCommander.rtp.startTestCase(commandCache.getTestschedule(), tc);

			this.ivctCommander.acquireSemaphore();
		}
		ivctCommander.addTestSessionSeparator();
		RuntimeParameters.setAbortTestScheduleBool(false);
		ivctCommander.rtp.setTestScheduleRunningBool(false);
		System.out.println("Test schedule finished: " + commandCache.getTestschedule());
	}
}
