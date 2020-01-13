package nato.ivct.commander;

import org.slf4j.Logger;

public class LoggerData {
	Logger logger;
	String sutName;
	String badgeName;
	String tcName;

	LoggerData(final Logger logger, final String sutName, final String badgeName, final String tcName) {
		this.logger = logger;
		this.sutName = sutName;
		this.badgeName = badgeName;
		this.tcName = tcName;
	}
}
