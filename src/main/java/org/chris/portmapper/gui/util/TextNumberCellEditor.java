/**
 * UPnP PortMapper - A tool for managing port forwardings via UPnP
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
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
    public void keyPressed(final KeyEvent event) {
        // ignore
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        // ignore
    }

    @Override
    public void keyTyped(final KeyEvent event) {
        // The user has clicked the cell
        try {
            this.currentPortNumber = Integer.valueOf(textField.getText());
        } catch (final NumberFormatException e) {
            // Do nothing
        }
    }
}
