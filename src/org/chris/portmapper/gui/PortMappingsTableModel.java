package org.chris.portmapper.gui;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.table.AbstractTableModel;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.PortMapping;

/**
 * @author chris
 * @version $Id$
 * 
 */
public class PortMappingsTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final ArrayList<PortMapping> mappings;
	private boolean remoteHostGiven = true;

	public PortMappingsTableModel() {
		super();
		this.mappings = new ArrayList<PortMapping>();
		this.remoteHostGiven = false;
	}

	public void setMappings(Collection<PortMapping> mappings) {
		this.mappings.clear();
		this.mappings.addAll(mappings);
		boolean newRemoteHostGiven = remoteHostGiven();
		if (newRemoteHostGiven != remoteHostGiven) {
			super.fireTableStructureChanged();
		}
		this.remoteHostGiven = newRemoteHostGiven;
		super.fireTableDataChanged();
	}

	public int getColumnCount() {
		return remoteHostGiven ? 6 : 5;
	}

	public int getRowCount() {
		return mappings.size();
	}

	public PortMapping getPortMapping(int index) {
		return mappings.get(index);
	}

	public Object getValueAt(int row, int col) {
		PortMapping mapping = mappings.get(row);
		if (remoteHostGiven) {
			switch (col) {
			case 0:
				return mapping.getProtocol();
			case 1:
				return (mapping.getRemoteHost() != null ? mapping
						.getRemoteHost() : ""); //$NON-NLS-1$
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
		} else {
			switch (col) {
			case 0:
				return mapping.getProtocol();
			case 1:
				return mapping.getExternalPort();
			case 2:
				return mapping.getInternalClient();
			case 3:
				return mapping.getInternalPort();
			case 4:
				return mapping.getDescription();
			default:
				throw new IllegalArgumentException("Column " + col //$NON-NLS-1$
						+ " does not exist"); //$NON-NLS-1$
			}
		}
	}

	@Override
	public String getColumnName(int col) {
		if (remoteHostGiven) {
			switch (col) {
			case 0:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.protocol"); //$NON-NLS-1$
			case 1:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.remote_host"); //$NON-NLS-1$
			case 2:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.external_port"); //$NON-NLS-1$
			case 3:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.internal_client"); //$NON-NLS-1$
			case 4:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.internal_port"); //$NON-NLS-1$
			case 5:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.description"); //$NON-NLS-1$
			default:
				throw new IllegalArgumentException("Column " + col //$NON-NLS-1$
						+ " does not exist"); //$NON-NLS-1$
			}
		} else {
			switch (col) {
			case 0:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.protocol"); //$NON-NLS-1$
			case 1:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.external_port"); //$NON-NLS-1$
			case 2:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.internal_client"); //$NON-NLS-1$
			case 3:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.internal_port"); //$NON-NLS-1$
			case 4:
				return PortMapperApp.getResourceMap().getString(
						"mainFrame.mappings.description");
			default:
				throw new IllegalArgumentException("Column " + col
						+ " does not exist");
			}
		}

	}

	private boolean remoteHostGiven() {
		for (PortMapping mapping : mappings) {
			if (mapping.getRemoteHost() != null
					&& mapping.getRemoteHost().length() > 0) {
				return true;
			}
		}
		return false;
	}

	public void removeMapping(int selectedRowNum) {
		this.mappings.remove(selectedRowNum);
		this.fireTableDataChanged();

	}
}
