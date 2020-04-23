package nato.ivct.gui.server.sut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.exception.VetoException;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.resource.BinaryResource;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.data.tile.TileColorScheme;
import org.eclipse.scout.rt.shared.services.common.security.ACCESS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdUpdateSUT;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.gui.server.ServerSession;
import nato.ivct.gui.server.ServerSession.SutTcResultDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.server.ts.TsService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.sut.CreateSuTPermission;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.ReadSuTPermission;
import nato.ivct.gui.shared.sut.SuTEditFormData;
import nato.ivct.gui.shared.sut.SuTFormData;
import nato.ivct.gui.shared.sut.SuTFormData.SutCapabilityStatusTable.SutCapabilityStatusTableRowData;
import nato.ivct.gui.shared.ts.TsFormData;
import nato.ivct.gui.shared.ts.TsFormData.TcTable.TcTableRowData;
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
    public String createTestreport(final String sutId){
        //TODO
        Path reportFolder = Paths.get("C:\\Entwicklung\\IVCT\\IVCT_Runtime\\IVCTsut\\"+sutId+"\\Reports\\");
        Path templateFolder = Paths.get("C:\\Entwicklung\\IVCT\\IVCT_Runtime\\IVCTsut\\"+sutId+"\\Reports\\Template\\");
        
        if (createReportJsonFile(sutId, reportFolder.toString() +"\\" + "Results.json").isEmpty()) {
            return null;
        }  
        
        return createPDFTestreport(templateFolder, reportFolder);
    }
    
    private final String createReportJsonFile(final String sutId, final String resultFile) {

        final JsonObject jReport = new JsonObject();
        
        //Result.json to JSON Object       
        final Optional<JsonObject> jResults = readJsonResultFile(sutId, resultFile);
        
        if (!jResults.isPresent()) {
            return "";
        }
        
        //Create JSON Report Structure
        /*
         * 1.SuT
         * 2.VerdictSummary
         * 3.Badges
         * 3.1. TestSuites
         * 3.1.1. TestCases
         * 3.1.1.1. TcResults
         */
           
        //SuT section
        transformSuTDesc(jResults.get(), jReport);
        
        //VerdictSummary section
        transformVerdictSummary(jResults.get(), jReport);
        
        //Badges section
        transformBadges(jResults.get(), jReport, sutId);
        
        //Fill with calculated SuT Verdict
        addSutVerdict(jReport);
 
        return reportJSONFile(jReport);
    }
 
    private String reportJSONFile(JsonElement jReport) {           
        String reportFile = "";  
        
        try {
            //TODO
            reportFile = "C:\\Entwicklung\\IVCT\\IVCT_Runtime\\IVCTsut\\hw_iosb\\Reports\\Report.json";
            LOG.debug("store report json object to file {}", reportFile);
            Files.write(Paths.get(reportFile), jReport.toString().getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (final InvalidPathException e) {
            LOG.error("invalid path for report json file", reportFile);
        }
        catch (final IOException e) {
            LOG.error("could not write report json object to file {}", reportFile);
        }
        return reportFile;
    }
    
    private void transformSuTDesc(final JsonObject jResults, final JsonObject jReport) {
        final String SUT_KW = "SuT";
        
        JsonObject sutSection = (JsonObject) jResults.get(SUT_KW);
        jReport.add(SUT_KW, sutSection);  
    }
    
    private void transformVerdictSummary(final JsonObject jResults, final JsonObject jReport) {
        final String VERDICTSUMMARY_KW = "VerdictSummary";
        final String SUTVERDICT_KW = "SutVerdict";
        
        JsonObject verdictSection = (JsonObject) jResults.get(VERDICTSUMMARY_KW);
        
        jReport.add(VERDICTSUMMARY_KW, verdictSection);
    }
    
    private void transformBadges(final JsonObject jResults, final JsonObject jReport, final String sutId) {
        final String BADGES_KW = "Badges";
        final String BADGEID_KW = "BadgeId";
        final String BADGENAME_KW = "BadgeName";
        final String BADGEVERDICT_KW = "BadgeVerdict";
        
        //SuT information
        final SutDescription sutDesc = BEANS.get(SuTService.class).getSutDescription(sutId);
        
        //Badge information
        Set<String> badges = sutDesc.badges;
        
        JsonArray badgeArray = new JsonArray();
        jReport.add(BADGES_KW, badgeArray);
        
        badges.stream().sorted().forEachOrdered(badgeId ->{
            BadgeDescription badgeDesc = BEANS.get(CbService.class).getBadgeDescription(badgeId);
            JsonObject badgeObj = new JsonObject();
            
            badgeObj.addProperty(BADGEID_KW, badgeDesc.ID);
            badgeObj.addProperty(BADGENAME_KW, badgeDesc.name);
            badgeObj.addProperty(BADGEVERDICT_KW, getBadgeConformanceStatus(sutId, badgeId));
  
            badgeArray.add(badgeObj);
            
            //TestSuite section
            transformTestSuites(jResults, badgeObj, badgeId);
        }); 
    }
    
    private void transformTestSuites(final JsonObject jResults, final JsonObject badgeObj, final String badgeId) {
        final String TESTSUITES_KW = "TestSuites";
        final String TSID_KW = "TsId";
        final String TSNAME_KW = "TsName";
        
        //TestSuite information
        final Set<String> tsList = BEANS.get(ITsService.class).getTsForIr(BEANS.get(ICbService.class).getIrForCb(badgeId));
        
        JsonArray tsArray = new JsonArray();
        badgeObj.add(TESTSUITES_KW, tsArray);
        
        tsList.stream().sorted().forEachOrdered(tsId ->{   
            TestSuiteDescription tsDesc = BEANS.get(TsService.class).getTsDescription(tsId); 
            JsonObject tsObj = new JsonObject();
            
            tsObj.addProperty(TSID_KW, tsDesc.id);
            tsObj.addProperty(TSNAME_KW, tsDesc.name);
  
            tsArray.add(tsObj);
            
            //TestCase section
            transformTestCases(jResults, tsObj, badgeId, tsId);
        });       
    }
    
    private void transformTestCases(final JsonObject jResults, final JsonObject tsObj, final String bdId, final String tsId) {
        final String TESTCASES_KW = "TestCases";
        final String TCID_KW = "TcId";
        final String TCNAME_KW = "TcName";
        final String TCRESULTS_KW = "TcResults";
        
        //TestCase information
        final Map<String, HashSet<String>> tcList = BEANS.get(ITsService.class).getTcListForBadge(bdId);
        
        JsonArray tcArray = new JsonArray();
        tsObj.add(TESTCASES_KW, tcArray);
        
        tcList.getOrDefault(tsId, new HashSet<String>()).stream().sorted().forEachOrdered(tcId -> {
            
            String tcName = Stream.of(tcId.split(Pattern.quote("."))).reduce((a, b) -> b).get();

            JsonObject tcObj = new JsonObject();
            
            tcObj.addProperty(TCID_KW, tcId);
            tcObj.addProperty(TCNAME_KW, tcName);

            tcArray.add(tcObj);
            
            JsonObject results = (JsonObject) jResults.get(TCRESULTS_KW);
            JsonObject tsSection = (JsonObject) results.get(tsId);
            JsonArray tcSection = (JsonArray) tsSection.get(tcId);
            
            //TcResult section
            transformTcResults(tcSection, tcObj);
        });  
    }


    private void transformTcResults(JsonArray tcSection, JsonObject tcObj) {
        final String TCRESULTS_KW = "TcResults";

        //TcResult information
        JsonArray tcResultArray = new JsonArray();
        tcObj.add(TCRESULTS_KW, tcResultArray);
        
        tcSection.forEach(result ->{
            tcResultArray.add(result);
        });
    }
    
    private void addSutVerdict(final JsonObject jReport) {
        final String VERDICTSUMMARY_KW = "VerdictSummary";
        final String SUTVERDICT_KW = "SutVerdict";
        
        String sutConformanceStatus = getSutConformanceStatus(jReport);
        jReport.getAsJsonObject(VERDICTSUMMARY_KW).addProperty(SUTVERDICT_KW, sutConformanceStatus);
        
    }
    
    private Optional<JsonObject> readJsonResultFile(final String sutId, final String resultFile) {
        try {
            return Optional.ofNullable(JsonParser.parseReader((Files.newBufferedReader(Paths.get(resultFile)))).getAsJsonObject());
        }
        catch (IOException | JsonIOException | JsonSyntaxException exc) {
            LOG.error("Result file does not exist", resultFile);
            return Optional.empty();
        }
    }   
    
    private String createPDFTestreport(final Path templateFolder, final Path reportFolder) {

        try {
            final String pathToReports = reportFolder.toString() +"\\";
            final String pathToTemplates = templateFolder.toString() +"\\";
            JRDataSource jrDataSource = new JsonQLDataSource(new File(pathToReports + "Report.json"));
            JasperReport jasperReport = JasperCompileManager.compileReport(pathToTemplates + "Report.jrxml");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_SuT.jrxml", "subreport_SuT.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_Verdict.jrxml", "subreport_Verdict.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TcResults.jrxml", "subreport_TcResults.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TestCases.jrxml", "subreport_TestCases.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TS.jrxml", "subreport_TS.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_Badge.jrxml", "subreport_Badge.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap(), jrDataSource);

            Date time = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyyMMdd-HHmmss");
            final String fileName = "Report" + simpleDateFormat.format(time) + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathToReports + fileName);
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

    private String getSutConformanceStatus(final JsonObject jReport) {
        final String BADGES_KW = "Badges";
        final String BADGEVERDICT_KW = "BadgeVerdict";
        
        String sutVerdict = "PASSED";
        for (JsonElement badge : jReport.getAsJsonArray(BADGES_KW)){
            String bdVerdict = badge.getAsJsonObject().get(BADGEVERDICT_KW).getAsString();
            sutVerdict = evalVerdicts(sutVerdict, bdVerdict);
        }
        
        return sutVerdict; 
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


    private String evalVerdicts(final String summaryVerdict, final String individualVerdict) {
        //PASSED < UNKNOWN < INCONCLUSIVE < FAILED
        String verdict = summaryVerdict;
        if (!"PASSED".equalsIgnoreCase(individualVerdict)) {
            if ("FAILED".equalsIgnoreCase(individualVerdict) || "FAILED".equalsIgnoreCase(summaryVerdict))
                verdict = "FAILED";
            else if ("INCONCLUSIVE".equalsIgnoreCase(individualVerdict) || "INCONCLUSIVE".equalsIgnoreCase(summaryVerdict))
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
