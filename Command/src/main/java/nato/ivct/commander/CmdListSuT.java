/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CmdListSuT implements Command {

	public HashMap<String, SutDescription> sutMap = new HashMap<String, SutDescription>();

	@Override
	public void execute() {
		// file loader to read the JSON descriptions of SuT's

		File dir = new File(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID));
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				Object obj;
				JSONParser parser = new JSONParser();
				try {
					SutDescription sut = new SutDescription();
					obj = parser.parse(new FileReader(file + "\\CS.json"));
					JSONObject jsonObj = (JSONObject) obj;
					sut.id = (String) jsonObj.get("id");
					sut.description = (String) jsonObj.get("description");
					sut.vendor = (String) jsonObj.get("vendor");
					sut.conformanceStatment = (String) jsonObj.get("badge");
					sutMap.put(sut.id, sut);
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}

		// // Create dummy data
		// SutDescription sut = new SutDescription();
		// sut.id = "SuT1";
		// sut.description = "Demonstration System";
		// sut.vendor = "Fraunhofer IOSB";
		// sut.conformanceStatment = "HelloWorld";
		// sutMap.put("SuT1", sut);
		//
		// sut = new SutDescription();
		// sut.id = "SuT2";
		// sut.description = "The same Demonstration System as the SuT1
		// demonstration system";
		// sut.vendor = "Fraunhofer IOSB";
		// sut.conformanceStatment = "HelloWorld";
		// sutMap.put("SuT2", sut);
		//
		// sut = new SutDescription();
		// sut.id = "SuT3";
		// sut.description = "Some other Demonstration System just to have some
		// other system with some very long description text that does not fit
		// into one single line";
		// sut.vendor = "Fraunhofer IOSB";
		// sut.conformanceStatment = "HelloWorld";
		// sutMap.put("SuT3", sut);

	}

}
