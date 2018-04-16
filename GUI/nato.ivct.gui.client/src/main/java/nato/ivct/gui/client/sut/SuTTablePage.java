package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.client.sut.SuTNodePage;
import nato.ivct.gui.client.sut.SuTTablePage.Table;
import nato.ivct.gui.shared.sut.ISuTService;
import nato.ivct.gui.shared.sut.SuTTablePageData;

@Data(SuTTablePageData.class)
public class SuTTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		return TEXTS.get("SuTTablePage");
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(ISuTService.class).getSuTTableData(filter));
	}

	@Override
	protected IPage<?> execCreateChildPage(ITableRow row) {
		SuTNodePage childPage = new SuTNodePage();
		childPage.setSutId(getTable().getSuTidColumn().getValue(row));
		return childPage;
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return false;
	}

	public class Table extends AbstractTable {

		
		@Order(1000)
		public class EditMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Edit");
			}

			@Override
			protected void execAction() {
				SuTForm form = new SuTForm(getSuTidColumn().getSelectedValue());
				form.setSutId(getSuTidColumn().getSelectedValue());
				form.addFormListener(new SuTFormListener());
				form.startModify();
			}
		}

		@Order(2000)
		public class NewMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("New");
			}

			@Override
			protected void execAction() {
				SuTForm form = new SuTForm(getSuTidColumn().getSelectedValue());
				form.addFormListener(new SuTFormListener());
				form.startNew();
			}
		}

		private class SuTFormListener implements FormListener {

			@Override
			public void formChanged(FormEvent e) {
				// reload page to reflect new/changed data after saving any
				// changes
				if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
					reloadPage();
				}
			}
		}

		public VendorColumn getVendorColumn() {
			return getColumnSet().getColumnByClass(VendorColumn.class);
		}

		public SuTDescriptionColumn getSuTDescriptionColumn() {
			return getColumnSet().getColumnByClass(SuTDescriptionColumn.class);
		}

		public SuTidColumn getSuTidColumn() {
			return getColumnSet().getColumnByClass(SuTidColumn.class);
		}

		@Order(1000)
		public class SuTidColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("SuTID");
			}

			@Override
			protected boolean getConfiguredDisplayable() {
				return true;
			}

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}

			@Override
			protected int getConfiguredWidth() {
				return 100;
			}
		}

		@Order(2000)
		public class SuTDescriptionColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("SuTDescription");
			}

			@Override
			protected int getConfiguredWidth() {
				return 500;
			}
		}

		@Order(3000)
		public class VendorColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Vendor");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}

	}
}
