package nato.ivct.gui.shared;

import java.security.BasicPermission;

public class UpdateOptionsPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateOptionsPermission() {
		super(UpdateOptionsPermission.class.getSimpleName());
	}
}
