package org.chris.portmapper.gui;

import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import org.chris.portmapper.PortMapperApp;
import org.jdesktop.application.Action;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	private final static String DIALOG_NAME = "about_dialog";
	private final static String ACTION_CLOSE = "about_dialog.close";

	public AboutDialog() throws HeadlessException {
		super(PortMapperApp.getInstance().getMainFrame(), true);

		ActionMap actionMap = PortMapperApp.getInstance().getContext()
				.getActionMap(this.getClass(), this);

		JPanel pane = new JPanel(new MigLayout("", "[center,grow]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		this.add(pane);

		pane.add(createLabel("MainWindow.program_name"), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$
		pane.add(createLabel("AboutDialog.description"), //$NON-NLS-1$
				"wrap"); //$NON-NLS-1$
		pane.add(createLabel("AboutDialog.created_by"), "wrap"); //$NON-NLS-1$ //$NON-NLS-2$
		pane
				.add(
						new JLabel(
								"<html><a href=\\\\\"http://sourceforge.net/projects/upnp-portmapper/\\\\\">http://sourceforge.net/projects/upnp-portmapper/</a></html>"), //$NON-NLS-1$
						"wrap unrelated"); //$NON-NLS-1$
		JButton closeButton = new JButton(actionMap.get(ACTION_CLOSE)); //$NON-NLS-1$
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		pane.add(closeButton);
		this.pack();
	}

	@Action(name = ACTION_CLOSE)
	public void close() {
		this.dispose();
	}

	private JLabel createLabel(String name) {
		String labelKey = getComponentName(name);
		JLabel newLabel = new JLabel(labelKey);
		newLabel.setName(labelKey);
		return newLabel;
	}

	/**
	 * @param name
	 * @return
	 */
	private String getComponentName(String name) {
		return DIALOG_NAME + "." + name;
	}
}
