package nato.ivct.gui.server.sut;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;
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
		SuTService sutService = (SuTService) BEANS.get(ISuTService.class);
		CbService cbService = (CbService) BEANS.get(CbService.class);
		SutDescription sutDesc = sutService.getSutDescription(searchText[0]);

		Set<BadgeDescription> badgesCollected = new TreeSet<>(Comparator.comparing(description -> description.name));
		for (int i = 0; i < sutDesc.conformanceStatment.length; i++) {
			BadgeDescription badge = cbService.getBadgeDescription(sutDesc.conformanceStatment[i]);
			addBadgeToCollection (badge, badgesCollected);
		}
		
		for (BadgeDescription bd:badgesCollected) {
			SuTBadgeTableRowData row = pageData.addRow();
			row.setBadgeId(bd.ID);
			row.setBadgeDesc(bd.description);
			row.setSuTBadgeResult("no result");
		}

		badge_hm.put(sutDesc.ID, pageData);
		return pageData;
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
