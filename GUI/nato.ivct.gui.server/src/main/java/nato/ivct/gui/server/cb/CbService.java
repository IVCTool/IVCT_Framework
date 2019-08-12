package nato.ivct.gui.server.cb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable.CbRequirementsTableRowData;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;

public class CbService implements ICbService {

	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	HashMap<String, BadgeDescription> cb_hm = null;

	@Override
	public Set<String> loadBadges() {
		if (cb_hm == null)
			// load badge descriptions
			waitForBadgeLoading(); 

		return new TreeSet<>(cb_hm.keySet());
	}

	public BadgeDescription getBadgeDescription(String cb) {
		if (cb_hm == null)
			waitForBadgeLoading();
		return cb_hm.get(cb);
	}

	void waitForBadgeLoading () {
		// wait until load badges job is finished
		IFuture<CmdListBadges> future = ServerSession.get().getLoadBadgesJob();
		CmdListBadges badgeCmd = (CmdListBadges) future.awaitDoneAndGet();
		cb_hm = badgeCmd.badgeMap;		
	}

	@Override
	public CbFormData load(CbFormData formData) {
		LOG.info("load badge description");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		BadgeDescription cb = cb_hm.get(formData.getCbId());
		formData.getCbName().setValue(cb.name);
		
		formData.getCbDescription().setValue(cb.description);

		// dependencies tree is built in CbDependenciesLookupService class
		return formData;
	}
	
	public byte[] loadBadgeIcon(String badgeId) {
		BadgeDescription cb = cb_hm.get(badgeId);
		
		if (cb.cbVisual == null) {
			LOG.error("No icon file for badge ID " + cb.ID);
			return null;
		}
		
		try {
			return Files.readAllBytes((Paths.get(cb.cbVisual)));
		} catch (IOException exe) {
			LOG.error("Could not open icon file " + cb.cbVisual == null ? ": Icon File not available" : cb.cbVisual);
			return null;
		}
	}

	@Override
	public CbRequirementsTable loadRequirements(final Set<String> badges) {
		CbRequirementsTable requirementTableRows = new CbRequirementsTable();
		for (String badge:badges) {
			BadgeDescription bd = getBadgeDescription(badge);
			for (InteroperabilityRequirement requirement:bd.requirements) {
				CbRequirementsTableRowData row = requirementTableRows.addRow();
				row.setRequirementId(requirement.ID);
				row.setRequirementDesc(requirement.description);
			}
		}

		return requirementTableRows;
	}
}
