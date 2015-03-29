package org.chris.portmapper.router.cling;

import java.util.Collection;
import java.util.Collections;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;
import org.chris.portmapper.router.cling.action.ActionService;
import org.chris.portmapper.router.cling.action.GetExternalIpAction;
import org.chris.portmapper.router.cling.callback.GetPortMappingEntry;
import org.chris.portmapper.router.cling.callback.SyncActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.meta.UDAVersion;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.support.igd.callback.PortMappingAdd;
import org.fourthline.cling.support.igd.callback.PortMappingDelete;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClingRouter extends AbstractRouter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RemoteService service;

    private final Registry registry;

    private final ControlPoint controlPoint;

    private final ActionService actionService;

    public ClingRouter(final RemoteService service, final Registry registry, final ControlPoint controlPoint) {
        super(getName(service));
        this.service = service;
        this.registry = registry;
        this.controlPoint = controlPoint;
        actionService = new ActionService(service, controlPoint);
    }

    private static String getName(final Service<?, ?> service) {
        return service.getDevice().getDisplayString();
    }

    @Override
    public String getExternalIPAddress() throws RouterException {
        return actionService.run(new GetExternalIpAction(service));
    }

    private <T> T executeClingCallback(final SyncActionCallback<T> callback) {
        return callback.execute();
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
        final PortMapping portMapping = executeClingCallback(new GetPortMappingEntry(service, controlPoint, 0));
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

    private org.fourthline.cling.support.model.PortMapping convertMapping(final PortMapping mapping) {
        final UnsignedIntegerFourBytes leaseTimeDuration = new UnsignedIntegerFourBytes(0);
        final UnsignedIntegerTwoBytes externalPort = new UnsignedIntegerTwoBytes(mapping.getExternalPort());
        final UnsignedIntegerTwoBytes internalPort = new UnsignedIntegerTwoBytes(mapping.getInternalPort());
        final org.fourthline.cling.support.model.PortMapping.Protocol protocol = mapping.getProtocol() == Protocol.TCP ? org.fourthline.cling.support.model.PortMapping.Protocol.TCP
                : org.fourthline.cling.support.model.PortMapping.Protocol.UDP;
        return new org.fourthline.cling.support.model.PortMapping(true, leaseTimeDuration, mapping.getRemoteHost(),
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
        logger.debug("Shutdown registry");
        registry.shutdown();
    }
}
