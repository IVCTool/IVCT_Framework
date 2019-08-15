package nato.ivct.gui.server.cb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.InteroperabilityRequirement;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable.CbRequirementsTableRowData;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;

public class CbService implements ICbService {

	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	CmdListBadges badgeCmd = null;

	@Override
	public Set<String> loadBadges() {
		if (badgeCmd == null)
			// load badge descriptions
			waitForBadgeIrLoading(); 

		return new TreeSet<>(badgeCmd.badgeMap.keySet());
	}

	public BadgeDescription getBadgeDescription(String cb) {
		if (badgeCmd == null)
			waitForBadgeIrLoading();
		return badgeCmd.badgeMap.get(cb);
	}
	
	public InteroperabilityRequirement getIrDescription(String ir) {
		if (badgeCmd == null)
			waitForBadgeIrLoading();
		return badgeCmd.irMap.get(ir);
	}

	void waitForBadgeIrLoading () {
		// wait until load badges job is finished
		IFuture<CmdListBadges> future = ServerSession.get().getLoadBadgesJob();
		badgeCmd = future.awaitDoneAndGet();
	}

	@Override
	public CbFormData load(CbFormData formData) {
		LOG.info("load badge description");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		BadgeDescription cb = badgeCmd.badgeMap.get(formData.getCbId());
		formData.getCbName().setValue(cb.name);
		
		formData.getCbDescription().setValue(cb.description);

		// dependencies tree is built in CbDependenciesLookupService class
		return formData;
	}
	
	public byte[] loadBadgeIcon(String badgeId) {
		BadgeDescription cb = badgeCmd.badgeMap.get(badgeId);
		
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
		CbRequirementsTable cbRequirementTableRows = new CbRequirementsTable();
		for (String badge:badges) {
			BadgeDescription bd = getBadgeDescription(badge);
			bd.requirements.forEach((irId, requirement)-> {
				CbRequirementsTableRowData row = cbRequirementTableRows.addRow();
				row.setRequirementId(requirement.ID);
				row.setRequirementDesc(requirement.description);
			});
		}

		return cbRequirementTableRows;
	}
}
