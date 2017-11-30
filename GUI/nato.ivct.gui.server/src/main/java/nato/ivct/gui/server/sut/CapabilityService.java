package nato.ivct.gui.server.sut;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.sut.CapabilityTablePageData;
import nato.ivct.gui.shared.sut.CapabilityTablePageData.CapabilityTableRowData;
import nato.ivct.gui.shared.sut.ICapabilityService;
import nato.ivct.gui.shared.sut.ISuTService;

public class CapabilityService implements ICapabilityService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private static HashMap<String, CapabilityTablePageData> cap_hm = new HashMap<String, CapabilityTablePageData>();

	public static CapabilityTablePageData getCapabilityTablePageData (String sut) {
		return cap_hm.get (sut);
	}
	
	/*
	 * get CapapbilityTablePageData for a specific SuT id. Create new one or select existing
	 * 
	 * @see nato.ivct.gui.shared.sut.ICapabilityService#getCapabilityTableData(org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter)
	 */
	@Override
	public CapabilityTablePageData getCapabilityTableData(SearchFilter filter) {
		String[] searchText = filter.getDisplayTexts();
		CapabilityTablePageData pageData = cap_hm.get (searchText);
		if (pageData == null) {
			pageData = new CapabilityTablePageData();
		}
		LOG.info("getCapabilityTableData");
		SuTService sutService = (SuTService) BEANS.get(ISuTService.class);
		CbService cbService = (CbService) BEANS.get(CbService.class);
		SutDescription sutDesc = sutService.getSutDescription(searchText[0]);

		for (int i = 0; i < sutDesc.conformanceStatment.length; i++) {
			BadgeDescription badge = cbService.getBadgeDescription(sutDesc.conformanceStatment[i]);
			if (badge != null) {
				collectInteroperabilityRequirements(pageData, badge);
			} else {
				LOG.warn("badge not found: " + sutDesc.conformanceStatment[i]);			
			}
		}
		cap_hm.put(sutDesc.ID, pageData);
		return pageData;
	}

	private void collectInteroperabilityRequirements(CapabilityTablePageData pageData, BadgeDescription badge,
			Set<BadgeDescription> badgesCollected) {
		if (badge == null) {
			LOG.warn("invalid badge received");
			return;
		}
		else if (badgesCollected.contains(badge)) {
			LOG.warn("recursive badge dependency ignored: " + badge.name);
			return;
		}
		else {
			for (int j = 0; j < badge.requirements.length; j++) {
				CapabilityTableRowData row = pageData.addRow();
				row.setBadgeId(badge.ID);
				row.setRequirementId(badge.requirements[j].ID);
				row.setRequirementDesc(badge.requirements[j].description);
				row.setAbstractTC(badge.requirements[j].TC);
				row.setTCresult("no result");
			}
			for (int k = 0; k < badge.dependency.length; k++) {
				CbService cbService = (CbService) BEANS.get(CbService.class);
				BadgeDescription dependentBadge = cbService.getBadgeDescription(badge.dependency[k]);
				if (dependentBadge != null) {
					badgesCollected.add(badge);
					collectInteroperabilityRequirements(pageData, dependentBadge, badgesCollected);
				}
			}
		}
	}

	private void collectInteroperabilityRequirements(CapabilityTablePageData pageData, BadgeDescription badge) {
		collectInteroperabilityRequirements (pageData, badge, new HashSet<BadgeDescription>());

	}

	public void executeTestCase(String sut, String tc, String badge) {
		// execute the CmdStartTc commands
		CbService cbService = (CbService) BEANS.get(CbService.class);
		BadgeDescription b = cbService.getBadgeDescription(badge);
		ServerSession.get().execStartTc(sut, tc, badge, b.tsRunTimeFolder);
		// mark test cases as being started
		CapabilityTablePageData capPage = cap_hm.get(sut);
		if (capPage == null) {
			LOG.error("no capability map found for SuT: " + sut);
		} else {
			for (int i = 0; i < capPage.getRowCount(); i++) {
				CapabilityTableRowData row = capPage.rowAt(i);
				if (row.getAbstractTC().equals(tc)) {
					row.setTCresult("starting");
				}
			}
		}

	}
}
