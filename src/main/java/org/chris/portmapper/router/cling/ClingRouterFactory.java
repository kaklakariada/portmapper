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
package org.chris.portmapper.router.cling;

import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.meta.RemoteService;
import org.jdesktop.application.Application.ExitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClingRouterFactory extends AbstractRouterFactory {

    private static final long DISCOVERY_TIMEOUT_SECONDS = 5;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ClingRouterFactory(final PortMapperApp app) {
        super(app, "Cling lib");
    }

    @Override
    protected List<IRouter> findRoutersInternal() throws RouterException {
        final UpnpServiceConfiguration config = new DefaultUpnpServiceConfiguration();
        final ClingRegistryListener clingRegistryListener = new ClingRegistryListener();
        final UpnpService upnpService = new UpnpServiceImpl(config, clingRegistryListener);
        shutdownServiceOnExit(upnpService);

        log.debug("Start searching using upnp service");
        upnpService.getControlPoint().search();
        final RemoteService service = (RemoteService) clingRegistryListener
                .waitForServiceFound(DISCOVERY_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        if (service == null) {
            log.debug("Did not find a service after {} seconds", DISCOVERY_TIMEOUT_SECONDS);
            return Collections.emptyList();
        }

        log.debug("Found service {}", service);
        return Arrays
                .<IRouter> asList(new ClingRouter(service, upnpService.getRegistry(), upnpService.getControlPoint()));
    }

    private void shutdownServiceOnExit(final UpnpService upnpService) {
        app.addExitListener(new ExitListener() {
            @Override
            public void willExit(final EventObject event) {
                log.debug("Shutdown upnp service");
                upnpService.shutdown();
            }

            @Override
            public boolean canExit(final EventObject event) {
                return true;
            }
        });
    }

    @Override
    protected IRouter connect(final String locationUrl) throws RouterException {
        throw new UnsupportedOperationException("Direct connection is not supported for Cling library.");
    }
}
