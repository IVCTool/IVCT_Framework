package nato.ivct.gui.shared;

import java.io.Serializable;

public class HeartBeatNotification implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * message elements
	 */
	public HbNotificationState notifyState = HbNotificationState.UNKNOWN;
    public String alertTime = null;
    public String heartBeatSender = null;
    public long lastSendingPeriod = 0;
    public String lastSendingTime = null;
    public boolean senderHealthState = false;
    public String comment = null;
    
	public static enum HbNotificationState {
		UNKNOWN("grey"), OK("green"), WARNING("yellow"), CRITICAL("red"), DEAD("black"); 
		private String state;
		HbNotificationState(String state_) {
			this.state = state_;
		}
		
		String state() {
			return state;
		}
	}
}
