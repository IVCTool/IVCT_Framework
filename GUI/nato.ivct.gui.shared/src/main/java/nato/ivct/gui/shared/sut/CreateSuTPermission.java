package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class CreateSuTPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateSuTPermission() {
		super(CreateSuTPermission.class.getSimpleName());
	}
}
