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

/*
 * This class starts the conformance test.
 */
public class StartConformanceTest implements Command {
	final IVCTcommander ivctCommander;
	final int counter;

	StartConformanceTest (IVCTcommander ivctCommander, final int counter) {
		this.ivctCommander = ivctCommander;
		this.counter = counter;
	}

	public void execute() {
		String setConformanceTestString = IVCTcommander.printJson("{ \"commandType\" : \"startConformanceTest\" }", this.counter);
		this.ivctCommander.sendToJms(setConformanceTestString);
	}
}
