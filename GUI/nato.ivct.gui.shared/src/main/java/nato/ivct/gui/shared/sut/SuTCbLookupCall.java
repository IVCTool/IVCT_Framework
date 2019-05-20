package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.shared.services.lookup.ILookupService;
import org.eclipse.scout.rt.shared.services.lookup.LookupCall;


public class SuTCbLookupCall extends LookupCall<String> {

    private static final long serialVersionUID = 1L;


    @Override
    protected Class<? extends ILookupService<String>> getConfiguredService() {
        return ISuTCbLookupService.class;
    }
}
