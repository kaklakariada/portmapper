package org.chris.portmapper.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import net.miginfocom.swing.MigLayout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.Settings;
import org.chris.portmapper.router.dummy.DummyRouterFactory;
import org.chris.portmapper.router.sbbi.SBBIRouterFactory;
import org.chris.portmapper.router.weupnp.WeUPnPRouterFactory;
import org.jdesktop.application.Action;

/**
 * This class represents the settings dialog.
 * 
 * @author chris
 * @version $Id$
 */
public class SettingsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final Log logger = LogFactory.getLog(this.getClass());

	private final static String DIALOG_NAME = "settings_dialog";

	private final static String ACTION_SAVE = DIALOG_NAME + ".save";
	private final static String ACTION_CANCEL = DIALOG_NAME + ".cancel";

	private JCheckBox useEntityEncoding;

	private JComboBox<Level> logLevelComboBox;
	private JComboBox<String> routerFactoryClassComboBox;

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

	private static JLabel createLabel(final String name) {
		final JLabel newLabel = new JLabel(name);
		newLabel.setName(name);
		return newLabel;
	}

	private JPanel getDialogPane() {
		final ActionMap actionMap = PortMapperApp.getInstance().getContext()
				.getActionMap(this.getClass(), this);
		final Settings settings = PortMapperApp.getInstance().getSettings();

		final JPanel dialogPane = new JPanel(new MigLayout("", // Layout
				// Constraints
				"[right]rel[left,grow 100]", // Column Constraints
				"")); // Row Constraints

		logger.debug("Use entity encoding is " + settings.isUseEntityEncoding());
		useEntityEncoding = new JCheckBox(
				"settings_dialog.use_entity_encoding",
				settings.isUseEntityEncoding());
		useEntityEncoding.setName("settings_dialog.use_entity_encoding");

		dialogPane.add(useEntityEncoding, "span 2, wrap");

		dialogPane.add(createLabel("settings_dialog.log_level"));

		logLevelComboBox = new JComboBox<>(new Vector<>(Arrays.asList(
				Level.ALL, Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN,
				Level.ERROR, Level.FATAL, Level.OFF)));
		logLevelComboBox.setSelectedItem(Level.toLevel(settings.getLogLevel()));

		dialogPane.add(logLevelComboBox, "wrap");

		dialogPane.add(createLabel("settings_dialog.upnp_lib"));

		routerFactoryClassComboBox = new JComboBox<>(new Vector<>(
				Arrays.asList(SBBIRouterFactory.class.getName(),
						WeUPnPRouterFactory.class.getName(),
						DummyRouterFactory.class.getName())));
		routerFactoryClassComboBox.setSelectedItem(settings
				.getRouterFactoryClassName());
		dialogPane.add(routerFactoryClassComboBox, "span 2, wrap");

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
		final Settings settings = PortMapperApp.getInstance().getSettings();
		settings.setUseEntityEncoding(useEntityEncoding.isSelected());
		settings.setLogLevel(((Level) logLevelComboBox.getSelectedItem())
				.toString());
		settings.setRouterFactoryClassName(routerFactoryClassComboBox
				.getSelectedItem().toString());

		PortMapperApp.getInstance().setLogLevel(settings.getLogLevel());

		logger.debug("Saved settings " + settings);
		this.dispose();
	}

	@Action(name = ACTION_CANCEL)
	public void cancel() {
		this.dispose();
	}
}
