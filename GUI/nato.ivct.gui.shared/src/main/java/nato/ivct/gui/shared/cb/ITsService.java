package nato.ivct.gui.shared.cb;

import java.util.HashMap;
import java.util.Set;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

import nato.ivct.gui.shared.ts.TsFormData;

@TunnelToServer
public interface ITsService extends IService {

	TsFormData load(TsFormData formData);

	Set<String> loadTestSuites();

	AbstractTableFieldBeanData loadRequirementsForTc(Set<String> testcases);

	Set<String> getTsForIr(Set<String> irSet);

	HashMap<String, Set<String>> getTcListForBadge(String cbId);
}
