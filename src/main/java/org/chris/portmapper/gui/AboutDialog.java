/**
 * UPnP PortMapper - A tool for managing port forwardings via UPnP
 * Copyright (C) 2015 Christoph Pirkl <christoph at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.chris.portmapper.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.ActionMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.gui.util.URLLabel;
import org.jdesktop.application.Action;

import net.miginfocom.swing.MigLayout;

// Deep inheritance hierarchy required by Swing API
@SuppressWarnings("squid:MaximumInheritanceDepth")
public class AboutDialog extends JDialog {

    private static final long serialVersionUID = 1L;
    private static final String DIALOG_NAME = "about_dialog";
    private static final String ACTION_CLOSE = "about_dialog.close";

    public AboutDialog(final PortMapperApp app) {
        super(app.getMainFrame(), true);

        this.setName(DIALOG_NAME);
        final ActionMap actionMap = app.getContext().getActionMap(this.getClass(), this);

        final JPanel pane = new JPanel(new MigLayout("", "[center,grow]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        this.add(pane);

        pane.add(createLabel("about_dialog.label1"), "wrap");
        pane.add(createLabel("about_dialog.label2"), "wrap");
        pane.add(createLabel("about_dialog.label3"), "wrap");
        pane.add(createLabel("about_dialog.label4"), "wrap");
        pane.add(new URLLabel("about_dialog.upnplib_label"), "split 3");
        pane.add(new URLLabel("about_dialog.weupnp_label"), "");
        pane.add(new URLLabel("about_dialog.cling_label"), "wrap");
        pane.add(new URLLabel("about_dialog.app_framework_label"), "wrap");
        pane.add(new URLLabel("about_dialog.slf4j_label"), "split 3");
        pane.add(new URLLabel("about_dialog.logback_label"), "");
        pane.add(new URLLabel("about_dialog.miglayout_label"), "wrap unrelated");

        pane.add(createLabel("about_dialog.label5"), "wrap related");
        pane.add(new URLLabel("about_dialog.homepage_label"), //$NON-NLS-1$
                "wrap unrelated"); //$NON-NLS-1$
        pane.add(new JButton(actionMap.get(ACTION_CLOSE)));

        // Register an action listener that closes the window when the ESC
        // button is pressed
        final KeyStroke escKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, true);
        final ActionListener windowCloseActionListener = e -> close();
        getRootPane().registerKeyboardAction(windowCloseActionListener, escKeyStroke,
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        this.pack();
    }

    @Action(name = ACTION_CLOSE)
    public void close() {
        setVisible(false);
        this.dispose();
    }

    private JLabel createLabel(final String name) {
        final JLabel newLabel = new JLabel(name);
        newLabel.setName(name);
        return newLabel;
    }
}
