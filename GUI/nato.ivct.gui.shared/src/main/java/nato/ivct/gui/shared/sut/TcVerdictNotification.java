package nato.ivct.gui.shared.sut;

import java.io.Serializable;

public class TcVerdictNotification implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String sutId = null;
	private String tcId = null;
	private String verdict = null;
	private String text = null;
	
	public String getVerdict() {
		return verdict;
	}
	public void setVerdict(final String _verdict) {
		this.verdict = _verdict;
	}
	public String getTcId() {
		return tcId;
	}
	public void setTcId(final String _tcId) {
		this.tcId = _tcId;
	}
	public String getText() {
		return text;
	}
	public void setText(final String text) {
		this.text = text;
	}
	public String getSutId() {
		return sutId;
	}
	public void setSutId(final String _sutId) {
		this.sutId = _sutId;
	}

}
