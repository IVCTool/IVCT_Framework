package nato.ivct.gui.server.cb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import com.google.api.client.repackaged.com.google.common.base.Strings;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.gui.shared.cb.ICbRequirementsLookupService;
import nato.ivct.gui.shared.cb.ICbService;

public class CbRequirementsLookupService extends AbstractLookupService<String> implements ICbRequirementsLookupService {

	@Override
	public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
		String BadgeId = call.getKey();
		CbService cbService = (CbService) BEANS.get(ICbService.class);
		BadgeDescription bd = cbService.getBadgeDescription(BadgeId);
		
		return Arrays.stream(bd.requirements).map(requirement -> new LookupRow<String>(requirement.ID, Strings.nullToEmpty(requirement.description) + ";;" + Strings.nullToEmpty(requirement.TC))).collect(Collectors.toCollection(ArrayList::new));
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
		return CollectionUtility.emptyArrayList();
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
		return CollectionUtility.emptyArrayList();
	}

	@Override
	public List<? extends ILookupRow<String>> getDataByAll(ILookupCall<String> call) {
		return CollectionUtility.emptyArrayList();
	}
}
