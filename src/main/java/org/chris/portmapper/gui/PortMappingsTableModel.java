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
package org.chris.portmapper.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.PortMapping;

public class PortMappingsTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;
    private final transient ArrayList<PortMapping> mappings;
    private final transient PortMapperApp app;

    public PortMappingsTableModel(final PortMapperApp app) {
        this.app = app;
        this.mappings = new ArrayList<>();
    }

    public void setMappings(final Collection<PortMapping> mappings) {
        this.mappings.clear();
        this.mappings.addAll(mappings);
        super.fireTableDataChanged();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }

    @Override
    public int getRowCount() {
        return mappings.size();
    }

    public PortMapping getPortMapping(final int index) {
        return mappings.get(index);
    }

    @Override
    public Object getValueAt(final int row, final int col) {
        final PortMapping mapping = mappings.get(row);
        switch (col) {
        case 0:
            return mapping.getProtocol();
        case 1:
            return (mapping.getRemoteHost() != null ? mapping.getRemoteHost() : ""); //$NON-NLS-1$
        case 2:
            return mapping.getExternalPort();
        case 3:
            return mapping.getInternalClient();
        case 4:
            return mapping.getInternalPort();
        case 5:
            return mapping.getDescription();
        default:
            throw new IllegalArgumentException("Column " + col //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }

    @Override
    public String getColumnName(final int col) {
        switch (col) {
        case 0:
            return app.getResourceMap().getString("mainFrame.mappings.protocol"); //$NON-NLS-1$
        case 1:
            return app.getResourceMap().getString("mainFrame.mappings.remote_host"); //$NON-NLS-1$
        case 2:
            return app.getResourceMap().getString("mainFrame.mappings.external_port"); //$NON-NLS-1$
        case 3:
            return app.getResourceMap().getString("mainFrame.mappings.internal_client"); //$NON-NLS-1$
        case 4:
            return app.getResourceMap().getString("mainFrame.mappings.internal_port"); //$NON-NLS-1$
        case 5:
            return app.getResourceMap().getString("mainFrame.mappings.description"); //$NON-NLS-1$
        default:
            throw new IllegalArgumentException("Column " + col //$NON-NLS-1$
                    + " does not exist"); //$NON-NLS-1$
        }
    }
}
