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
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdStartTestResultListener;
import nato.ivct.commander.CmdStartTestResultListener.OnResultListener;
import nato.ivct.commander.CmdStartTestResultListener.TcResult;

/**
 * IVCTcommander takes user input strings, creates and sends messages to the JMS bus,
 * listens to the JMS bus and forwards the messages via callbacks to the user
 * interface.
 *
 * @author Johannes Mulder (Fraunhofer IOSB)
 */
public class IVCTcommander implements OnResultListener {

	private static Vector<String> listOfVerdicts = new Vector<String>();
    public RuntimeParameters rtp = new RuntimeParameters();
    private CmdStartTestResultListener cmdStartTestResultListener;

    /**
     * public constructor.
     *
     * @throws IOException problems with loading properties
     */
    public IVCTcommander() throws IOException {
        cmdStartTestResultListener = new CmdStartTestResultListener(this);
    }

    public void addTestSessionSeparator() {
    	String blank = new String(" ");
		listOfVerdicts.addElement(blank);    	
    }

      public void listVerdicts() {
			System.out.println("SUT: " + rtp.getSutName());
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

    public void onResult(TcResult result) {
		String testSchedule = RuntimeParameters.getTestScheduleName();
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
		if (rtp.checkTestSuiteNameNew()) {
			String testSuiteStr = new String("Verdicts are:");
			listOfVerdicts.addElement(testSuiteStr);
			addTestSessionSeparator();
			rtp.setTestSuiteNameUsed();
		}
		listOfVerdicts.addElement(verdictStr);
		rtp.setTestCaseRunningBool(false);
		rtp.releaseSemaphore();
    }

}