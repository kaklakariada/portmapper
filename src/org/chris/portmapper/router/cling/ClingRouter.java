package org.chris.portmapper.router.cling;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicReference;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.meta.UDAVersion;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.igd.callback.GetExternalIP;
import org.teleal.cling.support.igd.callback.PortMappingAdd;
import org.teleal.cling.support.igd.callback.PortMappingDelete;

import com.esotericsoftware.minlog.Log;

/**
 * 
 */
public class ClingRouter extends AbstractRouter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Service<?, ?> service;

    private final Registry registry;

    private final ControlPoint controlPoint;

    public ClingRouter(final Service<?, ?> service, final Registry registry, final ControlPoint controlPoint) {
        super(getName(service));
        this.service = service;
        this.registry = registry;
        this.controlPoint = controlPoint;
    }

    private static String getName(final Service<?, ?> service) {
        return service.getDevice().getDisplayString();
    }

    @Override
    public String getExternalIPAddress() throws RouterException {
        final AtomicReference<String> externalIP = new AtomicReference<>();
        new GetExternalIP(service) {
            @Override
            public void failure(@SuppressWarnings("rawtypes") final ActionInvocation invocation,
                    final UpnpResponse operation, final String defaultMsg) {
                throw new RuntimeException("Failed to retrieve external ip address: " + defaultMsg);
            }

            @Override
            protected void success(final String externalIPAddress) {
                externalIP.set(externalIPAddress);
            }
        }.setControlPoint(controlPoint).run();
        return externalIP.get();
    }

    @Override
    public String getInternalHostName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getInternalPort() throws RouterException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Collection<PortMapping> getPortMappings() throws RouterException {
        return Collections.emptyList();
    }

    @Override
    public void logRouterInfo() throws RouterException {
        logger.info("Service id: " + service.getServiceId());
        logger.info("Reference: " + service.getReference());
        logger.info("Display name: " + service.getDevice().getDisplayString());
        final UDAVersion version = service.getDevice().getVersion();
        logger.info("Version: " + version.getMajor() + "." + version.getMinor());
    }

    @Override
    public void addPortMappings(final Collection<PortMapping> mappings) throws RouterException {
        // TODO Auto-generated method stub

    }

    @Override
    public void addPortMapping(final PortMapping mapping) throws RouterException {
        new PortMappingAdd(service, registry.getUpnpService().getControlPoint(), convertMapping(mapping)) {
            @Override
            public void success(@SuppressWarnings("rawtypes") final ActionInvocation invocation) {
                logger.debug("Port mapping added: " + mapping);
            }

            @Override
            public void failure(@SuppressWarnings("rawtypes") final ActionInvocation invocation,
                    final UpnpResponse operation, final String defaultMsg) {
                throw new RuntimeException("Failed to add port mapping: " + defaultMsg);
            }
        }.run(); // Synchronous!
    }

    private org.teleal.cling.support.model.PortMapping convertMapping(final PortMapping mapping) {
        final UnsignedIntegerFourBytes leaseTimeDuration = new UnsignedIntegerFourBytes(0);
        final UnsignedIntegerTwoBytes externalPort = new UnsignedIntegerTwoBytes(mapping.getExternalPort());
        final UnsignedIntegerTwoBytes internalPort = new UnsignedIntegerTwoBytes(mapping.getInternalPort());
        final org.teleal.cling.support.model.PortMapping.Protocol protocol = mapping.getProtocol() == Protocol.TCP ? org.teleal.cling.support.model.PortMapping.Protocol.TCP
                : org.teleal.cling.support.model.PortMapping.Protocol.UDP;
        return new org.teleal.cling.support.model.PortMapping(true, leaseTimeDuration, mapping.getRemoteHost(),
                externalPort, internalPort, mapping.getInternalClient(), protocol, mapping.getDescription());
    }

    @Override
    public void removeMapping(final PortMapping mapping) throws RouterException {
        new PortMappingDelete(service, convertMapping(mapping)) {
            @Override
            public void success(@SuppressWarnings("rawtypes") final ActionInvocation invocation) {
                logger.debug("Port mapping remove: " + mapping);
            }

            @Override
            public void failure(@SuppressWarnings("rawtypes") final ActionInvocation invocation,
                    final UpnpResponse operation, final String defaultMsg) {
                throw new RuntimeException("Failed to remove port mapping: " + defaultMsg);
            }
        }.run();
    }

    @Override
    public void removePortMapping(final Protocol protocol, final String remoteHost, final int externalPort)
            throws RouterException {
        removeMapping(new PortMapping(protocol, remoteHost, externalPort, null, 0, null));
    }

    @Override
    public void disconnect() {
        Log.debug("Shutdown registry");
        registry.shutdown();
    }
}
