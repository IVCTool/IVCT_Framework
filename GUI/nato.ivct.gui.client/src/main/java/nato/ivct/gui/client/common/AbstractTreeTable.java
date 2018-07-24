/*******************************************************************************
 * Copyright (c) 2014 BSI Business Systems Integration AG.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BSI Business Systems Integration AG - initial API and implementation
 ******************************************************************************/
package nato.ivct.gui.client.common;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.scout.rt.client.ui.basic.cell.Cell;
import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow;
import org.eclipse.scout.rt.client.ui.basic.table.ITableRowFilter;
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractColumn;
import org.eclipse.scout.rt.client.ui.basic.table.columns.IColumn;
import org.eclipse.scout.rt.platform.Order;
import org.eclipse.scout.rt.platform.exception.ProcessingException;

/**
 * Specific type of table that provides a tree-table behavior.
 *
 * @author jbr
 */
public abstract class AbstractTreeTable extends AbstractTable {
  //TODO: because of Bug 455478, it is not possible to have a generic parameter <KEY_TYPE> on this class.

  private final Set<Object> m_collapsedRowsKey = new HashSet<Object>();

  @Override
  protected boolean getConfiguredSortEnabled() {
    return false;
  }

  @Override
  protected void execInitTable() throws ProcessingException {
    // Add a row filter that simulates "collapsing" rows
    addRowFilter(new ITableRowFilter() {

      @Override
      public boolean accept(ITableRow row) {
        Object parentKey = getParentKeyColumn().getValue(row);
        while (parentKey != null) {
          if (isRowCollapsed(parentKey)) {
            return false;
          }
          ITableRow parentRow = findRowByKey(parentKey);
          parentKey = getParentKeyColumn().getValue(parentRow);
        }
        return true;
      }
    });
  }

  @Override
  protected void execDecorateCell(Cell view, ITableRow row, IColumn<?> col) throws ProcessingException {
    if (getColumnSet().getFirstVisibleColumn() == col) {
      StringBuilder prefix = new StringBuilder();
      Object parentKey = getParentKeyColumn().getValue(row);
      while (parentKey != null) {
        prefix.append("    ");
        ITableRow parentRow = findRowByKey(parentKey);
        parentKey = getParentKeyColumn().getValue(parentRow);
      }
      if (execIsNode(row)) {
        if (m_collapsedRowsKey.contains(getKeyColumn().getValue(row))) {
          prefix.append("[+] ");
        }
        else {
          prefix.append("[-] ");
        }
      }
      view.setText(prefix.toString() + view.getText());
    }
  }

  @Override
  protected void execRowAction(ITableRow row) throws ProcessingException {
    toggleExpandedState(row);
  }

  /**
   * Toggles the expanded state ({@link #isRowCollapsed(Object)}) for each row passed in the rows parameter.
   *
   * @param rows
   * @throws ProcessingException
   */
  public void toggleExpandedState(Collection<ITableRow> rows) throws ProcessingException {
    if (rows != null) {
      toggleExpandedState(rows.toArray(new ITableRow[rows.size()]));
    }
  }

  /**
   * Toggles the expanded state ({@link #isRowCollapsed(Object)}) for each row passed in the rows parameter.
   *
   * @param rows
   * @throws ProcessingException
   */
  public void toggleExpandedState(ITableRow... rows) throws ProcessingException {
    if (rows == null) {
      return;
    }
    for (ITableRow row : rows) {
      if (execIsNode(row)) {
        Object key = getKeyColumn().getValue(row);
        if (isRowCollapsed(key)) {
          m_collapsedRowsKey.remove(key);
        }
        else {
          m_collapsedRowsKey.add(key);
        }
        decorateCell(row, getColumnSet().getFirstVisibleColumn());
      }
    }
    applyRowFilters();
  }

  /**
   * Returns the if the row having this key in the {@link #getKeyColumn()} is collapsed or not.
   * This should only be called for rows that are defined as node ({@link #execIsNode(ITableRow)} for this row
   * should return true)
   *
   * @param key
   * @return
   */
  public boolean isRowCollapsed(Object key) {
    return m_collapsedRowsKey.contains(key);
  }

  /**
   * Returns the row having this key in the {@link #getKeyColumn()}
   *
   * @param key
   * @return row
   */
  public ITableRow findRowByKey(Object key) {
    for (ITableRow r : getRows()) {
      if (key.equals(getKeyColumn().getValue(r))) {
        return r;
      }
    }
    throw new IllegalArgumentException("No row found for the key '" + key + "'");
  }

  /**
   * Returns if this row is a node or not. Override in the concrete table, to define this depending on your business
   * logic.
   *
   * @param row
   * @return isNode
   */
  protected boolean execIsNode(ITableRow row) {
    return true;
  }

  /**
   * @return the ParentKeyColumn
   */
  public ParentKeyColumn getParentKeyColumn() {
    return getColumnSet().getColumnByClass(ParentKeyColumn.class);
  }

  /**
   * @return the KeyColumn
   */
  public KeyColumn getKeyColumn() {
    return getColumnSet().getColumnByClass(KeyColumn.class);
  }

  @Order(1000.0)
  public class KeyColumn extends AbstractColumn<Object> {

    @Override
    protected boolean getConfiguredDisplayable() {
      return false;
    }

  }

  @Order(1010.0)
  public class ParentKeyColumn extends AbstractColumn<Object> {

    @Override
    protected boolean getConfiguredDisplayable() {
      return false;
    }
  }
}
