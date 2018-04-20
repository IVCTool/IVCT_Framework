package nato.ivct.gui.client.sut;

import java.util.List;
import java.util.Set;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenuType;
import org.eclipse.scout.rt.client.ui.action.menu.TableMenuType;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.client.ResourceBase;
import nato.ivct.gui.shared.sut.ISuTCbService;
import nato.ivct.gui.shared.sut.SuTCbTablePageData;

@Data(SuTCbTablePageData.class)
public class SuTCbTablePage extends AbstractPageWithTable<SuTCbTablePage.Table> {

	private String sutId = null;
	// private TestCaseResultHandler resultHandler = null;

	@Override
	protected String getConfiguredTitle() {
		// TODO [hzg] verify translation
		return TEXTS.get("CapabilityTablePage");
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		String[] searchText = new String[1];
		searchText[0] = sutId;
		filter.setDisplayTexts(searchText);
		importPageData(BEANS.get(ISuTCbService.class).getSuTCbTableData(filter));
	}
	
	@Override
	protected IPage<?> execCreateChildPage(ITableRow row) {
		SuTCbNodePage childPage = new SuTCbNodePage();
		childPage.setSuTCbBadgeId(getTable().getBadgeIdColumn().getValue(row));
		return childPage;
	}

	@Override
	protected boolean getConfiguredLeaf() {
		// TODO Auto-generated method stub
		return false;
	}

	public class Table extends AbstractTable {

		@Order(1000)
		public class TCexecMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("TCexec");
			}

			@Override
			protected Set<? extends IMenuType> getConfiguredMenuTypes() {
				return CollectionUtility.hashSet(TableMenuType.SingleSelection, TableMenuType.MultiSelection);
			}

			@Override
			protected void execAction() {
				// start the result handler
				// resultHandler = new TestCaseResultHandler();

				// use ModelJobs to asynchronously start test case execution
				// sequence
				ModelJobs.schedule(new IRunnable() {

					@Override
					public void run() throws Exception {
						ISuTCbService cbService = BEANS.get(ISuTCbService.class);
						List<ITableRow> tcArray = getSelectedRows();
						for (ITableRow tr : tcArray) {
							tr.setCellValue(4, "starting");
							tr.setBackgroundColor(ResourceBase.RUNNING);
							String badge = tr.getCell(0).toString();
							String tcName = tr.getCell(3).toString();
							cbService.executeTestCase(sutId, tcName, badge);
						}
					}
				}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
			}
		}

		public AbstractTCColumn getAbstractTCColumn() {
			return getColumnSet().getColumnByClass(AbstractTCColumn.class);
		}

		public TCresultColumn getTCresultColumn() {
			return getColumnSet().getColumnByClass(TCresultColumn.class);
		}

		public RequirementDescColumn getRequirementDescColumn() {
			return getColumnSet().getColumnByClass(RequirementDescColumn.class);
		}

		public BadgeIdColumn getBadgeIdColumn() {
			return getColumnSet().getColumnByClass(BadgeIdColumn.class);
		}

		public RequirementIdColumn getCapabilityIdColumn() {
			return getColumnSet().getColumnByClass(RequirementIdColumn.class);
		}

		@Order(1000)
		public class BadgeIdColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Badge");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}

		@Order(2000)
		public class RequirementIdColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Requirement");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(3000)
		public class RequirementDescColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Description");
			}

			@Override
			protected int getConfiguredWidth() {
				return 500;
			}
		}

		@Order(4000)
		public class AbstractTCColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("TC");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(5000)
		public class TCresultColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("TCResult");
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}
	}

	public void setSutId(String _sutId) {
		sutId = _sutId;
	}

	public String getSutId() {
		return sutId;
	}
}
