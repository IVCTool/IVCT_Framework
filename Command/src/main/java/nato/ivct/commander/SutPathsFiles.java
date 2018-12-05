package nato.ivct.commander;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class SutPathsFiles {
	/**
	 * Get the path of the folder where SUTs are stored
	 * 
	 * @return path to SUTs home folder
	 */
	public String getSutsHomePath() {
		// If property is not set, do not have any access to any SUTs
		if (Factory.props.containsKey(Factory.IVCT_SUT_HOME_ID) == false) {
			return null;
		}
		return Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID);
	}

	/**
	 * Get the names of SUTs available
	 * 
	 * @return set of sut names
	 */
	public Set<String> getSuts() {
		Set<String> sutNames = new HashSet<String>();

		String sutsHomePath = getSutsHomePath();
		// Do not have any access to any SUTs
		if (sutsHomePath == null) {
			return sutNames;
		}

		File dir = new File(sutsHomePath);
		File[] filesList = dir.listFiles();
		for (File file : filesList) {
			if (file.isDirectory()) {
				sutNames.add(file.getName());
			}
		}
		return sutNames;
	}

	/**
	 * Get the path where the TcParam file(s) are located
	 * @param sutName the desired SUT name
	 * @return path where TcParam file(s) are located
	 */
	public String getTcParamPath(final String sutName) {
		String sutsHomePath = getSutsHomePath();
		// Do not have any access to any SUTs
		if (sutsHomePath == null) {
			return null;
		}

		return sutsHomePath + "/" + sutName;
	}

	/**
	 * Get the names of the TcParam files available. Currently only one,
	 * may change in the future
	 * 
	 * @param sutName the desired SUT name
	 * @param badgeName the name of the badge under consideration
	 * @return set of TcParam file names
	 */
	public Set<String> getTcParamFileNames(final String sutName, final String badgeName) {
		Set<String> tcParamFileNames = new HashSet<String>();

		String folderName = getTcParamPath(sutName);
		if (folderName == null) {
			return tcParamFileNames;
		}
		final File folder = new File(folderName + "/" + badgeName);
	    for (final File fileEntry : folder.listFiles()) {
	        if (fileEntry.isFile()) {
	        	String s = fileEntry.getName();
//              May have multiple TcParam file in the future
//	        	if (s.substring(len - 5, len).equals(".json")) {
	        	if (s.equals("TcParam.json")) {
		        	tcParamFileNames.add(s);
	        	}
	        }
	    }
		return tcParamFileNames;
	}

	/**
	 * Get the names of log files without path prefix
	 * 
	 * @param sutName the name of the SUT
	 * @param badgeName the name of the badge under consideration
	 * @return a set of log file names
	 */
	public Set<String> getSutLogFileNames(final String sutName, final String badgeName) {
		return getSutLogFileNames(sutName, badgeName, false);
	}

	/**
	 * Get the names of log files with/without path prefix depending on withPath parameter
	 * 
	 * @param sutName the name of the SUT
	 * @param badgeName the name of the badge under consideration
	 * @param withPath whether the path name is prefixed to log file name
	 * @return a set of log file names
	 */
	public Set<String> getSutLogFileNames(final String sutName, final String badgeName, final boolean withPath) {
		String path = getSutLogPathName(sutName, badgeName);
		final File folder = new File(path);
		Set<String> logFileNames = listLogFilesForFolder(folder, path, withPath);
		return logFileNames;
	}

	/**
	 * Get the path where the log files are located
	 * 
	 * @param sutName the name of the SUT
	 * @param badgeName the name of the badge under consideration
	 * @return path where logfiles are located or null
	 */
	public String getSutLogPathName(final String sutName, final String badgeName) {
		String sutsHome = getSutsHomePath();
		if (sutsHome == null) {
			return null;
		}
		return sutsHome + "/" + sutName + "/" + badgeName;
	}

	/**
	 * Get log file names with/without path prefix depending on withPath parameter
	 * 
	 * @param logfile folderName
	 * @return set of logfile names
	 */
	private Set<String> listLogFilesForFolder(final File folderName, final String path, final boolean withPath) {
		Set<String> logFileNames = new HashSet<String>();
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
