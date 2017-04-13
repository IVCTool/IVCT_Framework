package nato.ivct.gui.shared.security;

import java.security.PermissionCollection;

import org.eclipse.scout.rt.shared.ISession;
import org.eclipse.scout.rt.shared.services.common.security.AbstractAccessControlService;
import org.eclipse.scout.rt.shared.services.common.security.IAccessControlService;

/**
 * <h3>{@link UserIdAccessControlService}</h3>
 *
 * {@link IAccessControlService} service that uses {@link ISession#getUserId()}
 * as internal cache key required by {@link AbstractAccessControlService}
 * implementation.
 * <p>
 * Replace this service at server side to load permission collection. It is
 * <b>not</b> required to implement {@link #execLoadPermissions(String)} at
 * client side.
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
