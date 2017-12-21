package nato.ivct.gui.server.security;

import java.security.AllPermission;
import java.security.Permissions;

import org.eclipse.scout.rt.platform.Replace;
import org.eclipse.scout.rt.shared.security.RemoteServiceAccessPermission;

import nato.ivct.gui.shared.security.AccessControlService;

/**
 * <h3>{@link AccessControlService}</h3>
 *
 * @author hzg
 */
@Replace
public class ServerAccessControlService extends AccessControlService {

	@Override
	protected Permissions execLoadPermissions(String userId) {
		Permissions permissions = new Permissions();
		permissions.add(new RemoteServiceAccessPermission("*.shared.*", "*"));

		// TODO [hzg]: Fill access control service
		permissions.add(new AllPermission());
		return permissions;
	}
}
