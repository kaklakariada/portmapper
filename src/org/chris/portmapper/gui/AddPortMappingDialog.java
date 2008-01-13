package org.chris.portmapper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.PortMapping;
import org.chris.portmapper.router.Router;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.Action;

@SuppressWarnings("serial")
public class AddPortMappingDialog extends JDialog {

	private JComboBox presetComboBox;
	private JTextField remoteHostTextField, internalClientTextField,
			descriptionTextField;
	private JSpinner externalPortSpinner, internalPortSpinner;
	private JCheckBox useLocalhostCheckBox;
	private JRadioButton tcpRadioButton, udpRadioButton;

	private final static String DIALOG_NAME = "add_mapping_dialog";

	private final static String ACTION_OK = DIALOG_NAME + ".ok";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";

	private Log logger = LogFactory.getLog(this.getClass());

	public AddPortMappingDialog() {
		super(PortMapperApp.getInstance().getMainFrame(), true);
		initComponents();
	}

	private JLabel createLabel(String name) {
		JLabel newLabel = new JLabel(name);
		newLabel.setName(name);
		return newLabel;
	}

	private void initComponents() {
		ActionMap actionMap = PortMapperApp.getInstance().getContext()
				.getActionMap(this.getClass(), this);

		JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left,grow 100]", // Column Constraints
				"")); // Row Constraints
		// dialogPane.setName(DIALOG_NAME);
		dialogPane.setName(DIALOG_NAME);
		dialogPane.add(createLabel("add_mapping_dialog.preset"), "align label");
		presetComboBox = new JComboBox(new PresetComboBoxModel());
		presetComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				presetSelected((PortMapping) event.getItem());
			}
		});
		dialogPane.add(presetComboBox, "growx, span 2, wrap");

		descriptionTextField = new JTextField();
		dialogPane.add(createLabel("add_mapping_dialog.description"),
				"align label");
		dialogPane.add(descriptionTextField, "span 2, growx, wrap");

		tcpRadioButton = new JRadioButton("add_mapping_dialog.protocol_tcp",
				true);
		udpRadioButton = new JRadioButton("add_mapping_dialog.protocol_tcp",
				false);
		tcpRadioButton.setName("add_mapping_dialog.protocol_tcp");
		udpRadioButton.setName("add_mapping_dialog.protocol_tcp");
		ButtonGroup protocolButtonGroup = new ButtonGroup();
		protocolButtonGroup.add(tcpRadioButton);
		protocolButtonGroup.add(udpRadioButton);

		dialogPane.add(createLabel("add_mapping_dialog.protocol"),
				"align label");
		dialogPane.add(tcpRadioButton, "split 2");
		dialogPane.add(udpRadioButton, "wrap");

		remoteHostTextField = new JTextField();
		remoteHostTextField.setColumns(10);
		dialogPane.add(createLabel("add_mapping_dialog.remote_host"), "");
		dialogPane.add(remoteHostTextField, "growx");
		dialogPane
				.add(
						createLabel("add_mapping_dialog.remote_host_empty_for_all"), "wrap"); //$NON-NLS-2$

		externalPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535,
				1));

		dialogPane.add(createLabel("add_mapping_dialog.external_port"),
				"align label");
		dialogPane.add(externalPortSpinner, "wrap");

		internalClientTextField = new JTextField();
		internalClientTextField.setColumns(10);

		useLocalhostCheckBox = new JCheckBox(
				"add_mapping_dialog.internal_client_use_local_host", true);
		useLocalhostCheckBox
				.setName("add_mapping_dialog.internal_client_use_local_host");
		useLocalhostCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				internalClientTextField.setEnabled(!useLocalhostCheckBox
						.isSelected());
				if (useLocalhostCheckBox.isSelected()) {
					internalClientTextField.setText(PortMapperApp.getInstance()
							.getLocalHostAddress());
				} else {
					internalClientTextField.setText("");
				}
			}
		});

		// Check if the local host address can be retrieved

		String localHostAddress = PortMapperApp.getInstance()
				.getLocalHostAddress();

		if (localHostAddress != null) {
			internalClientTextField.setText(localHostAddress);
			internalClientTextField.setEnabled(false);
			useLocalhostCheckBox.setEnabled(true);
		} else {
			useLocalhostCheckBox.setSelected(false);
			useLocalhostCheckBox.setEnabled(false);
			internalClientTextField.setEnabled(true);
			internalClientTextField.setText("");
		}

		dialogPane.add(createLabel("add_mapping_dialog.internal_client"),
				"align label");
		dialogPane.add(internalClientTextField, "growx");
		dialogPane.add(useLocalhostCheckBox, "wrap");

		internalPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535,
				1));
		dialogPane.add(createLabel("add_mapping_dialog.internal_port"),
				"align label");
		dialogPane.add(internalPortSpinner, "wrap unrelated");

		dialogPane.add(new JButton(actionMap.get(ACTION_CANCEL)),
				"tag cancel, span 2");
		JButton okButton = new JButton(actionMap.get(ACTION_OK));
		dialogPane.add(okButton, "tag ok, wrap");

		setContentPane(dialogPane);
		dialogPane.getRootPane().setDefaultButton(okButton);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setModal(true);
		setName("dlg_task");
		pack();
		// setLocationRelativeTo(application.getMainWindow().getFrame());
		// setVisible(true);

	}

	protected void presetSelected(PortMapping item) {
		this.descriptionTextField.setText(item.getDescription());
		this.remoteHostTextField.setText(item.getRemoteHost());
		this.externalPortSpinner.setValue(item.getExternalPort());
		this.internalPortSpinner.setValue(item.getInternalPort());
		if (item.getInternalClient() != null) {
			this.useLocalhostCheckBox.setSelected(false);
			this.internalClientTextField.setText(item.getInternalClient());
		}
		this.tcpRadioButton.setSelected(item.getProtocol().equals(
				PortMapping.PROTOCOL_TCP));
		this.udpRadioButton.setSelected(item.getProtocol().equals(
				PortMapping.PROTOCOL_UDP));
	}

	@Action(name = ACTION_OK)
	public void ok() {
		Router router = PortMapperApp.getInstance().getRouter();
		PortMapping portMapping = this.getPortMapping();
		if (portMapping == null) {
			return;
		} else if (router == null) {
			logger.warn("Not connected to router, could not add mapping");
			this.dispose();
		} else {
			try {
				router.addPortMapping(portMapping);
			} catch (RouterException e) {
				logger.error("Could not add port mapping", e);
				JOptionPane.showMessageDialog(this, PortMapperApp
						.getResourceMap().getString("todo"), PortMapperApp
						.getResourceMap().getString("todo"),
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			this.dispose();
		}
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.dispose();
	}

	private PortMapping getPortMapping() {
		String remoteHost = validateIP(remoteHostTextField.getText(), true,
				getResourceString("add_mapping_dialog.remote_host.text"));

		if (remoteHost == null
				&& remoteHostTextField.getText().trim().length() > 0) {
			remoteHostTextField.requestFocus();
			return null;
		}

		String internalClient = validateIP(internalClientTextField.getText(),
				false,
				getResourceString("add_mapping_dialog.internal_client.text"));
		if (internalClient == null) {
			internalClientTextField.requestFocus();
			return null;
		}

		String description = descriptionTextField.getText().trim();

		Integer externalPort = (Integer) externalPortSpinner.getValue();
		Integer internalPort = (Integer) internalPortSpinner.getValue();
		String protocol = (tcpRadioButton.isSelected() ? PortMapping.PROTOCOL_TCP
				: PortMapping.PROTOCOL_UDP);

		PortMapping newPortMapping = null;
		if (externalPort != null && internalPort != null && protocol != null) {
			newPortMapping = new PortMapping(protocol, remoteHost,
					externalPort, internalClient, internalPort, description);
		}
		return newPortMapping;
	}

	private String getResourceString(String name, Object... args) {
		return PortMapperApp.getResourceMap().getString(name, args);
	}

	private String validateIP(String ipAddress, boolean optional,
			String fieldName) {

		if (ipAddress == null || ipAddress.trim().length() == 0) {
			if (!optional) {
				JOptionPane.showMessageDialog(this, getResourceString(
						"add_mapping_dialog.no_host_name_given", fieldName),
						getResourceString("add_mapping_dialog.error_title"),
						JOptionPane.WARNING_MESSAGE);
			}
			return null;
		}

		InetAddress address = null;
		try {
			address = InetAddress.getByName(ipAddress.trim());
		} catch (UnknownHostException e) {
			JOptionPane.showMessageDialog(this, getResourceString(
					"add_mapping_dialog.unresolved_host_name", ipAddress,
					fieldName),
					getResourceString("add_mapping_dialog.error_title"),
					JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if (address == null) {
			JOptionPane.showMessageDialog(this, getResourceString(
					"add_mapping_dialog.invalid_host_name", ipAddress,
					fieldName),
					getResourceString("add_mapping_dialog.error_title"),
					JOptionPane.WARNING_MESSAGE);
		}
		return address.getHostAddress();
	}
}
