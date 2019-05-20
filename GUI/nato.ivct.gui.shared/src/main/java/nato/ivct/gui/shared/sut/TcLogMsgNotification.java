package nato.ivct.gui.shared.sut;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TcLogMsgNotification implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
	
	private String sutId = null;
	private String badgeId = null;
	private String tcId = null;
	private String logMsg = null;
	private String logLevel = null;
	private String timeStamp = null;
	
	public String getSut() {
		return sutId;
	}
	public void setSutId(final String _sutId) {
		sutId = _sutId;
	}
	
	public String getBadgeId() {
		return badgeId;
	}
	public void setBadgeId(final String _badgeId) {
		badgeId = _badgeId;
	}
	
	public String getTcId() {
		return tcId;
	}
	public void setTcId(final String _tcId) {
		tcId = _tcId; 
	}
	
	public String getLogMsg() {
		return logMsg;
	}
	public void setLogMsg(final String _msg) {
		logMsg = _msg;
	}
	
	public String getLogLevel() {
		return logLevel;
	}
	public void setLogLevel(final String _logLevel) {
		logLevel = _logLevel;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(final long _timeStamp) {
		timeStamp = dateFormatter.format(new Date(_timeStamp));
	}
}
