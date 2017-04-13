package nato.ivct.gui.server.sut;

import java.util.HashMap;

import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.Command;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.ReadSuTPermission;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.SuTTablePageData;
import nato.ivct.gui.shared.sut.SuTTablePageData.SuTTableRowData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;

public class SuTService implements ISuTService {
	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	HashMap<String, SuTTableRowData> sut_hm = new HashMap<String, SuTTableRowData>();

	@Override
	public SuTTablePageData getSuTTableData(SearchFilter filter) {
		LOG.info ("getSuTTableData");
		SuTTablePageData pageData = new SuTTablePageData();
		// wait until collect SuT Descriptions Job has finished
		IFuture<CmdListSuT> future = ServerSession.get().getCmdJobs();
		Command result = future.awaitDoneAndGet();
		// copy sut descriptions into table rows
		CmdListSuT sutCmd = (CmdListSuT) result;
		for (SutDescription value : sutCmd.sutMap.values()) {
			SuTTableRowData row;
			row = pageData.addRow();
			row.setSuTid(value.id);
			row.setSuTDescription(value.description);
			sut_hm.put(row.getSuTid(), row);

		}
		return pageData;
	}

	@Override
	public SuTFormData prepareCreate(SuTFormData formData) {
		if (!ACCESS.check(new CreateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public SuTFormData create(SuTFormData formData) {
		if (!ACCESS.check(new CreateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public SuTFormData load(SuTFormData formData) {
		if (!ACCESS.check(new ReadSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public SuTFormData store(SuTFormData formData) {
		if (!ACCESS.check(new UpdateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}
}
