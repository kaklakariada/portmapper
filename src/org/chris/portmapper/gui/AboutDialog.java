package org.chris.portmapper.gui;

import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog {

	public AboutDialog(Frame owner) throws HeadlessException {
		super(owner, Messages.getString("AboutDialog.about_dialog_title")); //$NON-NLS-1$

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
		JButton closeButton = new JButton(Messages
				.getString("AboutDialog.close_dialog")); //$NON-NLS-1$
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		pane.add(closeButton);
		this.pack();
		this.setLocationRelativeTo(owner);
	}

	private JLabel createLabel(String labelKey) {
		JLabel newLabel = new JLabel(Messages.getString(labelKey));
		return newLabel;
	}
}
