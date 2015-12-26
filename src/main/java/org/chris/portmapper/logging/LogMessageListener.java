/**
 * This is free software licensed under the Terms of the GNU Public
 * license (GPL) V3 (see http://www.gnu.org/licenses/gpl-3.0.html
 * for details
 *
 * No warranty whatsoever is provided. Use at your own risk.
 *
 * @author Christoph
 */
package org.chris.portmapper.logging;

import javax.swing.JTextArea;

/**
 * The {@link LogMessageWriter} copies every written string to a {@link LogMessageListener}. All written strings are
 * buffered, so no string is missed. A {@link LogMessageListener} can be registered using method
 * {@link #registerListener(JTextArea)}.
 *
 * @author Christoph
 */
public interface LogMessageListener {

    /**
     * Process the given log message. This could mean e.g. to display the message to the user.
     * 
     * @param message
     *            the message to process.
     */
    public void addLogMessage(String message);
}
