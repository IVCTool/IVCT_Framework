package nato.ivct.gui.shared.cb;

import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCode;
import org.eclipse.scout.rt.shared.services.common.code.AbstractCodeType;

public class CapabilityCodeType extends AbstractCodeType<Integer, String> {

	private static final long serialVersionUID = 1L;
	public static final int ID = 0;

	@Override
	public Integer getId() {
		return ID;
	}

	@Order(1000)
	public static class CapCode extends AbstractCode<Integer> {
		private static final long serialVersionUID = 1L;
		public static final Integer ID = 0;

		@Override
		protected String getConfiguredText() {
			return TEXTS.get("MyNlsKey");
		}

		@Override
		public Integer getId() {
			return ID;
		}
	}
	
	
}
