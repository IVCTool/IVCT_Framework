package nato.ivct.gui.server.sut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdUpdateSUT;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.ReadSuTPermission;
import nato.ivct.gui.shared.sut.SuTEditFormData;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.SuTFormData.SutCapabilityStatusTable.SutCapabilityStatusTableRowData;
import nato.ivct.gui.shared.sut.UpdateSuTPermission;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.data.JsonQLDataSource;


public class SuTService implements ISuTService {

    private static final Logger             LOG    = LoggerFactory.getLogger(ServerSession.class);
    private HashMap<String, SutDescription> sutMap = null;

    @Override
    public Set<String> loadSuts() {
        if (sutMap == null) {
            // load SuT descriptions
            waitForSutLoading();
        }

        final Set<String> sortedSutSet = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        sortedSutSet.addAll(sutMap.keySet());
        return sortedSutSet;
    }


    public SutDescription getSutDescription(String sutId) {
        if (sutMap == null) {
            // load SuT descriptions
            waitForSutLoading();
        }
        return sutMap.get(sutId);
    }


    private void waitForSutLoading() {
        final IFuture<CmdListSuT> future1 = ServerSession.get().getLoadSuTJob();
        final CmdListSuT sutCmd = future1.awaitDoneAndGet();
        // copy sut descriptions into table rows
        sutMap = sutCmd.sutMap;
    }

    /*
     * functions for SuTFormData
     */


    @Override
    public SuTFormData load(SuTFormData formData) {
        LOG.info("load {}", getClass());
        if (!ACCESS.check(new ReadSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        // find the SuT description by selected SuTid.
        final SutDescription sut = sutMap.get(formData.getSutId());

        if (sut != null) {
            formData.setSutId(sut.ID);

            // fill the form data: GeneralBox
            formData.getName().setValue(sut.name == null ? sut.ID : sut.name);
            formData.getVersion().setValue(sut.version);
            formData.getSutVendor().setValue(sut.vendor);
            formData.getDescr().setValue(sut.description);
            formData.getRtiSettingDesignator().setValue(sut.settingsDesignator);
            formData.getFederationName().setValue(sut.federation);
            formData.getFederateName().setValue(sut.sutFederateName);

        }

        // fill the form data: SuTCapabilities table with conformance status
        loadCapabilityStatus(formData);

        // fill the form data: SuTReports table
        loadResultFiles(formData);

        return formData;
    }


    /*
     * functions for TestReport
     */
    @Override
    public String createTestreport(String sutId) {

        try {
            String pathToReports = "C:\\Entwicklung\\IVCT\\IVCT_Runtime\\IVCTsut\\hw_iosb\\Reports\\PDFReport\\";
            JRDataSource jrDataSource = new JsonQLDataSource(new File(pathToReports + "PDFReport.json"));
            JasperReport jasperReport = JasperCompileManager.compileReport(pathToReports + "Report.jrxml");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_SuT.jrxml", "subreport_SuT.jasper");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_Verdict.jrxml", "subreport_Verdict.jasper");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_TcResults.jrxml", "subreport_TcResults.jasper");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_TestCases.jrxml", "subreport_TestCases.jasper");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_TS.jrxml", "subreport_TS.jasper");
            JasperCompileManager.compileReportToFile(pathToReports + "subreport_Badge.jrxml", "subreport_Badge.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), jrDataSource);

            Date time = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyyMMdd-HHmmss");
            String fileName = "Report" + simpleDateFormat.format(time) + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, "C:\\Entwicklung\\IVCT\\IVCT_Runtime\\IVCTsut\\hw_iosb\\Reports\\" + fileName);
            return fileName;
        }
        catch (JRException e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public BinaryResource getTestReportFileContent(String sutId, String fileName) {
        BinaryResource fileContent = null;

        try {
            fileContent = new BinaryResource(fileName, Files.readAllBytes(Paths.get(Factory.getSutPathsFiles().getReportPath(sutId)).resolve(fileName)));
        }
        catch (IOException | InvalidPathException exc) {
            LOG.error("error to access fileName {}", fileName);
            fileContent = new BinaryResource(fileName, null);
        }

        return fileContent;
    }


    private SuTFormData loadResultFiles(final SuTFormData fd) {
        final Path folder = Paths.get(Factory.getSutPathsFiles().getReportPath(fd.getSutId()));

        try {
            getReportFilesOrderedByCreationDate(folder).forEach(path -> {
                final String resultFileName = path.getFileName().toString();
                LOG.info("report file found: {}", resultFileName);
                fd.getTestReportTable().addRow().setFileName(resultFileName);
            });
        }
        catch (final NoSuchFileException exc) {
            LOG.error("report files not found in folder: {}", folder);
        }
        catch (final IOException exc) {
            LOG.error(" ", exc);
        }

        return fd;
    }


    private Stream<Path> getReportFilesOrderedByCreationDate(final Path folder) throws IOException {
        try {
            return Files.find(folder, 1, (path, fileAttributes) -> {
                final String filenameToCheck = path.getFileName().toString();
                return fileAttributes.isRegularFile() && filenameToCheck.endsWith(".pdf");
            }).sorted(new FileCreationTimeComparator().reversed());
        }
        catch (final IllegalStateException exc) {
            throw new IOException(exc);
        }
    }

    private static final class FileCreationTimeComparator implements Comparator<Path> {

        @Override
        public int compare(Path path1, Path path2) {
            try {
                return Files.readAttributes(path1, BasicFileAttributes.class).creationTime().compareTo(Files.readAttributes(path2, BasicFileAttributes.class).creationTime());
            }
            catch (final IOException exc) {
                throw new IllegalStateException(exc);
            }
        }
    }

    private SuTFormData loadCapabilityStatus(final SuTFormData fd) {

        sutMap.get(fd.getSutId()).badges.stream().sorted().forEachOrdered(badgeId -> {
            final SutCapabilityStatusTableRowData row = fd.getSutCapabilityStatusTable().addRow();
            row.setCbBadgeID(badgeId);
            row.setCbBadgeName(BEANS.get(CbService.class).getBadgeDescription(badgeId).name);

            row.setCbBadgeStatus(getBadgeConformanceStatus(fd.getSutId(), badgeId));
        });
        return fd;
    }


    private String getBadgeConformanceStatus(final String sutId, final String badgeId) {
        String bdVerdict = "PASSED";

        //(tsId(tcSet)) 
        HashMap<String, HashSet<String>> tcList = BEANS.get(ITsService.class).getTcListForBadge(badgeId);

        for (Entry<String, HashSet<String>> entry: tcList.entrySet()) {
            String tsId = entry.getKey();
            HashSet<String> value = entry.getValue();
            for (String tcId: value) {
                final String tcVerdict = BEANS.get(ISuTTcService.class).getTcLastVerdict(sutId, tsId, tcId);
                bdVerdict = evalVerdicts(bdVerdict, tcVerdict);
            }
        }

        return bdVerdict;

    }


    private String evalVerdicts(final String bdVerdict, final String tcVerdict) {
        //PASSED < UNKNOWN < INCONCLUSIVE < FAILED
        String verdict = bdVerdict;
        if (!"PASSED".equalsIgnoreCase(tcVerdict)) {
            if ("FAILED".equalsIgnoreCase(tcVerdict) || "FAILED".equalsIgnoreCase(bdVerdict))
                verdict = "FAILED";
            else if ("INCONCLUSIVE".equalsIgnoreCase(tcVerdict) || "INCONCLUSIVE".equalsIgnoreCase(bdVerdict))
                verdict = "INCONCLUSIVE";
            else
                verdict = "UNKNOWN";
        }

        return verdict;

    }

    /*
     * functions for SuTEditFormData
     */


    @Override
    public SuTEditFormData load(final SuTEditFormData formData) {
        LOG.info("load {}", getClass());
        if (!ACCESS.check(new ReadSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }
        // find the SuT description by selected SuTid.
        final SutDescription sut = sutMap.get(formData.getSutId());
        formData.setSutId(sut.ID);

        // fill the form data: GeneralBox
        formData.getName().setValue(sut.name == null ? "" : sut.name);
        formData.getVersion().setValue(sut.version);
        formData.getSutVendor().setValue(sut.vendor);
        formData.getDescr().setValue(sut.description);
        formData.getRtiSettingDesignator().setValue(sut.settingsDesignator);
        formData.getFederationName().setValue(sut.federation);
        formData.getFederateName().setValue(sut.sutFederateName);

        return formData;
    }


    @Override
    public SuTEditFormData prepareCreate(final SuTEditFormData formData) {
        LOG.info("prepareCreate {}", getClass());
        if (!ACCESS.check(new CreateSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        return formData;
    }


    @Override
    public SuTEditFormData create(SuTEditFormData formData) {
        LOG.info("create {}", getClass());
        if (!ACCESS.check(new CreateSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        // fill the SUT description with the from values
        final SutDescription sut = new SutDescription();
        // set the attributes; the ID is provided by the execute() method
        sut.name = formData.getName().getValue();
        sut.version = formData.getVersion().getValue();
        sut.description = formData.getDescr().getValue();
        sut.vendor = formData.getSutVendor().getValue();
        sut.settingsDesignator = formData.getRtiSettingDesignator().getValue();
        sut.federation = formData.getFederationName().getValue();
        sut.sutFederateName = formData.getFederateName().getValue();

        // get the selected capabilities
        sut.badges = formData.getSuTCapabilityBox().getValue();

        // create new SuT
        try {
            sut.ID = new CmdUpdateSUT(sut).execute();
            // set the SUT ID in the form
            formData.setSutId(sut.ID);

            LOG.info("SuT description stored for {}", formData.getName().getValue());

        }
        catch (final VetoException vetoExc) {
            throw vetoExc;
        }
        catch (final Exception exe) {
            LOG.error("Error when storing SuT description for {}", formData.getName().getValue(), exe);
        }

        // Update SuT map
        updateSutMap(sut);

        return formData;
    }


    @Override
    public SuTEditFormData store(SuTEditFormData formData) {
        if (!ACCESS.check(new UpdateSuTPermission())) {
            throw new VetoException(TEXTS.get("AuthorizationFailed"));
        }

        // fill the SUT description with the from values
        final SutDescription sut = new SutDescription();
        // set the attributes; the ID is provided by the execute() method
        sut.ID = formData.getSutId();
        sut.name = formData.getName().getValue();
        sut.version = formData.getVersion().getValue();
        sut.description = formData.getDescr().getValue();
        sut.vendor = formData.getSutVendor().getValue();
        sut.settingsDesignator = formData.getRtiSettingDesignator().getValue();
        sut.federation = formData.getFederationName().getValue();
        sut.sutFederateName = formData.getFederateName().getValue();

        // get the selected capabilities
        sut.badges = formData.getSuTCapabilityBox().getValue();

        // edit a existing SuT
        try {
            sut.ID = new CmdUpdateSUT(sut).execute();
            LOG.info("SuT description stored for {}", formData.getName().getValue());
        }

        catch (final Exception exe) {
            LOG.error("Error when storing SuT description for {}", formData.getName().getValue(), exe);
        }

        // update SuT map
        updateSutMap(sut);

        return formData;
    }


    private void updateSutMap(final SutDescription sut) {
        LOG.info("update sutMap attribute");
        // TODO must be better re-written!
        CmdListSuT sutCmd = new CmdListSuT();
        sutCmd.execute();
        sutMap = sutCmd.sutMap;
    }
}
