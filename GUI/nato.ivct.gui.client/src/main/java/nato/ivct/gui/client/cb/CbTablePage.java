package nato.ivct.gui.client.cb;

import org.eclipse.scout.rt.client.dto.Data;
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable;
import org.eclipse.scout.rt.client.ui.form.FormEvent;
import org.eclipse.scout.rt.client.ui.form.FormListener;
import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.shared.TEXTS;
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter;

import nato.ivct.gui.client.cb.CbTablePage.Table;
import nato.ivct.gui.shared.cb.CbTablePageData;
import nato.ivct.gui.shared.cb.ICbService;

@Data(CbTablePageData.class)
public class CbTablePage extends AbstractPageWithTable<Table> {

	@Override
	protected String getConfiguredTitle() {
		// TODO [hzg] verify translation
		return TEXTS.get("CbTablePage");
	}

	@Override
	protected void execLoadData(SearchFilter filter) {
		importPageData(BEANS.get(ICbService.class).getCbTableData(filter));
	}

	@Override
	protected boolean getConfiguredLeaf() {
		return true;
	}

	public class Table extends AbstractTable {

		@Override
		protected Class<? extends IMenu> getConfiguredDefaultMenu() {
			return EditMenu.class;
		}

		@Order(1000)
		public class EditMenu extends AbstractMenu {
			@Override
			protected String getConfiguredText() {
				return TEXTS.get("Edit");
			}

			@Override
			protected void execAction() {
				CbForm form = new CbForm();
				form.setCbId(getCpIdColumn().getSelectedValue());
				form.addFormListener(new CbFormListener());
				form.startModify();
			}
		}

		private class CbFormListener implements FormListener {

			@Override
			public void formChanged(FormEvent e) {
				// reload page to reflect new/changed data after saving any
				// changes
				if (FormEvent.TYPE_CLOSED == e.getType() && e.getForm().isFormStored()) {
					reloadPage();
				}
			}
		}

		public CapabilityNameColumn getCapabilityNameColumn() {
			return getColumnSet().getColumnByClass(CapabilityNameColumn.class);
		}

		public CapabilityDescriptionColumn getCapabilityDescriptionColumn() {
			return getColumnSet().getColumnByClass(CapabilityDescriptionColumn.class);
		}

		public CbVisualColumn getCbVisualColumn() {
			return getColumnSet().getColumnByClass(CbVisualColumn.class);
		}

		public CpIdColumn getCpIdColumn() {
			return getColumnSet().getColumnByClass(CpIdColumn.class);
		}

		@Order(1)
		public class CpIdColumn extends AbstractStringColumn {
			@Override
			protected boolean getConfiguredDisplayable() {
				return true;
			}

			@Override
			protected String getConfiguredHeaderText() {
				// TODO Auto-generated method stub
				return TEXTS.get("ID");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}

			@Override
			protected boolean getConfiguredPrimaryKey() {
				return true;
			}
		}

		@Order(2000)
		public class CapabilityNameColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("CapabilityName");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}

		@Order(3000)
		public class CapabilityDescriptionColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("Description");
			}

			@Override
			protected int getConfiguredWidth() {
				return 400;
			}
		}

		@Order(4000)
		public class CbVisualColumn extends AbstractStringColumn {
			@Override
			protected String getConfiguredHeaderText() {
				return TEXTS.get("CbVisual");
			}

			@Override
			protected int getConfiguredWidth() {
				return 200;
			}
		}

	}
}
