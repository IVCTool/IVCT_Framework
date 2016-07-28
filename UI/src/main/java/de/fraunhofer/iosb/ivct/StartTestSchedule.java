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
	final String paramJson;
	final CommandCache commandCache;
	final IVCTcommander ivctCommander;
	final String testsuite;
	private int counter;

	StartTestSchedule (final CommandCache commandCache, IVCTcommander ivctCommander, final int counter) {
		this.commandCache = commandCache;
		this.ivctCommander = ivctCommander;
		this.counter = counter;
		this.testsuite = IVCTcommander.getTestSuiteName();
		this.paramJson = ivctCommander.rtp.paramJson;
	}
	
	public void execute() {
		for (String tc = commandCache.getNextTestCase(); tc != null; tc = commandCache.getNextTestCase()) {
			final String packageName = IVCTcommander.getPackageName(this.testsuite);
			if (packageName == null) {
	            System.out.println("StartTestCase: packageName not found for " + this.testsuite + " testcase " + tc + " not run");
	            return;
			}
            System.out.println("Start Test Case: " + tc);

            String tcRunDir = IVCTcommander.getTcRunDir();
			String startTestCaseString = IVCTcommander.printJson("startTestCase", this.counter++, "testCaseId", packageName + "." + tc, "tcRunDir", tcRunDir, "tcParam", this.paramJson);
			this.ivctCommander.sendToJms(startTestCaseString);
			this.ivctCommander.acquireSemaphore();
		}
        System.out.println("Test schedule finished: " + commandCache.getTestschedule());
	}
}
