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


import static org.junit.Assert.assertTrue;

import org.junit.Test;

import nato.ivct.commander.CmdListSuT;
import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.Factory;


public class FactoryTest {
	@Test
	public void testCreateCmdListBadgesMethod() {
		assertTrue("Factory Test createCmdListBadges should return CmdListBadges",
				Factory.createCmdListBadges() != null);
	}

	@Test
	public void testCreateCmdListSutMethod() {
		assertTrue("Factory Test createCmdListSut should return CmdListSut", Factory.createCmdListSut() != null);
	}

	@Test
	public void testCreateCmdQuitMethod() {
		assertTrue("Factory Test createCmdQuit should return CmdQuit", Factory.createCmdQuit() != null);
	}

	@Test
	public void testCreateCmdSetLogLevelMethod() {
		assertTrue("Factory Test createCmdSetLogLevel should return CmdSetLogLevel",
				Factory.createCmdSetLogLevel(LogLevel.DEBUG) != null);
	}

	@Test
	public void testCreateCmdStartTcMethod() {
		assertTrue("Factory Test createCmdStartTc should return CmdStartTc",
				Factory.createCmdStartTc("hw_iosb", "TS_HelloWorld", "some.test.case", "c:/tmp") != null);
	}

	@Test
	public void testCreateCmdStartTestResultListenerMethod() {
		class OnResultListenerTest implements OnResultListener {

			@Override
			public void onResult(TcResult result) {
				assertTrue (result.sutName != null);
				assertTrue (result.sutDir != null);
				assertTrue (result.testScheduleName != null);
				assertTrue (result.testcase != null);
				assertTrue (result.verdict != null);
				assertTrue (result.verdictText != null);	
			}

		}
		OnResultListener testListener = new OnResultListenerTest();
		assertTrue("Factory Test createCmdStartTestResultListener should return CmdStartTestResultListener",
				Factory.createCmdStartTestResultListener(testListener) != null);
	}

	@Test
	public void testHelloWorldFederate() {
		CmdListSuT cmd = Factory.createCmdListSut();
		cmd.execute();
		assertTrue("should have a list of SuTs", cmd.sutMap != null);
		assertTrue("hw_iosb should exist", cmd.sutMap.containsKey("hw_iosb"));
		SutDescription hw = cmd.sutMap.get("hw_iosb");
		assertTrue(hw.vendor.equals("Fraunhofer IOSB"));
	}
}
