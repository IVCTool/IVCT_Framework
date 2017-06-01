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

import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;

public class CmdListBadges implements Command {
	public HashMap<String, BadgeDescription> badgeMap = new HashMap<String, BadgeDescription>();

	@Override
	public void execute() {
		File dir = new File(Factory.props.getProperty(Factory.IVCT_TS_HOME_ID));
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			Object obj;
			JSONParser parser = new JSONParser();
			if (file.isFile()) {
				try {
					BadgeDescription badge = new BadgeDescription();
					obj = parser.parse(new FileReader(file));
					JSONObject jsonObj = (JSONObject) obj;
					badge.ID = (String) jsonObj.get("id");
					badge.name = (String) jsonObj.get("name");
					badge.description = (String) jsonObj.get("description");
					badge.tsRunTimeFolder = (String) jsonObj.get("tsRunTimeFolder");
					badge.cbVisual = (String) jsonObj.get("graphics");
					JSONArray depend = (JSONArray) jsonObj.get("dependency");
					if (depend != null) {
						badge.dependency = new String[depend.size()];
						for (int i = 0; i < depend.size(); i++) {
							badge.dependency[i] = depend.get(i).toString();
						}
					} else {
						badge.dependency = null;
					}
					JSONArray requirements = (JSONArray) jsonObj.get("requirements");
					if (requirements != null) {
						badge.requirements = new InteroperabilityRequirement[requirements.size()];
						for (int i = 0; i < requirements.size(); i++) {
							JSONObject req = (JSONObject) requirements.get(i);
							badge.requirements[i] = badge.new InteroperabilityRequirement();
							badge.requirements[i].ID = (String) req.get("id");
							badge.requirements[i].description = (String) req.get("description");
							badge.requirements[i].TC = (String) req.get("TC");
						}
					} else {
						badge.requirements = null;
					}

					badgeMap.put(badge.ID, badge);
				} catch (IOException | ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}

}
