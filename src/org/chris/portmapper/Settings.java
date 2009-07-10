/**
 * 
 */
package org.chris.portmapper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;

/**
 * @author chris
 * 
 */
public class Settings implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1349121864190290050L;

	public final static String PROPERTY_PORT_MAPPING_PRESETS = "presets";

	private List<PortMappingPreset> presets;
	private boolean useEntityEncoding;
	private String logLevel;
	private String routerFactoryClassName;

	private transient PropertyChangeSupport propertyChangeSupport;

	public Settings() {
		useEntityEncoding = true;
		logLevel = Level.INFO.toString();
		presets = new ArrayList<PortMappingPreset>();
		routerFactoryClassName = SBBIRouterFactory.class.getName();
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
		return "[Settings: presets=" + presets + ", useEntityEncoding="
				+ useEntityEncoding + ", logLevel=" + logLevel
				+ ", routerFactoryClassName=" + routerFactoryClassName + "]";
	}

	/**
	 * @return the useEntityEncoding
	 */
	public boolean isUseEntityEncoding() {
		return useEntityEncoding;
	}

	/**
	 * @param useEntityEncoding
	 *            the useEntityEncoding to set
	 */
	public void setUseEntityEncoding(boolean useEntityEncoding) {
		this.useEntityEncoding = useEntityEncoding;
	}

	/**
	 * @return
	 */
	public String getLogLevel() {
		return this.logLevel;
	}

	/**
	 * @param logLevel
	 *            the logLevel to set
	 */
	public void setLogLevel(String logLevel) {
		this.logLevel = logLevel;
	}

	public String getRouterFactoryClassName() {
		return routerFactoryClassName;
	}

	public void setRouterFactoryClassName(String routerFactoryClassName) {
		this.routerFactoryClassName = routerFactoryClassName;
	}
}
