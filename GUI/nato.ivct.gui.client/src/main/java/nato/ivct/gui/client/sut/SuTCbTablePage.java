package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;
import org.eclipse.scout.rt.platform.text.TEXTS;

import nato.ivct.gui.shared.sut.SuTCbTablePageData;


@Data(SuTCbTablePageData.class)
public class SuTCbTablePage extends AbstractPageWithTable<SuTCbTablePage.Table> {

    private String sutId = null;

    private String badgeId = null;


    @Override
    protected String getConfiguredTitle() {
        // TODO [hzg] verify translation
        return TEXTS.get("CapabilityTablePage");
    }


    //	@Override
    //	protected void execLoadData(SearchFilter filter) {
    //		String[] searchText = new String[1];
    //		searchText[0] = getBadgeId();
    //		filter.setDisplayTexts(searchText);
    //		importPageData(BEANS.get(ISuTCbService.class).getSuTCbTableData(filter));
    //	}
    //
    //	@Override
    //	protected IPage<?> execCreateChildPage(ITableRow row) {
    //		SuTTcNodePage childPage = new SuTTcNodePage();
    //		childPage.setCbTestCase(getTable().getAbstractTCColumn().getValue(row));
    //		childPage.setRequirementId(getTable().getCapabilityIdColumn().getValue(row));
    //		childPage.setBadgeId(badgeId);
    //		childPage.setSutId(sutId);
    //		return childPage;
    //	}

    @Override
    protected boolean getConfiguredLeaf() {
        // do not show child notes
        return true;
    }


    @Override
    protected void execPageActivated() throws ProcessingException {
        if (getDetailForm() == null) {
            final SuTCbForm form = new SuTCbForm();
            form.setSutId(getSutId());
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

    public class Table extends AbstractTable {

        public AbstractTcColumn getAbstractTcColumn() {
            return getColumnSet().getColumnByClass(AbstractTcColumn.class);
        }


        public TcStatusColumn getTcStatusColumn() {
            return getColumnSet().getColumnByClass(TcStatusColumn.class);
        }


        public RequirementDescColumn getRequirementDescColumn() {
            return getColumnSet().getColumnByClass(RequirementDescColumn.class);
        }


        public RequirementIdColumn getCapabilityIdColumn() {
            return getColumnSet().getColumnByClass(RequirementIdColumn.class);
        }

        @Order(1000)
        public class RequirementIdColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("RequirementId");
            }


            @Override
            protected int getConfiguredWidth() {
                return 100;
            }
        }

        @Order(2000)
        public class RequirementDescColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("RequirementDescription");
            }


            @Override
            protected int getConfiguredWidth() {
                return 500;
            }
        }

        @Order(3000)
        public class AbstractTcColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("TC");
            }


            @Override
            protected int getConfiguredWidth() {
                return 100;
            }
        }

        @Order(4000)
        public class TcStatusColumn extends AbstractStringColumn {
            @Override
            protected String getConfiguredHeaderText() {
                return TEXTS.get("TCStatus");
            }


            @Override
            protected int getConfiguredWidth() {
                return 100;
            }
        }
    }


    public void setBadgeId(String _badgeId) {
        badgeId = _badgeId;
    }


    public String getBadgeId() {
        return badgeId;
    }


    public String getSutId() {
        return sutId;
    }


    public void setSutId(String _sutId) {
        sutId = _sutId;
    }
}
