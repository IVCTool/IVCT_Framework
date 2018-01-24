package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.lookup.ILookupService;

@TunnelToServer
public interface ICbDependenciesByStringLookupService extends ILookupService<String> {
}
