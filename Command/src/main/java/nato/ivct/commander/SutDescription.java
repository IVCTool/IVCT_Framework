/* Copyright 2019, Johannes Mulder (Fraunhofer IOSB)

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

import java.util.HashSet;
import java.util.Set;

public class SutDescription {
		public String ID = new String();
		public String name = new String();
		public String version = new String();
		public String description = new String();
        public String vendor = new String();
        public String settingsDesignator = new String();
        public String federation = new String();
        public String sutFederateName = new String();
		public Set<String> badges = new HashSet<String>();
}
