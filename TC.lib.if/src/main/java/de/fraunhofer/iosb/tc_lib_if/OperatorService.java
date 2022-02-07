/*
 * Copyright 2021, Reinhard Herzog (Fraunhofer IOSB) Licensed under the Apache
 * License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0 Unless required by applicable law
 * or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package de.fraunhofer.iosb.tc_lib_if;

public interface OperatorService {

    public OperatorService initialize (String sutName, String testSuiteId, String tc, String tcLabel); // must be called before anything else
    public void sendOperatorMsgAndWaitConfirmation(String text) throws TcInconclusiveIf;
    public void sendTcStatus(String status, int percent);
}
