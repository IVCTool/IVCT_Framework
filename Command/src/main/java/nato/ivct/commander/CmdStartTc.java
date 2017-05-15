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

import javax.jms.MessageProducer;

public class CmdStartTc implements Command {
	MessageProducer producer;

	public CmdStartTc (MessageProducer p){
		producer = p;
	}
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}



//	{
//	  "commandType" : "startTestCase",
//	  "sequence" : "125",
//	  "testScheduleName" : "Badge100",
//	  "testCaseId" : "de.fraunhofer.iosb.tc_helloworld.TC0002",
//	  "tsRunFolder" : "TS_HelloWorld\\TS_HelloWorld\\bin",
//	  "tcParam" : {
//		  "federationName" : "HelloWorld",
//		  "rtiHostName" : "localhost",
//		  "sutFederateName" : "A"
//		}
//	}
