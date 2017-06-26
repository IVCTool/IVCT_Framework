package nato.ivct.gui.shared;

import java.security.BasicPermission;

public class CreateOptionsPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateOptionsPermission() {
		super(CreateOptionsPermission.class.getSimpleName());
	}
}
