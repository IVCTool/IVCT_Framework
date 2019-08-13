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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * The CmdListBadges implements the loading of the badge description files. The Command.execute will
 * load the json files located inside the IVCT_BADGE_HOME_ID folder, and will fill a hashmap with a
 * badge id as a key, and BadgeDescription as value. A simple badge file will look like the
 * following example:
 *
 * { "id" : "HelloWorld-2017", "version" : "2.0.0", "name" : "HelloWorld Tutorial Badge",
 * "description" : "This is a simple example for capability badge to test the compliance of an
 * federate to the hello world federation.", "graphics" : "HelloWorld-2017.png", "dependency" : [],
 * "requirements" : [ { "id" : "IR-HW-0001", "description" : "Test population growing rate", }, {
 * "id" : "IP-HW-0002", "description" : "Test inter-country communication", } ] }
 *
 * @author hzg
 *
 */
public class CmdListBadges implements Command {

    public Map<String, BadgeDescription> badgeMap = new HashMap<String, BadgeDescription>();
    private Map<String, InteroperabilityRequirement> irMap = new HashMap<String, InteroperabilityRequirement>();

    @Override
    public void execute() {
        Factory.LOGGER.trace("Factory.IVCT_BADGE_HOME_ID = " + Factory.props.getProperty(Factory.IVCT_BADGE_HOME_ID));
        String iconsFolder = Factory.props.getProperty(Factory.IVCT_BADGE_ICONS_ID);
        File dir = new File(Factory.props.getProperty(Factory.IVCT_BADGE_HOME_ID));
        String dirName = Factory.props.getProperty(Factory.IVCT_BADGE_HOME_ID);
        if (dir.exists() == false) {
            Factory.LOGGER.error("badge: {} does not exist", dirName);
            return;
        }
        irMap.clear();
        if (dir.isDirectory()) {
            Factory.LOGGER.trace("Read Badge descriptions from " + dir.getAbsolutePath());
            JSONParser parser = new JSONParser();
            File[] filesList = dir.listFiles();
            for (File file : filesList) {
                Factory.LOGGER.trace("reading badge description: " + file.getAbsolutePath());
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
                        badge.cbVisual = (String) jsonObj.get("graphics");
                        // if the badge defines an icon then try to get it from the icon folder
                        // (respectively from its default folder)
                        if (badge.cbVisual != null) {
                            Path iconFile = Paths.get(iconsFolder, badge.cbVisual);
                            if (iconFile.toFile().exists()) {
                                badge.cbVisual = iconFile.toString();
                            } else {
                                badge.cbVisual = null;
                            }
                        }
                        JSONArray depend = (JSONArray) jsonObj.get("dependency");
                        if (depend != null) {
                            badge.dependency = new HashSet<String>();
                            for (int i = 0; i < depend.size(); i++) {
                            	if (badge.dependency.contains(depend.get(i).toString()) == false) {
                            		badge.dependency.add(depend.get(i).toString());
                            	}
                            }
                        } else {
                            badge.dependency = null;
                        }
                        JSONArray requirements = (JSONArray) jsonObj.get("requirements");
                        badge.requirements = new HashMap <String, InteroperabilityRequirement>();
                        if (requirements != null) {
                            for (int i = 0; i < requirements.size(); i++) {
                                JSONObject req = (JSONObject) requirements.get(i);
                                InteroperabilityRequirement ir = new InteroperabilityRequirement();
                                ir.ID = (String) req.get("id");
                                ir.description = (String) req.get("description");
                                badge.requirements.put(ir.ID, ir);
                                irMap.put(ir.ID, ir);
                            }
                        }

                        this.badgeMap.put(badge.ID, badge);
                        fr.close();
                        fr = null;
                    } catch (IOException | ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
        } else {
            Factory.LOGGER.error("badge: {} value not a folder", dirName);
            return;
        }
    }

    public void collectIrForCs(Set<String> ir_set, Set<String> cs) {
        if (this.badgeMap == null) {
            return;
        }

        for (String badge_id : cs) {
            BadgeDescription b = this.badgeMap.get(badge_id);
            // collect badge requirements
            for (Map.Entry<String, InteroperabilityRequirement> entry : b.requirements.entrySet()) {
                ir_set.add(entry.getKey());
            }
            // collect recursively from dependend badges
            collectIrForCs(ir_set, b.dependency);
        }
    }
    
    /**
     * 
     * @param irName the id of the interoperability requirement
     * @return the InteroperabilityRequirement value
     */
    public InteroperabilityRequirement getIR (String irName) {
    	return irMap.get(irName);
    }
}
