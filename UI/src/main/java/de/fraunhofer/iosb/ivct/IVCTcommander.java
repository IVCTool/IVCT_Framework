/*
Copyright 2016, Johannes Mulder (Fraunhofer IOSB)
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.ivct;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.json.simple.JSONObject;

import nato.ivct.commander.CmdOperatorConfirmation;
import nato.ivct.commander.CmdOperatorRequestListener.OnOperatorRequestListener;
import nato.ivct.commander.CmdOperatorRequestListener.OperatorRequestInfo;
import nato.ivct.commander.CmdHeartbeatListen;
import nato.ivct.commander.CmdHeartbeatSend;
import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;
import nato.ivct.commander.HeartBeatMsgStatus.HbMsgState;
import nato.ivct.commander.Factory;

/**
 * IVCTcommander takes user input strings, creates and sends messages to the JMS bus,
 * listens to the JMS bus and forwards the messages via callbacks to the user
 * interface.
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCTcommander implements OnResultListener, CmdHeartbeatListen.OnCmdHeartbeatListen, OnOperatorRequestListener {
	private Map<String, String> uiHeartbeatDataMap = new HashMap<String, String>();

	public Map<String, String> getHeartBeatSenders() {
		return uiHeartbeatDataMap;
	}

    private boolean firstTime = true;
	private static Vector<String> listOfVerdicts = new Vector<String>();
    public RuntimeParameters rtp = new RuntimeParameters();
    private static CmdHeartbeatListen heartbeatListener;
    private boolean gotOperatorRequest = false;
    private String sutName;
    private String testSuiteId;
    private String tcName;
    private String text;

    // You can choose a special  Class to be monitored
    //private static String desiredHeartBeatSenderClass="Use_CmdHeartbeatSend";
    //private static String desiredHeartBeatSenderClass="TestRunner";

    /**
     * public constructor.
     *
     * @throws IOException problems with loading properties
     */
    public IVCTcommander() throws IOException {
        new CmdStartTestResultListener(this);
        // creating a instance of this 'client'  to  deliver it to the listener
        heartbeatListener = new CmdHeartbeatListen(this);

        // instantiating a new heartbeatListener and deliver this client  without a special Sender-class to observe
        //CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(querryClient);

        // instantiating a new heartbeatListener and deliver this client  an  a special Sender-class to observe
//        CmdHeartbeatListen heartbeatListener = new CmdHeartbeatListen(this, desiredHeartBeatSenderClass );

        heartbeatListener.execute();
    }

    public void addTestSessionSeparator() {
    	String blank = new String(" ");
		listOfVerdicts.addElement(blank);    	
    }

      public void listVerdicts(final String sutName) {
			System.out.println("SUT: " + sutName);
			if (listOfVerdicts.isEmpty()) {
	            System.out.println("--No verdicts found--");
			}
	        Iterator<String> itr = listOfVerdicts.iterator();
	        while(itr.hasNext()){
	            System.out.println(itr.next());
	        }
      }

      public void resetSUT() {
    	  listOfVerdicts.clear();
      }

	@Override
	public void hearHeartbeat(JSONObject heartBeat) {

		try {
			String heartbeatSenderName = (String) heartBeat.getOrDefault(CmdHeartbeatSend.HB_SENDER, "");
			String notifyState = (String)heartBeat.getOrDefault(CmdHeartbeatSend.HB_MESSAGESTATE, HbMsgState.UNKNOWN);
			if (heartbeatSenderName != null) {
			    uiHeartbeatDataMap.put(heartbeatSenderName, notifyState);
			}
		}
		catch (Exception exc) {
			exc.printStackTrace();
		}

	}

    public void onResult(TcResult result) {
		String testSchedule = rtp.getTestScheduleName();
		String testcase =  result.testcase;
		String verdict =  result.verdict;
		String verdictText =  result.verdictText;
 		if (testcase != null && verdict != null && verdictText != null) {
			System.out.println("The verdict is: " + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + " " + verdictText);
		}
		System.out.println("\n");
		String verdictStr = null;
		if (testSchedule == null) {
			verdictStr = new String("(single tc) " + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
		} else {
			verdictStr = new String(testSchedule + "." + testcase.substring(testcase.lastIndexOf(".") + 1) + '\t' + verdict + '\t' + verdictText);
		}
		if (firstTime) {
			String testSuiteStr = new String("Verdicts are:");
			listOfVerdicts.addElement(testSuiteStr);
			addTestSessionSeparator();
			firstTime = false;
		}
		listOfVerdicts.addElement(verdictStr);
		rtp.setTestCaseRunningBool(false);
		rtp.releaseSemaphore();
    }


    public void onOperatorRequest(OperatorRequestInfo operatorRequestInfo) {
    	sutName = operatorRequestInfo.sutName;
    	testSuiteId = operatorRequestInfo.testSuiteId;
    	tcName = operatorRequestInfo.testCaseId;
    	text = operatorRequestInfo.text;
		System.out.println("Operator request: " + operatorRequestInfo.testCaseId + " " + operatorRequestInfo.text);
		gotOperatorRequest = true;
    }

    public void sendOperatorConfirmation(boolean confirmationBoolean, String text) {
    	CmdOperatorConfirmation operatorConfirmationCmd = Factory.createCmdOperatorConfirmation(sutName, testSuiteId, tcName, confirmationBoolean, text);
    	operatorConfirmationCmd.execute();
		gotOperatorRequest = false;
    }

    public String getOperatorRequestTcId() {
    	return tcName;
    }

    public String getOperatorRequestText() {
    	return text;
    }

    public boolean isOperatorRequestOutstanding() {
    	return gotOperatorRequest;
    }
}