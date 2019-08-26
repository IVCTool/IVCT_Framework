package nato.ivct.commander;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SutPathsFiles {
	/**
	 * Get the path of the folder where SUTs are stored
	 *
	 * @return path to SUTs home folder
	 */
	public String getSutsHomePath() {
		return Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
	}

	/**
	 * Get the names of SUTs available
	 *
	 * @return set of sut names
	 */
	public List<String> getSuts() {
		List<String> sutNames = new ArrayList<>();

		String sutsHomePath = getSutsHomePath();
		// Do not have any access to any SUTs
		if (sutsHomePath == null) {
			return sutNames;
		}

		File dir = new File(sutsHomePath);
		if (dir.exists() == false || dir.isDirectory() == false) {
			return sutNames;
		}
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				sutNames.add(file.getName());
			}
		}
		return sutNames;
	}

	/**
	 * Get the path where the report file(s) of the requested SUT are located
	 * @param sutId the desired SUT name
	 * @return path where report file(s) are located
	 */
	public String getReportPath(final String sutId) {
		String sutsHomePath = getSutsHomePath();
		// Do not have any access to any SUTs
		if (sutsHomePath == null) {
			return null;
		}

		return sutsHomePath + "/" + sutId + "/Reports";
	}

	/**
	 * Get the names of report files without path prefix for the requested SUT
	 *
	 * @param sutId the ID of the desired SUT
	 * @return a set of report file names
	 */
	public List<String> getSutReportFileNames(final String sutId) {
		return getSutReportFileNames(sutId, false);
	}

	/**
	 * Get the names of report files with/without path prefix depending on withPath parameter
	 *
	 * @param sutId the ID of the desired SUT
	 * @param withPath whether the path name is prefixed to report file name
	 * @return a set of report file names
	 */
	public List<String> getSutReportFileNames(final String sutId, final boolean withPath) {
		List<String> reportFileNames = new ArrayList<>();
		String path = getReportPath(sutId);
		final File folder = new File(path);
		if (folder.exists() == false || folder.isDirectory() == false) {
			return reportFileNames;
		}
		reportFileNames = listReportFilesForFolder(folder, path, withPath);
		return reportFileNames;
	}


	/**
	 * Get report file names with/without path prefix depending on withPath parameter
	 *
	 * @param folderName report file
	 * @param path folder where to get the log files from
	 * @param withPath whether the path name is prefixed to report file name
	 * @return set of report file names
	 */
	private List<String> listReportFilesForFolder(final File folderName, final String path, final boolean withPath) {
		List<String> reportFileNames = new ArrayList<>();
		if (folderName == null) {
			return reportFileNames;
		}
	    for (final File fileEntry : folderName.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String s = fileEntry.getName();
	        	int len = s.length();
	        	if (s.substring(len - 4, len).equals(".txt")) {
	        		if (withPath) {
	        			reportFileNames.add(path + "/" + fileEntry.getName());
	        		} else {
	        			reportFileNames.add(fileEntry.getName());
	        		}
	        	}
	        }
	    }
		return reportFileNames;
	}

	/**
	 * Get the path where the TcParam file(s) are located
	 * @param sutId the desired SUT ID
	 * @return path where TcParam file(s) are located
	 * @param testsuiteName name of the testsuite
	 * @return the TcParamPath
	 */
	public String getTcParamPath(final String sutId, final String testsuiteName) {
		String sutsHomePath = getSutsHomePath();
		// Do not have any access to any SUTs
		if (sutsHomePath == null) {
			return null;
		}

		return sutsHomePath + "/" + sutId + "/" + testsuiteName;
	}

	/**
	 * Get the names of the TcParam files available without path prefix.
	 * Currently only one, may change in the future.
	 *
	 * @param sutId the desired SUT ID
	 * @param testsuiteName the name of the testsuite under consideration
	 * @return set of TcParam file names
	 */
	public List<String> getTcParamFileNames(final String sutId, final String testsuiteName) {
		return getTcParamFileNames(sutId, testsuiteName, false);
	}

	/**
	 * Get the names of the TcParam files available with/without path prefix depending
	 * on withPath parameter.
	 * Currently only one, may change in the future
	 *
	 * @param sutId the desired SUT ID
	 * @param testsuiteName the name of the testsuite under consideration
	 * @param withPath true if path shall be included
	 * @return set of TcParam file names
	 */
	public List<String> getTcParamFileNames(final String sutId, final String testsuiteName, final boolean withPath) {
		List<String> tcParamFileNames = new ArrayList<>();

		String folderName = getTcParamPath(sutId, testsuiteName);
		if (folderName == null) {
			return tcParamFileNames;
		}
		final File folder = new File(folderName);
		if (folder.exists() == false || folder.isDirectory() == false) {
			return tcParamFileNames;
		}
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String s = fileEntry.getName();
//              May have multiple TcParam file in the future
//	        	if (s.substring(len - 5, len).equals(".json")) {
	        	if (s.equals("TcParam.json")) {
                    if (withPath) {
                        tcParamFileNames.add(folderName + "/" + fileEntry.getName());
                    } else {
                        tcParamFileNames.add(fileEntry.getName());
                    }
	        	}
	        }
	    }
		return tcParamFileNames;
	}

	/**
	 * Get the names of log files without path prefix
	 *
	 * @param sutId the ID of the SUT
	 * @param testsuiteName the name of the testsuite under consideration
	 * @return a set of log file names
	 */
	public List<String> getSutLogFileNames(final String sutId, final String testsuiteName) {
		return getSutLogFileNames(sutId, testsuiteName, false);
	}

	/**
	 * Get the names of log files with/without path prefix depending on withPath parameter
	 *
	 * @param sutId the ID of the SUT
	 * @param testsuiteName the name of the testsuite under consideration
	 * @param withPath whether the path name is prefixed to log file name
	 * @return a set of log file names
	 */
	public List<String> getSutLogFileNames(final String sutId, final String testsuiteName, final boolean withPath) {
		List<String> logFileNames = new ArrayList<>();
		String path = getSutLogPathName(sutId, testsuiteName);
		final File folder = new File(path);
		if (folder.exists() == false || folder.isDirectory() == false) {
			return logFileNames;
		}
		logFileNames = listLogFilesForFolder(folder, path, withPath);
		return logFileNames;
	}

	/**
	 * Get the path where the log files are located
	 *
	 * @param sutId the ID of the SUT
	 * @param testsuiteName the name of the testsuite under consideration
	 * @return path where logfiles are located or null
	 */
	public String getSutLogPathName(final String sutId, final String testsuiteName) {
		String sutsHome = getSutsHomePath();
		if (sutsHome == null) {
			return null;
		}
		return sutsHome + "/" + sutId + "/" + testsuiteName + "/Logs";
	}

	/**
	 * Get log file names with/without path prefix depending on withPath parameter
	 *
	 * @param folderName log file
	 * @param path folder where to get the log files from
	 * @param withPath whether the path name is prefixed to log file name
	 * @return set of logfile names
	 */
	private List<String> listLogFilesForFolder(final File folderName, final String path, final boolean withPath) {
		List<String> logFileNames = new ArrayList<>();
		if (folderName == null) {
			return logFileNames;
		}
	    for (final File fileEntry : folderName.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String s = fileEntry.getName();
	        	int len = s.length();
	        	if (s.substring(len - 4, len).equals(".log")) {
	        		if (withPath) {
	        			logFileNames.add(path + "/" + fileEntry.getName());
	        		} else {
	        			logFileNames.add(fileEntry.getName());
	        		}
	        	}
	        }
	    }
		return logFileNames;
	}
}
