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

public class StartTestCase implements Command {
	final String paramJson;
	final String testcase;
	final String testsuite;
	final IVCTcommander ivctCommander;
	final int counter;

	StartTestCase(final String testcase, final IVCTcommander ivctCommander, final int counter) {
		this.testcase = testcase;
		this.ivctCommander = ivctCommander;
		this.counter = counter;
		this.testsuite = this.ivctCommander.getTestSuiteName();
		this.paramJson = ivctCommander.rtp.paramJson;
		ivctCommander.rtp.setTestCaseRunningBool(true);
	}

	public void execute() {
		final String packageName = ivctCommander.getPackageName(this.testsuite);
		if (packageName == null) {
            System.out.println("StartTestCase: packageName not found for " + this.testsuite + " testcase " + this.testcase + " not run");
            return;
		}
        String tsRunFolder = ivctCommander.getTsRunFolder();
        String testScheduleName = "";
		String startTestCaseString = IVCTcommander.printTestCaseJson(this.counter, testScheduleName, packageName + "." + this.testcase, tsRunFolder, this.paramJson);
		this.ivctCommander.sendToJms(startTestCaseString);
		this.ivctCommander.acquireSemaphore();
		ivctCommander.addTestSessionSeparator();
		ivctCommander.rtp.setTestCaseRunningBool(false);
	}
}
