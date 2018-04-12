package nato.ivct.gui.shared.sut;

import javax.annotation.Generated;

import org.eclipse.scout.rt.shared.data.basic.table.AbstractTableRowData;
import org.eclipse.scout.rt.shared.data.page.AbstractTablePageData;

/**
 * <b>NOTE:</b><br>
 * This class is auto generated by the Scout SDK. No manual modifications
 * recommended.
 */
@Generated(value = "nato.ivct.gui.client.sut.SuTTablePage", comments = "This class is auto generated by the Scout SDK. No manual modifications recommended.")
public class SuTTablePageData extends AbstractTablePageData {

	private static final long serialVersionUID = 1L;

	@Override
	public SuTTableRowData addRow() {
		return (SuTTableRowData) super.addRow();
	}

	@Override
	public SuTTableRowData addRow(int rowState) {
		return (SuTTableRowData) super.addRow(rowState);
	}

	@Override
	public SuTTableRowData createRow() {
		return new SuTTableRowData();
	}

	@Override
	public Class<? extends AbstractTableRowData> getRowType() {
		return SuTTableRowData.class;
	}

	@Override
	public SuTTableRowData[] getRows() {
		return (SuTTableRowData[]) super.getRows();
	}

	@Override
	public SuTTableRowData rowAt(int index) {
		return (SuTTableRowData) super.rowAt(index);
	}

	public void setRows(SuTTableRowData[] rows) {
		super.setRows(rows);
	}

	public static class SuTTableRowData extends AbstractTableRowData {

		private static final long serialVersionUID = 1L;
		public static final String suTid = "suTid";
		public static final String suTDescription = "suTDescription";
		public static final String vendor = "vendor";
		private String m_suTid;
		private String m_suTDescription;
		private String m_vendor;

		public String getSuTid() {
			return m_suTid;
		}

		public void setSuTid(String newSuTid) {
			m_suTid = newSuTid;
		}

		public String getSuTDescription() {
			return m_suTDescription;
		}

		public void setSuTDescription(String newSuTDescription) {
			m_suTDescription = newSuTDescription;
		}

		public String getVendor() {
			return m_vendor;
		}

		public void setVendor(String newVendor) {
			m_vendor = newVendor;
		}
	}
}
