/* Copyright 2018, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.commander;

import org.json.simple.JSONObject;

// The badge with its TcParam name/value pairs
public class BadgeTcParam {
	public String id;
	public JSONObject tcParam = null;

	/**
	 * Default constructor
	 */
	public BadgeTcParam() {
	}

	/**
	 * Setter method for id
	 * @param id
	 * @return
	 */
	public BadgeTcParam setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Setter method for tcParam
	 * @param tcParam
	 * @return
	 */
	public BadgeTcParam setTcParam(JSONObject tcParam) {
		this.tcParam = tcParam;
		return this;
	}
}
