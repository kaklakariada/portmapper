package org.chris.portmapper.gui;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.Settings;
import org.jdesktop.application.Action;

/**
 * This class represents the settings dialog.
 * 
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class SettingsDialog extends JDialog {

	private Log logger = LogFactory.getLog(this.getClass());

	private final static String DIALOG_NAME = "settings_dialog";

	private final static String ACTION_SAVE = DIALOG_NAME + ".save";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";

	private JCheckBox useEntityEncoding;

	private JComboBox logLevelComboBox;

	private JButton okButton;

	/**
	 * 
	 * @param portMappingPreset
	 */
	public SettingsDialog() {
		super(PortMapperApp.getInstance().getMainFrame(), true);
		logger.debug("Create settings dialog");
		this.setContentPane(this.getDialogPane());

		this.getRootPane().setDefaultButton(okButton);
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.setName(DIALOG_NAME);
		this.setModal(true);
		this.pack();
	}

	private static JLabel createLabel(String name) {
		JLabel newLabel = new JLabel(name);
		newLabel.setName(name);
		return newLabel;
	}

	private JPanel getDialogPane() {
		ActionMap actionMap = PortMapperApp.getInstance().getContext()
				.getActionMap(this.getClass(), this);
		Settings settings = PortMapperApp.getInstance().getSettings();

		JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left,grow 100]", // Column Constraints
				"")); // Row Constraints

		logger
				.debug("Use entity encoding is "
						+ settings.isUseEntityEncoding());
		useEntityEncoding = new JCheckBox(
				"settings_dialog.use_entity_encoding", settings
						.isUseEntityEncoding());
		useEntityEncoding.setName("settings_dialog.use_entity_encoding");

		dialogPane.add(useEntityEncoding, "span 2, wrap");

		dialogPane.add(createLabel("settings_dialog.log_level"));

		logLevelComboBox = new JComboBox(new Object[] { Level.ALL, Level.TRACE,
				Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR, Level.FATAL,
				Level.OFF });
		logLevelComboBox.setSelectedItem(settings.getLogLevel());

		dialogPane.add(logLevelComboBox, "wrap");

		dialogPane.add(new JButton(actionMap.get(ACTION_CANCEL)),
				"tag cancel, span 2");
		okButton = new JButton(actionMap.get(ACTION_SAVE));
		dialogPane.add(okButton, "tag ok, wrap");

		return dialogPane;
	}

	/**
	 * This method is executed when the user clicks the save button. The method
	 * saves the entered preset
	 */
	@Action(name = ACTION_SAVE)
	public void save() {

		Settings settings = PortMapperApp.getInstance().getSettings();
		settings.setUseEntityEncoding(useEntityEncoding.isSelected());
		settings.setLogLevel((Level) logLevelComboBox.getSelectedItem());

		PortMapperApp.getInstance().setLogLevel(settings.getLogLevel());

		logger.info("Saved settings " + settings);
		this.dispose();
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.dispose();
	}

	private String getResourceString(String name, Object... args) {
		return PortMapperApp.getResourceMap().getString(name, args);
	}
}
