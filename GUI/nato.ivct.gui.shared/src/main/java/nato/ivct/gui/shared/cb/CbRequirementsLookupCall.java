package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class CbRequirementsLookupCall extends LookupCall<String> {

	private static final long serialVersionUID = 1L;

	@Override
	protected Class<? extends ILookupService<String>> getConfiguredService() {
		return ICbRequirementsLookupService.class;
	}
}
