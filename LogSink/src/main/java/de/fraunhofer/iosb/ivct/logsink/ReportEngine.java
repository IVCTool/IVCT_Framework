/*
Copyright 2017, Johannes Mulder (Fraunhofer IOSB)

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

package de.fraunhofer.iosb.ivct.logsink;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nato.ivct.commander.CmdStartTestResultListener.TcResult;

public class ReportEngine {
    Logger LOGGER = LoggerFactory.getLogger(ReportEngine.class);
    private boolean havePrevFile = false;
    private int numFailed = 0;
    private int numInconclusive = 0;
    private int numPassed = 0;
    private BufferedWriter writer = null;
    private Path file = null;
    private Path path;
    private String knownSut = new String();
    private String baseFileName = "Report";
	private final String dashes = "//------------------------------------------------------------------------------";
	private final String failedStr = "FAILED";
	private final String inconclusiveStr = "INCONCLUSIVE";
	private final String passedStr = "PASSED";

	public Map<String, String> status = new HashMap<String, String>();
	
    /**
     * 
     */
    public ReportEngine() {
    }


    private void openFile(String sutPath, String fName) {
    	Charset charset = Charset.forName("ISO-8859-1");

    	if (havePrevFile) {
    		closeFile();
    	}
    	file = FileSystems.getDefault().getPath(sutPath, fName);
    	try {
    		writer = Files.newBufferedWriter(file, charset);
    	} catch (IOException x) {
    		System.err.format("IOException: %s%n", x);
    	}
    	havePrevFile = true;
    }
    
    private void closeFile() {
    	try {
    		if (writer != null) {
    			writer.newLine();
    			writer.write(dashes, 0, dashes.length());
    			writer.newLine();
    			String verdicts = "// Verdicts: Passed: " + numPassed + " Failed: " + numFailed + " Inconclusive: " + numInconclusive;
    			writer.write(verdicts);
    			writer.newLine();
    			writer.write(dashes, 0, dashes.length());
    			writer.newLine();
    			writer.close();
    			if (numFailed == 0 && numInconclusive == 0 && numPassed == 0) {
    				Files.deleteIfExists(path);
    			}
    		}
    		numFailed = 0;
    		numInconclusive = 0;
    		numPassed = 0;
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }

	public void onQuit() {
		closeFile();
	}

	public void onResult(TcResult result) {
		LOGGER.info("ReportEngine:checkMessage: announceVerdict");
		if (result.sutName.equals(knownSut) == false) {
			try {
				doSutChanged(result);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			knownSut = result.sutName;
		}
		String testScheduleName = result.testScheduleName;
		String testcase = result.testcase;
		String verdict = result.verdict;
		switch (verdict) {
		case failedStr:
			numFailed++;
			break;
		case inconclusiveStr:
			numInconclusive++;
			break;
		case passedStr:
			numPassed++;
			break;
		}
		status.put(testcase, verdict);

		String verdictText = result.verdictText;
		String announceVerdict;
		if (testScheduleName.isEmpty()) {
			announceVerdict = "VERDICT: " + testScheduleName + "(single tc) " + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + "    " + verdictText;
		} else {
			announceVerdict = "VERDICT: " + testScheduleName + "." + testcase.substring(testcase.lastIndexOf(".") + 1) + " " + verdict + "    " + verdictText;
		}
		try {
			writer.write(announceVerdict, 0, announceVerdict.length());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    private void doSutChanged (TcResult result) throws IOException {
		LocalDateTime ldt = LocalDateTime.now();
		String formattedMM = String.format("%02d", ldt.getMonthValue());
		String formatteddd = String.format("%02d", ldt.getDayOfMonth());
		String formattedhh = String.format("%02d", ldt.getHour());
		String formattedmm = String.format("%02d", ldt.getMinute());
		LOGGER.info("ReportEngine:doSutChanged: setSUT " + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + " " + formattedhh + " " + formattedmm);
		String sut =  result.sutName;
		String sutPath =  result.sutDir;
		LOGGER.info("ReportEngine:doSutChanged: sutPath: " + sutPath);
    	String fName = baseFileName + "_" + ldt.getYear() + "-" + formattedMM + "-" + formatteddd + "_" + formattedhh + "-" + formattedmm + ".txt";
    	openFile(sutPath, fName);
    	path = FileSystems.getDefault().getPath(sutPath, fName);
    	writer.write(dashes, 0, dashes.length());
		writer.newLine();
		String a = "// SUT: " + sut + "             Date: " + ldt.getYear() + "-" + ldt.getMonthValue() + "-" + ldt.getDayOfMonth() + " " + ldt.getHour() + ":" + ldt.getMinute();
		writer.write(a, 0, a.length());
		writer.newLine();
    	writer.write(dashes, 0, dashes.length());
		writer.newLine();
		writer.newLine();
		writer.flush();
    }
}
