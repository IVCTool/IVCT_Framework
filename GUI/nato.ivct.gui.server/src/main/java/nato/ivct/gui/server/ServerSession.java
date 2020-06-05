/* Copyright 2020, Reinhard Herzog, Johannes Mulder, Michael Theis, Felix Schöppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.server;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.scout.rt.platform.job.IFuture;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.server.AbstractServerSession;
import org.eclipse.scout.rt.server.session.ServerSessionProvider;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListBadges;
import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdListTestSuites;
import nato.ivct.commander.CmdSetLogLevel;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.Factory;


/**
 * <h3>{@link ServerSession}</h3>
 *
 * @author hzg
 */
public class ServerSession extends AbstractServerSession {

    private static final Pattern RESULT_EXP   = Pattern.compile("^.*?:\\s+(.*?)\\s+(.*?)\\s.*?([^()\\s]*?/[^()\\s]*?\\.log)?\\)?\\s*$");

    private static final String RESULTS_FILE_NAME = "Results";
    private static final String RESULTS_FILE_EXT = "json";

    private static IFuture<CmdListSuT>             loadSuTJob;
    private static IFuture<SutTcResultDescription> loadTcResultsJob;
    private static IFuture<CmdListBadges>          loadBadgesJob;
    private static IFuture<CmdListTestSuites>      loadTestSuitesJob;
    private static IFuture<CmdStartTc>             startTcJobs;

    public class SutTcResultDescription {

        /*
         * Result map of the form (sutId, (tsId, (logFile, verdict))). All elements are
         * from type String
         */
        public HashMap<String, HashMap<String, HashMap<String, String>>> sutResultMap = new HashMap<>();
    }

    /*
     * Load SuT descriptions job
     */
    public class LoadSuTdescriptions implements Callable<CmdListSuT> {

        @Override
        public CmdListSuT call() throws Exception {
            CmdListSuT sut;
            sut = Factory.createCmdListSut();
            sut.execute();
            return sut;
        }
    }

    /*
     * Load Badge descriptions job
     */
    public class LoadBadgeDescriptions implements Callable<CmdListBadges> {

        @Override
        public CmdListBadges call() throws Exception {
            CmdListBadges badges;
            badges = Factory.createCmdListBadges();
            badges.execute();
            return badges;
        }
    }

    /*
     * Load Test suite descriptions job
     */
    public class LoadTestSuiteDescriptions implements Callable<CmdListTestSuites> {

        @Override
        public CmdListTestSuites call() throws Exception {
            CmdListTestSuites testsuites;
            testsuites = Factory.createCmdListTestSuites();
            testsuites.execute();
            return testsuites;
        }

    }

    /*
     * Load TC execution results
     */
    public class LoadTcResults implements Callable<SutTcResultDescription> {

        @Override
        public SutTcResultDescription call() throws Exception {
            final SutTcResultDescription sutTcResults = new SutTcResultDescription();

            // get SuT list
            final List<String> sutList = Factory.getSutPathsFiles().getSuts();
            // iterate over all SuTs to get its report files
            sutList.forEach(sutId -> {
                final List<String> resultFiles = Factory.getSutPathsFiles().getSutReportFileNames(sutId, true);
                //parse each report file to get the verdict and the corresponding log file name
                resultFiles.forEach(resultFile -> {
                    if ((RESULTS_FILE_NAME + "." + RESULTS_FILE_EXT).equalsIgnoreCase(Paths.get(resultFile).getFileName().toString())) {
                        parseJsonResultFile(sutId, resultFile, sutTcResults);
                    }
                });
            });

            return sutTcResults;
        }


        @SuppressWarnings("unchecked")
        private void parseJsonResultFile(final String sutId, final String resultFile, final SutTcResultDescription sutTcResults) {
            final String TCRESULTS_KW = "TcResults";
            final String VERDICT_KW = "Verdict";
            final String LOGFILEPATH_KW = "LogFilePath";

            final JSONParser jparser = new JSONParser();
            JSONObject tcResults = null;
            try {
                tcResults = (JSONObject) jparser.parse(new String(Files.readAllBytes(Paths.get(resultFile))));
            }
            catch (ParseException | IOException exc) {
                LOG.error("Error reading//parsing the result file {}", resultFile);
                return;
            }

            final JSONObject results = (JSONObject) tcResults.get(TCRESULTS_KW);
            results.forEach((badgeId, badgeRes) -> {
                final List<String> logFiles = Factory.getSutPathsFiles().getSutLogFileNames(sutId, (String) badgeId);
                ((JSONObject) badgeRes).forEach((tcId, tcRes) -> {
                    ((JSONArray) tcRes).forEach(result -> {
                        try {
                            final String verdict = (String) ((JSONObject) result).get(VERDICT_KW);
                            final String logFileName = (String) ((JSONObject) result).get(LOGFILEPATH_KW);

                            // get the matching log file from the list
                            final Optional<String> matchingFileName = logFiles.stream().filter(fileName -> fileName.contains(logFileName)).findFirst();
                            if (matchingFileName.isPresent()) {
                                // add the match to the result map
                                sutTcResults.sutResultMap.computeIfAbsent(sutId, k -> new HashMap<>()).computeIfAbsent((String) badgeId, k -> new HashMap<>()).put(matchingFileName.get(), verdict);
                            }
                        }
                        catch (final ClassCastException exc) {
                            LOG.info("Illegal result: {}", result.toString());
                            return;
                        }

                    });
                });
            });
        }


        /*
         * Parse the verdict string of a report file
         * @param line the line to parse
         * @return a String array with 4 element with: [0]: extended test case name [1]:
         * verdict [2]: test suite (or badge) id [3]: log file name
         */
        private String[] parseVerdictLine(final String line) {
            final Matcher matcher = RESULT_EXP.matcher(line);

            return matcher.matches() ? matcher.replaceAll("$1 $2 $3").replace('/', ' ').split(" ") : new String[0];
        }
    }

    public void updateSutResultMap(final String sutId, final String tsId, final String tcFullName) {
        LOG.info("reload test results for all SuTs");
        loadTcResultsJob = Jobs.schedule(new LoadTcResults(), Jobs.newInput());
    }

    /*
     * Execute test case job
     */
    public class ExecuteTestCase implements Callable<CmdStartTc> {
        private final String sut;
        private final String tc;
        private final String badge;
        private final String settingsDesignator;
        private final String federationName;
        private final String federateName;

        public ExecuteTestCase(String sut, String tc, String badge, String settingsDesignator, String federationName, String federateName) {
            this.sut = sut;
            this.tc = tc;
            this.badge = badge;
            this.settingsDesignator = settingsDesignator;
            this.federationName = federationName;
            this.federateName = federateName;
        }


        @Override
        public CmdStartTc call() throws Exception {
            final CmdStartTc tcCmd = Factory.createCmdStartTc(sut, badge, tc, settingsDesignator, federationName, federateName);
            tcCmd.execute();
            return null;
        }

    }

    public class ExecuteSetLogLevel implements Callable<CmdSetLogLevel> {

        private LogLevel logLevel;

        public ExecuteSetLogLevel(String level) {
            switch (level == null ? "" : level) {
                case "debug":
                    logLevel = LogLevel.DEBUG;
                    break;
                case "info":
                    logLevel = LogLevel.INFO;
                    break;
                case "warn":
                    logLevel = LogLevel.WARNING;
                    break;
                case "error":
                    logLevel = LogLevel.ERROR;
                    break;
                case "trace":
                default:
                    logLevel = LogLevel.TRACE;
                    break;
            }
        }


        @Override
        public CmdSetLogLevel call() throws Exception {
            final CmdSetLogLevel setCmd = Factory.createCmdSetLogLevel(logLevel);
            setCmd.execute();
            return null;
        }

    }

    private static final long   serialVersionUID = 1L;
    private static final Logger LOG              = LoggerFactory.getLogger(ServerSession.class);

    public ServerSession() {
        super(true);
    }


    /**
     * @return The {@link ServerSession} which is associated with the current
     *         thread, or {@code null} if not found.
     */
    public static ServerSession get() {
        return ServerSessionProvider.currentSession(ServerSession.class);
    }


    @Override
    protected void execLoadSession() {
        LOG.info("created a new session for {}", getUserId());

        LOG.info("load SuT Information");
        loadSuTJob = Jobs.schedule(new LoadSuTdescriptions(), Jobs.newInput());

        LOG.info("load test results for all SuTs");
        loadTcResultsJob = Jobs.schedule(new LoadTcResults(), Jobs.newInput());

        LOG.info("load Badge and Interoperatbility Requirements Descriptions");
        loadBadgesJob = Jobs.schedule(new LoadBadgeDescriptions(), Jobs.newInput());

        LOG.info("load Testsuite Descriptions");
        loadTestSuitesJob = Jobs.schedule(new LoadTestSuiteDescriptions(), Jobs.newInput());
    }


    public IFuture<CmdListSuT> getLoadSuTJob() {
        return loadSuTJob;
    }


    public IFuture<CmdListBadges> getLoadBadgesJob() {
        return loadBadgesJob;
    }


    public IFuture<CmdListTestSuites> getLoadTestSuitesJob() {
        return loadTestSuitesJob;
    }


    public IFuture<SutTcResultDescription> getLoadTcResultsJob() {
        return loadTcResultsJob;
    }


    public void execStartTc(String sut, String tc, String badge, String settingsDesignator, String federationName, String federateName) {
        LOG.info("starting test case");
        startTcJobs = Jobs.schedule(new ExecuteTestCase(sut, tc, badge, settingsDesignator, federationName, federateName), Jobs.newInput());
    }


    public void setLogLevel(String level) {
        LOG.info("set log level");
        Jobs.schedule(new ExecuteSetLogLevel(level), Jobs.newInput());
    }
}
