/**
 * 
 */
package org.chris.portmapper.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

import org.chris.portmapper.router.PortMapping;

class PresetComboBoxModel implements ComboBoxModel {

	private List<PortMapping> presets;
	private PortMapping selectedPreset;
	private Collection<ListDataListener> dataListeners;

	public PresetComboBoxModel() {
		dataListeners = new LinkedList<ListDataListener>();
		presets = new ArrayList<PortMapping>();
		presets.add(new PortMapping(PortMapping.PROTOCOL_TCP, null, 22022,
				null, 22, "SSH (22022)"));
		presets.add(new PortMapping(PortMapping.PROTOCOL_TCP, null, 22, null,
				22, "SSH"));
		presets.add(new PortMapping(PortMapping.PROTOCOL_TCP, null, 80, null,
				80, "HTTP"));
		presets.add(new PortMapping(PortMapping.PROTOCOL_TCP, null, 8080, null,
				8080, "HTTP (8080)"));
		presets.add(new PortMapping(PortMapping.PROTOCOL_UDP, null, 27960,
				null, 27960, "ET + Quake 3 Server"));
		selectedPreset = null;
	}

	public Object getSelectedItem() {
		return selectedPreset;
	}

	public void setSelectedItem(Object anItem) {
		this.selectedPreset = (PortMapping) anItem;
	}

	public void addListDataListener(ListDataListener l) {
		this.dataListeners.add(l);
	}

	public Object getElementAt(int index) {
		return this.presets.get(index);
	}

	public int getSize() {
		return this.presets.size();
	}

	public void removeListDataListener(ListDataListener l) {
		this.dataListeners.remove(l);
	}
}