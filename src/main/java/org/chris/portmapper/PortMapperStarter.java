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
package org.chris.portmapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class PortMapperStarter {

    private static final Logger LOG = LoggerFactory.getLogger(PortMapperStarter.class);

    @SuppressWarnings("java:S4823") // Command line arguments are used securely
    public static void main(final String[] args) {
        redirectJavaUtilLoggingToLogback();
        final PortMapperCli cli = new PortMapperCli();
        try {
            cli.start(args);
        } catch (final Exception e) {
            LOG.error("PortMapper failed with exception " + e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void redirectJavaUtilLoggingToLogback() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
