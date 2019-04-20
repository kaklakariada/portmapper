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

import static java.util.stream.Collectors.*;

import java.time.Duration;
import java.util.EventObject;
import java.util.List;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.fourthline.cling.DefaultUpnpServiceConfiguration;
import org.fourthline.cling.UpnpService;
import org.fourthline.cling.UpnpServiceConfiguration;
import org.fourthline.cling.UpnpServiceImpl;
import org.fourthline.cling.model.message.header.UDADeviceTypeHeader;
import org.fourthline.cling.model.message.header.UpnpHeader;
import org.fourthline.cling.model.meta.RemoteService;
import org.jdesktop.application.Application.ExitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClingRouterFactory extends AbstractRouterFactory {

    private static final Duration DISCOVERY_TIMEOUT = Duration.ofSeconds(3);
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

        final UpnpHeader<?> searchType = new UDADeviceTypeHeader(ClingRegistryListener.IGD_DEVICE_TYPE);
        log.info("Start searching {} for device type {}", DISCOVERY_TIMEOUT, searchType);
        upnpService.getControlPoint().search(searchType, (int) DISCOVERY_TIMEOUT.toSeconds());
        return clingRegistryListener
                .waitForServiceFound(DISCOVERY_TIMEOUT) //
                .map(service -> (RemoteService) service)
                .map(service -> createRouter(service, upnpService)) //
                .collect(toList());
    }

    private ClingRouter createRouter(final RemoteService service, final UpnpService upnpService) {
        return new ClingRouter(service, upnpService.getRegistry(), upnpService.getControlPoint());
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
        throw new UnsupportedOperationException(
                "Direct connection via location URL is not supported for Cling library.");
    }
}
