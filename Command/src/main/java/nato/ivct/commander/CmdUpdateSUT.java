/* Copyright 2020, Reinhard Herzog, Johannes Mulder, Michael Theis, Felix Schoeppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;


public class CmdUpdateSUT {

    private static Logger             logger         = LoggerFactory.getLogger(CmdUpdateSUT.class);
    private SutDescription            sutDescription = null;
    private static Map<String, URL[]> testsuiteURLs  = new HashMap<>();
    private static CmdListBadges      badges;
    private static CmdListTestSuites  cmdListTestSuites;

    /**
     * @param sutDescription the SUT description
     */
    public CmdUpdateSUT(final SutDescription sutDescription) {
        this.sutDescription = sutDescription;
        if (this.sutDescription.ID == null || this.sutDescription.ID.isEmpty()) {
            createSUTid();
        }
        if (this.sutDescription.name == null) {
            this.sutDescription.name = this.sutDescription.ID;
        }
        this.sutDescription.badges = sutDescription.badges == null || sutDescription.badges.isEmpty() ? new HashSet<>() : sutDescription.badges;

        // get the badge descriptions
        badges = Factory.createCmdListBadges();
        badges.execute();

        // get testsuite descriptions
        cmdListTestSuites = new CmdListTestSuites();
        try {
            cmdListTestSuites.execute();
        }
        catch (Exception exc) {
            logger.error("", exc);
        }
    }


    private void createSUTid() {
        String sutId = this.sutDescription.name.replaceAll("\\W", "_");

        String sutsDir = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);

        int i = 0;
        // create a unique folder name
        String folderName = sutId;
        while (Files.exists(Paths.get(sutsDir + "\\" + folderName)) && Files.isDirectory(Paths.get(sutsDir + "\\" + folderName))) {
            folderName = sutId + "(" + i++ + ")";
        }

        this.sutDescription.ID = folderName;

    }


    /**
     * This method will check the parameter values are different to those already in
     * the CS.json file
     *
     * @param csJsonFileName the full name of the CS.json file
     * @param tmpSutDescription the sut description to be tested
     * @throws IOException in case of file access error
     * @throws org.json.simple.parser.ParseException in case of json parse error
     * @return true if new value is different
     */
    public boolean compareCSdata(String csJsonFileName, SutDescription tmpSutDescription) throws IOException, org.json.simple.parser.ParseException {
        StringBuilder sb = new StringBuilder();
        File cs = new File(csJsonFileName);
        if (cs.exists() && cs.isFile()) {
            try (BufferedReader fr = Files.newBufferedReader(cs.toPath())) {
                String s;
                while ((s = fr.readLine()) != null) {
                    sb.append(s);
                }
            }

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(sb.toString());
            // get a String from the JSON object
            String oldSUTid = (String) jsonObject.get(CmdListSuT.ID);
            if (oldSUTid != null) {
                if (!oldSUTid.equals(tmpSutDescription.ID)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldSUTname = (String) jsonObject.get(CmdListSuT.NAME);
            if (oldSUTname != null) {
                if (!oldSUTname.equals(tmpSutDescription.name)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldSUTversion = (String) jsonObject.get(CmdListSuT.VERSION);
            if (oldSUTversion != null) {
                if (!oldSUTversion.equals(tmpSutDescription.version)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldDescription = (String) jsonObject.get(CmdListSuT.DESCRIPTION);
            if (oldDescription != null) {
                if (!oldDescription.equals(tmpSutDescription.description)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldVendor = (String) jsonObject.get(CmdListSuT.VENDOR);
            if (oldVendor != null) {
                if (!oldVendor.equals(tmpSutDescription.vendor)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldSettingsDesignator = (String) jsonObject.get(CmdListSuT.SETTINGS_DESIGNATOR);
            if (oldSettingsDesignator != null) {
                if (!oldSettingsDesignator.equals(tmpSutDescription.settingsDesignator)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldFederationName = (String) jsonObject.get(CmdListSuT.FEDERATION_NAME);
            if (oldFederationName != null) {
                if (!oldFederationName.equals(tmpSutDescription.federation)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get a String from the JSON object
            String oldFederateName = (String) jsonObject.get(CmdListSuT.FEDERATE_NAME);
            if (oldFederateName != null) {
                if (!oldFederateName.equals(tmpSutDescription.sutFederateName)) {
                    return true;
                }
            }
            else {
                return true;
            }

            // get badge files list from the JSON object
            JSONArray badgeArray = (JSONArray) jsonObject.get(CmdListSuT.BADGE);
            if (tmpSutDescription.badges != null) {
                if (badgeArray != null) {
                    if (tmpSutDescription.badges.size() == badgeArray.size()) {
                        if (!tmpSutDescription.badges.isEmpty()) {
                            for (String entry: this.sutDescription.badges) {
                                if (badgeArray.contains(entry)) {
                                    continue;
                                }
                                return true;
                            }
                        }
                    }
                    else {
                        return true;
                    }
                }
                else {
                    badgeArray = new JSONArray();
                }
                boolean dataFound = false;
                for (int i = 0; i < badgeArray.size(); i++) {
                    for (String entry: this.sutDescription.badges) {
                        if (entry.equals(badgeArray.get(i))) {
                            dataFound = true;
                            break;
                        }
                    }
                    if (!dataFound) {
                        return true;
                    }
                }
            }
            else {
                if (!badgeArray.isEmpty()) {
                    return true;
                }
            }
        }
        else {
            if (cs.isDirectory()) {
                return false;
            }
            else {
                // New data
                return true;
            }
        }
        return false;
    }


    /**
     * @param ts the required testsuite
     */
    private static URL[] getTestsuiteUrls(final String ts) throws IOException {
        logger.trace(ts);
        URL[] myTestsuiteURLs = testsuiteURLs.get(ts);
        if (myTestsuiteURLs == null) {
            TestSuiteDescription tsd = cmdListTestSuites.testsuites.get(ts);
            if (tsd != null) {
                String tsPath = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID);
                logger.trace(tsPath);
                if (tsd.tsLibTimeFolder != null) {
                    String libPath = tsPath + "/" + tsd.tsLibTimeFolder;
                    logger.trace(libPath);
                    File dir = new File(libPath);
                    File[] filesList = dir.listFiles();
                    if (filesList == null) {
                        throw new IOException("getTestsuiteUrls: no testsuites found in: " + libPath);
                    }
                    URL[] urls = new URL[filesList.length];
                    for (int i = 0; i < filesList.length; i++) {
                        try {
                            urls[i] = filesList[i].toURI().toURL();
                        }
                        catch (MalformedURLException exc) {
                            logger.error("", exc);
                        }
                    }
                    testsuiteURLs.put(ts, urls);
                    return urls;
                }
            }
            else {
                throw new IOException("getTestsuiteUrls: unknown testsuite: " + ts);
            }
        }
        return myTestsuiteURLs;
    }

    private static boolean checkFileExists(String testFileName) throws IOException {
        // If the desired file exists, do not overwrite
        File f = new File(testFileName);
        if (f.exists()) {
            if (f.isDirectory()) {
                throw new IOException("Target resource is a directory: " + testFileName);
            }
            return true;
        }
        return false;
    }


    /**
     * This method will extract a resource from a known testsuite resource location
     * to a specified directory.
     *
     * @param ts the name of the testsuite of the resource required
     * @param dirName the name of the directory where the resource should be copied
     *            to
     * @param resourceName the name of the resource
     * @return {@code false} means file already exists or was extracted - NO overwrite {@code true}
     *         means error
     */
    private static boolean extractResource(final String ts, final String dirName, final String resourceName, final String extraParamTemplates) throws Exception {

        // Check if dirName exists and is a directory
        File d = new File(dirName);
        if (d.exists() && !d.isDirectory()) {
            throw new Exception("Target directory does not exist or is not a directory: " + dirName);
        }

        String testFileName = dirName + "/" + resourceName;
        if (checkFileExists(testFileName))
            return true;

        // Work through the list of badge jar/text url sources
        URL[] myTestsuiteURLs = getTestsuiteUrls(ts);
        if (myTestsuiteURLs == null) {
            throw new Exception("Unknown badge: " + ts);
        }
        for (int i = 0; i < myTestsuiteURLs.length; i++) {
            int pos = myTestsuiteURLs[i].getFile().lastIndexOf('/');
            if (resourceName.equals(myTestsuiteURLs[i].getFile().substring(pos + 1))) {
                String outputFileString = dirName + "/" + resourceName;
                try (java.io.InputStream is = new java.io.FileInputStream(myTestsuiteURLs[i].getFile()); java.io.FileOutputStream fo = new java.io.FileOutputStream(outputFileString);) {
                    IOUtils.copy(is, fo);
                    break;
                }
            }
            else {
                pos = myTestsuiteURLs[i].getFile().lastIndexOf('.');
                if (".jar".equals(myTestsuiteURLs[i].getFile().substring(pos))) {
                    if (extractFromJar(dirName, resourceName, myTestsuiteURLs[i].getFile(), extraParamTemplates)) {
                        break;
                    }
                }
            }
        }

        return false;
    }


    /**
     * This function will extract a named file from a named jar file and copy it to
     * the destination directory
     *
     * @param destdir the destination directory
     * @param extractFileName the required file name to be extracted
     * @param jarFileName the name of the jar to search
     * @param extraParamTemplates
     * @return true if file found, false if file found
     * @throws java.io.IOException if problem with the file.
     */
    private static boolean extractFromJar(final String destdir, final String extractFileName, final String jarFileName, String extraParamTemplates) throws IOException {
        boolean found = false;

        try (ZipInputStream stream = new ZipInputStream(new BufferedInputStream(new FileInputStream(jarFileName)))) {
            ZipEntry entry;
            while ((entry = stream.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    if (entry.getName().startsWith(extraParamTemplates)) {
                        String s = entry.getName();
                        int pos = s.lastIndexOf('/');
                        if (pos > 0) {
                            s = entry.getName().substring(pos + 1);
                        }
                        getFileContents(destdir, stream, s);
                        continue;
                    }
                }

                if (!entry.getName().equals(extractFileName)) {
                    continue;
                }
                found = true;

                final File fl = new File(destdir, entry.getName());
                if (fl.exists()) {
                    break;
                }
                if (!fl.exists()) {
                    fl.getParentFile().mkdirs();
                }
                if (entry.isDirectory()) {
                    continue;
                }
                getFileContents(destdir, stream, entry.getName());
            }
        }
        return found;

    }


    private static void getFileContents(String destdir, ZipInputStream stream, String entry) throws IOException {
        File tempFile = new File(destdir + "/" + entry);
        if (tempFile.exists()) {
            return;
        }
        Path outDir = Paths.get(destdir);
        Path filePath = outDir.resolve(entry);
        byte[] buffer = new byte[1024];
        try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(filePath), buffer.length)) {
            int length;
            while ((length = stream.read(buffer)) > 0) {
                bos.write(buffer, 0, length);
            }
        }
    }


    /**
     * Not all badges refer to a test suite with TcParams: some badges are only
     * containers, thus take only badges with TcParams.json files.
     *
     * @param testsuites the set of test suites
     * @param badge the current badge name being processed
     */
    void buildTestsuiteSet(Set<String> testsuites, final String badge) {
        BadgeDescription bd = badges.badgeMap.get(badge);
        if (bd != null) {
            for (String entry: bd.dependency) {
                buildTestsuiteSet(testsuites, entry);
            }
        }
    }


    @SuppressWarnings("unchecked")
    public String execute() throws Exception {
        // Do not use a null ID
        if (this.sutDescription.ID == null) {
            return this.sutDescription.ID;
        }

        // The SUT is placed in a known folder
        String sutsDir = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
        String sutDir = sutsDir + "/" + this.sutDescription.ID;
        File f = new File(sutDir);

        if (!f.exists() && !f.mkdir()) {
            logger.error("Failed to create directory: {}", sutDir);
        }

        // Check if no badges
        if (!this.sutDescription.badges.isEmpty()) {

            Set<String> irSet = new HashSet<>();

            // Get IRs for badges
            badges.collectIrForCs(irSet, this.sutDescription.badges);

            // For each badge, check if there is a testsuite with TcParams
            Set<TestSuiteDescription> tss = new HashSet<>();
            for (String ir: irSet) {
                TestSuiteDescription ts;
                ts = cmdListTestSuites.getTestSuiteforIr(ir);
                if (ts != null) {
                    tss.add(ts);
                }
            }

            Set<String> csTs = new HashSet<>();
            for (TestSuiteDescription entry: tss) {
                csTs.add(entry.id);
            }

            // For each test suite copy or modify the TcParam.json file
            for (String testsuite: csTs) {
                // Add badge folder
                f = Paths.get(sutDir, testsuite).toFile();
                if (!f.exists() && !f.mkdir()) {
                    logger.trace("Failed to create directory!");
                }

                // This is the file to copy
                extractResource(testsuite, f.getPath(), "TcParam.json", "ExtraParamTemplates");
            }
        }

        // If CS.json exists, only change what is different
        boolean dataChanged = false;
        String csJsonFileName = sutDir + '/' + "CS.json";
        dataChanged = compareCSdata(csJsonFileName, this.sutDescription);

        if (!dataChanged) {
            return this.sutDescription.ID;
        }

        // Update CS.json file
        JSONObject obj = new JSONObject();
        obj.put(CmdListSuT.ID, this.sutDescription.ID);
        obj.put(CmdListSuT.NAME, this.sutDescription.name);
        if (this.sutDescription.version == null) {
            obj.put(CmdListSuT.VERSION, "");
        }
        else {
            obj.put(CmdListSuT.VERSION, this.sutDescription.version);
        }

        if (this.sutDescription.description == null) {
            obj.put(CmdListSuT.DESCRIPTION, "");
        }
        else {
            obj.put(CmdListSuT.DESCRIPTION, this.sutDescription.description);
        }

        if (this.sutDescription.vendor == null) {
            obj.put(CmdListSuT.VENDOR, "");
        }
        else {
            obj.put(CmdListSuT.VENDOR, this.sutDescription.vendor);
        }

        // check for defaults
        if (this.sutDescription.settingsDesignator == null) {
            this.sutDescription.settingsDesignator = Factory.SETTINGS_DESIGNATOR_DEFLT;
        }
        obj.put(CmdListSuT.SETTINGS_DESIGNATOR, this.sutDescription.settingsDesignator);

        if (this.sutDescription.federation == null) {
            this.sutDescription.federation = Factory.FEDERATION_NAME_DEFLT;
        }
        obj.put(CmdListSuT.FEDERATION_NAME, this.sutDescription.federation);

        if (sutDescription.sutFederateName == null) {
            sutDescription.sutFederateName = Factory.FEDERATE_NAME_DEFLT;
        }
        obj.put(CmdListSuT.FEDERATE_NAME, this.sutDescription.sutFederateName);

        JSONArray list = new JSONArray();
        if (!this.sutDescription.badges.isEmpty()) {
            for (String entry: this.sutDescription.badges) {
                list.add(entry);
            }
        }

        obj.put("badge", list);

        try (FileWriter fw = new FileWriter(sutDir + "/" + "CS.json")) {
            fw.write(obj.toJSONString());
            fw.flush();

        }
        catch (IOException exc) {
            logger.error("", exc);
        }

        return this.sutDescription.ID;
    }


    public static boolean deleteSut(final String sutId) {

        String sutHome = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
        File file = Paths.get(sutHome, sutId).toFile();

        if (!file.exists() || !file.isDirectory()) {
            return false;
        }

        try {
            FileUtils.deleteDirectory(file);
            logger.info("SuT successfully deleted {}", sutId);
            return true;
        }
        catch (IOException exc) {
            logger.error("Error when deleting SuT " + sutId, exc);
        }
        return false;
    }
}
