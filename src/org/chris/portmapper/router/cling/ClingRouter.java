package org.chris.portmapper.router.cling;

import java.util.Collection;
import java.util.Collections;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;
import org.teleal.cling.model.types.UnsignedIntegerFourBytes;
import org.teleal.cling.model.types.UnsignedIntegerTwoBytes;
import org.teleal.cling.registry.Registry;
import org.teleal.cling.support.igd.callback.GetExternalIP;
import org.teleal.cling.support.igd.callback.PortMappingAdd;
import org.teleal.cling.support.igd.callback.PortMappingDelete;

/**
 * 
 */
public class ClingRouter extends AbstractRouter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Service<?, ?> service;

    private final Registry registry;

    public ClingRouter(final Service<?, ?> service, final Registry registry) {
        super(getName(service));
        this.service = service;
        this.registry = registry;
    }

    private static String getName(final Service<?, ?> service) {
        return service.getDevice().getDisplayString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#getExternalIPAddress()
     */
    @Override
    public String getExternalIPAddress() throws RouterException {
        final String externalIP;
        new GetExternalIP(service) {

            @Override
            public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
                // TODO Auto-generated method stub

            }

            @Override
            protected void success(final String externalIPAddress) {
                // externalIP = externalIPAddress;

            }
        }.run();
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#getInternalHostName()
     */
    @Override
    public String getInternalHostName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#getInternalPort()
     */
    @Override
    public int getInternalPort() throws RouterException {
        // TODO Auto-generated method stub
        return 0;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#getPortMappings()
     */
    @Override
    public Collection<PortMapping> getPortMappings() throws RouterException {
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#logRouterInfo()
     */
    @Override
    public void logRouterInfo() throws RouterException {
        logger.info("Service id: " + service.getServiceId());
        logger.info("Reference: " + service.getReference());
        logger.info("Display name: " + service.getDevice().getDisplayString());
        logger.info("Version: " + service.getDevice().getVersion());
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#addPortMappings(java.util.Collection)
     */
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
        final org.teleal.cling.support.model.PortMapping pm = new org.teleal.cling.support.model.PortMapping(true,
                leaseTimeDuration, mapping.getRemoteHost(), externalPort, internalPort, mapping.getInternalClient(),
                protocol, mapping.getDescription());
        return pm;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#removePortMapping(org.chris.portmapper .model.Protocol,
     * java.lang.String, int)
     */
    @Override
    public void removePortMapping(final Protocol protocol, final String remoteHost, final int externalPort)
            throws RouterException {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.chris.portmapper.router.IRouter#disconnect()
     */
    @Override
    public void disconnect() {
        // TODO Auto-generated method stub

    }

}
