package nato.ivct.gui.server.sut;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListSuT.SutDescription;
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
	 * get SuTBadgeTablePageData for a specific SuT id. Create new one or select existing
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
		collectBadgesForSut(sutDesc);
		
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
	
	public Set<String> SutCapabilities(final String SutId) {
		final Set<String> capList = Collections.emptySet();
		
		SutDescription sutDesc = ((SuTService) BEANS.get(ISuTService.class)).getSutDescription(SutId);
		collectBadgesForSut(sutDesc);
		m_collectedBadges.forEach(bd->capList.add(bd.ID));
		
		return capList;
	}
	
	private void collectBadgesForSut(final SutDescription _sutDesc) {
		CbService cbService = (CbService) BEANS.get(CbService.class);
		for (int i = 0; i < _sutDesc.conformanceStatment.length; i++) {
			BadgeDescription badge = cbService.getBadgeDescription(_sutDesc.conformanceStatment[i]);
			addBadgeToCollection (badge, m_collectedBadges);
		}
	}
	
	private void addBadgeToCollection (final BadgeDescription parentBadge, final Set<BadgeDescription> badgeCollection) {
		CbService cbService = (CbService) BEANS.get(ICbService.class);
		if (parentBadge != null) {
			badgeCollection.add(parentBadge);

			for (String dep:parentBadge.dependency) {
				BadgeDescription depBadge = cbService.getBadgeDescription(dep);
				badgeCollection.add(depBadge);
				
				addBadgeToCollection (depBadge, badgeCollection);
			}
		}
	}
	
}
