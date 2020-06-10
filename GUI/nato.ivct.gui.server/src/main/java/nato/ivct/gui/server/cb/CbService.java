/* Copyright 2020, Reinhard Herzog, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.server.cb;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
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

    private static final Logger LOG = LoggerFactory.getLogger(CbService.class);

    CmdListBadges badgeCmd = null;


    @Override
    public Set<String> loadBadges() {
        if (badgeCmd == null)
            // load badge descriptions
            waitForBadgeIrLoading();

        return new TreeSet<>(badgeCmd.badgeMap.keySet());
    }


    public BadgeDescription getBadgeDescription(final String cbId) {
        if (badgeCmd == null)
            waitForBadgeIrLoading();
        return badgeCmd.badgeMap.get(cbId);
    }


    public InteroperabilityRequirement getIrDescription(final String irId) {
        if (badgeCmd == null)
            waitForBadgeIrLoading();
        return badgeCmd.getIR(irId);
    }


    void waitForBadgeIrLoading() {
        // wait until load badges job is finished
        final IFuture<CmdListBadges> future = ServerSession.get().getLoadBadgesJob();
        badgeCmd = future.awaitDoneAndGet();
    }


    @Override
    public CbFormData load(CbFormData formData) {
        LOG.info("load badge description");
        if (!ACCESS.check(new ReadCbPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }
        final BadgeDescription cb = badgeCmd.badgeMap.get(formData.getCbId());
        formData.getCbName().setValue(cb.name);
        formData.getCbVersion().setValue(cb.version);
        formData.getCbDescription().setValue(cb.description);

        // dependencies tree is built in CbDependenciesLookupService class
        return formData;
    }


    @Override
    public byte[] loadBadgeIcon(final String badgeId) {
        final BadgeDescription cb = badgeCmd.badgeMap.get(badgeId);

        if (cb.cbVisual == null) {
            LOG.error("No icon file for badge ID {}", cb.ID);
            return null;
        }

        try {
            return Files.readAllBytes(Paths.get(cb.cbVisual));
        }
        catch (final IOException exc) {
            LOG.error("Could not open icon file {}", cb.cbVisual == null ? ": Icon File not available" : cb.cbVisual);
            return null;
        }
    }


    @Override
    public CbRequirementsTable loadRequirementTable(final Set<String> badges) {
        final CbRequirementsTable cbRequirementTableRows = new CbRequirementsTable();
        for (final String badge: badges) {
            final BadgeDescription bd = getBadgeDescription(badge);
            bd.requirements.forEach((irId, requirement) -> {
                final CbRequirementsTableRowData row = cbRequirementTableRows.addRow();
                row.setRequirementId(requirement.ID);
                row.setRequirementDesc(requirement.description);
            });
        }

        return cbRequirementTableRows;
    }


    @Override
    public HashSet<String> getIrForCb(final String cbId) {
        return new HashSet<>(getBadgeDescription(cbId).requirements.keySet());
    }
}
