package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class UpdateSuTPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateSuTPermission() {
		super(UpdateSuTPermission.class.getSimpleName());
	}
}
