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

package de.fraunhofer.iosb.testrunner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;
import nato.ivct.commander.CmdHeartbeatSend;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Simple test environment. The TestRunner takes the classnames of the tests as
 * commandline arguments and then executes the tests in the given order.
 *
 * @author sen (Fraunhofer IOSB)
 */
public class TestRunner {
    
    
    private boolean health;
    

	/**
	 * Command line entry point for the TestRunner.
	 *
	 * @param args
	 *            command line parameters
	 */
	public static void main(final String[] args)    {
		final Logger LOGGER = LoggerFactory.getLogger(TestRunner.class);
		String paramJson = null;
		IVCT_Verdict verdicts[] = new IVCT_Verdict[1];
		//new TestRunner().executeTests(LOGGER, "SuT", args, paramJson, verdicts);
		TestRunner testrunner = new TestRunner();
		testrunner.executeTests(LOGGER, "SuT", args, paramJson, verdicts);
		
		try {
		  testrunner.sendHeartbeat(LOGGER);
	    } catch (Exception ex) {
	        LOGGER.error("could not start  sendHeartbeat " + ex);
        }
		
	}

	/**
	 * execute the tests given as classnames.
	 *
	 * @param logger
	 *            The explicit logger to use
	 * @param sutName
	 *            The Name of the System under Test
	 * @param classnames
	 *            The classnames of the tests to execute
	 * @param paramJson
	 *            the test case parameters as a json value
	 * @param verdicts
	 *            the array of individual test case verdicts
	 */
	public void executeTests(final Logger logger, final String sutName, final String[] classnames, final String paramJson,
			final IVCT_Verdict verdicts[]) {
		int i = 0;

		for (final String classname : classnames) {
			AbstractTestCase testCase = null;
			try {
				testCase = (AbstractTestCase) Thread.currentThread().getContextClassLoader().loadClass(classname)
						.newInstance();
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
				logger.error("Could not instantiate " + classname + " !", ex);
			}
			if (testCase == null) {
				verdicts[i] = new IVCT_Verdict();
				verdicts[i].verdict = IVCT_Verdict.Verdict.INCONCLUSIVE;
				verdicts[i].text = "Could not instantiate " + classname;
				i++;
				continue;
			}
			testCase.setSutName(sutName);
			testCase.setTcName(classname);
			
			verdicts[i++] = testCase.execute(paramJson, logger);
		}
	}
	
	
	/*  implement a heartbeat
	 *  we instanciate CmdHeartbeatSend and set some variables there.
     *  When we call its execute method,
     *  CmdHeartbeatSend will send all 5 Seconds a message to ActiveMQ
     *  We can change the value of the variables to change the tenor of the message
     *  eg. heartbeatSend.setHealth(false)
     *  if this thread is stopped, CmdHeardbeatListen will give out an Alert-Status
     */
    
	public void sendHeartbeat(Logger _logger) throws Exception{ 
        
        this.health=true;

        CmdHeartbeatSend heartbeatSend = new CmdHeartbeatSend();

        // Basic Information for the Heartbeat
        heartbeatSend.setHeartbeatSender("TestRunner");     
        heartbeatSend.setHealth(this.health);
        
        heartbeatSend.execute();        

       // we check all 5 seconds our healthState and if it is false we inform CmdHeartbeatSend        
       Timer timer = new Timer();
       timer.schedule(new TimerTask() {
           @Override
           public void run() {
               
               if (getHealth()== false) {
                   _logger.warn("### TestRunner health State changed to false ### ");  // Debug
                 heartbeatSend.setHealth(getHealth() );
               }
               
           }
       }, 0, 5000);
       
       
    // --------------- for testing ------------------
       int count = 0;
       while (count < 15) {
           Thread.sleep(3000);
           //_logger.info("###  TestRunner CmdHeartbeatSend.health should be true ");  // Debug
           count++;
       }        
       _logger.info("### We change the CmdHeartbeatSend.health to false ");
       this.health=false;
              
       count = 0;
       while (count < 15) {
           Thread.sleep(3000);
           count++;
       }       
      System.exit(0) ;
      //-------------------------------------------------------

    }
    
    
   
    
    
    

    public boolean getHealth() {
        return health;
    }

    public void setHealth(boolean health) {
        this.health = health;
    }
    
}
