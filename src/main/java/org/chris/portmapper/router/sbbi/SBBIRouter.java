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
package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.Settings;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.util.EncodingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sbbi.upnp.devices.UPNPRootDevice;
import net.sbbi.upnp.impls.InternetGatewayDevice;
import net.sbbi.upnp.messages.UPNPResponseException;

/**
 * This class represents a router device and provides methods for managing port mappings and getting information about
 * the router. It useses the SBBI library's {@link InternetGatewayDevice}.
 */
public class SBBIRouter extends AbstractRouter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * The wrapped router device.
     */
    private final InternetGatewayDevice router;

    /**
     * The maximum number of port mappings that we will try to retrieve from the router.
     */
    private final static int MAX_NUM_PORTMAPPINGS = 500;

    private final PortMapperApp app;

    SBBIRouter(final PortMapperApp app, final InternetGatewayDevice router) {
        super(router.getIGDRootDevice().getModelName());
        this.app = app;
        this.router = router;
    }

    @Override
    public String getExternalIPAddress() throws RouterException {
        logger.debug("Get external IP address...");
        String ipAddress;
        try {
            ipAddress = router.getExternalIPAddress();
        } catch (final UPNPResponseException e) {
            throw new RouterException("Could not get external IP: " + e.getMessage(), e);
        } catch (final IOException e) {
            throw new RouterException("Could not get external IP: " + e.getMessage(), e);
        }
        logger.info("Got external IP address {} for router.", ipAddress);
        return ipAddress;
    }

    @Override
    public String getInternalHostName() {
        logger.debug("Get internal IP address...");
        final URL presentationURL = router.getIGDRootDevice().getPresentationURL();
        if (presentationURL == null) {
            logger.warn("Did not get presentation url");
            return null;
        }
        final String ipAddress = presentationURL.getHost();
        logger.info("Got internal host name '{}' for router.", ipAddress);
        return ipAddress;
    }

    @Override
    public int getInternalPort() {
        logger.debug("Get internal port of router...");
        final URL presentationURL = router.getIGDRootDevice().getPresentationURL();
        // Presentation URL may be null in some situations.
        if (presentationURL != null) {
            final int presentationUrlPort = presentationURL.getPort();
            // https://sourceforge.net/tracker/?func=detail&aid=3198378&group_id=213879&atid=1027466
            // Some routers send an invalid presentationURL, in this case use
            // URLBase.
            if (presentationUrlPort > 0) {
                logger.debug("Got valid internal port {} from presentation URL '{}'", presentationUrlPort,
                        presentationURL);
                return presentationUrlPort;
            } else {
                logger.debug("Got invalid port {} from presentation url '{}'", presentationUrlPort, presentationURL);
            }
        } else {
            logger.debug("Presentation url is null");
        }
        final URL urlBase = router.getIGDRootDevice().getURLBase();
        final int urlBasePort = urlBase.getPort();
        logger.debug("Presentation URL is null or returns invalid port: using port {} of base url '{}'", urlBasePort,
                urlBase);

        return urlBasePort;
    }

    @Override
    public Collection<PortMapping> getPortMappings() throws RouterException {
        return new SBBIPortMappingExtractor(router, MAX_NUM_PORTMAPPINGS).getPortMappings();
    }

    @Override
    public void logRouterInfo() throws RouterException {
        final Map<String, String> info = new HashMap<>();
        final UPNPRootDevice rootDevice = router.getIGDRootDevice();
        info.put("friendlyName", rootDevice.getFriendlyName());
        info.put("manufacturer", rootDevice.getManufacturer());
        info.put("modelDescription", rootDevice.getModelDescription());
        info.put("modelName", rootDevice.getModelName());
        info.put("serialNumber", rootDevice.getSerialNumber());
        info.put("vendorFirmware", rootDevice.getVendorFirmware());

        info.put("modelNumber", rootDevice.getModelNumber());
        info.put("modelURL", rootDevice.getModelURL());
        info.put("manufacturerURL", rootDevice.getManufacturerURL().toExternalForm());
        info.put("presentationURL",
                rootDevice.getPresentationURL() != null ? rootDevice.getPresentationURL().toExternalForm() : null);
        info.put("urlBase", rootDevice.getURLBase().toExternalForm());

        final SortedSet<String> sortedKeys = new TreeSet<>(info.keySet());

        for (final String key : sortedKeys) {
            final String value = info.get(key);
            logger.info("Router Info: {} = {}", key, value);
        }

        logger.info("def loc: {}", rootDevice.getDeviceDefLoc());
        logger.trace("def loc data: {}", rootDevice.getDeviceDefLocData());
        logger.info("icons: {}", rootDevice.getDeviceIcons());
        logger.info("device type: {}", rootDevice.getDeviceType());
        logger.info("direct parent: {}", rootDevice.getDirectParent());
        logger.info("disc udn: {}", rootDevice.getDiscoveryUDN());
        logger.info("disc usn: {}", rootDevice.getDiscoveryUSN());
        logger.info("udn: {}", rootDevice.getUDN());
    }

    private boolean addPortMapping(final String description, final Protocol protocol,
            final int externalPort, final String internalClient, final int internalPort, final int leaseDuration)
            throws RouterException {

        final String protocolString = protocol == Protocol.TCP ? "TCP" : "UDP";

        final String encodedDescription = encodeIfNecessary(description);

        try {
            return router.addPortMapping(encodedDescription, null, internalPort, externalPort,
                    internalClient, leaseDuration, protocolString);
        } catch (final IOException | UPNPResponseException e) {
            throw new RouterException("Could not add port mapping: " + e.getMessage(), e);
        }
    }

    private String encodeIfNecessary(final String description) {
        final Settings settings = app.getSettings();
        if (settings == null || settings.isUseEntityEncoding()) {
            return EncodingUtilities.htmlEntityEncode(description);
        }
        return description;
    }

    @Override
    public void addPortMappings(final Collection<PortMapping> mappings) throws RouterException {
        for (final PortMapping portMapping : mappings) {
            logger.info("Adding port mapping {}", portMapping);
            addPortMapping(portMapping);
        }
    }

    @Override
    public void addPortMapping(final PortMapping mapping) throws RouterException {
        logger.info("Adding port mapping {}", mapping.getCompleteDescription());
        addPortMapping(mapping.getDescription(), mapping.getProtocol(),
                mapping.getExternalPort(), mapping.getInternalClient(), mapping.getInternalPort(), 0);
    }

    @Override
    public void removeMapping(final PortMapping mapping) throws RouterException {
        removePortMapping(mapping.getProtocol(), mapping.getRemoteHost(), mapping.getExternalPort());
    }

    @Override
    public void removePortMapping(final Protocol protocol, final String remoteHost, final int externalPort)
            throws RouterException {
        final String protocolString = (protocol.equals(Protocol.TCP) ? "TCP" : "UDP");
        try {
            router.deletePortMapping(remoteHost, externalPort, protocolString);
        } catch (final IOException e) {
            throw new RouterException("Could not remove port mapping", e);
        } catch (final UPNPResponseException e) {
            throw new RouterException("Could not remove port mapping", e);
        }
    }

    @Override
    public void disconnect() {
        // Nothing to do right now.
    }

    public long getUpTime() {
        // The SBBI library does not provide a method for getting the uptime.
        return 0;
    }
}
