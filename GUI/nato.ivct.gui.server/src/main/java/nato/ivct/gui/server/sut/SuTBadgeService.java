/* Copyright 2020, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.server.sut;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.sut.ISuTBadgeService;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTBadgeTablePageData;
import nato.ivct.gui.shared.sut.SuTBadgeTablePageData.SuTBadgeTableRowData;

public class SuTBadgeService implements ISuTBadgeService {

	private static final Logger LOG = LoggerFactory.getLogger(SuTBadgeService.class);
	private static HashMap<String, SuTBadgeTablePageData> badgeHm = new HashMap<>();
	
	private static Set<BadgeDescription> mCollectedBadges = new TreeSet<>(Comparator.comparing(bdDesc -> bdDesc.name));

	
	/*
	 * get SuTBadgeTablePageData for a specific SuT ID. Create new one or select existing
	 */
	@Override
	public SuTBadgeTablePageData getSuTBadgeTableData(SearchFilter filter) {
		String[] searchText = filter.getDisplayTexts();
		SuTBadgeTablePageData pageData = badgeHm.get (searchText);
		if (pageData == null) {
			pageData = new SuTBadgeTablePageData();
		}

		LOG.info("getBadgeTableData");
		SutDescription sutDesc = ((SuTService) BEANS.get(ISuTService.class)).getSutDescription(searchText[0]);
		mCollectedBadges.clear();
		collectBadgesForSut (sutDesc);
		
		for (BadgeDescription bd:mCollectedBadges) {
			SuTBadgeTableRowData row = pageData.addRow();
			row.setBadgeId(bd.ID);
			row.setBadgeName(bd.name);
			row.setBadgeDesc(bd.description);
			row.setSuTBadgeResult("no result");
		}
		
		badgeHm.put(sutDesc.ID, pageData);
		return pageData;
	}
	
	private void collectBadgesForSut(final SutDescription sut) {
		CbService cbService = BEANS.get(CbService.class);
		
		sut.badges.stream().map(cbService::getBadgeDescription).forEach(badgeDescription -> addBadgeToCollection (badgeDescription, mCollectedBadges));
		
	}
	
	private void addBadgeToCollection (final BadgeDescription parentBadge, final Set<BadgeDescription> badgeCollection) {
		CbService cbService = (CbService) BEANS.get(ICbService.class);
		if (parentBadge != null) {
			badgeCollection.add(parentBadge);
			
			parentBadge.dependency.stream().map(cbService::getBadgeDescription).forEach(depBadge -> {
				badgeCollection.add(depBadge);
				addBadgeToCollection (depBadge, badgeCollection);
			});
		}
	}
	
}
