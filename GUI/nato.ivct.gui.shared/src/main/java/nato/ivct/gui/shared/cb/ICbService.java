package nato.ivct.gui.shared.cb;

import java.util.Set;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;

@TunnelToServer
public interface ICbService extends IService {

	CbFormData load(CbFormData formData);

	CbRequirementsTable loadRequirements(Set<String> badges);

	Set<String> loadBadges();

	byte[] loadBadgeIcon(String cbId);
}
