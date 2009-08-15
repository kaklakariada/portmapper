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
 * The {@link LogTextArea} appends all log message to the displayed text and
 * scrolls down.
 * 
 * @author Christoph
 * @version $Id$
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

	/*
	 * (non-Javadoc)
	 * 
	 * @seede.ursusminor.tasklist.web.standalone.logging.LogMessageListener#
	 * addLogMessage(java.lang.String)
	 */
	public void addLogMessage(String message) {
		this.append(message);
		this.setCaretPosition(this.getDocument().getLength());
	}
}
