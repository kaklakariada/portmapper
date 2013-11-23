package org.chris.portmapper.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.Settings;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.PortMappingPreset;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.model.SinglePortMapping;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

/**
 * This class represents the edit preset dialog.
 */
public class EditPresetDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static final String PROPERTY_PORTS = "ports";

	private JTextField remoteHostTextField, internalClientTextField,
			presetNameTextField;
	private final List<SinglePortMapping> ports;
	private final PropertyChangeSupport propertyChangeSupport;

	private JCheckBox useLocalhostCheckBox;
	private JTable portsTable;

	private final static String DIALOG_NAME = "preset_dialog";

	private final static String ACTION_SAVE = DIALOG_NAME + ".save";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";
	private static final String ACTION_ADD_PORT = DIALOG_NAME + ".add_port";
	private static final String ACTION_ADD_PORT_RANGE = DIALOG_NAME
			+ ".add_port_range";
	private static final String ACTION_REMOVE_PORT = DIALOG_NAME
			+ ".remove_port";
	private static final String PROPERTY_PORT_SELECTED = "portSelected";

	private final Log logger = LogFactory.getLog(this.getClass());
	private final PortMappingPreset editedPreset;

	private PortsTableModel tableModel;

	private final PortMapperApp app;

	public EditPresetDialog(final PortMapperApp app,
			final PortMappingPreset portMappingPreset) {
		super(app.getMainFrame(), true);
		this.app = app;
		this.editedPreset = portMappingPreset;
		this.ports = new LinkedList<>();
		this.setName(DIALOG_NAME);
		initComponents();
		copyValuesFromPreset();
		this.propertyChangeSupport = new PropertyChangeSupport(ports);
		propertyChangeSupport.addPropertyChangeListener(PROPERTY_PORTS,
				tableModel);

		// Register an action listener that closes the window when the ESC
		// button is pressed
		final KeyStroke escKeyStroke = KeyStroke.getKeyStroke(
				KeyEvent.VK_ESCAPE, 0, true);
		final ActionListener windowCloseActionListener = new ActionListener() {
			@Override
			public final void actionPerformed(final ActionEvent e) {
				cancel();
			}
		};
		getRootPane().registerKeyboardAction(windowCloseActionListener,
				escKeyStroke, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void copyValuesFromPreset() {
		remoteHostTextField.setText(editedPreset.getRemoteHost());
		presetNameTextField.setText(editedPreset.getDescription());

		for (final SinglePortMapping port : editedPreset.getPorts()) {
			this.ports.add((SinglePortMapping) port.clone());
		}

		final boolean useLocalhost = (editedPreset.getInternalClient() == null);

		final String localhostAddress = app.getLocalHostAddress();

		if (useLocalhost && localhostAddress == null) {
			useLocalhostCheckBox.setSelected(false);
			useLocalhostCheckBox.setEnabled(false);
		} else {

			useLocalhostCheckBox.setSelected(useLocalhost);
			internalClientTextField.setEnabled(!useLocalhost);

			internalClientTextField.setText(useLocalhost ? localhostAddress
					: editedPreset.getInternalClient());
		}
	}

	private static JLabel createLabel(final String name) {
		final JLabel newLabel = new JLabel(name);
		newLabel.setName(name);
		return newLabel;
	}

	private void initComponents() {
		final ActionMap actionMap = app.getContext().getActionMap(
				this.getClass(), this);

		final JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left,grow 100]", // Column Constraints
				"")); // Row Constraints

		presetNameTextField = new JTextField();
		dialogPane.add(createLabel("preset_dialog.description"), "align label");
		dialogPane.add(presetNameTextField, "span 2, growx, wrap");

		remoteHostTextField = new JTextField();
		remoteHostTextField.setColumns(10);
		dialogPane.add(createLabel("preset_dialog.remote_host"), "");
		dialogPane.add(remoteHostTextField, "growx");
		dialogPane.add(
				createLabel("preset_dialog.remote_host_empty_for_all"), "wrap"); //$NON-NLS-2$

		internalClientTextField = new JTextField();
		internalClientTextField.setColumns(10);

		useLocalhostCheckBox = new JCheckBox(
				"preset_dialog.internal_client_use_local_host", true);
		useLocalhostCheckBox
				.setName("preset_dialog.internal_client_use_local_host");
		useLocalhostCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent arg0) {
				internalClientTextField.setEnabled(!useLocalhostCheckBox
						.isSelected());
				if (useLocalhostCheckBox.isSelected()) {
					internalClientTextField.setText(app.getLocalHostAddress());
				} else {
					// internalClientTextField.setText("");
				}
			}
		});

		// Check if the local host address can be retrieved

		final String localHostAddress = app.getLocalHostAddress();

		if (localHostAddress != null) {
			logger.debug("Found localhost address " + localHostAddress
					+ ". Enable localhost checkbox.");
			internalClientTextField.setText(localHostAddress);
			internalClientTextField.setEnabled(false);
			useLocalhostCheckBox.setEnabled(true);
		} else {
			logger.debug("Did not find localhost address: disable localhost checkbox.");
			useLocalhostCheckBox.setSelected(false);
			useLocalhostCheckBox.setEnabled(false);
			internalClientTextField.setEnabled(true);
			internalClientTextField.setText("");
		}

		dialogPane.add(createLabel("preset_dialog.internal_client"),
				"align label");
		dialogPane.add(internalClientTextField, "growx");
		dialogPane.add(useLocalhostCheckBox, "wrap");

		dialogPane.add(getPortsPanel(), "span 3, grow, wrap");

		dialogPane.add(new JButton(actionMap.get(ACTION_CANCEL)),
				"tag cancel, span 2");
		final JButton okButton = new JButton(actionMap.get(ACTION_SAVE));
		dialogPane.add(okButton, "tag ok, wrap");

		setContentPane(dialogPane);
		dialogPane.getRootPane().setDefaultButton(okButton);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setModal(true);
		pack();
	}

	private Component getPortsPanel() {
		final ActionMap actionMap = app.getContext().getActionMap(
				this.getClass(), this);

		final JPanel portsPanel = new JPanel(new MigLayout("", "", ""));
		portsPanel.setBorder(BorderFactory.createTitledBorder(app
				.getResourceMap().getString("preset_dialog.ports.title")));

		tableModel = new PortsTableModel(app, ports);
		portsTable = new JTable(tableModel);
		portsTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		portsTable.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(final ListSelectionEvent e) {
						firePropertyChange(PROPERTY_PORT_SELECTED, false,
								isPortSelected());
					}
				});
		final JComboBox<Protocol> protocolComboBox = new JComboBox<>();
		protocolComboBox.addItem(Protocol.TCP);
		protocolComboBox.addItem(Protocol.UDP);
		portsTable.getColumnModel().getColumn(0)
				.setCellEditor(new DefaultCellEditor(protocolComboBox));

		portsPanel.add(new JScrollPane(portsTable), "spany 3");

		portsPanel.add(new JButton(actionMap.get(ACTION_ADD_PORT)), "wrap");
		portsPanel.add(new JButton(actionMap.get(ACTION_ADD_PORT_RANGE)),
				"wrap");
		portsPanel.add(new JButton(actionMap.get(ACTION_REMOVE_PORT)), "wrap");

		return portsPanel;
	}

	protected void presetSelected(final PortMapping item) {
		this.presetNameTextField.setText(item.getDescription());
		this.remoteHostTextField.setText(item.getRemoteHost());
		// this.externalPortSpinner.setValue(item.getExternalPort());
		// this.internalPortSpinner.setValue(item.getInternalPort());
		if (item.getInternalClient() != null) {
			this.useLocalhostCheckBox.setSelected(false);
			this.internalClientTextField.setText(item.getInternalClient());
		}
	}

	@Action(name = ACTION_ADD_PORT)
	public void addPort() {
		addPort(Protocol.TCP, 1, 1);
	}

	public void addPort(final Protocol protocol, final int internalPort,
			final int externalPort) {
		this.ports.add(new SinglePortMapping(protocol, internalPort,
				externalPort));
		firePropertyChange(PROPERTY_PORT_SELECTED, false, isPortSelected());
		propertyChangeSupport.firePropertyChange(PROPERTY_PORTS, null,
				this.ports);
	}

	@Action(name = ACTION_ADD_PORT_RANGE)
	public void addPortRange() {
		logger.debug("Open port range dialog");
		app.show(new AddPortRangeDialog(app, this));
	}

	@Action(name = ACTION_REMOVE_PORT, enabledProperty = PROPERTY_PORT_SELECTED)
	public void removePort() {

		// We have to delete the rows in descending order, else the wrong
		// row could be deleted. When removing a row, the indices of the
		// following rows will change. So we will delete the wrong row
		// when we try to delete a following row.
		// An IndexOutOfBoundsException could also occur.

		final int[] selectedRows = portsTable.getSelectedRows();
		Arrays.sort(selectedRows);

		for (int i = selectedRows.length - 1; i >= 0; i--) {
			final int row = selectedRows[i];
			logger.debug("Removing row " + row);
			this.ports.remove(row);
		}

		firePropertyChange(PROPERTY_PORT_SELECTED, false, isPortSelected());
		propertyChangeSupport.firePropertyChange(PROPERTY_PORTS, null,
				this.ports);
	}

	public boolean isPortSelected() {
		return this.portsTable.getSelectedRowCount() > 0;
	}

	/**
	 * This method is executed when the user clicks the save button. The method
	 * saves the entered preset
	 */
	@Action(name = ACTION_SAVE)
	public void save() {

		// Check, if the user entered a name for the preset and show an error
		// message.
		final String name = presetNameTextField.getText();
		if (name == null || name.trim().isEmpty()) {
			showErrorMessage("preset_dialog.error.title",
					"preset_dialog.error.no_description");
			return;
		}

		// Check, if a preset with the same name already exists.
		final Settings settings = app.getSettings();
		for (final PortMappingPreset preset : settings.getPresets()) {
			if (preset != editedPreset && preset.getDescription() != null
					&& preset.getDescription().equals(name)) {
				showErrorMessage("preset_dialog.error.title",
						"preset_dialog.error.duplicate_name");
				return;
			}
		}

		// Check, if the user added at least one port mapping to the preset.
		if (tableModel.getRowCount() == 0) {
			showErrorMessage("preset_dialog.error.title",
					"preset_dialog.error.no_ports");
			return;
		}

		if (useLocalhostCheckBox.isSelected()) {
			editedPreset.setInternalClient(null);
		} else {
			editedPreset.setInternalClient(internalClientTextField.getText());
		}
		editedPreset.setRemoteHost(remoteHostTextField.getText());
		editedPreset.setDescription(name);
		editedPreset.setPorts(this.ports);

		editedPreset.save(settings);

		logger.info("Saved preset '" + editedPreset.toString() + "'.");

		this.dispose();
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.setVisible(false);
		this.dispose();
	}

	private void showErrorMessage(final String titleKey, final String messageKey) {
		final ResourceMap resourceMap = app.getResourceMap();
		JOptionPane.showMessageDialog(this, resourceMap.getString(messageKey),
				resourceMap.getString(titleKey), JOptionPane.ERROR_MESSAGE);
	}
}
