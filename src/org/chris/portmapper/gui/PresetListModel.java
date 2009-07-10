/**
 * 
 */
package org.chris.portmapper.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.AbstractListModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.Settings;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class PresetListModel extends AbstractListModel implements
		PropertyChangeListener {

	private final Log logger = LogFactory.getLog(this.getClass());
	private final Settings settings;

	public PresetListModel(Settings settings) {
		super();
		this.settings = settings;
		settings.addPropertyChangeListener(
				Settings.PROPERTY_PORT_MAPPING_PRESETS, this);
	}

	/**
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		logger.debug("Presets have changed: update list");
		this.fireContentsChanged(this, 0, settings.getPresets().size() - 1);
	}

	/**
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	public Object getElementAt(int index) {
		return settings.getPresets().get(index);
	}

	/**
	 * @see javax.swing.ListModel#getSize()
	 */
	public int getSize() {
		if (settings == null || settings.getPresets() == null) {
			return 0;
		}
		return settings.getPresets().size();
	}

}
