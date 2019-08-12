package nato.ivct.gui.shared.cb;

import java.util.Set;

import org.eclipse.scout.rt.platform.service.IService;
import org.eclipse.scout.rt.shared.TunnelToServer;
import org.eclipse.scout.rt.shared.data.form.fields.tablefield.AbstractTableFieldBeanData;

import nato.ivct.gui.shared.cb.CbFormData.CbRequirementsTable;

@TunnelToServer
public interface ITsService extends IService {

//	TsFormData load(TsFormData formData);

//	CbRequirementsTable loadRequirements(Set<String> badges);

	Set<String> loadTestSuites();

	AbstractTableFieldBeanData loadRequirementsForTc(Set<String> testcases);
}
