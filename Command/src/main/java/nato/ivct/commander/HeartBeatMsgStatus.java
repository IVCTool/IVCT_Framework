package nato.ivct.commander;

import java.io.Serializable;

public class HeartBeatMsgStatus implements Serializable {
    
	private static final long serialVersionUID = 1L;
	
	public static enum HbMsgState {
		UNKNOWN("unknown"), INTIME("intime"), TIMEOUT("time-out"), WAITING("waiting"), ALERT("alert"), FAIL("fail"), DEAD("dead"); 
		private String state;
		HbMsgState(String state_) {
			this.state = state_;
		}
		
		String state() {
			return state;
		}
	}
}
