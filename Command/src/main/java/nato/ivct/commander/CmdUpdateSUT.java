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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmdUpdateSUT implements Command {

    private static Logger logger = LoggerFactory.getLogger(CmdUpdateSUT.class);
	private String sutId;
	private String sutDescription;
	private String vendorName;
	private BadgeTcParam[] badgeTcParams;
	private static Map<String, URL[]> badgeURLs = new HashMap<String, URL[]>();
	private static CmdListBadges badges;

	/**
	 * 
	 * @param sutId the SUT identifier
	 * @param sutDescription the SUT description
	 * @param vendorName the vendor name
	 * @param badgeTcParams the full list of badge names and optionally TcParams
	 */
	public CmdUpdateSUT(final String sutId, final String sutDescription, final String vendorName, final BadgeTcParam[] badgeTcParams) {
		this.sutId = sutId;
		this.sutDescription = sutDescription;
		this.vendorName = vendorName;
		this.badgeTcParams = badgeTcParams;
		// get the badge descriptions
		badges = new CmdListBadges();
		badges.execute();
	}

	/**
	 * This method will check the parameter values are different to those already in the CS.json file
	 * 
	 * @param csJsonFileName the full name of the CS.json file
	 * @param sutId the new sut identifier
	 * @param sutDescription the new sut description
	 * @param vendorName the new vendor name
	 * @param badgeTcParams the new list of badges supported
	 * @return true means some data is different, false means all data is the same
	 * @throws Exception in case of major error
	 */
	public static boolean compareCSdata(String csJsonFileName, String sutId, String sutDescription, String vendorName, BadgeTcParam[] badgeTcParams) throws Exception {
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
				String oldSUTname = (String) jsonObject.get("id");
				if (oldSUTname != null) {
					if (oldSUTname.equals(sutId) == false) {
						return true;
					}
				}
				// get a String from the JSON object
				String oldDescription = (String) jsonObject.get("description");
				if (oldDescription != null) {
					if (oldDescription.equals(sutDescription) == false) {
						return true;
					}
				}
				// get a String from the JSON object
				String oldVendor = (String) jsonObject.get("vendor");
				if (oldVendor != null) {
					if (oldVendor.equals(vendorName) == false) {
						return true;
					}
				}
				// get badge files list from the JSON object
				JSONArray badgeArray = (JSONArray) jsonObject.get("badge");
				if (badgeTcParams != null) {
					if (badgeArray != null) {
						if (badgeTcParams.length == badgeArray.size()) {
							if (badgeTcParams.length > 0) {
								for (int i = 0; i < badgeTcParams.length; i++) {
									if (badgeArray.contains(badgeTcParams[i].id)) {
										continue;
									}
									return true;
								}
							}
						} else {
							return true;
						}
					}
					boolean dataFound  = false;
					for (int i = 0; i < badgeArray.size(); i++) {
						for (int j = 0; j < badgeTcParams.length; j++) {
							if (badgeTcParams[j].id.equals(badgeArray.get(i))) {
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
	 * @param badge the required badge
	 */
	private static URL[] getBadgeUrls(final String badge) throws Exception {
		logger.trace(badge);
		URL[] myBadgeURLs = badgeURLs.get(badge);
		if (myBadgeURLs == null) {
			BadgeDescription bd = badges.badgeMap.get(badge);
			if (bd != null) {
				String ts_path = Factory.props.getProperty(Factory.IVCT_TS_HOME_ID);
				logger.trace(ts_path);
				if (bd.tsLibTimeFolder != null) {
					String lib_path = ts_path + "/" + bd.tsLibTimeFolder;
					logger.trace(lib_path);
					File dir = new File(lib_path);
					File[] filesList = dir.listFiles();
					URL[] urls = new URL[filesList.length];
					for (int i = 0; i < filesList.length; i++) {
						try {
							urls[i] = filesList[i].toURI().toURL();
						} catch (MalformedURLException e) {
							e.printStackTrace();
						}
					}
					badgeURLs.put(badge, urls);
					return urls;
				}
			} else {
				throw new Exception("getBadgeUrls unknown badge: " + badge);
			}
		}
		return null;
	}

	/**
	 * This method will extract a resource from a known badge resource location to
	 * a specified directory
	 * 
	 * @param badge the name of the badge of the resource required
	 * @param dirName the name of the directory where the resource should be copied to
	 * @param resourceName the name of the resource
	 * @return false means file already exists or was extracted - NO overwrite
	 *         true means error
	 */
	private static boolean extractResource(String badge, String dirName, String resourceName) throws Exception {

		// Check if dirName exists and is a directory
		File d = new File(dirName);
		if (d.exists() && !d.isDirectory()) {
			throw new Exception("Target directory does not exist or is not a directory: " + dirName);
		}

		// If the desired file exists, do not overwrite
		File f = new File(dirName + "/" + resourceName);
		if (f.exists()) { 
			if (f.isDirectory()) {
				throw new Exception("Target resource is a directory: " + dirName + "/" + resourceName);
			}
			return false;
		}

		// Work through the list of badge jar/text url sources
		URL[] myBadgeURLs = getBadgeUrls(badge);
		if (myBadgeURLs == null) {
			throw new Exception("Unknown badge: " + badge);
		}
		for (int i = 0; i < myBadgeURLs.length; i++) {
			try {
				int pos = myBadgeURLs[i].getFile().lastIndexOf('/');
				if (resourceName.equals(myBadgeURLs[i].getFile().substring(pos + 1))) {
					java.io.InputStream is = new java.io.FileInputStream(myBadgeURLs[i].getFile());
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
					pos = myBadgeURLs[i].getFile().lastIndexOf('.');
					if (".jar".equals(myBadgeURLs[i].getFile().substring(pos))) {
						if (extractFromJar(dirName, resourceName, myBadgeURLs[i].getFile())) {
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
	 * @return true if file found, false if file found
	 * @throws java.io.IOException if problem with the file.
	 */
	private static boolean extractFromJar(final String destdir, final String extractFileName, final String jarFileName) throws java.io.IOException {
		boolean found = false;
		java.util.jar.JarFile jarfile = new java.util.jar.JarFile(new java.io.File(jarFileName)); //jar file path(here sqljdbc4.jar)
		java.util.Enumeration<java.util.jar.JarEntry> enu= jarfile.entries();
		while(enu.hasMoreElements())
		{
			java.util.jar.JarEntry je = enu.nextElement();

			logger.trace(je.getName());
			if (je.getName().equals(extractFileName) == false) {
				continue;
			}
			logger.trace("GOT THE FILE");
			found = true;

			java.io.File fl = new java.io.File(destdir, je.getName());
			if(fl.exists()) {
				break;
			}
			if(!fl.exists())
			{
				fl.getParentFile().mkdirs();
				fl = new java.io.File(destdir, je.getName());
			}
			if(je.isDirectory())
			{
				continue;
			}
			java.io.InputStream is = jarfile.getInputStream(je);
			java.io.FileOutputStream fo = new java.io.FileOutputStream(fl);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				fo.write(buffer, 0, length);
			}
			fo.close();
			is.close();
			break;
		}
		jarfile.close();
		return found;

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
			if (bd.tsLibTimeFolder != null) {
				testsuites.add(badge);
			}
			for (int i = 0; i < bd.dependency.length; i++) {
				buildTestsuiteSet(testsuites, bd.dependency[i]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void execute() throws Exception {
		// The SUT is placed in a known folder
		String sutsDir = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
		String sutDir = sutsDir + "/" + sutId;
		File f = new File(sutDir);
		if (f.exists() == false) {
			if (f.mkdir() == false) {
				logger.error("Failed to create directory: " + sutDir);
			}
		}

		Set<String> testsuites = new HashSet<String>();
		// Check if no badges
		if (this.badgeTcParams != null) {
			
			// For each badge, check if there is a testsuite with TcParams
			for (int i = 0; i < this.badgeTcParams.length; i++) {
				buildTestsuiteSet(testsuites, this.badgeTcParams[i].id);
			}
			

			// For each test suite copy or modify the TcParam.json file
			for (String testsuite : testsuites) {
				// Add badge folder
				String sutBadge = sutDir + "/" + testsuite;
				f = new File(sutBadge);
				if (f.exists() == false) {
					if (f.mkdir() == false) {
						logger.trace("Failed to create directory!");
					}
				}

				// This is the file to copy
				if (extractResource(testsuite, sutBadge, "TcParam.json")) {
					throw new Exception("extractResource: Error occured!");
				}
			}
		}

		// If CS.json exists, only change what is different
		boolean dataChanged = false;
		String csJsonFileName = new String(sutDir + "/" + "CS.json");
		dataChanged = compareCSdata(csJsonFileName, this.sutId, this.sutDescription, this.vendorName, this.badgeTcParams);
		
		if (dataChanged == false) {
			return;
		}

		// Update CS.json file
        JSONObject obj = new JSONObject();
        obj.put("id", this.sutId);
        obj.put("description", this.sutDescription);
        obj.put("vendor", this.vendorName);

        JSONArray list = new JSONArray();
        if (badgeTcParams != null) {
        	int len = this.badgeTcParams.length;
        	for (int i = 0; i < len; i++) {
                list.add(this.badgeTcParams[i].id);
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
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        }

//        System.out.print(obj);
	}
}
