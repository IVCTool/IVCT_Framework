package nato.ivct.gui.shared.security;

import java.security.PermissionCollection;

import org.eclipse.scout.rt.shared.services.common.security.AbstractAccessControlService;

/**
 *
 * @author hzg
 */
public class AccessControlService extends AbstractAccessControlService<String> {

	@Override
	protected String getCurrentUserCacheKey() {
		return getUserIdOfCurrentUser();
	}

	@Override
	protected PermissionCollection execLoadPermissions(String userId) {
		return null;
	}
}
