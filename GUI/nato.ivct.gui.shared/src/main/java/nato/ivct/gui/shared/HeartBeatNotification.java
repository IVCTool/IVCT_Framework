/* Copyright 2020, Michael Theis (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

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
    
	public enum HbNotificationState {
		UNKNOWN("grey"), OK("green"), WARNING("yellow"), CRITICAL("red"), DEAD("black"); 
		private String state;
		HbNotificationState(String state) {
			this.state = state;
		}
		
		String state() {
			return state;
		}
	}
}
