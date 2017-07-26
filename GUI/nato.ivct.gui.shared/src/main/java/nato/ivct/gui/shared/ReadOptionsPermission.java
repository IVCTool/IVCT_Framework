package nato.ivct.gui.shared;

import java.security.BasicPermission;

public class ReadOptionsPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadOptionsPermission() {
		super(ReadOptionsPermission.class.getSimpleName());
	}
}
