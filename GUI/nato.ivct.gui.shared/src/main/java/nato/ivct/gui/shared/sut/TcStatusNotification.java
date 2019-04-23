/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.shared.sut;

import java.io.Serializable;

public class TcStatusNotification implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private String sutId = null;
	private String tcId = null;
	private String status = null;
	private int percent = 0;
	
	public String getSutId() {
		return sutId;
	}
	public void setSutId(final String _sutId) {
		this.sutId = _sutId;
	}
	public String getTcId() {
		return tcId;
	}
	public void setTcId(final String _tcId) {
		this.tcId = _tcId;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(final int _percent) {
		this.percent = _percent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(final String _status) {
		this.status = _status;
	}


}
