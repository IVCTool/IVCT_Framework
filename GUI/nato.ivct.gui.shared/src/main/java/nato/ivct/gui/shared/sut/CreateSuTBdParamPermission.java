package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class CreateSuTBdParamPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public CreateSuTBdParamPermission() {
		super(CreateSuTBdParamPermission.class.getSimpleName());
	}
}
