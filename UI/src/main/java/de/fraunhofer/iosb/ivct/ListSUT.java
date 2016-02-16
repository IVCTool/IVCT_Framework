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

public class ListSUT implements Command {
	final IVCTcommander ivctCommander;
	RuntimeParameters rtp;
	boolean displayList;

	ListSUT (IVCTcommander ivctCommander, RuntimeParameters rtp, final boolean displayList) {
		this.ivctCommander = ivctCommander;
		this.rtp = rtp;
		this.displayList = displayList;
	}

	public void execute() {
		this.rtp.suts = IVCTcommander.listSUT();
		if (displayList) {
			for (String entry : this.rtp.suts)
			{
				System.out.println(entry);
			}
		}
	}
}
