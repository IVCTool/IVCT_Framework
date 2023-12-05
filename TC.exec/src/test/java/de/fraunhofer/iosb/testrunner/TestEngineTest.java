package de.fraunhofer.iosb.testrunner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.apache.activemq.broker.BrokerService;

import java.lang.ref.SoftReference;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import nato.ivct.commander.CmdStartTc;
import nato.ivct.commander.CmdStartTcListener;
import nato.ivct.commander.Factory;
import nato.ivct.commander.CmdStartTcListener.TcInfo;

public class TestEngineTest {
    static final private Logger LOGGER = org.slf4j.LoggerFactory.getLogger(TestEngineTest.class);
	private static BrokerService broker;


	@BeforeAll
	public static void startBroker() throws Exception {
        LOGGER.info("Starting embedded broker");
        if (broker == null) {
            broker = new BrokerService();
            broker.addConnector("tcp://localhost:61616"); 
            broker.setPersistent(false);
            broker.start();
        }
    }

	 @AfterAll
	 public static void stopBroker() throws Exception {
	 	if (broker != null) {
	 		broker.stop();
	 		broker = null;
	 	}
	 	LOGGER.info("Embedded broker stopped");
	}  


    @Test
    public void testOnStartTestCaseTC_BASE_0001() throws InterruptedException {
        // create StartTestCase command
        TestEngine engine = new TestEngine();
        engine.startUp();

        // create StartTestCase command
        CmdStartTc stc = Factory.createCmdStartTc(
            "hw_iosb", 
            "NETN-BASE-4_0", 
            "org.nato.netn.base.TC_BASE_0001",
                                    "crcAddress=localhost:8989", 
            "TheWorld", 
            "TheFederate", 
            "Broadcast");
        stc.execute();

        Thread.sleep(100);
            SoftReference<Future<?>> future = engine.threadCache.get("org.nato.netn.base.TC_BASE_0001");
            while (!future.get().isDone()) {
                Thread.sleep(100);
            }

        LOGGER.debug("done");
    }
    
    @Test
    public void testOnStartTestCaseHwTC0001() throws InterruptedException {
        // create StartTestCase command
        TestEngine engine = new TestEngine();
        engine.startUp();

        // create StartTestCase command
        String tc = "de.fraunhofer.iosb.tc_helloworld.TC0001";
        CmdStartTc stc = Factory.createCmdStartTc(
            "hw_iosb", 
            "TS-HelloWorld-2019", 
            tc,				
            "crcAddress=localhost:8989", 
            "TheWorld", "TheFederate", 
            "Broadcast");

        stc.execute();

        Thread.sleep(100);
        SoftReference<Future<?>> future = engine.threadCache.get(tc);
        while (future != null && !future.get().isDone()) {
            Thread.sleep(100);
        }
        LOGGER.debug("done");
    }
    
    @Test
    public void testOnAbortTestCase() {
      
    }
    
    @Test
    public void testOnQuit() {
  
    }
  
    @Test
    public void testOnSetLogLevel() {
      
    }

  }