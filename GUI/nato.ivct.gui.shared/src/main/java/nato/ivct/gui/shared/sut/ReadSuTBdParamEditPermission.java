package nato.ivct.gui.shared.sut;

import java.security.BasicPermission;

public class ReadSuTBdParamEditPermission extends BasicPermission {

	private static final long serialVersionUID = 1L;

	public ReadSuTBdParamEditPermission() {
		super(ReadSuTBdParamEditPermission.class.getSimpleName());
	}
}
