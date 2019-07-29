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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;

public class CmdListBadges implements Command {
	public HashMap<String, BadgeDescription> badgeMap = new HashMap<String, BadgeDescription>();

	/**
	 * The structure of a badge description looks like:
	 * 
	 * { "id" : "HelloWorld", "version" : "0.4.0", "name" : "HelloWorld Tutorial
	 * Badge", "description" : "This is a simple example for ...", "graphics" :
	 * "/some/icon.png", "tsRunTimeFolder" :
	 * "TS_HelloWorld-0.4.0/TS_HelloWorld/bin", "tsLibTimeFolder" :
	 * "TS_HelloWorld-0.4.0/TS_HelloWorld/lib", "dependency" : ["HLA-BASE-2016"],
	 * "requirements" : [ { "id" : "IR-HW-0001", "description" : "Test population
	 * growing rate", "TC" : "de.fraunhofer.iosb.tc_helloworld.TC0001" }, { "id" :
	 * "IP-HW-0002", "description" : "Test inter-country communication", "TC" :
	 * "de.fraunhofer.iosb.tc_helloworld.TC0002" } ] }
	 */
	@Override
	public void execute() {
		Factory.LOGGER.trace("Factory.IVCT_BADGE_HOME_ID " + Factory.IVCT_BADGE_HOME_ID);
        String iconsFolder = Factory.props.getProperty(Factory.IVCT_BADGE_ICONS_ID);
		File dir = new File(Factory.props.getProperty(Factory.IVCT_BADGE_HOME_ID));
		if (dir.isDirectory()) {
			Factory.LOGGER.trace("Read Badge descriptions from " + dir.getAbsolutePath());
            JSONParser parser = new JSONParser();
			File[] filesList = dir.listFiles();
			for (File file : filesList) {
				Object obj;
				if (file.isFile() && file.getName().toLowerCase().endsWith(".json")) {
					FileReader fr = null;
					try {
						BadgeDescription badge = new BadgeDescription();
						fr = new FileReader(file);
						obj = parser.parse(fr);
						JSONObject jsonObj = (JSONObject) obj;
						badge.ID = (String) jsonObj.get("id");
						badge.version = (String) jsonObj.get("version");
						badge.name = (String) jsonObj.get("name");
						badge.description = (String) jsonObj.get("description");
						badge.tsRunTimeFolder = (String) jsonObj.get("tsRunTimeFolder");
						badge.tsLibTimeFolder = (String) jsonObj.get("tsLibTimeFolder");
						badge.cbVisual = (String) jsonObj.get("graphics");
						// if the badge defines an icon then try to get it from the icon folder 
						// (respectively from its default folder)
						if (badge.cbVisual != null) {
							Path iconFile = Paths.get(iconsFolder, badge.cbVisual);
							if (iconFile.toFile().exists())
								badge.cbVisual = iconFile.toString();
							else
							    badge.cbVisual = null;
						}
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
						fr.close();
						fr = null;
					} catch (IOException | ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			}
		} else {
			Factory.LOGGER.error(Factory.IVCT_BADGE_HOME_ID + " folder not found");
			return;
		}
	}
}
