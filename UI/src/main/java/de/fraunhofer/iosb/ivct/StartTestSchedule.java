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

import java.util.concurrent.Semaphore;

public class StartTestSchedule implements Command {
	final CommandCache commandCache;
	final IVCTcommander ivctCommander;
	final String testsuite;
	private static Semaphore semaphore = new Semaphore(1);

	StartTestSchedule (final CommandCache commandCache, IVCTcommander ivctCommander, final String testsuite) {
		this.commandCache = commandCache;
		this.ivctCommander = ivctCommander;
		this.testsuite = testsuite;
	}
	
	// Call this from JMS thread listener to start the next test case
	public void gotVerdict() {
		semaphore.release();
	}

	public void execute() {
		for (String tc = commandCache.getNextTestCase(); tc != null; tc = commandCache.getNextTestCase()) {
			final String packageName = IVCTcommander.getPackageName(this.testsuite);
			if (packageName == null) {
	            System.out.println("StartTestCase: packageName not found for " + this.testsuite + " testcase " + tc + " not run");
	            return;
			}
            System.out.println("START TEST CASE");

			String startTestCaseString = IVCTcommander.printJson("startTestCase", "testCaseId", packageName + "." + tc);
			this.ivctCommander.sendToJms(startTestCaseString);
			try {
				semaphore.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
