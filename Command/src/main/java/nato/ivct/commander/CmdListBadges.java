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

	/**
	 * The structure of a badge description looks like:
	 * 
	 * {
	 *   "id" 				: 	"HelloWorld",
	 *   "name" 			: 	"HelloWorld Tutorial Badge",
	 *   "description"		:	"This is a simple example for ...",
	 *   "graphics"			:	"/some/icon.png",
	 *   "tsRunTimeFolder"	:	"TS_HelloWorld/TS_HelloWorld/bin",
	 *   "dependency"		:	["HLA-BASE-2016"],
	 *   "requirements"		:	[
	 *   	{
	 *   		"id"			:	"IR-HW-0001",
	 *   		"description"	:	"Test population growing rate",
	 *   		"TC"			:	"de.fraunhofer.iosb.tc_helloworld.TC0001"
	 *   	},
	 *   	{
	 *   		"id"			:	"IP-HW-0002",
	 *   		"description"	:	"Test inter-country communication",
	 *   		"TC"			:	"de.fraunhofer.iosb.tc_helloworld.TC0002"
	 *   	}
	 *   ] 
	 * }
	 */
	@Override
	public void execute() {
		Factory.LOGGER.info("Factory.IVCT_BADGE_HOME_ID " + Factory.IVCT_BADGE_HOME_ID);
		File dir = new File(Factory.props.getProperty(Factory.IVCT_BADGE_HOME_ID));
		Factory.LOGGER.info("Read Badge descriptions from " + dir.getAbsolutePath());
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
