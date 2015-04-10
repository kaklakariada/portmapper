/**
 *
 */
package org.chris.portmapper.gui.util;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 * @author chris
 */
public class TextNumberCellEditor extends AbstractCellEditor implements TableCellEditor, KeyListener {

    private static final long serialVersionUID = 1L;
    private Integer currentPortNumber;
    private final JTextField textField;

    public TextNumberCellEditor(final int value, final int numColumns) {
        this.currentPortNumber = value;
        this.textField = new JTextField(this.currentPortNumber.toString(), numColumns);
        this.textField.addKeyListener(this);
    }

    @Override
    public Object getCellEditorValue() {
        return this.currentPortNumber;
    }

    @Override
    public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected,
            final int row, final int column) {
        this.currentPortNumber = (Integer) value;
        this.textField.setText(this.currentPortNumber.toString());
        return this.textField;
    }

    @Override
    public void keyPressed(final KeyEvent arg0) {
        // ignore
    }

    @Override
    public void keyReleased(final KeyEvent arg0) {
        // ignore
    }

    @Override
    public void keyTyped(final KeyEvent arg0) {
        // The user has clicked the cell
        try {
            this.currentPortNumber = new Integer(textField.getText());
        } catch (final NumberFormatException e) {
            // Do nothing
        }
    }
}
