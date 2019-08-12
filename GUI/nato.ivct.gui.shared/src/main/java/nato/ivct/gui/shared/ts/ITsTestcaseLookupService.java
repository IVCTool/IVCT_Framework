package nato.ivct.gui.shared.ts;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.lookup.ILookupService;

@TunnelToServer
public interface ITsTestcaseLookupService extends ILookupService<String> {
}
