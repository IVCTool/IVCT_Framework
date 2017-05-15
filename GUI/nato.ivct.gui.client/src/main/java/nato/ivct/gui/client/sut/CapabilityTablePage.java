package nato.ivct.gui.client.sut;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.eclipse.scout.rt.client.context.ClientRunContext;
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
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.job.Jobs;
import org.eclipse.scout.rt.platform.util.CollectionUtility;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.client.sut.CapabilityTablePage.Table;
import nato.ivct.gui.shared.sut.CapabilityTablePageData;
import nato.ivct.gui.shared.sut.ICapabilityService;

@Data(CapabilityTablePageData.class)
public class CapabilityTablePage extends AbstractPageWithTable<Table> {

	private String sutId = null;

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
		importPageData(BEANS.get(ICapabilityService.class).getCapabilityTableData(filter));
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
				Jobs.schedule(new IRunnable() {
					@Override
					public void run() throws Exception {
						
						// use ModelJobs to synchronize with GUI
						ModelJobs.schedule(new IRunnable() {

							@Override
							public void run() throws Exception {
								List<ITableRow> tcArray = getSelectedRows();
								for (ITableRow tr : tcArray) {
									List<Object> tc = tr.getKeyValues();
									tr.setCellValue(4, "starting");
									tr.setBackgroundColor("FFA500");
									for (Object o : tc) {
										String s = o.toString();
									}
								}
							}
						}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
					}
				}, Jobs.newInput().withName("Executing Test cases").withRunContext(ClientRunContexts.copyCurrent()));
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
				return TEXTS.get("ATC");
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
