package org.chris.portmapper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.InetAddress;
import java.net.UnknownHostException;

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
import javax.swing.text.JTextComponent;

import net.miginfocom.swing.MigLayout;

import org.chris.portmapper.Application;
import org.chris.portmapper.router.PortMapping;

public class CreatePortMappingDialog {

	private JDialog dialog;
	private JComboBox presetComboBox;
	private JTextField remoteHostTextField, internalClientTextField,
			descriptionTextField;
	private JSpinner externalPortSpinner, internalPortSpinner;
	private JCheckBox useLocalhostCheckBox;
	private Application application;
	private JRadioButton tcpRadioButton, udpRadioButton;

	public CreatePortMappingDialog(Application app) {
		this.application = app;
		setupDialog();
	}

	private void setupDialog() {
		JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// //$NON-NLS-1$
				// Constraints
				"[right]rel[left,grow 100]", // Column Constraints
				// //$NON-NLS-1$
				"")); // Row Constraints //$NON-NLS-1$

		dialogPane.add(new JLabel(Messages
				.getString("CreatePortMappingDialog.preset")), "align label"); //$NON-NLS-1$
		presetComboBox = new JComboBox(new PresetComboBoxModel());
		presetComboBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent event) {
				presetSelected((PortMapping) event.getItem());
			}
		});
		dialogPane.add(presetComboBox, "growx, span 2, wrap"); //$NON-NLS-1$

		descriptionTextField = new JTextField();
		descriptionTextField.setName(Messages
				.getString("CreatePortMappingDialog.description")); //$NON-NLS-1$
		dialogPane.add(new JLabel(descriptionTextField.getName()),
				"align label");
		dialogPane.add(descriptionTextField, "span 2, growx, wrap"); //$NON-NLS-1$

		tcpRadioButton = new JRadioButton(Messages
				.getString("CreatePortMappingDialog.protocol_tcp"), true); //$NON-NLS-1$
		udpRadioButton = new JRadioButton(Messages
				.getString("CreatePortMappingDialog.protocol_udp"), false); //$NON-NLS-1$
		ButtonGroup protocolButtonGroup = new ButtonGroup();
		protocolButtonGroup.add(tcpRadioButton);
		protocolButtonGroup.add(udpRadioButton);

		dialogPane.add(new JLabel(Messages
				.getString("CreatePortMappingDialog.protocol")), "align label"); //$NON-NLS-1$
		dialogPane.add(tcpRadioButton, "split 2"); //$NON-NLS-1$
		dialogPane.add(udpRadioButton, "wrap"); //$NON-NLS-1$

		remoteHostTextField = new JTextField();
		remoteHostTextField.setName(Messages
				.getString("CreatePortMappingDialog.remote_host")); //$NON-NLS-1$
		remoteHostTextField.setColumns(10);
		dialogPane.add(new JLabel(remoteHostTextField.getName()));
		dialogPane.add(remoteHostTextField, "growx"); //$NON-NLS-1$
		dialogPane
				.add(
						new JLabel(
								Messages
										.getString("CreatePortMappingDialog.remote_host_empty_for_all")), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$

		externalPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535,
				1));
		externalPortSpinner.setName(Messages
				.getString("CreatePortMappingDialog.external_port")); //$NON-NLS-1$
		dialogPane
				.add(new JLabel(externalPortSpinner.getName()), "align label");
		dialogPane.add(externalPortSpinner, "wrap"); //$NON-NLS-1$

		internalClientTextField = new JTextField();
		internalClientTextField.setColumns(10);
		internalClientTextField.setName(Messages
				.getString("CreatePortMappingDialog.internal_client")); //$NON-NLS-1$

		useLocalhostCheckBox = new JCheckBox(
				Messages
						.getString("CreatePortMappingDialog.internal_client_use_local_host"), true); //$NON-NLS-1$
		useLocalhostCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				internalClientTextField.setEnabled(!useLocalhostCheckBox
						.isSelected());
				if (useLocalhostCheckBox.isSelected()) {
					internalClientTextField.setText(application.getRouter()
							.getLocalHostAddress());
				}
			}
		});

		// Check if the local host address can be retrieved
		String localHostAddress = application.getRouter().getLocalHostAddress();
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

		dialogPane.add(new JLabel(internalClientTextField.getName()),
				"align label");
		dialogPane.add(internalClientTextField, "growx"); //$NON-NLS-1$
		dialogPane.add(useLocalhostCheckBox, "wrap"); //$NON-NLS-1$

		internalPortSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 65535,
				1));
		internalPortSpinner.setName(Messages
				.getString("CreatePortMappingDialog.internal_port")); //$NON-NLS-1$
		dialogPane
				.add(new JLabel(internalPortSpinner.getName()), "align label");
		dialogPane.add(internalPortSpinner, "wrap unrelated"); //$NON-NLS-1$

		JButton okButton = new JButton(Messages
				.getString("CreatePortMappingDialog.ok_button")); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				createPortMapping();
			}
		});

		JButton cancelButton = new JButton(Messages
				.getString("CreatePortMappingDialog.cancel_button")); //$NON-NLS-1$
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				closeDialog();
			}
		});

		// dialogPane.add(new JLabel());
		dialogPane.add(cancelButton, "tag cancel, span 2"); //$NON-NLS-1$
		dialogPane.add(okButton, "tag ok, wrap"); //$NON-NLS-1$

		dialog = new JDialog(application.getMainWindow().getFrame(), Messages
				.getString("CreatePortMappingDialog.window_title"), true); //$NON-NLS-1$
		dialog.setContentPane(dialogPane);
		dialogPane.getRootPane().setDefaultButton(okButton);
		dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		dialog.pack();
		dialog.setLocationRelativeTo(application.getMainWindow().getFrame());
		dialog.setVisible(true);

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

	protected void closeDialog() {
		dialog.setVisible(false);
		dialog.dispose();
	}

	public static void addPortMapping(Application app) {
		new CreatePortMappingDialog(app);
	}

	private void createPortMapping() {
		PortMapping portMapping = this.getPortMapping();

		if (portMapping != null) {
			application.addMapping(portMapping);
			closeDialog();
		}
	}

	private PortMapping getPortMapping() {
		String remoteHost = validateIP(remoteHostTextField, true);
		String description = descriptionTextField.getText().trim();
		String internalClient = validateIP(internalClientTextField, false);
		Integer externalPort = (Integer) externalPortSpinner.getValue();
		Integer internalPort = (Integer) internalPortSpinner.getValue();
		String protocol = (tcpRadioButton.isSelected() ? PortMapping.PROTOCOL_TCP
				: PortMapping.PROTOCOL_UDP);

		PortMapping newPortMapping = null;
		if (description != null
				&& internalClient != null
				&& externalPort != null
				&& internalPort != null
				&& protocol != null
				&& (remoteHost != null || remoteHostTextField.getText().trim()
						.length() == 0)) {
			newPortMapping = new PortMapping(protocol, remoteHost,
					externalPort, internalClient, internalPort, description);
		}
		return newPortMapping;
	}

	private String validateIP(JTextComponent text, boolean optional) {

		if (text.getText() == null || text.getText().trim().length() == 0) {
			if (!optional) {
				JOptionPane
						.showMessageDialog(
								dialog,
								Messages
										.getString("CreatePortMappingDialog.invalid_host_name.message") //$NON-NLS-1$
								, text.getName(), JOptionPane.WARNING_MESSAGE);
			}
			return null;
		}

		InetAddress address = null;
		try {
			address = InetAddress.getByName(text.getText().trim());
		} catch (UnknownHostException e) {
			JOptionPane
					.showMessageDialog(
							dialog,
							Messages
									.getString("CreatePortMappingDialog.unresolved_host_name.error_message") //$NON-NLS-1$
							, text.getName(), JOptionPane.WARNING_MESSAGE);
			return null;
		}
		if (address == null) {
			JOptionPane
					.showMessageDialog(
							dialog,
							Messages
									.getString("CreatePortMappingDialog.invalid_host_name.message") //$NON-NLS-1$
							, text.getName(), JOptionPane.WARNING_MESSAGE);
		}
		return address.getHostAddress();
	}
}
