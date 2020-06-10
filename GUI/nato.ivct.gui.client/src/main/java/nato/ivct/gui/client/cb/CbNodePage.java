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

package nato.ivct.gui.client.cb;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class CbNodePage extends AbstractPageWithNodes {
	private String mBadgeId = null;
	private String mPageTitle = null;

	public CbNodePage() {
		mPageTitle = TEXTS.get("CbNodePage");
	}
	
	public CbNodePage(final String badgeId) {
		mPageTitle = mBadgeId = badgeId;
	}
	
	@Override
	protected String getConfiguredTitle() {
		return mPageTitle;
	}

	@Override
	protected void execCreateChildPages(List<IPage<?>> pageList) {
		super.execCreateChildPages(pageList);
	}

	@Override
	protected boolean getConfiguredLeaf() {
		// do not show child notes
		return true;
	}

	@Override
	protected void execPageActivated() throws ProcessingException {
	  if (getDetailForm() == null) {
	    CbForm form = new CbForm();
	    form.setCbId(getBadgeId());
	    setDetailForm(form);
	    form.startView();
	  }
	}
	
	@Override
	protected void execPageDeactivated() throws ProcessingException {
	  if (getDetailForm() != null) {
	    getDetailForm().doClose();
	    setDetailForm(null);
	  }
	}

	public String getBadgeId() {
		return mBadgeId;
	}

	public void setBadgeId(String badgeId) {
		this.mBadgeId = badgeId;
	}
}
