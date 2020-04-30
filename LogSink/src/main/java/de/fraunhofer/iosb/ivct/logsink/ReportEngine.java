/*
 * Copyright 2017, Johannes Mulder (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.ivct.logsink;

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
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cedarsoftware.util.io.JsonWriter;

import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.Factory;
import nato.ivct.commander.SutDescription;
import nato.ivct.commander.SutPathsFiles;


public class ReportEngine {
    Logger               LOGGER          = LoggerFactory.getLogger(ReportEngine.class);
    private int          numFailed       = 0;
    private int          numInconclusive = 0;
    private int          numPassed       = 0;
    private Path         resultFile      = null;
    private Path         reportFile      = null;
    private String       knownSut        = new String();
    private final String baseFileName    = "Results";
    private final String reportFileName  = "Report";
    private final String failedStr       = "FAILED";
    private final String inconclusiveStr = "INCONCLUSIVE";
    private final String passedStr       = "PASSED";

    // keys for the results json structure
    private final String TESTSYSTEM_KW        = "Testsystem";
    private final String TESTSYSTEMREV_KW     = "TestSystemRev";
    private final String SUT_KW               = "SuT";
    private final String SUTID_KW             = "SutId";
    private final String SUTNAME_KW           = "SutName";
    private final String SUTREV_KW            = "SutRev";
    private final String SUTVENDOR_KW         = "SutVendor";
    private final String VERDICTSUMMARY_KW    = "VerdictSummary";
    private final String NUMOFPASSED_KW       = "NumOfPassed";
    private final String NUMOFFAILED_KW       = "NumOfFailed";
    private final String NUMOFINCONCLUSIVE_KW = "NumOfInconclusive";
    private final String TCRESULTS_KW         = "TcResults";
    private final String TIMESTAMP_KW         = "TimeStamp";
    private final String TESTSUITE_KW         = "TestSuite";
    private final String VERDICT_KW           = "Verdict";
    private final String COMMENT_KW           = "Comment";
    private final String LOGFILEPATH_KW       = "LogFilePath";

    // json results container
    private JSONObject tcResults = new JSONObject();

    public Map<String, String> status = new HashMap<>();


    /**
     *
     */
    public ReportEngine() {}


    private void closeFile() {
        try {
            //    		if (writer != null) {
            //    			writer.newLine();
            //    			writer.write(dashes, 0, dashes.length());
            //    			writer.newLine();
            //    			String verdicts = "// Verdicts: Passed: " + numPassed + " Failed: " + numFailed + " Inconclusive: " + numInconclusive;
            //    			writer.write(verdicts);
            //    			writer.newLine();
            //    			writer.write(dashes, 0, dashes.length());
            //    			writer.newLine();
            //    			writer.close();
            //    			if (numFailed == 0 && numInconclusive == 0 && numPassed == 0) {
            //    				Files.deleteIfExists(path);
            //    			}
            //    		}
            //    	} catch (IOException e) {
            //    		// TODO Auto-generated catch block
            //    		e.printStackTrace();
        }
        finally {
            numFailed = 0;
            numInconclusive = 0;
            numPassed = 0;
        }
    }


    public void onQuit() {
        closeFile();
    }


    @SuppressWarnings("unchecked")
    public void onResult(TcResult result, String tcLogName) {
        LOGGER.info("ReportEngine:checkMessage: announceVerdict");
        if (!result.sutName.equals(knownSut)) {
            try {
                doSutChanged(result.sutName);
            }
            catch (final IOException e) {
                e.printStackTrace();
            }
            knownSut = result.sutName;
        }

        final String testScheduleName = result.testScheduleName;
        final String testcase = result.testcase;
        final String verdict = result.verdict;
        final String verdictText = result.verdictText;

        // check if the result section already exists
        final JSONObject allResults = (JSONObject) tcResults.computeIfAbsent(TCRESULTS_KW, key -> new JSONObject());
        // check if the requested badge/testschedule section already exists
        final JSONObject testsuiteResults = (JSONObject) allResults.computeIfAbsent(testScheduleName, key -> new JSONObject());
        //check if the requested testcase section already exists
        final JSONArray testcaseResults = (JSONArray) testsuiteResults.computeIfAbsent(testcase, key -> new JSONArray());

        // add result
        final HashMap<String, String> resElement = new HashMap<>();
        resElement.put(TIMESTAMP_KW, new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZZZ").format(new Date()).toString());
        resElement.put(VERDICT_KW, verdict);
        resElement.put(COMMENT_KW, verdictText);
        resElement.put(LOGFILEPATH_KW, tcLogName);
        testcaseResults.add(resElement);

        // update result summary
        final JSONObject resSummary = (JSONObject) tcResults.get(VERDICTSUMMARY_KW);

        switch (verdict) {
            case failedStr:
                numFailed++;
                resSummary.put(NUMOFFAILED_KW, numFailed);
                break;
            case inconclusiveStr:
                numInconclusive++;
                resSummary.put(NUMOFINCONCLUSIVE_KW, numInconclusive);
                break;
            case passedStr:
                numPassed++;
                resSummary.put(NUMOFPASSED_KW, numPassed);
                break;
        }
        status.put(testcase, verdict);

        // update result file
        writeResultsToFile(resultFile);

    }


    private void doSutChanged(String sut) throws IOException {
        final SutPathsFiles sutPathsFiles = Factory.getSutPathsFiles();
        final String reportPath = sutPathsFiles.getReportPath(sut);
        File f = null;
        boolean bool = false;

        try {
            // returns pathnames for files and directory
            f = new File(reportPath);

            // create
            bool = f.mkdir();

            if (bool == true) {
                LOGGER.debug("Directory created: " + reportPath);
            }
        }
        catch (final Exception e) {
            // if any error occurs
            e.printStackTrace();
            System.out.print("Directory ERROR " + bool);
        }

        final String fName = baseFileName + ".json";
        resultFile = Paths.get(reportPath, fName);
        if (Files.notExists(resultFile)) {
            Files.createFile(resultFile);
            initializeResults(sut);
            writeResultsToFile(resultFile);
        }
        
        final String reportName = reportFileName + ".json";
        reportFile = Paths.get(reportPath, reportName);
        if (Files.notExists(reportFile)) {
            Files.createFile(reportFile);
        }

        // fill the internal JSON result object
        final JSONParser jparser = new JSONParser();
        try {
            tcResults = (JSONObject) jparser.parse(new String(Files.readAllBytes(resultFile)));
        }
        catch (final ParseException e) {
            LOGGER.error("Error reading//parsing the result file {}", resultFile.toString());
            e.printStackTrace();
        }

        // set the internal verdict counter
        numFailed = Integer.parseInt(((JSONObject) tcResults.get(VERDICTSUMMARY_KW)).get(NUMOFFAILED_KW).toString());
        numInconclusive = Integer.parseInt(((JSONObject) tcResults.get(VERDICTSUMMARY_KW)).get(NUMOFINCONCLUSIVE_KW).toString());
        numPassed = Integer.parseInt(((JSONObject) tcResults.get(VERDICTSUMMARY_KW)).get(NUMOFPASSED_KW).toString());
    }


    @SuppressWarnings("unchecked")
    private void initializeResults(String sutId) {
        final CmdListSuT sutList = Factory.createCmdListSut();
        sutList.execute();
        final SutDescription sutDesc = sutList.sutMap.get(sutId);
        if (sutDesc == null) {
            LOGGER.error("SuT not found: " + sutId);
            return;
        }

        setTestSystemHeader();
        setSutHeader(sutDesc);
        initializeVerdictSummary();

        tcResults.put(TCRESULTS_KW, new JSONObject());
    }


    private void setTestSystemHeader() {}


    @SuppressWarnings("unchecked")
    private void setSutHeader(final SutDescription sutDesc) {

        // insert the SuT header
        final JSONObject sutHeader = new JSONObject();
        sutHeader.put(SUTID_KW, sutDesc.ID);
        sutHeader.put(SUTNAME_KW, sutDesc.name);
        sutHeader.put(SUTREV_KW, sutDesc.version);
        sutHeader.put(SUTVENDOR_KW, sutDesc.vendor);

        tcResults.put(SUT_KW, sutHeader);
    }


    @SuppressWarnings("unchecked")
    private void initializeVerdictSummary() {
        final JSONObject verdictSummery = new JSONObject();
        verdictSummery.put(NUMOFPASSED_KW, 0);
        verdictSummery.put(NUMOFFAILED_KW, 0);
        verdictSummery.put(NUMOFINCONCLUSIVE_KW, 0);

        tcResults.put(VERDICTSUMMARY_KW, verdictSummery);
    }


    private void writeResultsToFile(final Path resultFilePath) {
        final Map<String, Object> options = new HashMap<>();
        options.put(JsonWriter.PRETTY_PRINT, true);
        options.put(JsonWriter.TYPE, false);

        try {
            LOGGER.debug("store TC result to file {}", resultFilePath.toString());
            Files.write(resultFilePath, JsonWriter.objectToJson(tcResults, options).getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
        }
        catch (final InvalidPathException e) {
            LOGGER.error("invalid path for TC parameter file: {}", resultFilePath.toString());
        }
        catch (final IOException e) {
            LOGGER.error("could not write result to file {}", resultFilePath.toString());
            e.printStackTrace();
        }
    }
}
