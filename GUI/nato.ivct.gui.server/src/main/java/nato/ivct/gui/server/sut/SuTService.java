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
import nato.ivct.commander.CmdListSuT.SutDescription;
import nato.ivct.commander.Command;
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
	private HashMap<String, SutDescription> sutMap = null;

	HashMap<String, SutDescription> sut_hm = new HashMap<String, SutDescription>();
	
	public SutDescription getSutDescription(String sutId) {
		return sutMap.get(sutId);
	}

	@Override
	public SuTTablePageData getSuTTableData(SearchFilter filter) {
		LOG.info ("getSuTTableData");
		SuTTablePageData pageData = new SuTTablePageData();
		// wait until collect SuT Descriptions Job has finished
		IFuture<CmdListSuT> future1 = ServerSession.get().getCmdJobs();
		ServerSession.get().getLoadBadgesJob().awaitDone();
		Command resultSuT = future1.awaitDoneAndGet();
		// copy sut descriptions into table rows
		CmdListSuT sutCmd = (CmdListSuT) resultSuT;
		sutMap = sutCmd.sutMap;
		for (SutDescription value : sutMap.values()) {
			SuTTableRowData row;
			row = pageData.addRow();
			row.setSuTid(value.ID);
			row.setSuTDescription(value.description);
			row.setVendor(value.vendor);
			String cs = "";
			for (int i=0; i<value.conformanceStatment.length; i++){
				cs = cs + value.conformanceStatment[i].toString();
				if (i < value.conformanceStatment.length - 1) {
					cs = cs + ", ";
				}
			}
			row.setBadge(cs);
			sut_hm.put(row.getSuTid(), value);

		}
		return pageData;
	}

	@Override
	public SuTFormData prepareCreate(SuTFormData formData) {
		LOG.info ("prepareCreate");
		if (!ACCESS.check(new CreateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public SuTFormData create(SuTFormData formData) {
		LOG.info ("create");
		if (!ACCESS.check(new CreateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}

	@Override
	public SuTFormData load(SuTFormData formData) {
		LOG.info ("load");
		if (!ACCESS.check(new ReadSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// find the SuT description by selected SuTid.
		SutDescription sut = sutMap.get(formData.getSutId());
		// fill the form data
		formData.setSutId(sut.ID);
		formData.getName().setValue(sut.ID);
		formData.getSutVendor().setValue(sut.vendor);
		formData.getDescr().setValue(sut.description);
		//formData.getCapabilities().setValue (String.join(",", Arrays.asList(sut.conformanceStatment))); this causes API check failure !?!
		String caps = "";
		for (String s : sut.conformanceStatment) {
			if (caps.equals("")) {
				caps = s;
			}
			else {
				caps = caps + ", " + s;				
			}
		}
		formData.getCapabilities().setValue(caps);
		return formData;
	}

	@Override
	public SuTFormData store(SuTFormData formData) {
		LOG.info ("store");
		if (!ACCESS.check(new UpdateSuTPermission())) {
			throw new VetoException(TEXTS.get("AuthorizationFailed"));
		}
		// TODO [hzg] add business logic here.
		return formData;
	}
}
