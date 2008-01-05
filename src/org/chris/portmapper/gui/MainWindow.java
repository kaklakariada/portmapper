package org.chris.portmapper.gui;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import net.miginfocom.swing.MigLayout;

import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chris.portmapper.Application;
import org.chris.portmapper.Application.ApplicationState;
import org.chris.portmapper.logging.TextAreaWriter;
import org.chris.portmapper.router.PortMapping;

public class MainWindow {

	private Application application;

	private PortMappingsTableModel tableModel;
	private JTable mappingsTable;
	private JLabel externalIPLabel, internalIPLabel;
	private JFrame frame;
	private JComboBox presetMappingComboBox;
	private Collection<JButton> connectionButtons;

	public MainWindow(Application application) {
		this.application = application;
		this.connectionButtons = new LinkedList<JButton>();
		createAndShowGUI();

	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event-dispatching thread.
	 */
	private void createAndShowGUI() {
		// Create and set up the window.
		frame = new JFrame(Messages.getString("MainWindow.program_name")); //$NON-NLS-1$
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new MigLayout("", "[fill, grow]", //$NON-NLS-1$ //$NON-NLS-2$
				"[grow 50]unrelated[]unrelated[grow 50]")); //$NON-NLS-1$

		frame.add(getMappingsPanel(), "wrap"); //$NON-NLS-1$
		frame.add(getRouterPanel(), "wrap"); //$NON-NLS-1$
		frame.add(getLogPanel(), "wrap"); //$NON-NLS-1$

		// frame.setSize(300, 300);
		// Display the window.
		frame.pack();

		frame.setVisible(true);
	}

	private JButton createButton(String labelKey) {
		JButton newButton = new JButton(Messages.getString(labelKey));
		this.connectionButtons.add(newButton);
		return newButton;
	}

	private JComponent getRouterPanel() {
		JButton updateInfoButton = createButton("MainWindow.update"); //$NON-NLS-1$
		updateInfoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				application.updateRouterIPAddresses();
			}
		});

		JButton routerInfoButton = createButton("MainWindow.router_info"); //$NON-NLS-1$
		routerInfoButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				application.displayRouterInfo();
			}
		});

		JButton copyExternalIPButton = createButton("MainWindow.copy_ip"); //$NON-NLS-1$
		copyExternalIPButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						copyTextToClipboard(externalIPLabel.getText());
					}
				});

		JButton copyInternalIPButton = createButton("MainWindow.copy_ip"); //$NON-NLS-1$
		copyInternalIPButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						copyTextToClipboard(internalIPLabel.getText());
					}
				});

		JButton reconnectButton = new JButton(Messages
				.getString("MainWindow.reconnect")); //$NON-NLS-1$
		reconnectButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				application.reconnect();
			}
		});

		JButton aboutButton = new JButton(Messages
				.getString("MainWindow.about")); //$NON-NLS-1$
		aboutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				AboutDialog aboutDialog = new AboutDialog(getFrame());
				aboutDialog.setVisible(true);
			}
		});

		JPanel routerPanel = new JPanel(new MigLayout("", "", ""));
		routerPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("MainWindow.router"))); //$NON-NLS-1$

		routerPanel
				.add(
						new JLabel(Messages.getString("MainWindow.external_ip")), "align label"); //$NON-NLS-1$ //$NON-NLS-2$
		externalIPLabel = new JLabel(Messages
				.getString("MainWindow.not_connected")); //$NON-NLS-1$
		routerPanel.add(externalIPLabel, "width 120!"); //$NON-NLS-1$
		routerPanel.add(copyExternalIPButton);
		routerPanel.add(updateInfoButton,
				"wrap, sizegroup routerbutton, spany 2, aligny base"); //$NON-NLS-1$

		routerPanel.add(
				new JLabel(Messages.getString("MainWindow.internal_ip")),
				"align label");
		internalIPLabel = new JLabel(Messages
				.getString("MainWindow.not_connected"));
		routerPanel.add(internalIPLabel, "width 120!");
		routerPanel.add(copyInternalIPButton, "wrap");

		routerPanel.add(reconnectButton, "sizegroup routerbutton"); //$NON-NLS-1$
		routerPanel.add(routerInfoButton, "sizegroup routerbutton"); //$NON-NLS-1$
		routerPanel.add(aboutButton, "skip 2, sizegroup routerbutton"); //$NON-NLS-1$

		return routerPanel;
	}

	private JComponent getLogPanel() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris").getAppender("jtextarea"); //$NON-NLS-1$ //$NON-NLS-2$

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

		JPanel logPanel = new JPanel(new MigLayout("", "[grow, fill]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		logPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("MainWindow.log_messages"))); //$NON-NLS-1$
		logPanel.add(scrollPane, "height 100:200:"); //$NON-NLS-1$

		return logPanel;
	}

	private JComponent getMappingsPanel() {
		// Mappings panel

		tableModel = new PortMappingsTableModel();
		mappingsTable = new JTable(tableModel);
		mappingsTable
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		mappingsTable.setSize(new Dimension(400, 100));

		JScrollPane mappingsTabelPane = new JScrollPane();
		mappingsTabelPane.setViewportView(mappingsTable);

		presetMappingComboBox = new JComboBox(new PresetComboBoxModel());

		JButton addMappingButton = createButton("MainWindow.add_mapping"); //$NON-NLS-1$
		addMappingButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				CreatePortMappingDialog.addPortMapping(application);
			}
		});

		JButton addPresetMappingButton = createButton("MainWindow.add_preset_mapping"); //$NON-NLS-1$
		addPresetMappingButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						PortMapping portMapping = (PortMapping) presetMappingComboBox
								.getSelectedItem();
						presetMappingSelected(portMapping);

					}
				});

		JButton updateMappingsButton = createButton("MainWindow.update_mappings"); //$NON-NLS-1$
		updateMappingsButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						application.updatePortMappings();
					}
				});

		JButton removeMappingButton = createButton("MainWindow.remove_mapping"); //$NON-NLS-1$
		removeMappingButton
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(java.awt.event.ActionEvent evt) {
						application.removeMapping();
					}
				});

		JPanel mappingsPanel = new JPanel(new MigLayout("", "[fill,grow]", //$NON-NLS-1$ //$NON-NLS-2$
				"[grow,fill][]")); //$NON-NLS-1$
		mappingsPanel.setBorder(BorderFactory.createTitledBorder(Messages
				.getString("MainWindow.port_mappings"))); //$NON-NLS-1$
		mappingsPanel.add(mappingsTabelPane, "height 100:200:, wrap"); //$NON-NLS-1$
		mappingsPanel.add(presetMappingComboBox, "growx, split 5"); //$NON-NLS-1$
		mappingsPanel.add(addPresetMappingButton, ""); //$NON-NLS-1$
		mappingsPanel.add(addMappingButton, "sizegroup editmappingbutton"); //$NON-NLS-1$
		mappingsPanel.add(removeMappingButton, "sizegroup editmappingbutton"); //$NON-NLS-1$
		mappingsPanel.add(updateMappingsButton,
				"sizegroup editmappingbutton, wrap"); //$NON-NLS-1$
		return mappingsPanel;
	}

	private void presetMappingSelected(PortMapping selectedItem) {

		if (selectedItem != null) {
			String localHostAddress = this.application.getRouter()
					.getLocalHostAddress();
			if (localHostAddress == null) {
				JOptionPane
						.showMessageDialog(
								frame,
								Messages
										.getString("MainWindow.error_getting_localhost_address"),
								Messages.getString("MainWindow.error"),
								JOptionPane.ERROR_MESSAGE);
			} else {
				PortMapping newMapping = (PortMapping) selectedItem.clone();
				newMapping.setInternalClient(localHostAddress);
				application.addMapping(newMapping);
			}
		}
	}

	private void copyTextToClipboard(String text) {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		clipboard.setContents(new StringSelection(text), new ClipboardOwner() {
			public void lostOwnership(Clipboard clipboard, Transferable contents) {
			}
		});
	}

	public JFrame getFrame() {
		return frame;
	}

	public void setExternalIPLabel(String ip) {
		this.externalIPLabel.setText(ip);
	}

	public void setInternalIPLabel(String ip) {
		this.internalIPLabel.setText(ip);
	}

	public void setPortMappings(Collection<PortMapping> mappings) {
		this.tableModel.setMappings(mappings);
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

	public void setApplicationState(ApplicationState state) {
		if (!state.equals(ApplicationState.CONNECTED)) {
			setExternalIPLabel(Messages.getString("MainWindow.not_connected"));
			setInternalIPLabel(Messages.getString("MainWindow.not_connected"));
		}
		for (JButton button : this.connectionButtons) {
			button.setEnabled(state.equals(ApplicationState.CONNECTED));
		}
	}
}