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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.miginfocom.swing.MigLayout;

/**
 * The main view.
 */
public class PortMapperView extends FrameView {

    private static final String MAIN_FRAME_ROUTER_NOT_CONNECTED = "mainFrame.router.not_connected";
    private static final String ACTION_SHOW_ABOUT_DIALOG = "mainFrame.showAboutDialog";
    private static final String ACTION_DISPLAY_ROUTER_INFO = "mainFrame.router.info";
    private static final String ACTION_CONNECT_ROUTER = "mainFrame.router.connect";
    private static final String ACTION_DISCONNECT_ROUTER = "mainFrame.router.disconnect";
    private static final String ACTION_COPY_INTERNAL_ADDRESS = "mainFrame.router.copyInternalAddress";
    private static final String ACTION_COPY_EXTERNAL_ADDRESS = "mainFrame.router.copyExternalAddress";
    private static final String ACTION_UPDATE_ADDRESSES = "mainFrame.router.updateAddresses";
    private static final String ACTION_UPDATE_PORT_MAPPINGS = "mainFrame.mappings.update";

    private static final String ACTION_PORTMAPPER_SETTINGS = "mainFrame.portmapper.settings";

    private static final String ACTION_REMOVE_MAPPINGS = "mainFrame.mappings.remove";

    private static final String ACTION_CREATE_PRESET_MAPPING = "mainFrame.preset_mappings.create";
    private static final String ACTION_EDIT_PRESET_MAPPING = "mainFrame.preset_mappings.edit";
    private static final String ACTION_REMOVE_PRESET_MAPPING = "mainFrame.preset_mappings.remove";
    private static final String ACTION_USE_PRESET_MAPPING = "mainFrame.preset_mappings.use";

    private static final Logger logger = LoggerFactory.getLogger(PortMapperView.class);

    private static final String PROPERTY_MAPPING_SELECTED = "mappingSelected";
    private static final String PROPERTY_ROUTER_CONNECTED = "connectedToRouter";
    private static final String PROPERTY_PRESET_MAPPING_SELECTED = "presetMappingSelected";

    private PortMappingsTableModel tableModel;
    private JTable mappingsTable;
    private JLabel externalIPLabel;
    private JLabel internalIPLabel;
    private JList<PortMappingPreset> portMappingPresets;
    private final PortMapperApp app;

    public PortMapperView(final PortMapperApp app) {
        super(app);
        this.app = app;
        initView();
    }

    private void initView() {
        // Create and set up the window.
        final JPanel panel = new JPanel();
        panel.setLayout(new MigLayout("", "[fill, grow]", "[grow 50]unrelated[]unrelated[grow 50]"));

        panel.add(getMappingsPanel(), "wrap");
        panel.add(getRouterPanel(), "grow 0, split 2");
        panel.add(getPresetPanel(), "wrap");
        panel.add(getLogPanel(), "wrap");

        this.setComponent(panel);
    }

    private JComponent getRouterPanel() {
        final ActionMap actionMap = this.getContext().getActionMap(this.getClass(), this);
        final JPanel routerPanel = new JPanel(new MigLayout("", "[fill, grow][]", ""));
        routerPanel
                .setBorder(BorderFactory.createTitledBorder(app.getResourceMap().getString("mainFrame.router.title")));

        routerPanel.add(new JLabel(app.getResourceMap().getString("mainFrame.router.external_address")), "align label"); //$NON-NLS-2$
        externalIPLabel = new JLabel(app.getResourceMap().getString(MAIN_FRAME_ROUTER_NOT_CONNECTED));
        routerPanel.add(externalIPLabel, "width 130!");
        routerPanel.add(new JButton(actionMap.get(ACTION_COPY_EXTERNAL_ADDRESS)), "sizegroup router");
        routerPanel.add(new JButton(actionMap.get(ACTION_UPDATE_ADDRESSES)),
                "wrap, spany 2, aligny base, sizegroup router");

        routerPanel.add(new JLabel(app.getResourceMap().getString("mainFrame.router.internal_address")), "align label");
        internalIPLabel = new JLabel(app.getResourceMap().getString(MAIN_FRAME_ROUTER_NOT_CONNECTED));
        routerPanel.add(internalIPLabel, "width 130!");
        routerPanel.add(new JButton(actionMap.get(ACTION_COPY_INTERNAL_ADDRESS)), "wrap, sizegroup router");

        final JButton connectDisconnectButton = new JButton(actionMap.get(ACTION_CONNECT_ROUTER));
        routerPanel.add(connectDisconnectButton, "");
        routerPanel.add(new JButton(actionMap.get(ACTION_DISPLAY_ROUTER_INFO)), "sizegroup router");
        routerPanel.add(new JButton(actionMap.get(ACTION_SHOW_ABOUT_DIALOG)), "sizegroup router, wrap");

        this.addPropertyChangeListener(evt -> {
            if (evt.getPropertyName().equals(PROPERTY_ROUTER_CONNECTED)) {
                logger.debug("Connection state changed to {}", evt.getNewValue());

                if (evt.getNewValue().equals(Boolean.TRUE)) {
                    connectDisconnectButton.setAction(actionMap.get(ACTION_DISCONNECT_ROUTER));
                } else {
                    connectDisconnectButton.setAction(actionMap.get(ACTION_CONNECT_ROUTER));
                }
            }
        });
        routerPanel.add(new JButton(actionMap.get(ACTION_PORTMAPPER_SETTINGS)), "");

        return routerPanel;
    }

    private JComponent getLogPanel() {

        final LogTextArea logTextArea = new LogTextArea();

        app.setLogMessageListener(logTextArea);

        final JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(logTextArea);

        final JPanel logPanel = new JPanel(new MigLayout("", "[grow, fill]", "[grow, fill]"));
        logPanel.setBorder(
                BorderFactory.createTitledBorder(app.getResourceMap().getString("mainFrame.log_messages.title")));
        logPanel.add(scrollPane, "height 100::");

        return logPanel;
    }

    private JComponent getPresetPanel() {
        final ActionMap actionMap = this.getContext().getActionMap(this.getClass(), this);

        final JPanel presetPanel = new JPanel(new MigLayout("", "[grow, fill][]", ""));
        presetPanel.setBorder(BorderFactory
                .createTitledBorder(app.getResourceMap().getString("mainFrame.port_mapping_presets.title")));

        portMappingPresets = new JList<>(new PresetListModel(app.getSettings()));
        portMappingPresets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        portMappingPresets.setLayoutOrientation(JList.VERTICAL);

        portMappingPresets.addListSelectionListener(e -> {
            logger.trace("Selection of preset list has changed: {}", isPresetMappingSelected());
            firePropertyChange(PROPERTY_PRESET_MAPPING_SELECTED, false, isPresetMappingSelected());
        });

        presetPanel.add(new JScrollPane(portMappingPresets), "spany 4, grow");

        presetPanel.add(new JButton(actionMap.get(ACTION_CREATE_PRESET_MAPPING)), "wrap, sizegroup preset_buttons");
        presetPanel.add(new JButton(actionMap.get(ACTION_EDIT_PRESET_MAPPING)), "wrap, sizegroup preset_buttons");
        presetPanel.add(new JButton(actionMap.get(ACTION_REMOVE_PRESET_MAPPING)), "wrap, sizegroup preset_buttons");
        presetPanel.add(new JButton(actionMap.get(ACTION_USE_PRESET_MAPPING)), "wrap, sizegroup preset_buttons");

        return presetPanel;
    }

    private JComponent getMappingsPanel() {
        // Mappings panel

        final ActionMap actionMap = this.getContext().getActionMap(this.getClass(), this);

        tableModel = new PortMappingsTableModel(app);
        mappingsTable = new JTable(tableModel);
        mappingsTable.setAutoCreateRowSorter(true);
        mappingsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        mappingsTable.setSize(new Dimension(400, 100));
        mappingsTable.getSelectionModel().addListSelectionListener(
                e -> firePropertyChange(PROPERTY_MAPPING_SELECTED, false, isMappingSelected()));

        final JScrollPane mappingsTabelPane = new JScrollPane();
        mappingsTabelPane.setViewportView(mappingsTable);

        final JPanel mappingsPanel = new JPanel(new MigLayout("", "[fill,grow]", "[grow,fill][]"));
        mappingsPanel.setName("port_mappings");
        final Border panelBorder = BorderFactory
                .createTitledBorder(app.getResourceMap().getString("mainFrame.port_mappings.title"));
        mappingsPanel.setBorder(panelBorder);
        mappingsPanel.add(mappingsTabelPane, "height 100::, span 2, wrap");

        mappingsPanel.add(new JButton(actionMap.get(ACTION_REMOVE_MAPPINGS)), "");
        mappingsPanel.add(new JButton(actionMap.get(ACTION_UPDATE_PORT_MAPPINGS)), "wrap");
        return mappingsPanel;
    }

    @Action(name = ACTION_UPDATE_ADDRESSES, enabledProperty = PROPERTY_ROUTER_CONNECTED)
    public void updateAddresses() {
        final IRouter router = app.getRouter();
        if (router == null) {
            externalIPLabel.setText(app.getResourceMap().getString(MAIN_FRAME_ROUTER_NOT_CONNECTED));
            internalIPLabel.setText(app.getResourceMap().getString(MAIN_FRAME_ROUTER_NOT_CONNECTED));
            return;
        }
        externalIPLabel.setText(app.getResourceMap().getString("mainFrame.router.updating"));
        internalIPLabel.setText(app.getResourceMap().getString("mainFrame.router.updating"));
        internalIPLabel.setText(router.getInternalHostName());
        try {
            externalIPLabel.setText(router.getExternalIPAddress());
        } catch (final RouterException e) {
            externalIPLabel.setText("");
            logger.error("Did not get external IP address", e);
        }
    }

    @Action(name = ACTION_CONNECT_ROUTER)
    public Task<Void, Void> connectRouter() {
        return new ConnectTask(app);
    }

    @Action(name = ACTION_DISCONNECT_ROUTER)
    public void disconnectRouter() {
        app.disconnectRouter();
        updateAddresses();
        updatePortMappings();
    }

    private void addMapping(final Collection<PortMapping> portMappings) {
        final IRouter router = app.getRouter();
        if (router == null) {
            return;
        }

        try {
            router.addPortMappings(portMappings);
            logger.info("{} port mapping added successfully", portMappings.size());
        } catch (final RouterException e) {
            logger.error("Could not add port mapping", e);
            JOptionPane.showMessageDialog(this.getFrame(), "The port mapping could not be added.\n" + e.getMessage(),
                    "Error adding port mapping", JOptionPane.WARNING_MESSAGE);
        }

        this.updatePortMappings();
    }

    @Action(name = ACTION_REMOVE_MAPPINGS, enabledProperty = PROPERTY_MAPPING_SELECTED)
    public void removeMappings() {
        final Collection<PortMapping> selectedMappings = this.getSelectedPortMappings();
        for (final PortMapping mapping : selectedMappings) {
            logger.info("Removing mapping {}", mapping);
            try {
                app.getRouter().removeMapping(mapping);
            } catch (final RouterException e) {
                logger.error("Could not remove port mapping " + mapping, e);
                break;
            }
            logger.info("Mapping was removed successfully: {}", mapping);
        }
        if (!selectedMappings.isEmpty()) {
            updatePortMappings();
        }
    }

    @Action(name = ACTION_DISPLAY_ROUTER_INFO, enabledProperty = PROPERTY_ROUTER_CONNECTED)
    public void displayRouterInfo() {
        final IRouter router = app.getRouter();
        if (router == null) {
            logger.warn("Not connected to router, could not get router info");
            return;
        }
        try {
            router.logRouterInfo();
        } catch (final RouterException e) {
            logger.error("Could not get router info", e);
        }
    }

    @Action(name = ACTION_SHOW_ABOUT_DIALOG)
    public void showAboutDialog() {
        app.show(new AboutDialog(app));
    }

    @Action(name = ACTION_COPY_INTERNAL_ADDRESS, enabledProperty = PROPERTY_ROUTER_CONNECTED)
    public void copyInternalAddress() {
        this.copyTextToClipboard(this.internalIPLabel.getText());
    }

    @Action(name = ACTION_COPY_EXTERNAL_ADDRESS, enabledProperty = PROPERTY_ROUTER_CONNECTED)
    public void copyExternalAddress() {
        this.copyTextToClipboard(this.externalIPLabel.getText());
    }

    @Action(name = ACTION_UPDATE_PORT_MAPPINGS, enabledProperty = PROPERTY_ROUTER_CONNECTED)
    public void updatePortMappings() {
        final IRouter router = app.getRouter();
        if (router == null) {
            this.tableModel.setMappings(Collections.<PortMapping> emptyList());
            return;
        }
        try {
            final Collection<PortMapping> mappings = router.getPortMappings();
            logger.info("Found {} mappings", mappings.size());
            this.tableModel.setMappings(mappings);
        } catch (final RouterException e) {
            logger.error("Could not get port mappings", e);
        }
    }

    @Action(name = ACTION_USE_PRESET_MAPPING, enabledProperty = PROPERTY_PRESET_MAPPING_SELECTED)
    public void addPresetMapping() {
        final PortMappingPreset selectedItem = this.portMappingPresets.getSelectedValue();
        if (selectedItem != null) {
            final String localHostAddress = app.getLocalHostAddress();
            if (selectedItem.useLocalhostAsInternalClient() && localHostAddress == null) {
                JOptionPane.showMessageDialog(this.getFrame(),
                        app.getResourceMap().getString("messages.error_getting_localhost_address"),
                        app.getResourceMap().getString("messages.error"), JOptionPane.ERROR_MESSAGE);
            } else {
                addMapping(selectedItem.getPortMappings(localHostAddress));
            }
        }
    }

    @Action(name = ACTION_CREATE_PRESET_MAPPING)
    public void createPresetMapping() {
        app.show(new EditPresetDialog(app, new PortMappingPreset()));
    }

    @Action(name = ACTION_EDIT_PRESET_MAPPING, enabledProperty = PROPERTY_PRESET_MAPPING_SELECTED)
    public void editPresetMapping() {
        final PortMappingPreset selectedPreset = this.portMappingPresets.getSelectedValue();
        app.show(new EditPresetDialog(app, selectedPreset));
    }

    @Action(name = ACTION_PORTMAPPER_SETTINGS)
    public void changeSettings() {
        logger.debug("Open Settings dialog");
        app.show(new SettingsDialog(app));
    }

    @Action(name = ACTION_REMOVE_PRESET_MAPPING, enabledProperty = PROPERTY_PRESET_MAPPING_SELECTED)
    public void removePresetMapping() {
        final PortMappingPreset selectedPreset = this.portMappingPresets.getSelectedValue();
        app.getSettings().removePresets(selectedPreset);
    }

    public void fireConnectionStateChange() {
        firePropertyChange(PROPERTY_ROUTER_CONNECTED, !isConnectedToRouter(), isConnectedToRouter());
    }

    public boolean isConnectedToRouter() {
        return app.isConnected();
    }

    public boolean isMappingSelected() {
        return this.isConnectedToRouter() && !this.getSelectedPortMappings().isEmpty();
    }

    public boolean isPresetMappingSelected() {
        return this.portMappingPresets.getSelectedValue() != null;
    }

    /**
     * Get the port mappings currently selected in the table.
     *
     * @return the currently selected port mappings.
     */
    public Collection<PortMapping> getSelectedPortMappings() {
        final int[] selectedRows = mappingsTable.getSelectedRows();
        if (selectedRows == null || selectedRows.length == 0) {
            return Collections.emptyList();
        }
        final Collection<PortMapping> selectedMappings = new ArrayList<>(selectedRows.length);
        for (final int rowIndex : selectedRows) {
            if (rowIndex >= 0) {
                // The table could be sorted, so convert the row index for
                // the model
                final int modelRowIndex = mappingsTable.convertRowIndexToModel(rowIndex);
                final PortMapping mapping = tableModel.getPortMapping(modelRowIndex);
                if (mapping != null) {
                    selectedMappings.add(mapping);
                }
            }
        }
        return selectedMappings;
    }

    private void copyTextToClipboard(final String text) {
        final Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        logger.trace("Copy text '{}' to clipbord", text);
        clipboard.setContents(new StringSelection(text), (clip, contents) -> logger.trace("Lost clipboard ownership"));
    }

    private class ConnectTask extends Task<Void, Void> {

        private final PortMapperApp app;

        public ConnectTask(final PortMapperApp app) {
            super(app);
            this.app = app;
        }

        @Override
        protected Void doInBackground() throws Exception {
            logger.trace("Connecting to router...");
            app.connectRouter();
            message("updateAddresses");
            logger.trace("Updating addresses...");
            updateAddresses();
            message("updatePortMappings");
            logger.trace("Updating port mappings...");
            updatePortMappings();
            logger.trace("done");
            return null;
        }

        @Override
        protected void failed(final Throwable cause) {
            logger.warn("Could not connect to router: {}", cause.getMessage(), cause);
            logger.warn("Could not connect to router: {}", cause.getMessage());
        }
    }
}
