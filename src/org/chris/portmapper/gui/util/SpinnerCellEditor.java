/**
 * 
 */
package org.chris.portmapper.gui.util;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class SpinnerCellEditor extends AbstractCellEditor implements
		TableCellEditor, ChangeListener {

	private Integer currentPortNumber;
	private JSpinner spinner;

	/**
	 * 
	 */
	public SpinnerCellEditor(int value, int min, int max, int step) {
		this.currentPortNumber = value;
		this.spinner = new JSpinner(new SpinnerNumberModel(
				this.currentPortNumber.intValue(), min, max, step));
		spinner.addChangeListener(this);
	}

	/**
	 * 
	 * @see javax.swing.CellEditor#getCellEditorValue()
	 */
	public Object getCellEditorValue() {
		return this.currentPortNumber;
	}

	/**
	 * 
	 * @see javax.swing.table.TableCellEditor#getTableCellEditorComponent(javax.swing
	 *      .JTable, java.lang.Object, boolean, int, int)
	 */
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		this.currentPortNumber = (Integer) value;
		spinner.setValue(this.currentPortNumber);
		return spinner;
	}

	/**
	 * @see javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent)
	 */
	public void stateChanged(ChangeEvent event) {
		// The user has clicked the cell
		this.currentPortNumber = (Integer) spinner.getValue();

	}

}
