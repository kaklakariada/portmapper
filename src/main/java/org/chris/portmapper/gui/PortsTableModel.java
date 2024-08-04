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
package org.chris.portmapper.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.model.SinglePortMapping;

/**
 * The table model for the ports table.
 */
public class PortsTableModel extends AbstractTableModel implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private final List<SinglePortMapping> ports;
    private final transient PortMapperApp app;

    public PortsTableModel(final PortMapperApp app, final List<SinglePortMapping> ports) {
        this.app = app;
        this.ports = ports;
    }

    @Override
    public int getColumnCount() {
        return 3;
    }

    @Override
    public int getRowCount() {
        return ports.size();
    }

    @Override
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        final SinglePortMapping port = ports.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return port.getProtocol();
        case 1:
            return port.getExternalPort();
        case 2:
            return port.getInternalPort();
        default:
            throw new IllegalArgumentException("Column " + columnIndex //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }

    @Override
    public Class<?> getColumnClass(final int columnIndex) {
        switch (columnIndex) {
        case 0:
            return Protocol.class;
        case 1:
            return Integer.class;
        case 2:
            return Integer.class;
        default:
            throw new IllegalArgumentException("Column " + columnIndex //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }

    @Override
    public String getColumnName(final int column) {
        switch (column) {
        case 0:
            return app.getResourceMap().getString("preset_dialog.ports.protocol");
        case 1:
            return app.getResourceMap().getString("preset_dialog.ports.external");
        case 2:
            return app.getResourceMap().getString("preset_dialog.ports.internal");
        default:
            throw new IllegalArgumentException("Column " + column //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }

    @Override
    public boolean isCellEditable(final int rowIndex, final int columnIndex) {
        return true;
    }

    @Override
    public void setValueAt(final Object value, final int rowIndex, final int columnIndex) {
        final SinglePortMapping port = ports.get(rowIndex);
        switch (columnIndex) {
        case 0:
            port.setProtocol((Protocol) value);
            break;
        case 1:
            port.setExternalPort((Integer) value);
            break;
        case 2:
            port.setInternalPort((Integer) value);
            break;
        default:
            throw new IllegalArgumentException("Column " + columnIndex //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        this.fireTableDataChanged();
    }
}
