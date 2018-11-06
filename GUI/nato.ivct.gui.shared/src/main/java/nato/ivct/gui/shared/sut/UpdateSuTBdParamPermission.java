package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class UpdateSuTBdParamPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public UpdateSuTBdParamPermission() {
		super(UpdateSuTBdParamPermission.class.getSimpleName());
	}
}
