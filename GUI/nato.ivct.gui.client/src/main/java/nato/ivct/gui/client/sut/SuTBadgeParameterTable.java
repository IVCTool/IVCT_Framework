package nato.ivct.gui.client.sut;

import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractLongColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.text.TEXTS;

public class SuTBadgeParameterTable extends AbstractTable {
  
	  public ParentIdColumn getParentIdColumn() {
	      return getColumnSet().getColumnByClass(ParentIdColumn.class);
	  }

	  public IdColumn getIdColumn() {
		    return getColumnSet().getColumnByClass(IdColumn.class);
	  }
		
	  public ParameterNameColumn getParameterNameColumn() {
	    return getColumnSet().getColumnByClass(ParameterNameColumn.class);
	  }
		
	  public ParameterValueColumn getParameterValueColumn() {
	    return getColumnSet().getColumnByClass(ParameterValueColumn.class);
	  }

    @Override
    protected void execDecorateRow(ITableRow row) {
      if (getParameterValueColumn().getValue(row) == null) {
        row.setIconId("font:\uE001");
      }
    }

	  @Order(10)
	  public class IdColumn extends AbstractLongColumn {
	
	    @Override
	    protected boolean getConfiguredDisplayable() {
	      return false;
	    }
	
	    @Override
	    protected boolean getConfiguredPrimaryKey() {
	      return true;
	    }
	
	  }
	
	  @Order(15)
	  public class ParentIdColumn extends AbstractLongColumn {

      @Override
      protected boolean getConfiguredParentKey() {
        return true;
      }

	    @Override
	    protected boolean getConfiguredDisplayable() {
	      return false;
	    }
  	}

	
	@Order(1000)
	public class ParameterNameColumn extends AbstractStringColumn {

		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("ParameterName");
		}

		@Override
		protected int getConfiguredWidth() {
			return 200;
		}
	}

	@Order(2000)
	public class ParameterValueColumn extends AbstractStringColumn {
		@Override
		protected String getConfiguredHeaderText() {
			return TEXTS.get("ParameterValue");
		}

		@Override
		protected int getConfiguredWidth() {
			return 200;
		}
	}
}
