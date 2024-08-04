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

import javax.swing.AbstractListModel;

import org.chris.portmapper.Settings;
import org.chris.portmapper.model.PortMappingPreset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PresetListModel extends AbstractListModel<PortMappingPreset> implements PropertyChangeListener {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(PresetListModel.class);
    private final Settings settings;

    public PresetListModel(final Settings settings) {
        this.settings = settings;
        settings.addPropertyChangeListener(Settings.PROPERTY_PORT_MAPPING_PRESETS, this);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent arg0) {
        logger.debug("Presets have changed: update list");
        this.fireContentsChanged(this, 0, settings.getPresets().size() - 1);
    }

    @Override
    public PortMappingPreset getElementAt(final int index) {
        return settings.getPresets().get(index);
    }

    @Override
    public int getSize() {
        if (settings == null || settings.getPresets() == null) {
            return 0;
        }
        return settings.getPresets().size();
    }
}
