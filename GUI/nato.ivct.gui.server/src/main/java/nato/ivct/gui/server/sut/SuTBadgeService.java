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
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.sut.ISuTBadgeService;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTBadgeTablePageData;
import nato.ivct.gui.shared.sut.SuTBadgeTablePageData.SuTBadgeTableRowData;

public class SuTBadgeService implements ISuTBadgeService {

	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private static HashMap<String, SuTBadgeTablePageData> badge_hm = new HashMap<String, SuTBadgeTablePageData>();
	
	private static Set<BadgeDescription> m_collectedBadges = new TreeSet<>(Comparator.comparing(bdDesc -> bdDesc.name));

	
	/*
	 * get SuTBadgeTablePageData for a specific SuT ID. Create new one or select existing
	 */
	@Override
	public SuTBadgeTablePageData getSuTBadgeTableData(SearchFilter filter) {
		String[] searchText = filter.getDisplayTexts();
		SuTBadgeTablePageData pageData = badge_hm.get (searchText);
		if (pageData == null) {
			pageData = new SuTBadgeTablePageData();
		}

		LOG.info("getBadgeTableData");
		SutDescription sutDesc = ((SuTService) BEANS.get(ISuTService.class)).getSutDescription(searchText[0]);
		m_collectedBadges.clear();
		collectBadgesForSut (sutDesc);
		
		for (BadgeDescription bd:m_collectedBadges) {
			SuTBadgeTableRowData row = pageData.addRow();
			row.setBadgeId(bd.ID);
			row.setBadgeName(bd.name);
			row.setBadgeDesc(bd.description);
			row.setSuTBadgeResult("no result");
		}
		
		badge_hm.put(sutDesc.ID, pageData);
		return pageData;
	}
	
	private void collectBadgesForSut(final SutDescription sut) {
		CbService cbService = (CbService) BEANS.get(CbService.class);
		
		sut.badges.stream().map(cbService::getBadgeDescription).forEach(badgeDescription -> addBadgeToCollection (badgeDescription, m_collectedBadges));
		
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
