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
package org.chris.portmapper.logging;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JTextArea;

/**
 * This class implements an output stream that appends to a list and allows listeners to register for written lines.
 */
public class LogMessageOutputStream extends OutputStream {

    /**
     * The listener to which the strings will be forwarded.
     */
    private LogMessageListener logListener;

    /**
     * The buffer to which the written strings are added until a listener is registered.
     */
    private List<String> unprocessedMessagesBuffer;

    /**
     * Creates a new {@link LogMessageOutputStream}. At creation time, no listener is registered, so that all added text
     * is stored in a buffer.
     */
    public LogMessageOutputStream() {
        unprocessedMessagesBuffer = new LinkedList<>();
    }

    @Override
    public void close() {
        // ignore
    }

    @Override
    public void flush() {
        // ignore
    }

    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        final String line = new String(b, off, len);
        addMessage(line);
    }

    @Override
    public void write(final int b) throws IOException {
        throw new UnsupportedOperationException();
    }

    /**
     * Append the given message to the registered {@link LogMessageListener}. If no listener is registered, the string
     * is written to a buffer. When a listener is registered, the buffered text will be appended to the listener.
     *
     * @param message
     *            the message to append.
     */
    public void addMessage(final String message) {
        if (this.logListener != null) {
            this.logListener.addLogMessage(message);
        } else {
            unprocessedMessagesBuffer.add(message);
        }
    }

    /**
     * Registers a {@link JTextArea}, so that all strings written to this writer are appended to the given text area.
     * After registration, all buffered strings are appended to the text area, so that no string is missed.
     *
     * @param textArea
     *            the text area to wich to append the strings.
     */
    public void registerListener(final LogMessageListener textArea) {
        this.logListener = textArea;

        // append the buffered text to the text area.
        for (final String line : unprocessedMessagesBuffer) {
            this.logListener.addLogMessage(line);
        }
        // we do not need the buffer any more, all text will be appended
        // to the text area.
        this.unprocessedMessagesBuffer = null;
    }
}
