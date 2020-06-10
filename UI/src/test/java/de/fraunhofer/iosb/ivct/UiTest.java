package de.fraunhofer.iosb.ivct;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.activemq.broker.BrokerService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.Factory;

public class UiTest {
	private static BrokerService broker = new BrokerService();
	private static Logger log = LoggerFactory.getLogger(UiTest.class);

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
			log.error("stopBroker",e);
		}
	}


	@Test
	public void testCreateCmdLineTool() {
		log.info("testCreateCmdLineTool enter");

    	LogConfigurationHelper.configureLogging();

        // Handle callbacks
		Factory.initialize();

		CmdLineTool clt;
    	try {
    		clt = new CmdLineTool();
    	} catch (IOException e) {
			log.error("testCreateCmdLineTool",e);
    		log.info("testCreateCmdLineTool: new IVCTcommander: {}", e);
    		return;
    	}
		assertTrue("CmdLineTool is a null pointer", clt != null);

		(new CmdStartTestResultListener(CmdLineTool.ivctCommander)).execute();

		log.info("testCreateCmdLineTool leave");
	}

	@Test
	public void testCheckCtTcTsRunning() {
		log.info("testCheckCtTcTsRunning enter");

		// Simple null pointer test
		RuntimeParameters rp = new RuntimeParameters();
		assertTrue("RuntimeParameters is a null pointer", rp != null);

		// No test case/schedule started, thus should be not running
		boolean tcr = rp.checkCtTcTsRunning("testRunTimeParameters");
		assertTrue("checkCtTcTsRunning: should not be running", false == tcr);

		// Just to test if method is robust against null pointers
		tcr = rp.checkCtTcTsRunning(null);
		assertTrue("checkCtTcTsRunning: null pointer not detected", true == tcr);

		log.info("testCheckCtTcTsRunning leave");
	}

	@Test
	public void testResetSUTvariables() {
		log.info("testResetSUTvariables enter");

		// Simple null pointer test
		IVCTcommander ivctCommander = null;
		try {
			ivctCommander = new IVCTcommander();
		} catch (IOException e) {
			log.error("testResetSUTvariables",e);
		}
		assertTrue("IVCTcommander is a null pointer", ivctCommander != null);

		// Reset SUT should reset verdict list
    	ivctCommander.rtp.resetSut();
    	ivctCommander.resetSUT();
		String tc = ivctCommander.rtp.getTestCaseName();
		assertTrue("Testcase name is not a null pointer", tc == null);
		String ts = ivctCommander.rtp.getTestScheduleName();
		assertTrue("Testschedule name is not a null pointer", ts == null);

		log.info("testResetSUTvariables leave");
	}

}
