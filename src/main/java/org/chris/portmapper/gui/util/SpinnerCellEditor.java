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

import javax.swing.AbstractCellEditor;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.TableCellEditor;

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
