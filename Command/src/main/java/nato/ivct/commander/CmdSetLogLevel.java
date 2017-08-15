package nato.ivct.commander;

import org.json.simple.JSONObject;

public class CmdSetLogLevel implements Command {
	private LogLevel logLevel= LogLevel.INFO;
	
	public void setLogLevel(LogLevel logLevel) {
		this.logLevel = logLevel;
	}

	public enum LogLevel {
		TRACE, DEBUG, INFO, WARNING, ERROR
	}
	
	public CmdSetLogLevel (LogLevel level) {
		logLevel = level;
	}

	/*
	 *      	"commandType : "setLogLevel"
	 *          "sequence    : counter 
	 *          "logLevelId"      : "trace" | "debug" | "info" | "warning" | "error"
	 *          
	 * @see nato.ivct.commander.Command#execute()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		JSONObject startCmd = new JSONObject();
		startCmd.put("commandType", "setLogLevel");
		startCmd.put("sequence", Integer.toString(Factory.newCmdCount()));
		startCmd.put("logLevelId", logLevel.name());
		Factory.sendToJms(startCmd.toString());
	}

}
