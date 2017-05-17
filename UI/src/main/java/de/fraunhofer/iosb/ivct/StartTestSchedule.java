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
	final String sut;
	final String sutDir;
	final String testsuite;
	private int counter;

	StartTestSchedule (final CommandCache commandCache, IVCTcommander ivctCommander, final int counter) {
		this.commandCache = commandCache;
		this.ivctCommander = ivctCommander;
		this.counter = counter;
		this.sut = this.ivctCommander.rtp.getSutName();
		this.sutDir = IVCTcommander.getSUTdir() + "\\" + ivctCommander.rtp.getSutName();
		this.testsuite = this.ivctCommander.getTestSuiteName();
		this.paramJson = ivctCommander.rtp.paramJson;
		ivctCommander.rtp.setTestScheduleRunningBool(true);
	}
	
	public void execute() {
		int currentTestcase = 0;
		int numberOfTestCases = commandCache.getNumberOfTestCases();
		
		for (String tc = commandCache.getNextTestCase(); tc != null; tc = commandCache.getNextTestCase()) {
			if (RuntimeParameters.getAbortTestScheduleBool()) {
				break;
			}
			final String packageName = ivctCommander.getPackageName(this.testsuite);
			if (packageName == null) {
	            System.out.println("StartTestCase: packageName not found for " + this.testsuite + " testcase " + tc + " not run");
	            return;
			}
			currentTestcase += 1;
            System.out.println("Start Test Case: " + tc + " (" + currentTestcase + " of " + numberOfTestCases + ")");

            String testScheduleName = "";
            if (ivctCommander.rtp.getTestScheduleRunningBool()) {
            	testScheduleName = RuntimeParameters.getTestScheduleName();
            }
            String tsRunFolder = ivctCommander.getTsRunFolder();
			String startTestCaseString = IVCTcommander.printTestCaseJson(this.counter++, this.sut, this.sutDir, testScheduleName, packageName + "." + tc, tsRunFolder, this.paramJson);
			this.ivctCommander.sendToJms(startTestCaseString);
			this.ivctCommander.acquireSemaphore();
		}
		ivctCommander.addTestSessionSeparator();
    	RuntimeParameters.setAbortTestScheduleBool(false);
		ivctCommander.rtp.setTestScheduleRunningBool(false);
        System.out.println("Test schedule finished: " + commandCache.getTestschedule());
	}
}
