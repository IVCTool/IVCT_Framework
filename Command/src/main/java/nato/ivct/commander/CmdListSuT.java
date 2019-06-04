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

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class CmdListSuT implements Command {

    public static final String ID                  = "id";
    public static final String NAME                = "name";
    public static final String DESCRIPTION         = "description";
    public static final String VENDOR              = "vendor";
    public static final String VERSION             = "version";
    public static final String BADGE               = "badge";
    public static final String SETTINGS_DESIGNATOR = "settingsDesignator";
    public static final String FEDERATION_NAME     = "federationName";

    
	public HashMap<String, SutDescription> sutMap = new HashMap<String, SutDescription>();

	/**
	 * The CmdListSuT command reads the SuT conformance statement JSON files, and creates a 
	 * hashMap for (id,SutDescription)
	 * 
	 * The format of a conformance statement is expected as follows:
	 * 
	 * {
     *    "id"                 : "hw_iosb",
     *    "name"               : "HelloWorld",
     *    "description"        : "HelloWorld system under federate for IVCT demonstration",
     *    "vendor"             : "Fraunhofer IOSB",
     *    "version"            : "1.0",
     *    "badge"              : ["HLA-BASE-2017"],
     *    "settingsDesignator" : "crcAddress=localhost:8989",
     *    "federationName"     : "TheWorld"
     * }
     * 
	 */
	@Override
	public void execute() {
		// file loader to read the JSON descriptions of SuT's

		// If property is not set, do not have any access to any SUTs
		if (Factory.props.containsKey(Factory.IVCT_SUT_HOME_ID) == false) {
			return;
		}
		File dir = new File(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID));
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				FileReader fReader = null;
				Object obj;
				JSONParser parser = new JSONParser();
				try {
					SutDescription sut = new SutDescription();
					fReader = new FileReader(file + "/CS.json");
					obj = parser.parse(fReader);
					JSONObject jsonObj = (JSONObject) obj;
					sut.ID = (String) jsonObj.get(ID);
					sut.name = (String) jsonObj.get(NAME);
					if (sut.name == null) {
						sut.name = sut.ID;
					}
					sut.version = (String) jsonObj.get(VERSION);
					sut.description = (String) jsonObj.get(DESCRIPTION);
                    sut.vendor = (String) jsonObj.get(VENDOR);
                    sut.settingsDesignator = (String) jsonObj.get(SETTINGS_DESIGNATOR);
                    sut.federation = (String) jsonObj.get(FEDERATION_NAME);
					JSONArray cs = (JSONArray) jsonObj.get(BADGE);
					
                    if (sut.ID == null) Factory.LOGGER.error("ID is undefined");
                    if (sut.name == null) Factory.LOGGER.error("name is undefined");
                    if (sut.version == null) Factory.LOGGER.warn("version is undefined");
                    if (sut.description == null) Factory.LOGGER.warn("description is undefined");
                    if (sut.vendor == null) Factory.LOGGER.warn("vendor is undefined");
                    if (sut.settingsDesignator == null) {
                        Factory.LOGGER.error("settingsDesignator is undefined, using default: " + Factory.SETTINGS_DESIGNATOR_DEFLT);
                        sut.settingsDesignator = Factory.SETTINGS_DESIGNATOR_DEFLT;
                    }
                    if (sut.federation == null) {
                        Factory.LOGGER.error("federation is undefined, using default: " + Factory.FEDERATION_NAME_DEFLT);
                        sut.federation = Factory.FEDERATION_NAME_DEFLT;
                    }
					
					for (int i=0; i < cs.size(); i++) {
						sut.badges.add(cs.get(i).toString());
					}
					sutMap.put(sut.ID, sut);
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					if (fReader != null) {
						try {
							fReader.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}

			}
			else {
				Factory.LOGGER.error(Factory.IVCT_SUT_HOME_ID + " folder not found");
			}
		}
	}

}
