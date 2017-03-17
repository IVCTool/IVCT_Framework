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

import org.json.simple.JSONObject;

public class SetSUT implements Command {
	final String sut;
	final String sutPath;
	final IVCTcommander ivctCommander;
	final int counter;

	SetSUT(final String sut, IVCTcommander ivctCommander, String sutPath, final int counter) {
		this.sut = sut;
		this.ivctCommander = ivctCommander;
		this.sutPath = sutPath;
		this.counter = counter;
	}

	public void execute() {
		JSONObject obj = new JSONObject();
		obj.put("commandType", "setSUT");
		obj.put("sequence", new Integer(counter));
		obj.put("sut", this.sut);
		obj.put("sutPath", this.sutPath);
		String setSutString = obj.toString();
		
		this.ivctCommander.sendToJms(setSutString);
	}
}
