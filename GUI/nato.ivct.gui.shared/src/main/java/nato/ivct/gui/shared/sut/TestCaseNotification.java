package nato.ivct.gui.shared.sut;

import java.io.Serializable;

public class TestCaseNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sut = null;
	private String tc = null;
	private String verdict = null;
	private String text = null;
	
	public String getVerdict() {
		return verdict;
	}
	public void setVerdict(String verdict) {
		this.verdict = verdict;
	}
	public String getTc() {
		return tc;
	}
	public void setTc(String tc) {
		this.tc = tc;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getSut() {
		return sut;
	}
	public void setSut(String sut) {
		this.sut = sut;
	}

}
