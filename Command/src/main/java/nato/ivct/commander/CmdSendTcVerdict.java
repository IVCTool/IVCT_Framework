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

public class CmdSendTcVerdict implements Command {
	
	private String sutName;
	private String sutDir;
	private String testScheduleName;
	private String testcase;
	private String verdict;
	private String verdictText;

	public CmdSendTcVerdict (String sutName, String sutDir, String testScheduleName, String testcase, String verdict, String verdictText) {
		this.sutName = sutName;
		this.sutDir = sutDir;
		this.testScheduleName = testScheduleName;
		this.testcase = testcase;
		this.verdict = verdict;
		this.verdictText = verdictText;
		
	}


	/**
	 * Execute the Send Test Case Verdict command
	 * 
	 *  { "commandType" : "announceVerdict", 
	 *    "sequence" : counter,
	 *    "sutName" : sutName,
	 *    "sutDir" :  sutDir,
	 *    "testScheduleName" : testScheduleName
	 *    "testcase" : testcase,
	 *    "verdict" : "PASSED" | "FAILED" | "INCONCLUSIVE",
	 *    "verdictText" : text
	 *  }
	 *  
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void execute() {
		JSONObject verdictCmd = new JSONObject();
		verdictCmd.put("commandType", "announceVerdict");
		verdictCmd.put("sequence", Integer.toString(Factory.newCmdCount()));
		verdictCmd.put("sutName", sutName);
		verdictCmd.put("sutDir", sutDir);
		verdictCmd.put("testScheduleName", testScheduleName);
		verdictCmd.put("testcase", testcase);
		verdictCmd.put("verdict", verdict);
		verdictCmd.put("verdictText", verdictText);
		Factory.sendToJms(verdictCmd.toString());
	}

}
