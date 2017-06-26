package nato.ivct.commander;

import org.json.simple.JSONObject;


@SuppressWarnings("unchecked")
public class CmdQuit implements Command {

	@Override
	public void execute() {
		JSONObject quitCmd = new JSONObject();
		quitCmd.put("commandType", "quit");
		quitCmd.put("sequence", Integer.toString(Factory.getCmdCounter()));
		Factory.sendToJms(quitCmd.toString());
	}

}
