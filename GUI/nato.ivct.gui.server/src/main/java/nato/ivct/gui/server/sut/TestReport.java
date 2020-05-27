package nato.ivct.gui.server.sut;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.scout.rt.platform.BEANS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import nato.ivct.commander.BadgeDescription;
import nato.ivct.commander.CmdSendLogMsg;
import nato.ivct.commander.Factory;
import nato.ivct.commander.InteroperabilityRequirement;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.CmdListTestSuites.TestCaseDesc;
import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;
import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.server.ts.TsService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.cb.ITsService;
import nato.ivct.gui.shared.sut.ISuTTcService;
import nato.ivct.gui.shared.sut.TcLogMsgNotification;
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
         * 2.TestSystem
         * 3.TestConfiguration
         * 4.Badges
         * 4.1. TestSuites
         * 4.1.1. TestCases
         * 4.1.1.1. TcResult
         * 4.1.1.1.1 LoggingData
         * 5.SuTVerdict
         */
           
        //SuT section
        transformSuTDesc(jReport, sutId);
        
        //Test System section
        addTestSystemDesc(jReport);
        
        //Test Configuration section
        addTestConfigurationDesc(jReport, sutId);
        
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
        catch (final InvalidPathException exc) {
            LOG.error("invalid path for report json file: " + reportFile, exc);
        }
        catch (final IOException exc) {
            LOG.error("could not write report json object to file " + reportFile, exc);
        }
        return reportFile;
    }
    
    private static void transformSuTDesc(final JsonObject jReport, final String sutId) {
        final String SUT_KW         = "SuT";
        final String SUTID_KW       = "SutId";
        final String SUTNAME_KW     = "SutName";
        final String SUTREV_KW      = "SutRev";
        final String SUTVENDOR_KW   = "SutVendor";
        final String SUTDESC_KW     = "SutDescription";
        
        // SuT information
        final SutDescription sutDesc = BEANS.get(SuTService.class).getSutDescription(sutId);
        
        // Insert the SuT section
        JsonObject sutSection = new JsonObject();
        
        sutSection.addProperty(SUTID_KW, sutDesc.ID);
        sutSection.addProperty(SUTNAME_KW, sutDesc.name);
        sutSection.addProperty(SUTREV_KW, sutDesc.version);
        sutSection.addProperty(SUTVENDOR_KW, sutDesc.vendor);
        sutSection.addProperty(SUTDESC_KW, sutDesc.description);

        jReport.add(SUT_KW, sutSection);  
    }
    
    private static void addTestSystemDesc(final JsonObject jReport) {
        final String TestSystem_KW  = "TestSystem";
        final String Version_KW     = "Version";
        final String Build_KW       = "Build";
        
        // Insert the TestSystem section
        JsonObject testSystemSection = new JsonObject();
        
        testSystemSection.addProperty(Version_KW, Factory.getVersion());
        testSystemSection.addProperty(Build_KW,  Factory.getBuild());
        
        jReport.add(TestSystem_KW, testSystemSection);
    }
    
    private static void addTestConfigurationDesc(final JsonObject jReport, final String sutId) {
        final String TESTCONFIGURATION_KW   = "TestConfiguration";
        final String FEDERATION_KW          = "Federation";
        final String SUTFEDERATE_KW         = "SutFederate";
        final String RTI_KW                 = "Rti";
        final String SETTINGSDESIGNATOR_KW  = "SettingsDesignator";
        
        // SuT information
        final SutDescription sutDesc = BEANS.get(SuTService.class).getSutDescription(sutId);
        
        // Insert the test configuration section
        JsonObject testConfigurationSection = new JsonObject();
        
        testConfigurationSection.addProperty(FEDERATION_KW, sutDesc.federation);
        testConfigurationSection.addProperty(SUTFEDERATE_KW, sutDesc.sutFederateName);
        testConfigurationSection.addProperty(RTI_KW, Factory.props.getProperty(Factory.RTI_ID));
        testConfigurationSection.addProperty(SETTINGSDESIGNATOR_KW, Factory.props.getProperty(Factory.SETTINGS_DESIGNATOR));
        
        jReport.add(TESTCONFIGURATION_KW, testConfigurationSection);
    }
    
    private static void transformBadges(final JsonObject jResults, final JsonObject jReport, final String sutId) {
        final String BADGES_KW          = "Badges";
        final String BADGEID_KW         = "BadgeId";
        final String BADGENAME_KW       = "BadgeName";
        final String BADGEVERDICT_KW    = "BadgeVerdict";
        final String BADGEVERSION_KW    = "BadgeVersion";
        final String BADGEDESC_KW       = "BadgeDescription";
        
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
            badgeObj.addProperty(BADGEVERSION_KW, badgeDesc.version);
            badgeObj.addProperty(BADGEDESC_KW, badgeDesc.description);
            badgeObj.addProperty(BADGEVERDICT_KW, getBadgeConformanceStatus(sutId, badgeId));
  
            badgeArray.add(badgeObj);
            
            //Interoperability requirement section
            addInteroperabilityRequirementForBadge(badgeObj, badgeDesc.requirements);
            
            //TestSuite section
            transformTestSuites(jResults, badgeObj, sutId, badgeId);
        }); 
    }
    
    private static void addInteroperabilityRequirementForBadge(final JsonObject badgeObj, final Map<String, InteroperabilityRequirement> requirements) {
        final String IRFORBADGE_KW  = "IrForBadge";
        final String IRID_KW        = "IrId";
        final String IRDESC_KW      = "IrDescription";
        
        JsonArray irArray = new JsonArray();
        
        requirements.keySet().stream().sorted().forEachOrdered(irId -> {
            JsonObject irObj = new JsonObject();
            
            irObj.addProperty(IRID_KW, requirements.get(irId).ID);
            irObj.addProperty(IRDESC_KW, requirements.get(irId).description);
            
            irArray.add(irObj);
        });
       
        badgeObj.add(IRFORBADGE_KW, irArray);
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
    
    private static void transformTestSuites(final JsonObject jResults, final JsonObject badgeObj, final String sutId, final String badgeId) {
        final String TESTSUITES_KW  = "TestSuites";
        final String TSID_KW        = "TsId";
        final String TSNAME_KW      = "TsName";
        final String TSVERSION_KW   = "TsVersion";
        final String TSDESC_KW      = "TsDescription";
        
        //TestSuite information
        final Set<String> tsList = BEANS.get(ITsService.class).getTsForIr(BEANS.get(ICbService.class).getIrForCb(badgeId));
        
        JsonArray tsArray = new JsonArray();
        badgeObj.add(TESTSUITES_KW, tsArray);
        
        tsList.stream().sorted().forEachOrdered(tsId ->{   
            TestSuiteDescription tsDesc = BEANS.get(TsService.class).getTsDescription(tsId); 
            JsonObject tsObj = new JsonObject();
            
            tsObj.addProperty(TSID_KW, tsDesc.id);
            tsObj.addProperty(TSNAME_KW, tsDesc.name);
            tsObj.addProperty(TSVERSION_KW, tsDesc.version);
            tsObj.addProperty(TSDESC_KW, tsDesc.description);
  
            tsArray.add(tsObj);
            
            //TestCase section
            transformTestCases(jResults, tsObj, sutId, badgeId, tsId);
        });       
    }
    
    private static void transformTestCases(final JsonObject jResults, final JsonObject tsObj, final String sutId, final String bdId, final String tsId) {
        final String TESTCASES_KW   = "TestCases";
        final String TCID_KW        = "TcId";
        final String TCNAME_KW      = "TcName";
        final String TCRESULTS_KW   = "TcResults";
        final String TCDESC_KW      = "TcDescription";
        
        //TestCase information
        final Map<String, HashSet<String>> tcList = BEANS.get(ITsService.class).getTcListForBadge(bdId);
        
        //TestCase Description
        final Map<String, TestCaseDesc> tcDesc = BEANS.get(TsService.class).getTsDescription(tsId).testcases;
        
        JsonArray tcArray = new JsonArray();
        tsObj.add(TESTCASES_KW, tcArray);
        
        tcList.getOrDefault(tsId, new HashSet<String>()).stream().sorted().forEachOrdered(tcId -> {
            
            String tcName = Stream.of(tcId.split(Pattern.quote("."))).reduce((a, b) -> b).get();

            JsonObject tcObj = new JsonObject();
            
            tcObj.addProperty(TCID_KW, tcId);
            tcObj.addProperty(TCNAME_KW, tcName);
            tcObj.addProperty(TCDESC_KW, tcDesc.get(tcId).description);

            tcArray.add(tcObj);
            
            JsonObject results = (JsonObject) jResults.get(TCRESULTS_KW);
            JsonObject tsSection = Optional.ofNullable((JsonObject) results.get(tsId)).orElseGet(JsonObject::new);
            JsonArray tcSection = Optional.ofNullable((JsonArray) tsSection.get(tcId)).orElseGet(JsonArray::new);
            
            //Interoperability requirement section
            final HashMap<String, String> irDesc = BEANS.get(TsService.class).getIrForTc(tcId);
            addInteroperabilityRequirementForTc(tcObj, irDesc);
            
            //TcResult section
            transformTcResult(tcSection, tcObj, sutId, tsId);
        });  
    }
    
    private static void addInteroperabilityRequirementForTc(final JsonObject tcObj, final HashMap<String, String> irDesc) {
        final String IRFORTC_KW = "IrForTc";
        final String IRID_KW    = "IrId";
        final String IRDESC_KW  = "IrDescription";
        
        JsonArray irArray = new JsonArray();
        
        irDesc.keySet().stream().sorted().forEachOrdered(irId -> {
            JsonObject irObj = new JsonObject();
            
            irObj.addProperty(IRID_KW, irId);
            irObj.addProperty(IRDESC_KW, irDesc.get(irId));
            
            irArray.add(irObj);
        });
       
        tcObj.add(IRFORTC_KW, irArray);
    }


    private static void transformTcResult(final JsonArray tcSection, final JsonObject tcObj, final String sutId, final String tsId) {
        final String TCRESULT_KW = "TcResult";
        final String LOGFILEPATH_KW = "LogFilePath";

        //TcResult information
        JsonArray tcResultArray = new JsonArray();
        tcObj.add(TCRESULT_KW, tcResultArray);
        
        if (tcSection == null)
            return;
        
        StreamSupport.stream(tcSection.spliterator(), false).sorted(Comparator.comparing(TestReport::getTimeStamp).reversed()).findFirst()
            .ifPresent(tcResult -> {
                //Logging data section
                transformLoggingData(tcResult.getAsJsonObject(), sutId, tsId, tcResult.getAsJsonObject().get(LOGFILEPATH_KW).getAsString());
                tcResultArray.add(tcResult);
            });
    }
    
    private static ZonedDateTime getTimeStamp(JsonElement element) {
        final String TIMESTAMP_KW = "TimeStamp";
        try {
            return ZonedDateTime.parse(element.getAsJsonObject().get(TIMESTAMP_KW).getAsString(), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ"));
        } catch (IllegalArgumentException | DateTimeParseException exc) {
            LOG.warn("incorrect time format ", exc);
            return ZonedDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneId.systemDefault());
        }
    }
    
    private static void transformLoggingData(final JsonObject tcResult, final String sutId, final String tsId, final String logFileName) {
        final String LOGGINGDATA_KW = "LoggingData";
        final String LEVEL_KW       = "level";
        final String TIME_KW        = "time";
        final String EVENT_KW       = "event";
        
        JsonArray loggingDataArray = new JsonArray();
        tcResult.add(LOGGINGDATA_KW, loggingDataArray);
        
        // Open logfile and read content
        final Path logFilePath = Paths.get(Factory.getSutPathsFiles().getSutLogPathName(sutId, tsId), logFileName);
        try {
            java.nio.file.Files.lines(logFilePath).map(line -> {
                TcLogMsgNotification logMsgNotification = null;
                try {
                    final JsonObject jObj = new Gson().fromJson(line, JsonObject.class);
                    logMsgNotification = new TcLogMsgNotification();
                    logMsgNotification.setLogLevel(jObj.get(CmdSendLogMsg.LOG_MSG_LEVEL).getAsString());
                    logMsgNotification.setTimeStamp(jObj.get(CmdSendLogMsg.LOG_MSG_TIME).getAsLong());
                    logMsgNotification.setLogMsg(jObj.get(CmdSendLogMsg.LOG_MSG_EVENT).getAsString());
                }
                catch (JsonSyntaxException exc) {
                    LOG.info("incorrect log format " + logFilePath, exc);
                }
                return Optional.ofNullable(logMsgNotification);
            }).filter(Optional::isPresent).forEach(optionalLogMsgNotification -> {
                    final TcLogMsgNotification logMsgNotification = optionalLogMsgNotification.get();
                    JsonObject loggingDataElement = new JsonObject();
                    loggingDataElement.addProperty(LEVEL_KW, logMsgNotification.getLogLevel());
                    loggingDataElement.addProperty(TIME_KW, logMsgNotification.getTimeStamp());
                    loggingDataElement.addProperty(EVENT_KW, logMsgNotification.getLogMsg());
                    loggingDataArray.add(loggingDataElement);
            });
        }
        catch (final NoSuchFileException exc) {
            LOG.info("log files not found: " + logFilePath, exc);
        }
        catch (final IOException exc) {
            LOG.error("", exc);
        }
    }
    
    private static void addSutVerdict(final JsonObject jReport) {
        final String SUT_KW = "SuT";
        final String SUTVERDICT_KW = "SutVerdict";
        
        String sutConformanceStatus = getSutConformanceStatus(jReport);
        jReport.getAsJsonObject(SUT_KW).addProperty(SUTVERDICT_KW, sutConformanceStatus);
        
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
        final Path jsonPath = Paths.get(resultFile);
        if (!jsonPath.toFile().exists())
            return Optional.empty();
        
        try {
            return Optional.ofNullable(JsonParser.parseReader((Files.newBufferedReader(jsonPath))).getAsJsonObject());
        }
        catch (IOException | JsonIOException | JsonSyntaxException exc) {
            LOG.error("Result file does not exist: "  + resultFile, exc);
            return Optional.empty();
        }
    }   
    
    static String createPDFTestreport(final String templateFolder, final Path reportFolder) {

        try {
            final String pathToReports = reportFolder.toString() + File.separatorChar;
            final String pathToTemplates = templateFolder + File.separatorChar;
            JRDataSource jrDataSource = new JsonQLDataSource(new File(pathToReports + "Report.json"));
            JasperReport jasperReport = JasperCompileManager.compileReport(pathToTemplates + "Report.jrxml");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_SuT.jrxml", "subreport_SuT.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_ConformanceStatus.jrxml", "subreport_ConformanceStatus.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TestSystem.jrxml", "subreport_TestSystem.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TestConfiguration.jrxml", "subreport_TestConfiguration.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_BadgeSummary.jrxml", "subreport_BadgeSummary.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_Badge.jrxml", "subreport_Badge.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_IrForBadge.jrxml", "subreport_IrForBadge.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TS.jrxml", "subreport_TS.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TestCases.jrxml", "subreport_TestCases.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_IrForTc.jrxml", "subreport_IrForTc.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_TcResult.jrxml", "subreport_TcResult.jasper");
            JasperCompileManager.compileReportToFile(pathToTemplates + "subreport_Logfile.jrxml", "subreport_Logfile.jasper");
            

            final Map<String,Object> parameters = new HashMap<>();
            parameters.put("IVCT_BADGE_ICONS", "file:///" + Factory.props.getProperty(Factory.IVCT_BADGE_ICONS_ID) + "/");
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, jrDataSource);

            Date time = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("_yyyyMMdd-HHmmss");
            final String fileName = "Report" + simpleDateFormat.format(time) + ".pdf";
            JasperExportManager.exportReportToPdfFile(jasperPrint, pathToReports + fileName);
            return fileName;
        }
        catch (JRException exc) {
            LOG.error("Report creation failed", exc);
        }

        return null;
    }


}
