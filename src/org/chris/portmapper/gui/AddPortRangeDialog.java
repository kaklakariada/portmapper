package org.chris.portmapper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.model.Protocol;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;

/**
 * This class represents the dialog that can add a range of ports to the edit
 * presets dialog.
 * 
 * @author chris
 * @version $Id$
 */
public class AddPortRangeDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static String DIALOG_NAME = "add_port_range_dialog";

	private final static String ACTION_ADD = DIALOG_NAME + ".add";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";

	private final EditPresetDialog editPresetDialog;

	private JTextField internalPortFrom;
	private JTextField internalPortTo;

	private JTextField externalPortFrom;
	private JTextField externalPortTo;

	private JCheckBox internalEqualsExternalPorts;

	private JButton okButton;

	private JComboBox<Protocol> protocolComboBox;

	private final PortMapperApp app;

	public AddPortRangeDialog(final PortMapperApp app,
			final EditPresetDialog editPresetDialog) {
		super(app.getMainFrame(), true);
		this.app = app;

		this.editPresetDialog = editPresetDialog;

		logger.debug("Create settings dialog");
		this.setContentPane(this.getDialogPane());

		this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setName(DIALOG_NAME);
		this.setModal(true);
		this.pack();

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

	private static JLabel createLabel(final String postfix) {
		final String completeName = DIALOG_NAME + "." + postfix;
		final JLabel newLabel = new JLabel(completeName);
		newLabel.setName(completeName);
		return newLabel;
	}

	private JPanel getDialogPane() {

		final JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left]", // Column Constraints
				"")); // Row Constraints

		internalPortFrom = new JTextField(5);
		internalPortTo = new JTextField(5);
		externalPortFrom = new JTextField(5);
		externalPortTo = new JTextField(5);

		externalPortFrom.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (internalEqualsExternalPorts.isSelected()) {
					internalPortFrom.setText(externalPortFrom.getText());
				}
			}

			@Override
			public void keyTyped(final KeyEvent e) {
				// ignored
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				// ignored
			}
		});

		externalPortTo.addKeyListener(new KeyListener() {
			@Override
			public void keyReleased(final KeyEvent e) {
				if (internalEqualsExternalPorts.isSelected()) {
					internalPortTo.setText(externalPortTo.getText());
				}
			}

			@Override
			public void keyTyped(final KeyEvent e) {
				// ignored
			}

			@Override
			public void keyPressed(final KeyEvent e) {
				// ignored
			}
		});

		protocolComboBox = new JComboBox<>();
		protocolComboBox.addItem(Protocol.TCP);
		protocolComboBox.addItem(Protocol.UDP);
		protocolComboBox.setSelectedIndex(0);

		internalEqualsExternalPorts = new JCheckBox(
				"add_port_range_dialog.external_equal_internal");
		internalEqualsExternalPorts
				.setName("add_port_range_dialog.external_equal_internal");
		internalEqualsExternalPorts.setSelected(true);
		internalEqualsExternalPorts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				logger.debug("Checkbox value changed");
				internalPortFrom.setEnabled(!internalEqualsExternalPorts
						.isSelected());
				internalPortTo.setEnabled(!internalEqualsExternalPorts
						.isSelected());

				if (internalEqualsExternalPorts.isSelected()) {
					internalPortTo.setText(externalPortTo.getText());
					internalPortFrom.setText(externalPortFrom.getText());
				}
			}
		});

		internalPortFrom.setEnabled(!internalEqualsExternalPorts.isSelected());
		internalPortTo.setEnabled(!internalEqualsExternalPorts.isSelected());

		dialogPane.add(createLabel("protocol"));
		dialogPane.add(protocolComboBox, "wrap");

		dialogPane.add(createLabel("external_ports_from"));
		dialogPane.add(externalPortFrom);
		dialogPane.add(createLabel("external_ports_to"));
		dialogPane.add(externalPortTo, "wrap");

		dialogPane.add(internalEqualsExternalPorts, "left, span 4, wrap");

		dialogPane.add(createLabel("internal_ports_from"));
		dialogPane.add(internalPortFrom);
		dialogPane.add(createLabel("internal_ports_to"));
		dialogPane.add(internalPortTo, "wrap");

		final ActionMap actionMap = app.getContext().getActionMap(
				this.getClass(), this);
		dialogPane.add(new JButton(actionMap.get(ACTION_CANCEL)),
				"tag cancel, span 2");
		okButton = new JButton(actionMap.get(ACTION_ADD));
		dialogPane.add(okButton, "tag ok, span 2, wrap");

		return dialogPane;
	}

	/**
	 * This method is executed when the user clicks the save button. The method
	 * saves the entered preset
	 */
	@Action(name = ACTION_ADD)
	public void addPortRange() {
		final int newInternalPortFrom;
		final int newInternalPortTo;
		final int newExternalPortFrom;
		final int newExternalPortTo;

		try {
			newInternalPortFrom = Integer.parseInt(this.internalPortFrom
					.getText());
			newInternalPortTo = Integer.parseInt(this.internalPortTo.getText());
			newExternalPortFrom = Integer.parseInt(this.externalPortFrom
					.getText());
			newExternalPortTo = Integer.parseInt(this.externalPortTo.getText());
		} catch (final NumberFormatException e) {
			showErrorMessage("add_port_range_dialog.invalid_number.title",
					"add_port_range_dialog.invalid_number.message");
			return;
		}

		if (newInternalPortFrom >= newInternalPortTo) {
			showErrorMessage(
					"add_port_range_dialog.invalid_internal_range.title",
					"add_port_range_dialog.invalid_internal_range.message");
			return;
		}

		if (newExternalPortFrom >= newExternalPortTo) {
			showErrorMessage(
					"add_port_range_dialog.invalid_external_range.title",
					"add_port_range_dialog.invalid_external_range.message");
			return;
		}

		if (newInternalPortTo - newInternalPortFrom != newExternalPortTo
				- newExternalPortFrom) {
			showErrorMessage(
					"add_port_range_dialog.invalid_range_length.title",
					"add_port_range_dialog.invalid_range_length.message");
			return;
		}

		for (int i = newInternalPortFrom; i <= newInternalPortTo; i++) {
			final int internalPort = i;
			final int externalPort = (internalPort - newInternalPortFrom)
					+ newExternalPortFrom;
			final Protocol selectedProtocol = (Protocol) protocolComboBox
					.getSelectedItem();
			editPresetDialog.addPort(selectedProtocol, internalPort,
					externalPort);
		}

		this.dispose();
	}

	private void showErrorMessage(final String titleKey, final String messageKey) {
		final ResourceMap resourceMap = PortMapperApp.getResourceMap();
		JOptionPane.showMessageDialog(this, resourceMap.getString(messageKey),
				resourceMap.getString(titleKey), JOptionPane.ERROR_MESSAGE);
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.dispose();
	}
}
