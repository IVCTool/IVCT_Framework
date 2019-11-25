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

import ch.qos.logback.core.util.SystemInfo;
import de.fraunhofer.iosb.tc_lib.AbstractTestCase;
import de.fraunhofer.iosb.tc_lib.IVCT_Verdict;

import nato.ivct.commander.CmdHeartbeatSend;



/**
 * Simple test environment. The TestRunner takes the classnames of the tests as
 * commandline arguments and then executes the tests in the given order.
 *
 * @author sen (Fraunhofer IOSB)
 */
public class TestRunner implements CmdHeartbeatSend.OnCmdHeartbeatSend {
    
    
    protected boolean health = true;
    protected String myClassName;
    
    public TestRunner() {
    	myClassName = this.getClass().getSimpleName();
    }
    

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
	
	
	/*  implement a heartbeat ,  brf 05.07.2019 (Fraunhofer IOSB)
	 *  we instanciate CmdHeartbeatSend and deliver a instance of this class
     *  When we call its execute method,
     *  CmdHeartbeatSend will fetch all 5 Seconds the health state from  'here'
     *  and send all 5 Seconds a message to ActiveMQ
     *  So if the value for health is changed here, this will change the tenor 
     *  of the message  CmdHeartbeatSend  sends to ActiveMQ
     *  if this thread is stopped, CmdHeardbeatListen will give out an Alert-Status
     */
    
	public void sendHeartbeat(Logger _logger) throws Exception{ 
        
        this.health=true; 

        System.out.println("Start_CmdHeartbeatListener_main create his instance: " +this);    // Debug
        CmdHeartbeatSend heartbeatSend = new  CmdHeartbeatSend(this);
        
        heartbeatSend.execute();
        
       
    // --------------- for testing ------------------
//       int count = 0;
//       while (count < 10) {
//           Thread.sleep(3000);
//           //_logger.info("###  TestRunner CmdHeartbeatSend.health should be true ");  // Debug
//           count++;
//       }        
//       _logger.info("### For Testing - we change the CmdHeartbeatSend.health to false ");
//       this.health=false;
//              
//       count = 0;
//       while (count < 10) {
//           Thread.sleep(3000);
//           count++;
//       }       
//      System.exit(0) ;
      //-------------------------------------------------------

    }
    

    
	public String getMyClassName() {
        return myClassName;
    }
    

    public boolean getMyHealth() {
        return health;
    }
    
}