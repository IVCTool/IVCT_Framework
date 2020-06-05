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

package nato.ivct.gui.client.outlines;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.client.ts.TsNodePage;
import nato.ivct.gui.shared.Icons;
import nato.ivct.gui.shared.cb.ITsService;

/**
 * <h3>{@link TsOutline}</h3>
 *
 * @author hzg
 */
@Order(1000)
public class TsOutline extends AbstractOutline {

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		Set<String> testsuites = BEANS.get(ITsService.class).loadTestSuites();
		testsuites.forEach(s -> {TsNodePage np = new TsNodePage(s); pageList.add(np);});
	}

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("Testsuites");
	}

	@Override
	protected String getConfiguredIconId() {
		return Icons.CategoryBold;
	}
}
