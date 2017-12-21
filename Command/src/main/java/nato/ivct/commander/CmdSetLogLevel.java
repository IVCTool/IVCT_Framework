/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

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
