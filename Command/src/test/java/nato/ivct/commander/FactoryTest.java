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


import java.util.concurrent.Semaphore;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;

public class FactoryTest {
	private static BrokerService broker = new BrokerService();

	@BeforeAll
	public static void startBroker() throws Exception {
		// configure the broker
		broker.addConnector("tcp://localhost:61616"); 
		broker.setPersistent(false);

		broker.start();
	}

	@AfterAll
	public static void stopBroker() throws Exception {
		try {
			broker.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateCmdListBadgesMethod() {
		CmdListBadges lb = Factory.createCmdListBadges();
		assertNotNull(lb, "Factory Test createCmdListBadges should return CmdListBadges");
		lb.execute();
		assertTrue(lb.badgeMap.size() > 0, "Some Badges should be found");
	}

	@Test
	public void testReadVersion() {
	    Factory.readVersion();
		assertNotNull(Factory.getVersion());
	}

	@Test
	public void testCreateCmdListSutMethod() {
		CmdListSuT cl = Factory.createCmdListSut();
		assertNotNull(cl, "Factory Test createCmdListSut should return CmdListSut");
		cl.execute();
		assertTrue(cl.sutMap.size() > 0, "Some SuT's should be found");
	}

	@Test
	public void testCreateCmdQuitMethod() {
		Semaphore semaphore = new Semaphore(0);
		class OnQuitListenerTest implements CmdQuitListener.OnQuitListener {
			@Override
			public void onQuit() {
				semaphore.release(1);
			}
		}

		// setup the result listener
		OnQuitListenerTest testListener = new OnQuitListenerTest();
		CmdQuitListener resultListener = Factory.createCmdQuitListener(testListener);
		assertNotNull(resultListener, "Factory Test CmdQuitListener should return result");
		resultListener.execute();
		// create and send the quit cmd
		CmdQuit qc = Factory.createCmdQuit();
		assertNotNull(qc,"Factory Test createCmdQuit should return CmdQuit {}");
		qc.execute();
		// wait until quit cmd is received
        try {
        	semaphore.acquire(1);

        } catch (InterruptedException e) {
        	e.printStackTrace();
        }	}

	@Test
	public void testCmdSendTcStatus() {
		Semaphore semaphore = new Semaphore(0);
		class OnTcStatusListenerTest implements CmdTcStatusListener.OnTcStatusListener {
			@Override
			public void onTcStatus(CmdTcStatusListener.TcStatus status) {
			semaphore.release(1);
			}
		}
		
		// setup the result listener
		OnTcStatusListenerTest statusListener = new OnTcStatusListenerTest();
		CmdTcStatusListener resultListener = Factory.createCmdTcStatusListener(statusListener);
		assertNotNull(resultListener);
		resultListener.execute();
		// create and send status msg
		CmdSendTcStatus cmd = Factory.createCmdSendTcStatus();
		assertNotNull(cmd);
		cmd.execute();
		// wait until result is received
        try {
        	semaphore.acquire(1);

        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

	@Test
	public void testCreateCmdSetLogLevelMethod() {
		Semaphore semaphore = new Semaphore(0);
		class OnSetLogLevelListenerTest implements CmdSetLogLevelListener.OnSetLogLevelListener {
			LogLevel logMsg = null;
			@Override
			public void onSetLogLevel(LogLevel msg) {
				logMsg = msg;
				semaphore.release(1);
			}
		}		
		// setup the result listener
		OnSetLogLevelListenerTest logMsgListener = new OnSetLogLevelListenerTest();
		CmdSetLogLevelListener resultListener = Factory.createCmdSetLogLevelListener(logMsgListener);
		assertNotNull(resultListener);
		resultListener.execute();
		// create and send some LogLevel messages
		CmdSetLogLevel sll = Factory.createCmdSetLogLevel(LogLevel.DEBUG);
		assertNotNull(sll);
		try {
			// set to TRACE
			sll.setLogLevel(LogLevel.TRACE);
			sll.execute();
			semaphore.acquire(1);
			assertEquals(LogLevel.TRACE, logMsgListener.logMsg);
			// set to DEBUG
			sll.setLogLevel(LogLevel.DEBUG);
			sll.execute();
			semaphore.acquire(1);
			assertEquals(LogLevel.DEBUG, logMsgListener.logMsg);
			// set to INFO
			sll.setLogLevel(LogLevel.INFO);
			sll.execute();
			semaphore.acquire(1);
			assertEquals(LogLevel.INFO, logMsgListener.logMsg);
			// set to WARNING
			sll.setLogLevel(LogLevel.WARNING);
			sll.execute();
			semaphore.acquire(1);
			assertEquals(LogLevel.WARNING, logMsgListener.logMsg);
			// set to ERROR
			sll.setLogLevel(LogLevel.ERROR);
			sll.execute();
			semaphore.acquire(1);
			assertEquals(LogLevel.ERROR, logMsgListener.logMsg);
        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

	@Test
	public void testCreateCmdStartTcMethod() {
		CmdStartTc stc = Factory.createCmdStartTc("hw_iosb", "HelloWorld-2019", "some.test.case", "crcAddress=localhost:8989", "TheWorld", "TheFederate", "broadcast");
		assertNotNull(stc, "Factory Test createCmdStartTc should return CmdStartTc");
		stc.execute();
		assertEquals("hw_iosb", stc.getSut());
		assertEquals("HelloWorld-2019", stc.getSuiteName());
		stc = Factory.createCmdStartTc("xyz", "xyz-1.0.0", "some.test.case", "crcAddress=localhost:8989", "TheWorld", "TheFederate", "broadcast");
		assertNotNull(stc, "Factory Test createCmdStartTc should return CmdStartTc");
		stc.execute();
		assertNotNull(stc.getSut(), "Get SuT name");
		assertNotNull(stc.getSuiteName(), "Get Badge name");
	}

	@Test
	public void testCreateCmdStartTestResultListenerMethod() {
		Semaphore semaphore = new Semaphore(0);
		class OnResultListenerTest implements OnResultListener {

			@Override
			public void onResult(TcResult result) {
				assertEquals("hw_iosb", result.sutName);
				assertEquals("tcDir", result.sutDir);
				assertEquals("HelloWorld-2019", result.testScheduleName);
				assertEquals("some.test.case", result.testcase);
				assertEquals("verdict", result.verdict);
				assertEquals("verdictText", result.verdictText);
				semaphore.release(1);
			}

		}

		// setup the result listener
		OnResultListener testListener = new OnResultListenerTest();
		CmdStartTestResultListener resultListener = Factory.createCmdStartTestResultListener(testListener);
		assertNotNull(resultListener, "Factory Test createCmdStartTestResultListener should return CmdStartTestResultListener");
		resultListener.execute();
		// send the test result
		CmdSendTcVerdict stc = new CmdSendTcVerdict("hw_iosb", "tcDir", "HelloWorld-2019", "some.test.case", "verdict", "verdictText");
        assertNotNull(stc, "Factory Test CmdSendTcVerdict should return some value");
        stc.execute();
		// wait until result is received
        try {
        	semaphore.acquire(1);

        } catch (InterruptedException e) {
        	e.printStackTrace();
        }
	}

	@Test
	public void testCreateCmdUpdateSUTMethod() throws Exception {

		Factory.initialize();
		// If property is not set, do not have any access to any SUTs
		if (!Factory.props.containsKey(Factory.IVCT_SUT_HOME_ID)) {
			return;
		}
		SutDescription sutDescription = new SutDescription();
		sutDescription.name = "hw_iosb";
		sutDescription.description = "HelloWorld system under federate for IVCT demonstration";
		sutDescription.vendor = "Fraunhofer IOSB";
		sutDescription.badges.add("HLA-BASE-2019");
		sutDescription.badges.add("RPR-Encoding-2.0");
		CmdUpdateSUT cus = Factory.createCmdUpdateSUT(sutDescription);
		cus.execute();
		String csJsonFilename = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID) + "/" + sutDescription.ID + "/" + "CS.json";
		// The parameters should be the same. Thus expected false.
		assertFalse(cus.compareCSdata(csJsonFilename, sutDescription), "CS.json values should be equal");
		SutDescription tmpSutDescription = new SutDescription();

		copySUT(tmpSutDescription, sutDescription);
		tmpSutDescription.name = "dummy";
		// The name is changed. Thus expected true.
		assertTrue(cus.compareCSdata(csJsonFilename, tmpSutDescription), "CS.json sut name change should be detected");

		copySUT(tmpSutDescription, sutDescription);
		tmpSutDescription.description = "dummy";
		// The description is changed. Thus expected true.
		assertTrue(cus.compareCSdata(csJsonFilename, tmpSutDescription), "CS.json sut description change should be detected");

		copySUT(tmpSutDescription, sutDescription);
		tmpSutDescription.vendor = "dummy";
		// The vendor has changed. Thus expected true.
		assertTrue(cus.compareCSdata(csJsonFilename, tmpSutDescription), "CS.json vendor change should be detected");

		copySUT(tmpSutDescription, sutDescription);
		tmpSutDescription.version = "dummy";
		// The version has changed. Thus expected true.
		assertTrue(cus.compareCSdata(csJsonFilename, tmpSutDescription), "CS.json version change should be detected");

		copySUT(tmpSutDescription, sutDescription);
		tmpSutDescription.badges.add("dummy");
		// The badges have changed. Thus expected true.
		assertTrue(cus.compareCSdata(csJsonFilename, tmpSutDescription), "CS.json badge change should be detected");
	}

	private void copySUT(SutDescription tmpSutDescription, SutDescription sutDescription) {
		tmpSutDescription.ID = sutDescription.ID;
		tmpSutDescription.name = sutDescription.name;
		tmpSutDescription.description = sutDescription.description;
		tmpSutDescription.vendor = sutDescription.vendor;
		for (String entry : sutDescription.badges) {
			tmpSutDescription.badges.add(entry);
		}
	}

	@Test
	public void testHelloWorldFederate() {
		CmdListSuT cmd = Factory.createCmdListSut();
		cmd.execute();
		assertNotNull(cmd.sutMap, "should have a list of SuTs");
		assertTrue(cmd.sutMap.containsKey("hw_iosb"), "hw_iosb should exist");
		SutDescription hw = cmd.sutMap.get("hw_iosb");
		assertEquals("Fraunhofer IOSB", hw.vendor);
	}
}
