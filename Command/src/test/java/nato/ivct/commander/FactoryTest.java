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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;

import org.apache.activemq.broker.BrokerService;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import nato.ivct.commander.CmdSetLogLevel.LogLevel;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;

public class FactoryTest {
	private static BrokerService broker = new BrokerService();

	@BeforeClass
	public static void startBroker() throws Exception {
		// configure the broker
		broker.addConnector("tcp://localhost:61616");
		broker.setPersistent(false);

		broker.start();
	}

	@AfterClass
	public static void stopBroker() throws Exception {
		try {
			broker.stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testCreateCmdListBadgesMethod() {
		CmdListBadges lb = Factory.createCmdListBadges();
		assertTrue("Factory Test createCmdListBadges should return CmdListBadges", lb != null);
		lb.execute();
		if (lb.badgeMap.size() == 0) {
//			throw new AssumptionViolatedException("Inconclusive");
			return;
		}
		assertTrue("Some Badges should be found", lb.badgeMap.size() > 0);
	}
	
	@Test
	public void testReadVersion() {
	    Factory.readVersion();
	}

	@Test
	public void testCreateCmdListSutMethod() {
		CmdListSuT cl = Factory.createCmdListSut();
		assertTrue("Factory Test createCmdListSut should return CmdListSut", cl != null);
		cl.execute();
		if (cl.sutMap.size() == 0) {
//			throw new AssumptionViolatedException("Inconclusive");
			return;
		}
		assertTrue("Some SuT's should be found", cl.sutMap.size() > 0);
	}

	@Test
	public void testCreateCmdQuitMethod() {
		CmdQuit qc = Factory.createCmdQuit();
		assertTrue("Factory Test createCmdQuit should return CmdQuit", qc != null);
		qc.execute();
	}

	@Test
	public void testCmdSendTcStatus() {
		CmdSendTcStatus cmd = Factory.createCmdSendTcStatus();
		assertTrue("Factory Test createCmdQuit should return CmdQuit", cmd != null);
		cmd.execute();
	}

	@Test
	public void testCmdSendTcVerdict() {
		CmdSendTcVerdict cmd = Factory.createCmdSendTcVerdict("sut", "sutDir", "testScheduleName", "testcase", "verdict", "verdictText");
		assertTrue("Factory Test createCmdQuit should return CmdQuit", cmd != null);
		cmd.execute();
	}

	@Test
	public void testCreateCmdSetLogLevelMethod() {
		CmdSetLogLevel sll = Factory.createCmdSetLogLevel(LogLevel.DEBUG);
		assertTrue("Factory Test createCmdSetLogLevel should return CmdSetLogLevel", sll != null);
		sll.setLogLevel(LogLevel.TRACE);
		sll.execute();
		sll.setLogLevel(LogLevel.DEBUG);
		sll.execute();
		sll.setLogLevel(LogLevel.INFO);
		sll.execute();
		sll.setLogLevel(LogLevel.WARNING);
		sll.execute();
		sll.setLogLevel(LogLevel.ERROR);
		sll.execute();
	}

	@Test
	public void testCreateCmdStartTcMethod() {
		CmdStartTc stc = Factory.createCmdStartTc("hw_iosb", "HelloWorld-2017", "some.test.case", "crcAddress=localhost:8989", "TheWorld", "TheFederate");
		assertTrue("Factory Test createCmdStartTc should return CmdStartTc", stc != null);
		stc.execute();
		assertTrue("Get SuT name", stc.getSut().contentEquals("hw_iosb"));
		assertTrue("Get Badge name", stc.getSuiteName().contentEquals("HelloWorld-2017"));
		stc = Factory.createCmdStartTc("xyz", "xyz-1.0.0", "some.test.case", "crcAddress=localhost:8989", "TheWorld", "TheFederate");
		assertTrue("Factory Test createCmdStartTc should return CmdStartTc", stc != null);
		stc.execute();
		assertTrue("Get SuT name", stc.getSut() != null);
		assertTrue("Get Badge name", stc.getSuiteName() != null);
	}

	@Test
	public void testCreateCmdStartTestResultListenerMethod() {
		Semaphore semaphore = new Semaphore(0);
		class OnResultListenerTest implements OnResultListener {

			@Override
			public void onResult(TcResult result) {
				assertTrue(result.sutName != null);
				assertTrue(result.sutDir != null);
				assertTrue(result.testScheduleName != null);
				assertTrue(result.testcase != null);
				assertTrue(result.verdict != null);
				assertTrue(result.verdictText != null);
				semaphore.release();
			}

		}
		OnResultListener testListener = new OnResultListenerTest();
		CmdStartTestResultListener resultListener = Factory.createCmdStartTestResultListener(testListener);
		assertTrue("Factory Test createCmdStartTestResultListener should return CmdStartTestResultListener",
				resultListener != null);
		resultListener.execute();
		
		CmdSendTcVerdict stc = new CmdSendTcVerdict("hw_iosb", "tcDir", "HelloWorld-2017", "some.test.case", "verdict", "verdictText");
        assertTrue("Factory Test CmdSendTcVerdict should return some value", stc != null);
        stc.execute();
        try {
        	semaphore.acquire();

        } catch (InterruptedException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
        }
	}

	@Test
	public void testCreateCmdUpdateSUTMethod() {

		// If property is not set, do not have any access to any SUTs
		if (Factory.props.containsKey(Factory.IVCT_SUT_HOME_ID) == false) {
			return;
		}
		String vendorName = "Fraunhofer IOSB";
		Set<BadgeTcParam> badgeTcParams = new HashSet<BadgeTcParam>();
		SutDescription sutDescription = new SutDescription();
		sutDescription.name = "hw_iosb";
		sutDescription.description = "HelloWorld system under federate for IVCT demonstration";
		sutDescription.vendor = "Fraunhofer IOSB";
		sutDescription.badges.add("HLA-BASE-2017");
		sutDescription.badges.add("TS_HLA_EncodingRulesTester-2017");
		CmdUpdateSUT cus = Factory.createCmdUpdateSUT(sutDescription);
		try {
			cus.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String csJsonFilename = Factory.props.getProperty(Factory.IVCT_SUT_HOME_ID) + "/" + sutDescription.name + "/" + "CS.json";
		try {
			// The parameters should be the same. Thus expected false.
			assertFalse("CS.json values should be equal", cus.compareCSdata(csJsonFilename, sutDescription));
			SutDescription tmpSutDescription = new SutDescription();

			copySUT(tmpSutDescription, sutDescription);
			tmpSutDescription.name = "dummy";
			// The name is changed. Thus expected true.
			assertTrue("CS.json sut name change should be detected", cus.compareCSdata(csJsonFilename, tmpSutDescription));

			copySUT(tmpSutDescription, sutDescription);
			tmpSutDescription.description = "dummy";
			// The description is changed. Thus expected true.
			assertTrue("CS.json sut description change should be detected", cus.compareCSdata(csJsonFilename, tmpSutDescription));

			copySUT(tmpSutDescription, sutDescription);
			tmpSutDescription.vendor = "dummy";
			// The vendor has changed. Thus expected true.
			assertTrue("CS.json vendor change should be detected", cus.compareCSdata(csJsonFilename, tmpSutDescription));

			copySUT(tmpSutDescription, sutDescription);
			tmpSutDescription.version = "dummy";
			// The version has changed. Thus expected true.
			assertTrue("CS.json version change should be detected", cus.compareCSdata(csJsonFilename, tmpSutDescription));

			copySUT(tmpSutDescription, sutDescription);
			tmpSutDescription.badges.add("dummy");
			// The badges have changed. Thus expected true.
			assertTrue("CS.json badge change should be detected", cus.compareCSdata(csJsonFilename, tmpSutDescription));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		assertTrue("should have a list of SuTs", cmd.sutMap != null);
		if (cmd.sutMap.containsKey("hw_iosb") == false) {
//			throw new AssumptionViolatedException("Inconclusive");
			return;
		}
		assertTrue("hw_iosb should exist", cmd.sutMap.containsKey("hw_iosb"));
		SutDescription hw = cmd.sutMap.get("hw_iosb");
		assertTrue(hw.vendor.equals("Fraunhofer IOSB"));
	}
}
