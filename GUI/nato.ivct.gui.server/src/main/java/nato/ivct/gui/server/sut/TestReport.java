package nato.ivct.gui.server.sut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.eclipse.scout.rt.platform.BEANS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.server.ts.TsService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.sut.ISuTTcService;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonQLDataSource;

class TestReport {
    
    private static  final Logger LOG    = LoggerFactory.getLogger(TestReport.class);
    
    private TestReport() {}
    
    static String createReportJsonFile(final String sutId, final String resultFile) {
        final Path reportFolder = Paths.get(Factory.getSutPathsFiles().getReportPath(sutId));
        final String reportFile = Paths.get(reportFolder.toString(), "Report.json").toString();
        
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
        transformSuTDesc(jReport, sutId);
        
        //VerdictSummary section
        transformVerdictSummary(jResults.get(), jReport);
        
        //Badges section
        transformBadges(jResults.get(), jReport, sutId);
        
        //Fill with calculated SuT Verdict
        addSutVerdict(jReport);
 
        return reportJSONFile(jReport, reportFile);
    }
 
    private static String reportJSONFile(final JsonElement jReport, final String reportFile) {           
        try {
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
    
    private static void transformSuTDesc(final JsonObject jReport, final String sutId) {
        final String SUT_KW         = "SuT";
        final String SUTID_KW       = "SutId";
        final String SUTNAME_KW     = "SutName";
        final String SUTREV_KW      = "SutRev";
        final String SUTVENDOR_KW   = "SutVendor";
        
        // SuT information
        final SutDescription sutDesc = BEANS.get(SuTService.class).getSutDescription(sutId);
        
        // Insert the SuT section
        JsonObject sutSection = new JsonObject();
        
        sutSection.addProperty(SUTID_KW, sutDesc.ID);
        sutSection.addProperty(SUTNAME_KW, sutDesc.name);
        sutSection.addProperty(SUTREV_KW, sutDesc.version);
        sutSection.addProperty(SUTVENDOR_KW, sutDesc.vendor);

        jReport.add(SUT_KW, sutSection);  
    }
    
    private static void transformVerdictSummary(final JsonObject jResults, final JsonObject jReport) {
        final String VERDICTSUMMARY_KW = "VerdictSummary";
        
        JsonObject verdictSection = (JsonObject) jResults.get(VERDICTSUMMARY_KW);
        
        jReport.add(VERDICTSUMMARY_KW, verdictSection);
    }
    
    private static void transformBadges(final JsonObject jResults, final JsonObject jReport, final String sutId) {
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
    
    static String getBadgeConformanceStatus(final String sutId, final String badgeId) {
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
    
    private static void transformTestSuites(final JsonObject jResults, final JsonObject badgeObj, final String badgeId) {
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
    
    private static void transformTestCases(final JsonObject jResults, final JsonObject tsObj, final String bdId, final String tsId) {
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
            JsonObject tsSection = Optional.ofNullable((JsonObject) results.get(tsId)).orElseGet(JsonObject::new);
            JsonArray tcSection = Optional.ofNullable((JsonArray) tsSection.get(tcId)).orElseGet(JsonArray::new);
            
            //TcResult section
            transformTcResults(tcSection, tcObj);
        });  
    }


    private static void transformTcResults(JsonArray tcSection, JsonObject tcObj) {
        final String TCRESULTS_KW = "TcResults";

        //TcResult information
        JsonArray tcResultArray = new JsonArray();
        tcObj.add(TCRESULTS_KW, tcResultArray);
        
        if (tcSection == null)
            return;
        
        tcSection.forEach(result ->{
            tcResultArray.add(result);
        });
    }
    
    private static void addSutVerdict(final JsonObject jReport) {
        final String VERDICTSUMMARY_KW = "VerdictSummary";
        final String SUTVERDICT_KW = "SutVerdict";
        
        String sutConformanceStatus = getSutConformanceStatus(jReport);
        jReport.getAsJsonObject(VERDICTSUMMARY_KW).addProperty(SUTVERDICT_KW, sutConformanceStatus);
        
    }
    
    private static String getSutConformanceStatus(final JsonObject jReport) {
        final String BADGES_KW = "Badges";
        final String BADGEVERDICT_KW = "BadgeVerdict";
        
        String sutVerdict = "PASSED";
        for (JsonElement badge : jReport.getAsJsonArray(BADGES_KW)){
            String bdVerdict = badge.getAsJsonObject().get(BADGEVERDICT_KW).getAsString();
            sutVerdict = evalVerdicts(sutVerdict, bdVerdict);
        }
        
        return sutVerdict; 
    }
    
    private static String evalVerdicts(final String summaryVerdict, final String individualVerdict) {
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
    
    private static Optional<JsonObject> readJsonResultFile(final String sutId, final String resultFile) {
        try {
            return Optional.ofNullable(JsonParser.parseReader((Files.newBufferedReader(Paths.get(resultFile)))).getAsJsonObject());
        }
        catch (IOException | JsonIOException | JsonSyntaxException exc) {
            LOG.error("Result file does not exist", resultFile);
            return Optional.empty();
        }
    }   
    
    static String createPDFTestreport(final String templateFolder, final Path reportFolder) {

        try {
            final String pathToReports = reportFolder.toString() +"\\";
            final String pathToTemplates = templateFolder +"\\";
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


}
