/* Copyright 2020, Felix Schoeppenthau (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.client;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import nato.ivct.gui.shared.HeartBeatNotification;


public class TestEngineLookupCall extends LocalLookupCall<String> {

    private static final long serialVersionUID = 1L;

    @Override
    protected List<LookupRow<String>> execCreateLookupRows() {
        return HeartBeatNotificationHandler.getHbLastReceivedMap().values().stream().filter(TestEngineLookupCall::isActiveTestEngine).map(HeartBeatNotification::getTestEngineLabel).sorted().map(testEngine -> new LookupRow<String>(testEngine, testEngine)).collect(Collectors.toList());
    }

    private static boolean isActiveTestEngine(final HeartBeatNotification notification) {
        if (("notNecessaryForLogSink").equalsIgnoreCase(notification.getTestEngineLabel()))
            return false;

        switch (notification.getNotificationState()) {
            case OK:
            case WARNING:
                return true;
            default:
                return false;
        }
    }
    
    public String getInitialTestEngine() {
        List<LookupRow<String>> lookupRow = execCreateLookupRows();
        if (!lookupRow.isEmpty()) {
            LookupRow<String> initialTestEngine = lookupRow.get(0);
            return initialTestEngine.getKey();
        }
        return "";
    }
}
