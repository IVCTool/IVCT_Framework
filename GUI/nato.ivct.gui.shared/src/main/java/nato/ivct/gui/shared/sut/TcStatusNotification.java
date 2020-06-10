/* Copyright 2020, Reinhard Herzog, Michael Theis (Fraunhofer IOSB)

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
	public void setSutId(final String sutId) {
		this.sutId = sutId;
	}
	public String getTcId() {
		return tcId;
	}
	public void setTcId(final String tcId) {
		this.tcId = tcId;
	}
	public int getPercent() {
		return percent;
	}
	public void setPercent(final int percent) {
		this.percent = percent;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(final String status) {
		this.status = status;
	}


}
