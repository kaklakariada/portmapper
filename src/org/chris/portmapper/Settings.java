/**
 * 
 */
package org.chris.portmapper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.chris.portmapper.router.PortMappingPreset;

/**
 * @author chris
 * 
 */
public class Settings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1349121868190190000L;

	public final static String PROPERTY_PORT_MAPPING_PRESETS = "presets";
	private List<PortMappingPreset> presets;

	private transient PropertyChangeSupport propertyChangeSupport;

	public Settings() {
		presets = new ArrayList<PortMappingPreset>();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}

	public List<PortMappingPreset> getPresets() {
		return presets;
	}

	public void setPresets(List<PortMappingPreset> presets) {
		this.presets = presets;
	}

	public void addPreset(PortMappingPreset newPreset) {
		List<PortMappingPreset> oldPresets = new ArrayList<PortMappingPreset>(
				this.presets);
		this.presets.add(newPreset);
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, oldPresets,
				new ArrayList<PortMappingPreset>(this.presets));
	}

	public void removePresets(PortMappingPreset selectedPreset) {
		List<PortMappingPreset> oldPresets = new ArrayList<PortMappingPreset>(
				this.presets);
		this.presets.remove(selectedPreset);
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, oldPresets,
				new ArrayList<PortMappingPreset>(this.presets));
	}

	/**
	 * @param portMappingPreset
	 */
	public void savePreset(PortMappingPreset portMappingPreset) {
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, null,
				new ArrayList<PortMappingPreset>(this.presets));
	}

	@Override
	public String toString() {
		return "[Settings: presets=" + presets + "]";
	}
}
