package nato.ivct.gui.shared.cb;

import java.security.BasicPermission;

public class UpdateCbPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateCbPermission() {
		super(UpdateCbPermission.class.getSimpleName());
	}
}
