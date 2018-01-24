package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class CbDependenciesByStringLookupCall extends LookupCall<String> {

	private static final long serialVersionUID = 1L;
	
	private String m_cbId;
	
	public String getCbId() {
		return m_cbId;
	}
	
	public void setCbId (String cbId) {
		m_cbId = cbId;
	}

	@Override
	protected Class<? extends ILookupService<String>> getConfiguredService() {
		return ICbDependenciesByStringLookupService.class;
	}
}
