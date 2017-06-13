package nato.ivct.commander;

import org.json.simple.JSONObject;

public class CmdSetLogLevel implements Command {
	private String logLevel= null;

	public CmdSetLogLevel (String level) {
		logLevel = level;
	}

	/*
	 *      	"commandType : "setLogLevel"
	 *          "sequence    : counter 
	 *          "param"      : "trace" | "debug" | "info" | "warning" | "error"
	 *          
	 * @see nato.ivct.commander.Command#execute()
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		JSONObject startCmd = new JSONObject();
		startCmd.put("commandType", "startTestsetLogLevel");
		startCmd.put("sequence", Integer.toString(Factory.newCmdCount()));
		startCmd.put("param", logLevel);
		Factory.sendToJms(startCmd.toString());
	}

}
