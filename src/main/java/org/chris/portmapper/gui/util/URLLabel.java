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
 *
 */
package org.chris.portmapper.gui.util;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JLabel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class implements a label that looks and behaves like a link, i.e. you can click on it and the URL is opened in a
 * browser.
 */
public class URLLabel extends JLabel {

    private static final long serialVersionUID = 1L;

    private final static Logger logger = LoggerFactory.getLogger(URLLabel.class);

    private String text;

    private final Desktop desktop;

    private URI uri;

    public URLLabel(final String name) {
        this.text = name;
        this.setLabelText();
        this.setName(name);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent arg0) {
                openUrl();
            }
        });
    }

    private void openUrl() {
        logger.debug("User clicked on URLLabel: open URL '{}' in browser", uri);
        if (desktop == null || !desktop.isSupported(Desktop.Action.BROWSE)) {
            logger.warn("Opening URLs is not supported on this machine, please open url manually: {}", uri);
            return;
        }
        try {
            desktop.browse(uri);
        } catch (final IOException e) {
            throw new RuntimeException("Error opening uri " + uri, e);
        }
    }

    private static URI createUri(final String url) {
        try {
            return new URI(url);
        } catch (final URISyntaxException e) {
            throw new RuntimeException("Error creating URI for url " + url);
        }
    }

    private void setLabelText() {
        final String url = uri != null ? uri.toString() : "";
        super.setText("<html><a href=\\\\\\\\\\\"" + url + "\\\\\\\\\\\">" + text + "</a></html>");
    }

    public String getUrl() {
        return uri.toString();
    }

    public void setUrl(final String url) {
        this.uri = createUri(url);
        setLabelText();
    }

    public String getLabel() {
        return text;
    }

    public void setLabel(final String text) {
        this.text = text;
        setLabelText();
    }
}
