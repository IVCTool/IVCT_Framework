package nato.ivct.gui.server.ts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.commander.InteroperabilityRequirement;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.ts.TsFormData;
import nato.ivct.gui.shared.ts.TsFormData.TcTable;
import nato.ivct.gui.shared.ts.TsFormData.TcTable.TcTableRowData;
import nato.ivct.gui.shared.ts.TsFormData.TsRequirementsTable;
import nato.ivct.gui.shared.ts.TsFormData.TsRequirementsTable.TsRequirementsTableRowData;


public class TsService implements ITsService {

    private static final Logger LOG = LoggerFactory.getLogger(ServerSession.class);

    private CmdListTestSuites tsCmd = null;


    @Override
    public Set<String> loadTestSuites() {
        if (tsCmd == null)
            // load badge descriptions
            waitForTestSuiteLoading();

        return new TreeSet<>(tsCmd.testsuites.keySet());
    }


    public TestSuiteDescription getTsDescription(String tsId) {
        if (tsCmd == null)
            waitForTestSuiteLoading();
        return tsCmd.testsuites.get(tsId);
    }


    void waitForTestSuiteLoading() {
        // wait until load badges job is finished
        final IFuture<CmdListTestSuites> future = ServerSession.get().getLoadTestSuitesJob();
        tsCmd = future.awaitDoneAndGet();
    }


    @Override
    public TsFormData load(TsFormData formData) {
        LOG.info("load testsuite description");

        final TestSuiteDescription ts = tsCmd.testsuites.get(formData.getTsId());
        formData.getTsName().setValue(ts.name);
        formData.getTsVersion().setValue(ts.version);
        formData.getTsDescription().setValue(ts.description);

        // load test case list of this testsuite
        final TcTable tcTable = formData.getTcTable();
        ts.testcases.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEachOrdered(tc -> {
            final TcTableRowData row = tcTable.addRow();
            row.setTcId(tc.getValue().tc);
            row.setTcName(Stream.of(tc.getValue().tc.split(Pattern.quote("."))).reduce((a, b) -> b).get());
            row.setTcDesc(tc.getValue().description);
        });

        return formData;
    }


    @Override
    public TsRequirementsTable loadRequirementsForTc(final Set<String> testcases) {
        final TsRequirementsTable tsRequirementTableRows = new TsRequirementsTable();
        final Set<String> irIdList = new HashSet<>();

        testcases.forEach(tc -> irIdList.addAll(tsCmd.getIrForTc(tc)));

        final CbService cbService = BEANS.get(CbService.class);

        // put the requirements into the table sorted by its ID
        irIdList.stream().sorted().forEachOrdered(irId -> {
            final InteroperabilityRequirement ir = cbService.getIrDescription(irId);
            final TsRequirementsTableRowData row = tsRequirementTableRows.addRow();
            row.setRequirementId(ir.ID);
            row.setRequirementDesc(ir.description);
        });

        return tsRequirementTableRows;
    }


    @Override
    public HashSet<String> getTsForIr(Set<String> irSet) {
        return new HashSet<>(tsCmd.getTsForIr(irSet));
    }


    @Override
    public HashMap<String, HashSet<String>> getTcListForBadge(String cbId) {
        final HashMap<String, HashSet<String>> tcList = new HashMap<>();

        final Set<String> irList = BEANS.get(CbService.class).getIrForCb(cbId);
        final Map<String, TestSuiteDescription> tsDescMap = tsCmd.filterForIr(irList);
        tsDescMap.forEach((tsId, tsDesc) -> tcList.put(tsId, new HashSet<>(tsDesc.testcases.keySet())));

        return tcList;
    }


    @Override
    public HashMap<String, String> getIrForTc(String tcId) {
        final HashMap<String, String> irList = new HashMap<String, String>();
        final Set<String> irs = tsCmd.getIrForTc(tcId);

        irs.stream().sorted().forEach(irId -> irList.put(irId, BEANS.get(CbService.class).getIrDescription(irId).description));
        return irList;
    }
}
