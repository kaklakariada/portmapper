/**
 * 
 */
package org.chris.portmapper.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.SinglePortMapping;
import org.chris.portmapper.router.SinglePortMapping.Protocol;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class PortsTableModel extends AbstractTableModel implements
		PropertyChangeListener {

	List<SinglePortMapping> ports;

	public PortsTableModel(List<SinglePortMapping> ports) {
		super();
		this.ports = ports;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return ports.size();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		SinglePortMapping port = ports.get(rowIndex);
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
	public Class<?> getColumnClass(int columnIndex) {
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
	public String getColumnName(int column) {
		switch (column) {
		case 0:
			return PortMapperApp.getResourceMap().getString(
					"preset_dialog.ports.protocol");
		case 1:
			return PortMapperApp.getResourceMap().getString(
					"preset_dialog.ports.external");
		case 2:
			return PortMapperApp.getResourceMap().getString(
					"preset_dialog.ports.internal");
		default:
			throw new IllegalArgumentException("Column " + column //$NON-NLS-1$
					+ " does not exist"); //$NON-NLS-1$
		}
	}

	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		SinglePortMapping port = ports.get(rowIndex);
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

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		this.fireTableDataChanged();
	}
}
