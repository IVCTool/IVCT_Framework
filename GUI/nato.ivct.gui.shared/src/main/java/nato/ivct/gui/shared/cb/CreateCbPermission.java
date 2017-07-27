package nato.ivct.gui.shared.cb;

import java.security.BasicPermission;

public class CreateCbPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateCbPermission() {
		super(CreateCbPermission.class.getSimpleName());
	}
}
