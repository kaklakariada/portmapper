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
package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

import net.sbbi.upnp.impls.InternetGatewayDevice;

/**
 * Router factory using the SBBI UPnP library.
 */
public class SBBIRouterFactory extends AbstractRouterFactory {

    /**
     * The timeout in milliseconds for finding a router device.
     */
    private static final int DISCOVERY_TIMEOUT = 5000;

    public SBBIRouterFactory(final PortMapperApp app) {
        super(app, "SBBI UPnP lib");
    }

    @Override
    protected List<IRouter> findRoutersInternal() throws RouterException {

        final InternetGatewayDevice[] devices;
        try {
            devices = InternetGatewayDevice.getDevices(DISCOVERY_TIMEOUT);
        } catch (final IOException e) {
            throw new RouterException("Could not find devices", e);
        }

        if (devices == null || devices.length == 0) {
            return Collections.emptyList();
        }

        final List<IRouter> routers = new ArrayList<>(devices.length);

        for (final InternetGatewayDevice device : devices) {
            routers.add(new SBBIRouter(app, device));
        }

        return routers;
    }

    @Override
    protected IRouter connect(final String locationUrl) throws RouterException {
        throw new UnsupportedOperationException(
                "Direct connection via location URL is not supported for SBBI library.");
    }
}
