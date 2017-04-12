/* Copyright 2015, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import java.util.HashMap;

public class CmdListSuT implements Command {

	public HashMap<String, SutDescription> sutMap = new HashMap<String, SutDescription>();

	@Override
	public void execute() {
		// TODO [hzg] implement the some file loader to read the JSON descriptions of SuT's
		
		// Create dummy data
		SutDescription sut = new SutDescription();
		sut.id = "SuT1";
		sut.description = "Demonstration System";
		sut.vendor = "Fraunhofer IOSB";
		sut.conformanceStatment = "HelloWorld";
		sutMap.put("SuT1", sut);

		sut = new SutDescription();
		sut.id = "SuT2";
		sut.description = "The same Demonstration System as the SuT1 demonstration system";
		sut.vendor = "Fraunhofer IOSB";
		sut.conformanceStatment = "HelloWorld";
		sutMap.put("SuT2", sut);

		sut = new SutDescription();
		sut.id = "SuT3";
		sut.description = "Some other Demonstration System just to have some other system with some very long description text that does not fit into one single line";
		sut.vendor = "Fraunhofer IOSB";
		sut.conformanceStatment = "HelloWorld";
		sutMap.put("SuT3", sut);
		
	}

}
