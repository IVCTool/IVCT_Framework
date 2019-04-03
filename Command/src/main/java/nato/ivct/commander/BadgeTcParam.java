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
	private String id;
	private JSONObject tcParam = null;

	/**
	 * Default constructor
	 */
	public BadgeTcParam() {
	}

	/**
	 * Get the id value.
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter method for id
	 * @param id Badge Id
	 * @return the tcParam
	 */
	public BadgeTcParam setId(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Get the tcParam value.
	 * @return the tcParam
	 */
	public JSONObject getTcParam() {
		return tcParam;
	}

	/**
	 * Setter method for tcParam
	 * @param tcParam TcParam Object
	 * @return the tcParam
	 */
	public BadgeTcParam setTcParam(JSONObject tcParam) {
		this.tcParam = tcParam;
		return this;
	}
}
