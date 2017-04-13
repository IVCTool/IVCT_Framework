package nato.ivct.gui.shared.cb;

import java.security.BasicPermission;

public class ReadCbPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadCbPermission() {
		super(ReadCbPermission.class.getSimpleName());
	}
}
