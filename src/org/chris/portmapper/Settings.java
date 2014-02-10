package org.chris.portmapper;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;

import ch.qos.logback.classic.Level;

/**
 * @author chris
 * @version $Id$
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
		presets = new ArrayList<>();
		routerFactoryClassName = SBBIRouterFactory.class.getName();
		propertyChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(final String property,
			final PropertyChangeListener listener) {
		this.propertyChangeSupport
				.addPropertyChangeListener(property, listener);
	}

	public List<PortMappingPreset> getPresets() {
		return presets;
	}

	public void setPresets(final List<PortMappingPreset> presets) {
		this.presets = presets;
	}

	public void addPreset(final PortMappingPreset newPreset) {
		final List<PortMappingPreset> oldPresets = new ArrayList<>(this.presets);
		this.presets.add(newPreset);
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, oldPresets, new ArrayList<>(
						this.presets));
	}

	public void removePresets(final PortMappingPreset selectedPreset) {
		final List<PortMappingPreset> oldPresets = new ArrayList<>(this.presets);
		this.presets.remove(selectedPreset);
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, oldPresets, new ArrayList<>(
						this.presets));
	}

	public void savePreset(final PortMappingPreset portMappingPreset) {
		this.propertyChangeSupport.firePropertyChange(
				PROPERTY_PORT_MAPPING_PRESETS, null, new ArrayList<>(
						this.presets));
	}

	@Override
	public String toString() {
		return "[Settings: presets=" + presets + ", useEntityEncoding="
				+ useEntityEncoding + ", logLevel=" + logLevel
				+ ", routerFactoryClassName=" + routerFactoryClassName + "]";
	}

	public boolean isUseEntityEncoding() {
		return useEntityEncoding;
	}

	public void setUseEntityEncoding(final boolean useEntityEncoding) {
		this.useEntityEncoding = useEntityEncoding;
	}

	public String getLogLevel() {
		return this.logLevel;
	}

	public void setLogLevel(final String logLevel) {
		this.logLevel = logLevel;
	}

	public String getRouterFactoryClassName() {
		return routerFactoryClassName;
	}

	public void setRouterFactoryClassName(final String routerFactoryClassName) {
		this.routerFactoryClassName = routerFactoryClassName;
	}
}
