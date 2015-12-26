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
/**
 * This is free software licensed under the Terms of the GNU Public
 * license (GPL) V3 (see http://www.gnu.org/licenses/gpl-3.0.html
 * for details
 *
 * No warranty whatsoever is provided. Use at your own risk.
 *
 * @author Christoph Pirkl
 */
package org.chris.portmapper.gui;

import java.awt.Font;

import javax.swing.JTextArea;

import org.chris.portmapper.logging.LogMessageListener;

/**
 * The {@link LogTextArea} appends all log message to the displayed text and scrolls down.
 *
 * @author Christoph
 */
@SuppressWarnings("serial")
public class LogTextArea extends JTextArea implements LogMessageListener {

    /**
     * Create a new instance and set default properties.
     */
    public LogTextArea() {
        super();
        setFont(Font.decode("Monospaced"));
        setEditable(false);
        setWrapStyleWord(true);
        setLineWrap(false);
    }

    @Override
    public void addLogMessage(final String message) {
        this.append(message);
        this.setCaretPosition(this.getDocument().getLength());
    }
}
