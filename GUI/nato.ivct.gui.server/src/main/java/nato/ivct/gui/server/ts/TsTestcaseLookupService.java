package nato.ivct.gui.server.ts;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.CbDependenciesLookupCall;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.ts.ITsTestcaseLookupService;
import nato.ivct.gui.shared.ts.TsTestcaseLookupCall;

public class TsTestcaseLookupService extends AbstractLookupService<String> implements ITsTestcaseLookupService {

	@Override
	public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return null;
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return null;
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return null;
	}


//	@Override
//	public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
//		return CollectionUtility.emptyArrayList();
//	}
//
//	@Override
//	public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
//		return CollectionUtility.emptyArrayList();
//	}
//
//	@Override
//	public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
//		return CollectionUtility.emptyArrayList();
//	}

	@Override
	public List<? extends ILookupRow<String>> getDataByAll(ILookupCall<String> call) {
		// get TS
		String tsId = ((TsTestcaseLookupCall) call).getTsId();

		ArrayList<LookupRow<String>> tcList = CollectionUtility.emptyArrayList();
		
		TsService tsService = (TsService) BEANS.get(ITsService.class);
		TestSuiteDescription ts = tsService.getTsDescription(tsId);
		
		
		
		for (String tc:ts.testcases) {
			LookupRow<String> lookupRow = new LookupRow<String>(dep, cbService.getBadgeDescription(dep).name);
			if (parentBadgeId != null)
				lookupRow = (LookupRow<String>) lookupRow.withParentKey(parentBadgeId);
			treeList.add(lookupRow);
			
			addBadgeToTreeList (dep, treeList);
		}
		
		return tcList;
	}
	
	private void addBadgeToTreeList (final String parentBadgeId, final ArrayList<LookupRow<String>> treeList) {
		CbService cbService = (CbService) BEANS.get(ICbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(parentBadgeId);
		
		for (String dep:bd.dependency) {
			LookupRow<String> lookupRow = new LookupRow<String>(dep, cbService.getBadgeDescription(dep).name);
			if (parentBadgeId != null)
				lookupRow = (LookupRow<String>) lookupRow.withParentKey(parentBadgeId);
			treeList.add(lookupRow);
			
			addBadgeToTreeList (dep, treeList);
		}
	}
}
