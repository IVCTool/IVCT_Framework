package nato.ivct.gui.server.cb;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.gui.shared.cb.CbDependenciesByStringLookupCall;
import nato.ivct.gui.shared.cb.ICbDependenciesByStringLookupService;
import nato.ivct.gui.shared.cb.ICbService;

public class CbDependenciesByStringLookupService extends AbstractLookupService<String>
		implements ICbDependenciesByStringLookupService {

	@Override
	public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return CollectionUtility.emptyArrayList();
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return CollectionUtility.emptyArrayList();
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
		// TODO [the] Auto-generated method stub.
		return CollectionUtility.emptyArrayList();
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByAll(ILookupCall<String> call) {
		// get route element
		String cbId = ((CbDependenciesByStringLookupCall) call).getCbId();

		ArrayList<LookupRow<String>> depTreeList = CollectionUtility.emptyArrayList();
		
		addBadgeToTreeList (cbId, depTreeList);
		
		return depTreeList;
	}
	
	private void addBadgeToTreeList (final String parentBadgeId, final ArrayList<LookupRow<String>> treeList) {
		CbService cbService = (CbService) BEANS.get(ICbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(parentBadgeId);
		
		for (String dep:bd.dependency) {
			LookupRow<String> lookupRow = new LookupRow<String>(dep, cbService.getBadgeDescription(dep).name);
			if (parentBadgeId != null)
				lookupRow = lookupRow.withParentKey(parentBadgeId);
			treeList.add(lookupRow);
			
			addBadgeToTreeList (dep, treeList);
		}

	}
}
