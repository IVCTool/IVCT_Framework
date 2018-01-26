package nato.ivct.gui.server.cb;

import java.util.List;

import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.testing.platform.runner.RunWithSubject;
import org.eclipse.scout.rt.testing.server.runner.RunWithServerSession;
import org.eclipse.scout.rt.testing.server.runner.ServerTestRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.shared.cb.CbRequirementsLookupCall;

@RunWithSubject("anonymous")
@RunWith(ServerTestRunner.class)
@RunWithServerSession(ServerSession.class)
public class CbRequirementsLookupCallTest {

	protected CbRequirementsLookupCall createLookupCall() {
		return new CbRequirementsLookupCall();
	}

	@Test
	public void testLookupByAll() {
		CbRequirementsLookupCall call = createLookupCall();
		// TODO [the] fill call
		List<? extends ILookupRow<String>> data = call.getDataByAll();
		// TODO [the] verify data
	}

	@Test
	public void testLookupByKey() {
		CbRequirementsLookupCall call = createLookupCall();
		// TODO [the] fill call
		List<? extends ILookupRow<String>> data = call.getDataByKey();
		// TODO [the] verify data
	}

	@Test
	public void testLookupByText() {
		CbRequirementsLookupCall call = createLookupCall();
		// TODO [the] fill call
		List<? extends ILookupRow<String>> data = call.getDataByText();
		// TODO [the] verify data
	}

	// TODO [the] add test cases
}
