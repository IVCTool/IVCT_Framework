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

package nato.ivct.gui.client.ts;

import java.util.List;

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class TsNodePage extends AbstractPageWithNodes {
	private String mTsId = null;
	private String mPageTitle = null;

	public TsNodePage() {
		mPageTitle = TEXTS.get("TsNodePage");
	}
	
	public TsNodePage(final String badgeId) {
		mPageTitle = mTsId = badgeId;
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
	    TsForm form = new TsForm();
	    form.setTsId(getTsId());
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

	public String getTsId() {
		return mTsId;
	}

	public void setTsId(String tsId) {
		this.mTsId = tsId;
	}
}
