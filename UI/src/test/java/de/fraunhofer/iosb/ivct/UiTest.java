package de.fraunhofer.iosb.ivct;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.LinkedList;

import org.junit.Test;

import de.fraunhofer.iosb.messaginghelpers.LogConfigurationHelper;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.Factory;

public class UiTest {

	@Test
	public void testCreateCmdLineTool() {
		System.out.println("testCreateCmdLineTool enter");

    	LogConfigurationHelper.configureLogging();

        // Handle callbacks
		Factory.initialize();

		CmdLineTool clt;
    	try {
    		clt = new CmdLineTool();
    	} catch (IOException e) {
    		e.printStackTrace();
    		System.out.println("CmdLineTool: new IVCTcommander: " + e);
    		return;
    	}
		assertTrue("CmdLineTool is a null pointer", clt != null);

		(new CmdStartTestResultListener(CmdLineTool.ivctCommander)).execute();
		
		System.out.println("testCreateCmdLineTool leave");
	}

	@Test
	public void testCommandCache() {
		System.out.println("testCommandCache enter");

		String first = "first";
		String testschedule = "testschedule";
		List<String> testcases = new LinkedList<String>();
		testcases.add(first);
		CommandCache cc = new CommandCache(testschedule, testcases);
		assertTrue("CmdLineTool is a null pointer", cc != null);

		String ts = cc.getTestschedule();
		assertEquals(testschedule, ts);

		int num = cc.getNumberOfTestCases();
		assertEquals(1, num);

		String ntc = cc.getNextTestCase();
		assertEquals(first, ntc);

		ntc = cc.getNextTestCase();
		assertEquals(null, ntc);

		System.out.println("testCommandCache leave");
	}

	@Test
	public void testCheckCtTcTsRunning() {
		System.out.println("testCheckCtTcTsRunning enter");

		// Simple null pointer test
		RuntimeParameters rp = new RuntimeParameters();
		assertTrue("RuntimeParameters is a null pointer", rp != null);

		// No test case/schedule started, thus should be not running
		boolean tcr = rp.checkCtTcTsRunning("testRunTimeParameters");
		assertTrue("checkCtTcTsRunning: should not be running", false == tcr);

		// Just to test if method is robust against null pointers
		tcr = rp.checkCtTcTsRunning(null);
		assertTrue("checkCtTcTsRunning: null pointer not detected", true == tcr);

		System.out.println("testCheckCtTcTsRunning leave");
	}

	@Test
	public void testResetSUTvariables() {
		System.out.println("testResetSUTvariables enter");

		// Simple null pointer test
		IVCTcommander ivctCommander = null;
		try {
			ivctCommander = new IVCTcommander();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("IVCTcommander is a null pointer", ivctCommander != null);

		// Reset SUT should reset verdict list
    	ivctCommander.rtp.resetSut();
    	ivctCommander.resetSUT();
		String tc = RuntimeParameters.getTestCaseName();
		assertTrue("Testcase name is not a null pointer", tc == null);
		String ts = RuntimeParameters.getTestScheduleName();
		assertTrue("Testschedule name is not a null pointer", ts == null);
		String tsn = ivctCommander.rtp.getTestSuiteName();
		assertTrue("Testschedule name is not a null pointer", tsn == null);

		System.out.println("testResetSUTvariables leave");
	}

}
