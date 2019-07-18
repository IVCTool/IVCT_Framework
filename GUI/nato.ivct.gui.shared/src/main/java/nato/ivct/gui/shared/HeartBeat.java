package nato.ivct.gui.shared;

import java.io.Serializable;

public class HeartBeat implements Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String messageState = null;
    public String alertTime = null;
    public String heartBeatSender = null;
    public long lastSendingPeriod = (long) 0;
    public String lastSendingTime = null;
    public boolean senderHealthState = false;
}
