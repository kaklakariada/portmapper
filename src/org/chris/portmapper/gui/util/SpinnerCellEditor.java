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
 */
public class SpinnerCellEditor extends AbstractCellEditor implements TableCellEditor, ChangeListener {

    private static final long serialVersionUID = 1L;
    private Integer currentPortNumber;
    private final JSpinner spinner;

    public SpinnerCellEditor(final int value, final int min, final int max, final int step) {
        this.currentPortNumber = value;
        this.spinner = new JSpinner(new SpinnerNumberModel(this.currentPortNumber.intValue(), min, max, step));
        spinner.addChangeListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return this.currentPortNumber;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column) {
        this.currentPortNumber = (Integer) value;
        spinner.setValue(this.currentPortNumber);
        return spinner;
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
        // The user has clicked the cell
        this.currentPortNumber = (Integer) spinner.getValue();
    }
}
