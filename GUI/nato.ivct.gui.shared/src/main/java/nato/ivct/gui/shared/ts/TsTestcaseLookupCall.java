package nato.ivct.gui.shared.ts;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;

public class TsTestcaseLookupCall extends LookupCall<String> {

	private static final long serialVersionUID = 1L;
	
	private String m_tsId;
	
	public String getTsId() {
		return m_tsId;
	}
	
	public void setTsId (String tsId) {
		m_tsId = tsId;
	}

	@Override
	protected Class<? extends ILookupService<String>> getConfiguredService() {
		return ITsTestcaseLookupService.class;
	}
}
