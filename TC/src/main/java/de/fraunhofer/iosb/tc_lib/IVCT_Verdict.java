package de.fraunhofer.iosb.tc_lib;

public class IVCT_Verdict {
	public enum Verdict {
	    PASSED, FAILED, INCONCLUSIVE 
	}

	public Verdict verdict;
	public String text;
	
	public String toJson(final String testcase, int counter) {
		String s = new String();
		switch (verdict) {
		case PASSED:
			s = "PASSED";
			text = "ok";
			break;
		case FAILED:
			s = "FAILED";
			break;
		default:
			s = "INCONCLUSIVE";
			break;
		}
		String str = new String("{ \"commandType\" : \"announceVerdict\", \"sequence\" : \"" + counter + "\", \"testcase\" : \"" + testcase + "\", \"verdict\" : \"" + s + "\", \"verdictText\" : \"" + this.text + "\" }");
		return str;
	}
}
