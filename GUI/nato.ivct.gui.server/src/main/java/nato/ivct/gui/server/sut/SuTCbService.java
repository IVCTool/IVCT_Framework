package nato.ivct.gui.server.sut;

import java.util.HashMap;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbTablePageData;
import nato.ivct.gui.shared.sut.SuTCbTablePageData.SuTCbTableRowData;

public class SuTCbService implements ISuTCbService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private static HashMap<String, SuTCbTablePageData> cap_hm = new HashMap<String, SuTCbTablePageData>();

//	public static SuTCbTablePageData getCapabilityTablePageData (String sut) {
//		return cap_hm.get (sut);
//	}
	
	/*
	 * get CapapbilityTablePageData for a specific SuT id. Create new one or select existing
	 * 
	 * @see nato.ivct.gui.shared.sut.ICapabilityService#getCapabilityTableData(org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter)
	 */
	@Override
	public SuTCbTablePageData getSuTCbTableData(SearchFilter filter) {
		String[] searchText = filter.getDisplayTexts();
		SuTCbTablePageData pageData = cap_hm.get (searchText);
		if (pageData == null) {
			pageData = new SuTCbTablePageData();
		}

		LOG.info("getCapabilityTableData");
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription badge = cbService.getBadgeDescription(searchText[0]);
		for (int j = 0; j < badge.requirements.length; j++) {
			SuTCbTableRowData row = pageData.addRow();
			row.setRequirementId(badge.requirements[j].ID);
			row.setRequirementDesc(badge.requirements[j].description);
			row.setAbstractTC(badge.requirements[j].TC);
			row.setTCstatus("no result");
		}
		
		cap_hm.put(badge.ID, pageData);
		return pageData;
	}

	public void executeTestCase(String sutId, String tc, String badgeId) {
		// execute the CmdStartTc commands
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription b = cbService.getBadgeDescription(badgeId);
		ServerSession.get().execStartTc(sutId, tc, badgeId, b.tsRunTimeFolder);
		// mark test cases as being started
		SuTCbTablePageData capPage = cap_hm.get(badgeId);
		if (capPage == null) {
			LOG.error("no capability map found for badge: " + badgeId);
		} else {
			for (int i = 0; i < capPage.getRowCount(); i++) {
				SuTCbTableRowData row = capPage.rowAt(i);
				if (row.getAbstractTC().equals(tc)) {
					row.setTCstatus("starting");
				}
			}
		}
	}
}
