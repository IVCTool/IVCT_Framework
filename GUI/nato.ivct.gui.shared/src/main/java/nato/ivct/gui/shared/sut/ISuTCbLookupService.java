package nato.ivct.gui.shared.sut;

import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.services.lookup.ILookupService;


@TunnelToServer
public interface ISuTCbLookupService extends ILookupService<String> {}
