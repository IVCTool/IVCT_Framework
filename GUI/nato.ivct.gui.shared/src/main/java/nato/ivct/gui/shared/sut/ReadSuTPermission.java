package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class ReadSuTPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadSuTPermission() {
		super(ReadSuTPermission.class.getSimpleName());
	}
}
