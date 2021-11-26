/*
 * Copyright 2021, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */


package de.fraunhofer.iosb.testrunner;

import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.tc_lib_if.OperatorService;
import de.fraunhofer.iosb.tc_lib_if.TcInconclusive;
import nato.ivct.commander.CmdOperatorConfirmation;
import nato.ivct.commander.CmdOperatorConfirmationListener;
import nato.ivct.commander.CmdOperatorRequest;
import nato.ivct.commander.Factory;
import nato.ivct.commander.CmdOperatorConfirmationListener.OnOperatorConfirmationListener;
import nato.ivct.commander.CmdOperatorConfirmationListener.OperatorConfirmationInfo;


public class OperatorServiceImpl implements OperatorService {
    private Semaphore semOperatorRequest = new Semaphore(0);
    OperatorConfirmationInfo confirmation = null;
    private String testEngineLabel;

    class TcOnOperatorConfirmationListener implements OnOperatorConfirmationListener {
        @Override
        public void onOperatorConfirmation(OperatorConfirmationInfo operatorConfirmationInfo) {
            confirmation = operatorConfirmationInfo;
            Logger tcLogger = LoggerFactory.getLogger(operatorConfirmationInfo.testCaseId);

            if (operatorConfirmationInfo.testEngineLabel != null) {
                tcLogger.info("Testengine.onOperatorConfirmation get OperatorConfirmationInfo.testEngineLabel: " + operatorConfirmationInfo.testEngineLabel);
        
                if (!verifyTestEngineLabel("onOperatorConfirmation", tcLogger, operatorConfirmationInfo.testEngineLabel)) {
                    return;
                }
            }
            tcLogger.info("OperatorConfirmation: {}, Text: {}", operatorConfirmationInfo.confirmationBool, operatorConfirmationInfo.text);
            semOperatorRequest.release();            
        }
        private boolean verifyTestEngineLabel(String requestingMethod, Logger tcLogger, String testEngineLabel_) {
            boolean competence = true;
            if (!(testEngineLabel_.equals(testEngineLabel) || testEngineLabel_.equals(Factory.TESTENGINE_LABEL_DEFLT))) {
                tcLogger.debug("TestEngine." + requestingMethod + ": This Job is not for this TestEngine - do not accept\"  ");
                competence = false;
            }
            return competence;
        }
    }

    @Override
    public void sendOperatorMsgAndWaitConfirmation(String sutName, String testSuiteId, String tc, String text) throws TcInconclusive {
        // create listener before sending operator message
        TcOnOperatorConfirmationListener operatorListener = new TcOnOperatorConfirmationListener();
		(new CmdOperatorConfirmationListener(operatorListener)).execute();

    	CmdOperatorRequest operatorRequestCmd = Factory.createCmdOperatorRequest(sutName, testSuiteId, tc, text);
    	operatorRequestCmd.execute();
    	
        try {
            semOperatorRequest.acquire();
        } catch (InterruptedException interrupt) {
            throw new TcInconclusive("Test case aborted", interrupt);
        }
        
    	if (confirmation.confirmationBool == false) {
    		if (confirmation.text != null) {
                throw new TcInconclusive("Operator reject message: " + confirmation.text);
    		} else {
                throw new TcInconclusive("Operator reject message: - no reject text -");
    		}
    	} 
    }


    public OperatorServiceImpl (String myLabel) {
        testEngineLabel = myLabel;
    }
}
