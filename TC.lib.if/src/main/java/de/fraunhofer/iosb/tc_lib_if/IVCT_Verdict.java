/*
Copyright 2015, Johannes Mulder (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package de.fraunhofer.iosb.tc_lib_if;

public class IVCT_Verdict {
	public enum Verdict {
		PASSED, FAILED, INCONCLUSIVE;
	}

	public Verdict verdict;
	public String text = "ok";
	

	public String toString() {
		String result;
		switch (this.verdict) {
		case PASSED:
			result = "PASSED";
		case FAILED:
			result = "FAILED";
		case INCONCLUSIVE:
			result = "INCONCLUSIVE";
		default:
			result = "UNDEFINED";
		}
		return result + ": " + text;
	}
}
