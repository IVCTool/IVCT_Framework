/* Copyright 2020, Reinhard Herzog (Fraunhofer IOSB)

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
