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

package nato.ivct.gui.server.sut;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.server.services.lookup.AbstractLookupService;
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall;
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow;
import org.eclipse.scout.rt.shared.services.lookup.LookupRow;

import nato.ivct.gui.server.cb.CbService;
import nato.ivct.gui.shared.cb.ICbService;
import nato.ivct.gui.shared.sut.ISuTCbLookupService;


public class SuTCbLookupService extends AbstractLookupService<String> implements ISuTCbLookupService {

    @Override
    public List<? extends ILookupRow<String>> getDataByKey(ILookupCall<String> call) {
        return CollectionUtility.emptyArrayList();
    }


    @Override
    public List<? extends ILookupRow<String>> getDataByRec(ILookupCall<String> call) {
        return CollectionUtility.emptyArrayList();
    }


    @Override
    public List<? extends ILookupRow<String>> getDataByText(ILookupCall<String> call) {
        return CollectionUtility.emptyArrayList();
    }


    @Override
    public List<? extends ILookupRow<String>> getDataByAll(ILookupCall<String> call) {
        ArrayList<LookupRow<String>> cbList = CollectionUtility.emptyArrayList();
        CbService cbService = (CbService) BEANS.get(ICbService.class);

        cbService.loadBadges().forEach(id-> {
        	LookupRow<String> lookupRow = new LookupRow<>(id, cbService.getBadgeDescription(id).ID);
            cbList.add(lookupRow);
        });
        
        return cbList;
    }
}
