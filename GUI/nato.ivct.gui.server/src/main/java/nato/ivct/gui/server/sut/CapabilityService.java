package nato.ivct.gui.server.sut;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.sut.CapabilityTablePageData;
import nato.ivct.gui.shared.sut.CapabilityTablePageData.CapabilityTableRowData;
import nato.ivct.gui.shared.sut.ICapabilityService;
import nato.ivct.gui.shared.sut.ISuTService;

public class CapabilityService implements ICapabilityService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	@Override
	public CapabilityTablePageData getCapabilityTableData(SearchFilter filter) {
		CapabilityTablePageData pageData = new CapabilityTablePageData();
		// TODO [hzg] fill pageData.
		LOG.info("getCapabilityTableData");
		String[] searchText = filter.getDisplayTexts();
		SuTService sutService = (SuTService) BEANS.get(ISuTService.class);
		CbService cbService = (CbService) BEANS.get(CbService.class);
		SutDescription sutDesc = sutService.getSutDescription(searchText[0]);

		for (int i = 0; i < sutDesc.conformanceStatment.length; i++) {
			BadgeDescription badge = cbService.getBadgeDescription(sutDesc.conformanceStatment[i]);
			if (badge != null) {
				for (int j = 0; j < badge.requirements.length; j++) {
					CapabilityTableRowData row = pageData.addRow();
					row.setBadgeId(sutDesc.conformanceStatment[i]);
					row.setRequirementId(badge.requirements[j].ID);
					row.setRequirementDesc(badge.requirements[j].description);
					row.setAbstractTC(badge.requirements[j].TC);
					row.setTCresult("no result");
				}
			}
		}

		return pageData;
	}
}
