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

package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.shared.sut.ISuTBadgeService;
import nato.ivct.gui.shared.sut.SuTBadgeTablePageData;


@Data(SuTBadgeTablePageData.class)
public class SuTBadgeTablePage extends AbstractPageWithTable<SuTBadgeTablePage.Table> {

    private String mSutId     = null;
    private String mPageTitle = null;


    public SuTBadgeTablePage() {
        mPageTitle = TEXTS.get("SuTBadgeTablePage");
    }


    public SuTBadgeTablePage(final String sutId) {
        mPageTitle = mSutId = sutId;
    }


    @Override
    protected String getConfiguredTitle() {
        return mPageTitle;
    }


    @Override
    protected boolean getConfiguredLeaf() {
        // show children if any
        return false;
    }


    @Override
    protected void execLoadData(SearchFilter filter) {
        final String[] searchText = new String[1];
        searchText[0] = getSutId();
        filter.setDisplayTexts(searchText);
        importPageData(BEANS.get(ISuTBadgeService.class).getSuTBadgeTableData(filter));
    }


    @Override
    protected IPage<?> execCreateChildPage(ITableRow row) {
        final SuTCbNodePage childPage = new SuTCbNodePage();
        childPage.setBadgeId(getTable().getBadgeIdColumn().getValue(row));
        childPage.setSutId(getSutId());
        return childPage;
    }


    @Override
    protected void execPageActivated() throws ProcessingException {
        if (getDetailForm() == null) {
            final SuTForm form = new SuTForm("");
            form.setSutId(getSutId());
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

    public class Table extends AbstractTable {

        public BadgeNameColumn getBadgeNameColumn() {
            return getColumnSet().getColumnByClass(BadgeNameColumn.class);
        }


        public SuTBadgeResultColumn getSuTBadgeResultColumn() {
            return getColumnSet().getColumnByClass(SuTBadgeResultColumn.class);
        }


        public BadgeDescColumn getBadgeDescColumn() {
            return getColumnSet().getColumnByClass(BadgeDescColumn.class);
        }


        public BadgeIdColumn getBadgeIdColumn() {
            return getColumnSet().getColumnByClass(BadgeIdColumn.class);
        }

        @Order(1000)
        public class BadgeIdColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("BadgeId");
            }


            @Override
            protected int getConfiguredWidth() {
                return 200;
            }
        }

        @Order(1500)
        public class BadgeNameColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("BadgeName");
            }


            @Override
            protected int getConfiguredWidth() {
                return 100;
            }
        }

        @Order(2000)
        public class BadgeDescColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("BadgeDescription");
            }


            @Override
            protected int getConfiguredWidth() {
                return 500;
            }
        }

        @Order(3000)
        public class SuTBadgeResultColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("BadgeTestResult");
            }


            @Override
            protected int getConfiguredWidth() {
                return 100;
            }
        }
    }


    public void setSutId(String sutId) {
        mSutId = sutId;
    }


    public String getSutId() {
        return mSutId;
    }
}
