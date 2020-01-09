/* Copyright 2018, Johannes Mulder (Fraunhofer IOSB)

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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdListTestSuites.TestSuiteDescription;

public class CmdUpdateSUT {

    private static Logger logger = LoggerFactory.getLogger(CmdUpdateSUT.class);
    private SutDescription sutDescription = null;
	private static Map<String, URL[]> testsuiteURLs = new HashMap<>();
    private static CmdListBadges badges;
    private static CmdListTestSuites cmdListTestSuites;

	/**
	 *
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
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	private void createSUTid() {
		this.sutDescription.ID = this.sutDescription.name.replaceAll("\\W", "_");
	}

	/**
	 * This method will check the parameter values are different to those already in the CS.json file
	 *
	 * @param csJsonFileName the full name of the CS.json file
	 * @param tmpSutDescription the sut description to be tested
	 * @throws Exception in case of major error
	 * @return true if new value is different
	 */
	public boolean compareCSdata(String csJsonFileName, SutDescription tmpSutDescription) throws Exception {
		StringBuilder sb = new StringBuilder();
		File cs = new File(csJsonFileName);
		if (cs.exists() && cs.isFile()) {
			FileReader fr = null;
			try {
				fr = new FileReader(csJsonFileName);
				BufferedReader br = new BufferedReader(fr);
				String s;
				while((s = br.readLine()) != null) {
					sb.append(s);
				}
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("execute: IOException" + csJsonFileName);
			} finally {
				if (fr != null) {
					try {
						fr.close();
					} catch (IOException e) {
						e.printStackTrace();
						throw new Exception("execute: file close: IOException");
					}
				}
			}
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject;
			try {
				jsonObject = (JSONObject) jsonParser.parse(sb.toString());
				// get a String from the JSON object
				String oldSUTid = (String) jsonObject.get(CmdListSuT.ID);
				if (oldSUTid != null) {
					if (oldSUTid.equals(tmpSutDescription.ID) == false) {
						return true;
					}
				} else {
					return true;
				}

				// get a String from the JSON object
				String oldSUTname = (String) jsonObject.get(CmdListSuT.NAME);
				if (oldSUTname != null) {
					if (oldSUTname.equals(tmpSutDescription.name) == false) {
						return true;
					}
				} else {
					return true;
				}

				// get a String from the JSON object
				String oldSUTversion = (String) jsonObject.get(CmdListSuT.VERSION);
				if (oldSUTversion != null) {
					if (oldSUTversion.equals(tmpSutDescription.version) == false) {
						return true;
					}
				} else {
					return true;
				}

				// get a String from the JSON object
				String oldDescription = (String) jsonObject.get(CmdListSuT.DESCRIPTION);
				if (oldDescription != null) {
					if (oldDescription.equals(tmpSutDescription.description) == false) {
						return true;
					}
				} else {
					return true;
				}

                // get a String from the JSON object
                String oldVendor = (String) jsonObject.get(CmdListSuT.VENDOR);
				if (oldVendor != null) {
					if (oldVendor.equals(tmpSutDescription.vendor) == false) {
						return true;
					}
				} else {
					return true;
				}

                // get a String from the JSON object
                String oldSettingsDesignator = (String) jsonObject.get(CmdListSuT.SETTINGS_DESIGNATOR);
				if (oldSettingsDesignator != null) {
					if (oldSettingsDesignator.equals(tmpSutDescription.settingsDesignator) == false) {
						return true;
					}
				} else {
					return true;
				}

                // get a String from the JSON object
                String oldFederationName = (String) jsonObject.get(CmdListSuT.FEDERATION_NAME);
				if (oldFederationName != null) {
					if (oldFederationName.equals(tmpSutDescription.federation) == false) {
						return true;
					}
				} else {
					return true;
				}

                // get a String from the JSON object
                String oldFederateName = (String) jsonObject.get(CmdListSuT.FEDERATE_NAME);
				if (oldFederateName != null) {
					if (oldFederateName.equals(tmpSutDescription.sutFederateName) == false) {
						return true;
					}
				} else {
					return true;
				}

				// get badge files list from the JSON object
				JSONArray badgeArray = (JSONArray) jsonObject.get(CmdListSuT.BADGE);
				if (tmpSutDescription.badges != null) {
					if (badgeArray != null) {
						if (tmpSutDescription.badges.size() == badgeArray.size()) {
							if (tmpSutDescription.badges.size() > 0) {
								for (String entry : this.sutDescription.badges) {
									if (badgeArray.contains(entry)) {
										continue;
									}
									return true;
								}
							}
						} else {
							return true;
						}
					} else {
						badgeArray = new JSONArray();
					}
					boolean dataFound  = false;
					for (int i = 0; i < badgeArray.size(); i++) {
						for (String entry : this.sutDescription.badges) {
							if (entry.equals(badgeArray.get(i))) {
								dataFound = true;
								break;
							}
						}
						if (dataFound == false) {
							return true;
						}
					}
				} else {
					if (badgeArray.size() > 0) {
						return true;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new Exception("execute: Exception");
			}
		} else {
			if (cs.isDirectory()) {
				return false;
			} else {
				// New data
				return true;
			}
		}
		return false;
	}

	/**
	 *
	 * @param ts the required testsuite
	 */
	private static URL[] getTestsuiteUrls(final String ts) throws Exception {
		logger.trace(ts);
		URL[] myTestsuiteURLs = testsuiteURLs.get(ts);
		if (myTestsuiteURLs == null) {
			TestSuiteDescription tsd = cmdListTestSuites.testsuites.get(ts);
			if (tsd != null) {
				String ts_path = Factory.props.getProperty(Factory.IVCT_TS_DIST_HOME_ID);
				logger.trace(ts_path);
				if (tsd.tsLibTimeFolder != null) {
					String lib_path = ts_path + "/" + tsd.tsLibTimeFolder;
					logger.trace(lib_path);
					File dir = new File(lib_path);
					File[] filesList = dir.listFiles();
					if (filesList == null) {
						throw new Exception("getTestsuiteUrls: no testsuites found in: " + lib_path);
					}
					URL[] urls = new URL[filesList.length];
					for (int i = 0; i < filesList.length; i++) {
						try {
							urls[i] = filesList[i].toURI().toURL();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					testsuiteURLs.put(ts, urls);
					return urls;
				}
			} else {
				throw new Exception("getTestsuiteUrls: unknown testsuite: " + ts);
			}
		}
		return myTestsuiteURLs;
	}

	/**
	 * @throws Exception
	 *
	 */
	private static boolean checkFileExists(String testFileName) throws Exception {
		// If the desired file exists, do not overwrite
		File f = new File(testFileName);
		if (f.exists()) {
			if (f.isDirectory()) {
				throw new Exception("Target resource is a directory: " + testFileName);
			}
			return true;
		}
		return false;
	}

	/**
	 * This method will extract a resource from a known testsuite resource location to
	 * a specified directory
	 *
	 * @param ts the name of the testsuite of the resource required
	 * @param dirName the name of the directory where the resource should be copied to
	 * @param resourceName the name of the resource
	 * @return false means file already exists or was extracted - NO overwrite
	 *         true means error
	 */
	private static boolean extractResource(String ts, String dirName, String resourceName, String extraParamTemplates) throws Exception {

		// Check if dirName exists and is a directory
		File d = new File(dirName);
		if (d.exists() && !d.isDirectory()) {
			throw new Exception("Target directory does not exist or is not a directory: " + dirName);
		}

		String testFileName = new String(dirName + "/" + resourceName);
		if (checkFileExists(testFileName))
			return true;

		// Work through the list of badge jar/text url sources
		URL[] myTestsuiteURLs = getTestsuiteUrls(ts);
		if (myTestsuiteURLs == null) {
			throw new Exception("Unknown badge: " + ts);
		}
		for (int i = 0; i < myTestsuiteURLs.length; i++) {
			try {
				int pos = myTestsuiteURLs[i].getFile().lastIndexOf('/');
				if (resourceName.equals(myTestsuiteURLs[i].getFile().substring(pos + 1))) {
					java.io.InputStream is = new java.io.FileInputStream(myTestsuiteURLs[i].getFile());
					String outputFileString = new String(dirName + "/" + resourceName);
					java.io.FileOutputStream fo = new java.io.FileOutputStream(outputFileString);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = is.read(buffer)) > 0) {
						fo.write(buffer, 0, length);
					}
					fo.close();
					is.close();
					break;
				} else {
					pos = myTestsuiteURLs[i].getFile().lastIndexOf('.');
					if (".jar".equals(myTestsuiteURLs[i].getFile().substring(pos))) {
						if (extractFromJar(dirName, resourceName, myTestsuiteURLs[i].getFile(), extraParamTemplates)) {
							break;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new Exception("extractResource: IOException");
			}
		}

		return false;
	}

	/**
	 * This function will extract a named file from a named jar file and
	 * copy it to the destination directory
	 *
	 * @param destdir the destination directory
	 * @param extractFileName the required file name to be extracted
	 * @param jarFileName the name of the jar to search
	 * @param extraParamTemplates
	 * @return true if file found, false if file found
	 * @throws java.io.IOException if problem with the file.
	 */
	private static boolean extractFromJar(final String destdir, final String extractFileName, final String jarFileName, String extraParamTemplates) throws java.io.IOException {
		boolean found = false;
		FileInputStream fis = new FileInputStream(jarFileName);
        BufferedInputStream bis = new BufferedInputStream(fis);
        ZipInputStream stream = new ZipInputStream(bis);
		ZipEntry entry;
		while((entry = stream.getNextEntry()) != null)
		{
			if (!entry.isDirectory())
			{
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
			
			if (entry.getName().equals(extractFileName) == false) {
				continue;
			}
			found = true;

			java.io.File fl = new java.io.File(destdir, entry.getName());
			if(fl.exists()) {
				break;
			}
			if (!fl.exists())
			{
				fl.getParentFile().mkdirs();
				fl = new java.io.File(destdir, entry.getName());
			}
			if (entry.isDirectory())
			{
				continue;
			}
			getFileContents(destdir, stream, entry.getName());
		}
		bis.close();
		fis.close();
		return found;

	}
	
	private static void getFileContents(String destdir, ZipInputStream stream, String entry) throws IOException {
		File tempFile = new File(destdir + "/" + entry);
		if (tempFile.exists()) {
			return;
		}
		Path outDir = Paths.get(destdir);
		Path filePath = outDir.resolve(entry);
		java.io.FileOutputStream fos = new java.io.FileOutputStream(filePath.toFile());
		byte[] buffer = new byte[1024];
		BufferedOutputStream bos = new BufferedOutputStream(fos, buffer.length);
		int length;
		while ((length = stream.read(buffer)) > 0) {
			bos.write(buffer, 0, length);
		}
		bos.close();
		fos.close();
	}

	/**
	 * Not all badges refer to a test suite with TcParams: some
	 * badges are only containers, thus take only badges with TcParams.json
	 * files.
	 *
	 * @param testsuites the set of test suites
	 * @param badge the current badge name being processed
	 */
	void buildTestsuiteSet(Set<String> testsuites, final String badge) {
		BadgeDescription bd = badges.badgeMap.get(badge);
		if (bd != null) {
			for (String entry : bd.dependency) {
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
		if (f.exists() == false) {
			if (f.mkdir() == false) {
				logger.error("Failed to create directory: " + sutDir);
			}
		}

        Set<String> badges_list = new HashSet<>();
		// Check if no badges
		if (!this.sutDescription.badges.isEmpty()) {

	        Set<String> ir_set = new HashSet <String>();

	        // Get IRs for badges
	        badges.collectIrForCs(ir_set, this.sutDescription.badges);

			// For each badge, check if there is a testsuite with TcParams
	        Set<TestSuiteDescription> tss = new HashSet <TestSuiteDescription>();
			for (String ir : ir_set) {
				TestSuiteDescription ts;
				ts = this.cmdListTestSuites.getTestSuiteforIr(ir);
				if (ts != null) {
					tss.add(ts);
				}
			}


	        Set<String> csTs = new HashSet<String>();
	        for (TestSuiteDescription entry : tss) {
	        	if (csTs.contains(entry)) {
	        		continue;
	        	}
	        	csTs.add(entry.id);
	        }


			// For each test suite copy or modify the TcParam.json file
			for (String testsuite : csTs) {
				// Add badge folder
				String sutBadge = sutDir + "/" + testsuite;
				f = new File(sutBadge);
				if (f.exists() == false) {
					if (f.mkdir() == false) {
						logger.trace("Failed to create directory!");
					}
				}

				// This is the file to copy
				extractResource(testsuite, sutBadge, "TcParam.json", "ExtraParamTemplates");
			}
		}

		// If CS.json exists, only change what is different
		boolean dataChanged = false;
		String csJsonFileName = new String(sutDir + "/" + "CS.json");
		dataChanged = compareCSdata(csJsonFileName, this.sutDescription);

		if (dataChanged == false) {
			return this.sutDescription.ID;
		}

		// Update CS.json file
        JSONObject obj = new JSONObject();
        obj.put(CmdListSuT.ID, this.sutDescription.ID);
        obj.put(CmdListSuT.NAME, this.sutDescription.name);
        if (this.sutDescription.version == null) {
            obj.put(CmdListSuT.VERSION, "");
        } else {
        	obj.put(CmdListSuT.VERSION, this.sutDescription.version);
        }

        if (this.sutDescription.description == null) {
        	obj.put(CmdListSuT.DESCRIPTION, "");
        } else {
        	obj.put(CmdListSuT.DESCRIPTION, this.sutDescription.description);
        }

        if (this.sutDescription.vendor == null) {
        	obj.put(CmdListSuT.VENDOR, "");
        } else {
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
        if (this.sutDescription.badges.size() != 0) {
            for (String entry : this.sutDescription.badges) {
                list.add(entry);
        	}
        }

        obj.put("badge", list);

        FileWriter fw = null;
        try {
        	fw = new FileWriter(sutDir + "/" + "CS.json");
        	fw.write(obj.toJSONString());
        	fw.flush();

        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	if (fw != null) {
        		try {
        			fw.close();
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        	}
        }

		return this.sutDescription.ID;
	}
}
