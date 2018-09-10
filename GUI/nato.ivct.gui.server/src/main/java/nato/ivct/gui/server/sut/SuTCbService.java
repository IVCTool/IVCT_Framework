package nato.ivct.gui.server.sut;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.commander.Factory;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.CreateCbPermission;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.cb.UpdateCbPermission;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbFormData;
import nato.ivct.gui.shared.sut.SuTCbFormData.CbRequirementsTable.CbRequirementsTableRowData;
import nato.ivct.gui.shared.sut.SuTCbTablePageData;
import nato.ivct.gui.shared.sut.SuTCbTablePageData.SuTCbTableRowData;

public class SuTCbService implements ISuTCbService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);
	private static HashMap<String, SuTCbTablePageData> cap_hm = new HashMap<String, SuTCbTablePageData>();

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
		CbService cbService = BEANS.get(CbService.class);
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
		CbService cbService = BEANS.get(CbService.class);
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

	@Override
	public SuTCbFormData load(SuTCbFormData formData) {
		LOG.info("load");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}

		CbService cbService = BEANS.get(CbService.class);
		BadgeDescription badgeDescription = cbService.getBadgeDescription(formData.getCbId());
		formData.getCbName().setValue(badgeDescription.name);
		formData.getCbDescription().setValue(badgeDescription.description);
		
		// fill requirement table of this form
		importRequirements(formData, badgeDescription);

		return formData;
	}
	
	@Override
	public String loadBadgeParams(String sutId, String badgeId) {
		Path paramFile = null;
		try {
			paramFile = getParamFile(sutId, badgeId);
			if (Files.notExists(paramFile)) {
				LOG.info("badge parameter file " + paramFile.toString() + " does not exist");
				return null;
			}
			LOG.debug("load badge parameters from file " + paramFile.toString());
			return new String(Files.readAllBytes(paramFile));
		} catch (InvalidPathException e) {
			LOG.error("invalid path for badge parameter file " + paramFile.toString());
			return null;
		} catch (Exception e) {
			LOG.error("could not read badge parameters from file " + paramFile.toString());
			e.printStackTrace();
			return null;
		}
	}
	
	private Path getParamFile (String sutId, String badgeId) throws InvalidPathException {
		return Paths.get(Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID), sutId, badgeId, "TcParam.json");
	}

	private SuTCbFormData importRequirements(final SuTCbFormData fd, final BadgeDescription bd) {
		for (InteroperabilityRequirement requirement:bd.requirements) {
			CbRequirementsTableRowData row = fd.getCbRequirementsTable().addRow();
			row.setRequirementId(requirement.ID);
			row.setRequirementDesc(requirement.description);
			row.setAbstractTC(requirement.TC);
		}
		
		return fd;
	}

	@Override
	public SuTCbFormData store(SuTCbFormData formData) {
		LOG.info("store");
		if (!ACCESS.check(new UpdateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		return formData;
	}

	@Override
	public SuTCbFormData prepareCreate(SuTCbFormData formData) {
		LOG.info("prepareCreate");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		return formData;
	}

	@Override
	public SuTCbFormData create(SuTCbFormData formData) {
		LOG.info("create");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		return formData;
	}
}
