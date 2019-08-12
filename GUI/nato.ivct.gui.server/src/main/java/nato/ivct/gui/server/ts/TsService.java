package nato.ivct.gui.server.ts;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.scout.rt.platform.job.IFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.BadgeDescription.InteroperabilityRequirement;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable.CbRequirementsTableRowData;

public class TsService implements ITsService {

	private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

	HashMap<String,TestSuiteDescription> ts_hm = null;

	@Override
	public Set<String> loadTestSuites() {
		if (ts_hm == null)
			// load badge descriptions
			waitFortestSuiteLoading(); 

		return new TreeSet<>(ts_hm.keySet());
	}

	public TestSuiteDescription getTsDescription(String tsId) {
		if (ts_hm == null)
			waitFortestSuiteLoading();
		return ts_hm.get(tsId);
	}

	void waitFortestSuiteLoading () {
		// wait until load badges job is finished
		IFuture<CmdListTestSuites> future = ServerSession.get().getLoadTestSuitesJob();
		CmdListTestSuites tsCmd = (CmdListTestSuites) future.awaitDoneAndGet();
		ts_hm = tsCmd.testsuites;		
	}

//	@Override
//	public CbFormData load(CbFormData formData) {
//		LOG.info("load badge description");
//		if (!ACCESS.check(new ReadCbPermission())) {
//			throw new VetoException(TEXTS.get("AuthorizationFailed"));
//		}
//		BadgeDescription cb = cb_hm.get(formData.getCbId());
//		formData.getCbName().setValue(cb.name);
//		
//		formData.getCbDescription().setValue(cb.description);
//
//		// dependencies tree is built in CbDependenciesLookupService class
//		return formData;
//	}
	


	@Override
	public CbRequirementsTable loadRequirementsForTc(final Set<String> testcases) {
		CbRequirementsTable requirementTableRows = new CbRequirementsTable();
		for (String tc:testcases) {
			Set<String> irList = getIrForTc(tc);
			for (String irId:irList) {
				
				CbRequirementsTableRowData row = requirementTableRows.addRow();
				row.setRequirementId(requirement.ID);
				row.setRequirementDesc(requirement.description);
			}
		}

		return requirementTableRows;
	}
}
