package nato.ivct.gui.server.cb;

import java.util.HashMap;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.Command;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.CbFormData;
import nato.ivct.gui.shared.cb.CbTablePageData;
import nato.ivct.gui.shared.cb.CbTablePageData.CbTableRowData;
import nato.ivct.gui.shared.cb.CreateCbPermission;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ReadCbPermission;
import nato.ivct.gui.shared.cb.UpdateCbPermission;

public class CbService implements ICbService {

	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	HashMap<String, CbTableRowData> cb_hm = new HashMap<String, CbTableRowData>();

	@Override
	public CbTablePageData getCbTableData(SearchFilter filter) {
		LOG.info ("getCbTableData");
		CbTablePageData pageData = new CbTablePageData();
		// wait until load badges job is finished 
		IFuture<CmdListBadges> future = ServerSession.get().getLoadBadgesJob();
		Command result = future.awaitDoneAndGet();
		// copy results into table rows
		CmdListBadges badgeCmd = (CmdListBadges) result;
		CbTableRowData row;
		for (BadgeDescription value : badgeCmd.badgeMap.values()) {
           row = pageData.addRow();
           row.setCapabilityName(value.capabilityName);
           row.setCapabilityDescription(value.description);
           row.setCbVisual(value.cbVisual);
           cb_hm.put(row.getCapabilityName(), row);
		}
		return pageData;
	}

	@Override
	public CbTablePageData getCbTableData(SearchFilter filter, String sutId) {
		// TODO Auto-generated method stub

		LOG.info ("getCbTableData with SuT restriction");
		CbTablePageData pageData = new CbTablePageData();
		return pageData;
	}

	@Override
	public CbFormData prepareCreate(CbFormData formData) {
		LOG.info ("prepareCreate");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public CbFormData create(CbFormData formData) {
		LOG.info ("create");
		if (!ACCESS.check(new CreateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public CbFormData load(CbFormData formData) {
		LOG.info ("load");
		if (!ACCESS.check(new ReadCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public CbFormData store(CbFormData formData) {
		LOG.info ("store");
		if (!ACCESS.check(new UpdateCbPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}
}
