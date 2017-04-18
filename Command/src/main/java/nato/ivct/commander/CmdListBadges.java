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

import java.util.HashMap;

public class CmdListBadges implements Command {
	public HashMap<String, BadgeDescription> badgeMap = new HashMap<String, BadgeDescription>();


	@Override
	public void execute() {
		// TODO [hzg] implement the some file loader to read the JSON descriptions of Badges
		
		// Create dummy data
		BadgeDescription badge;
		badge = new BadgeDescription();
		badge.capabilityName = "HLA_BASE_2016";
		badge.description = "The Basic HLA Certification Test";
		badge.cbVisual = "file://some/path/icon.png";
		badgeMap.put("HLA_BASE_2016", badge);

		badge = new BadgeDescription();
		badge.capabilityName = "NETN-AGG-2016";
		badge.description = "NETN-FOM v2.0 Aggregate FOM Module";
		badge.cbVisual = "file://some/path/icon.png";
		badgeMap.put("NETN-AGG-2016", badge);

		badge = new BadgeDescription();
		badge.capabilityName = "NETN-ENTITY-2016";
		badge.description = "NETN FOM v2.0 Physical FOM Module";
		badge.cbVisual = "file://some/path/icon.png";
		badgeMap.put("NETN-ENTITY-2016", badge);
		
		badge = new BadgeDescription();
		badge.capabilityName = "CWIX-DR-2017";
		badge.description = "Simulation Interoperability Compliance Badge for CWIX 2017";
		badge.cbVisual = "file://some/path/icon.png";
		badgeMap.put("CWIX-DR-2017", badge);

		badge = new BadgeDescription();
		badge.capabilityName = "CWIX-WARFARE-2017";
		badge.description = "Simulation Interoperability Compliance Badge for CWIX 2017 WARFARE";
		badge.cbVisual = "file://some/path/icon.png";
		badgeMap.put("CWIX-WARFARE-2017", badge);

	}

}
