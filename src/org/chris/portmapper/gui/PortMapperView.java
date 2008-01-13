/**
 * 
 */
package org.chris.portmapper.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.logging.TextAreaWriter;
import org.chris.portmapper.router.PortMapping;
import org.chris.portmapper.router.Router;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.Action;
import org.jdesktop.application.FrameView;

/**
 * @author chris
 * 
 */
public class PortMapperView extends FrameView {

	private static final String ACTION_SHOW_ABOUT_DIALOG = "mainFrame.showAboutDialog";
	private static final String ACTION_DISPLAY_ROUTER_INFO = "mainFrame.router.info";
	private static final String ACTION_CONNECT_ROUTER = "mainFrame.router.connect";
	private static final String ACTION_COPY_INTERNAL_ADDRESS = "mainFrame.router.copyInternalAddress";
	private static final String ACTION_COPY_EXTERNAL_ADDRESS = "mainFrame.router.copyExternalAddress";
	private static final String ACTION_UPDATE_ADDRESSES = "mainFrame.router.updateAddresses";
	private static final String ACTION_UPDATE_PORT_MAPPINGS = "mainFrame.mappings.update";
	private static final String ACTION_REMOVE_MAPPINGS = "mainFrame.mappings.remove";
	private static final String ACTION_ADD_MAPPING = "mainFrame.mappings.add";
	private static final String ACTION_ADD_PRESET_MAPPING = "mainFrame.mappings.addPreset";

	private Log logger = LogFactory.getLog(this.getClass());

	private static final String PROPERTY_MAPPING_SELECTED = "mappingSelected";
	private static final String PROPERTY_ROUTER_CONNECTED = "connectedToRouter";
	private static final String PROPERTY_PRESET_MAPPING_SELECTED = "presetMappingSelected";

	private PortMappingsTableModel tableModel;
	private JTable mappingsTable;
	private JLabel externalIPLabel, internalIPLabel;
	private JComboBox presetMappingComboBox;

	/**
	 * @param application
	 */
	public PortMapperView() {
		super(PortMapperApp.getInstance());
		initView();
	}

	/**
	 * 
	 */
	private void initView() {
		// Create and set up the window.
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout("", "[fill, grow]",
				"[grow 50]unrelated[]unrelated[grow 50]"));

		panel.add(getMappingsPanel(), "wrap");
		panel.add(getRouterPanel(), "wrap");
		panel.add(getLogPanel(), "wrap");

		this.setComponent(panel);
	}

	private JComponent getRouterPanel() {
		ActionMap actionMap = this.getContext().getActionMap(this.getClass(),
				this);
		JPanel routerPanel = new JPanel(new MigLayout("", "", ""));
		routerPanel.setBorder(BorderFactory.createTitledBorder(PortMapperApp
				.getResourceMap().getString("mainFrame.router")));

		routerPanel.add(new JLabel(PortMapperApp.getResourceMap().getString(
				"mainFrame.router.external_address")), "align label"); //$NON-NLS-2$
		externalIPLabel = new JLabel(PortMapperApp.getResourceMap().getString(
				"mainFrame.router.not_connected"));
		routerPanel.add(externalIPLabel, "width 120!");
		routerPanel
				.add(new JButton(actionMap.get(ACTION_COPY_EXTERNAL_ADDRESS)));
		routerPanel.add(new JButton(actionMap.get(ACTION_UPDATE_ADDRESSES)),
				"wrap, sizegroup routerbutton, spany 2, aligny base");

		routerPanel.add(new JLabel(PortMapperApp.getResourceMap().getString(
				"mainFrame.router.internal_address")), "align label");
		internalIPLabel = new JLabel(PortMapperApp.getResourceMap().getString(
				"mainFrame.router.not_connected"));
		routerPanel.add(internalIPLabel, "width 120!");
		routerPanel.add(
				new JButton(actionMap.get(ACTION_COPY_INTERNAL_ADDRESS)),
				"wrap");

		routerPanel.add(new JButton(actionMap.get(ACTION_CONNECT_ROUTER)),
				"sizegroup routerbutton");
		routerPanel.add(new JButton(actionMap.get(ACTION_DISPLAY_ROUTER_INFO)),
				"sizegroup routerbutton");
		routerPanel.add(new JButton(actionMap.get(ACTION_SHOW_ABOUT_DIALOG)),
				"skip 2, sizegroup routerbutton");

		return routerPanel;
	}

	private JComponent getLogPanel() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris").getAppender("jtextarea");

		JTextArea logTextArea = new JTextArea();
		// logTextArea.setColumns(40);
		// logTextArea.setRows(10);
		logTextArea.setEditable(false);
		logTextArea.setWrapStyleWord(true);
		logTextArea.setLineWrap(true);

		// LoggingDocument loggingDocument = new LoggingDocument();
		// writerAppender.setWriter(new DocumentWriter(loggingDocument));
		writerAppender.setWriter(new TextAreaWriter(logTextArea));

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(logTextArea);

		JPanel logPanel = new JPanel(new MigLayout("", "[grow, fill]",
				"[grow, fill]"));
		logPanel.setBorder(BorderFactory.createTitledBorder(PortMapperApp
				.getResourceMap().getString("mainFrame.log_messages")));
		logPanel.add(scrollPane, "height 100::");

		return logPanel;
	}

	private JComponent getMappingsPanel() {
		// Mappings panel

		ActionMap actionMap = this.getContext().getActionMap(this.getClass(),
				this);

		tableModel = new PortMappingsTableModel();
		mappingsTable = new JTable(tableModel);
		mappingsTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mappingsTable.setSize(new Dimension(400, 100));
		mappingsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						firePropertyChange(PROPERTY_MAPPING_SELECTED, false,
								isMappingSelected());
					}
				});

		JScrollPane mappingsTabelPane = new JScrollPane();
		mappingsTabelPane.setViewportView(mappingsTable);

		presetMappingComboBox = new JComboBox(new PresetComboBoxModel());
		presetMappingComboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				firePropertyChange(PROPERTY_PRESET_MAPPING_SELECTED, false,
						isPresetMappingSelected());
			}
		});

		JPanel mappingsPanel = new JPanel(new MigLayout("", "[fill,grow]",
				"[grow,fill][]"));
		mappingsPanel.setName("port_mappings");
		Border panelBorder = BorderFactory.createTitledBorder(PortMapperApp
				.getResourceMap().getString("mainFrame.port_mappings"));
		mappingsPanel.setBorder(panelBorder);
		mappingsPanel.add(mappingsTabelPane, "height 100::, wrap");
		mappingsPanel.add(presetMappingComboBox, "growx, split 5");
		mappingsPanel.add(
				new JButton(actionMap.get(ACTION_ADD_PRESET_MAPPING)), "");
		mappingsPanel.add(new JButton(actionMap.get(ACTION_ADD_MAPPING)),
				"sizegroup editmappingbutton");
		mappingsPanel.add(new JButton(actionMap.get(ACTION_REMOVE_MAPPINGS)),
				"sizegroup editmappingbutton");
		mappingsPanel.add(new JButton(actionMap
				.get(ACTION_UPDATE_PORT_MAPPINGS)),
				"sizegroup editmappingbutton, wrap");
		return mappingsPanel;
	}

	@Action(name = ACTION_UPDATE_ADDRESSES, enabledProperty = PROPERTY_ROUTER_CONNECTED)
	public void updateAddresses() {
		Router router = PortMapperApp.getInstance().getRouter();
		if (router == null) {
			logger.warn("Not connected to router, can not update addresses");
			return;
		}
		internalIPLabel.setText(router.getInternalIPAddress());
		try {
			externalIPLabel.setText(router.getExternalIPAddress());
		} catch (RouterException e) {
			externalIPLabel.setText("");
			logger.error("Did not get external IP address", e);
		}
	}

	@Action(name = ACTION_CONNECT_ROUTER)
	public void connectRouter() {
		boolean connected = PortMapperApp.getInstance().connectRouter();
		if (connected) {
			updateAddresses();
			updatePortMappings();
		}
	}

	private void addMapping(PortMapping portMapping) {
		boolean success = false;
		try {
			success = PortMapperApp.getInstance().getRouter().addPortMapping(
					portMapping);
		} catch (RouterException e) {
			logger.error("Could not add port mapping", e);
		}

		if (success) {
			logger.info("Portmapping was added successfully");
		} else {
			logger.warn("Portmapping was NOT added successfully");
			JOptionPane.showMessageDialog(this.getFrame(),
					"The port mapping could not be added.",
					"Error adding port mapping", JOptionPane.WARNING_MESSAGE);
		}
		this.updatePortMappings();
	}

	@Action(name = ACTION_REMOVE_MAPPINGS, enabledProperty = PROPERTY_MAPPING_SELECTED)
	public void removeMappings() {
		Collection<PortMapping> selectedMappings = this
				.getSelectedPortMappings();
		for (PortMapping mapping : selectedMappings) {
			logger.info("Removing mapping " + mapping);
			boolean success = false;
			try {
				success = PortMapperApp.getInstance().getRouter()
						.removeMapping(mapping);
			} catch (RouterException e) {
				logger.error("Could not remove port mapping " + mapping, e);
				break;
			}
			if (success) {
				logger.info("Mapping was removed successfully: " + mapping);
			} else {
				logger
						.error("Mapping was not removed successfully: "
								+ mapping);
				break;
			}
		}
		if (selectedMappings.size() > 0) {
			updatePortMappings();
		}
	}

	@Action(name = ACTION_DISPLAY_ROUTER_INFO, enabledProperty = PROPERTY_ROUTER_CONNECTED)
	public void displayRouterInfo() {
		Router router = PortMapperApp.getInstance().getRouter();
		if (router == null) {
			logger.warn("Not connected to router, could not get router info");
			return;
		}
		try {
			router.logRouterInfo();
		} catch (RouterException e) {
			logger.error("Could not get router info", e);
		}
	}

	@Action(name = ACTION_SHOW_ABOUT_DIALOG)
	public void showAboutDialog() {
		PortMapperApp.getInstance().show(new AboutDialog());
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
		Router router = PortMapperApp.getInstance().getRouter();
		if (router != null) {
			try {
				Collection<PortMapping> mappings = router.getPortMappings();
				logger.info("Found " + mappings.size() + " mappings");
				this.tableModel.setMappings(mappings);
			} catch (RouterException e) {
				logger.error("Could not get port mappings", e);
			}
		} else {
			this.tableModel.setMappings(new LinkedList<PortMapping>());
			logger
					.warn("Not connected to router, could not update port mappings");
		}
	}

	@Action(name = ACTION_ADD_PRESET_MAPPING, enabledProperty = PROPERTY_PRESET_MAPPING_SELECTED)
	public void addPresetMapping() {
		PortMapping selectedItem = (PortMapping) presetMappingComboBox
				.getSelectedItem();
		if (selectedItem != null) {
			String localHostAddress = PortMapperApp.getInstance()
					.getLocalHostAddress();
			if (localHostAddress == null) {
				JOptionPane.showMessageDialog(this.getFrame(), PortMapperApp
						.getResourceMap().getString(
								"messages.error_getting_localhost_address"),
						PortMapperApp.getResourceMap().getString(
								"messages.error"), JOptionPane.ERROR_MESSAGE);
			} else {
				PortMapping newMapping = (PortMapping) selectedItem.clone();
				newMapping.setInternalClient(localHostAddress);
				addMapping(newMapping);
			}
		}
	}

	@Action(name = ACTION_ADD_MAPPING, enabledProperty = PROPERTY_ROUTER_CONNECTED)
	public void addMapping() {
		PortMapperApp.getInstance().show(new AddPortMappingDialog());
		updatePortMappings();
	}

	public void fireConnectionStateChange(boolean stateBefore) {
		firePropertyChange(PROPERTY_ROUTER_CONNECTED, stateBefore,
				PortMapperApp.getInstance().getRouter() != null);
	}

	public boolean isConnectedToRouter() {
		// return PortMapperApp.getInstance().getRouter() != null;
		return true;
	}

	public boolean isMappingSelected() {
		return this.isConnectedToRouter()
				&& this.getSelectedPortMappings().size() > 0;
	}

	public boolean isPresetMappingSelected() {
		return this.isConnectedToRouter()
				&& this.presetMappingComboBox.getSelectedItem() != null;
	}

	public Collection<PortMapping> getSelectedPortMappings() {
		int[] selectedRows = mappingsTable.getSelectedRows();
		Collection<PortMapping> selectedMappings = new LinkedList<PortMapping>();
		if (selectedRows != null) {
			for (int rowNumber : selectedRows) {
				if (rowNumber >= 0) {
					PortMapping mapping = tableModel.getPortMapping(rowNumber);
					if (mapping != null) {
						selectedMappings.add(mapping);
					}
				}
			}
		}
		return selectedMappings;
	}

	private void copyTextToClipboard(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), new ClipboardOwner() {
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		});
	}
}
