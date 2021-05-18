package de.fraunhofer.iosb.ivct;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
		assertNotNull(clt, "CmdLineTool is a null pointer");

		(new CmdStartTestResultListener(CmdLineTool.ivctCommander)).execute();

		log.info("testCreateCmdLineTool leave");
	}

	@Test
	public void testCheckCtTcTsRunning() {
		log.info("testCheckCtTcTsRunning enter");

		// Simple null pointer test
		RuntimeParameters rp = new RuntimeParameters();
		assertNotNull(rp, "RuntimeParameters is a null pointer");

		// No test case/schedule started, thus should be not running
		boolean tcr = rp.checkCtTcTsRunning("testRunTimeParameters");
		assertFalse(tcr, "checkCtTcTsRunning: should not be running");

		// Just to test if method is robust against null pointers
		tcr = rp.checkCtTcTsRunning(null);
		assertTrue(tcr, "checkCtTcTsRunning: null pointer not detected");

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
		assertNotNull(ivctCommander, "IVCTcommander is a null pointer");

		// Reset SUT should reset verdict list
    	ivctCommander.rtp.resetSut();
    	ivctCommander.resetSUT();
		String tc = ivctCommander.rtp.getTestCaseName();
		assertNotNull(tc, "Testcase name is not a null pointer");
		String ts = ivctCommander.rtp.getTestScheduleName();
		assertNotNull(ts, "Testschedule name is not a null pointer");

		log.info("testResetSUTvariables leave");
	}

}
