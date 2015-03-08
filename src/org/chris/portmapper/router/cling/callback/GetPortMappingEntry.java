package org.chris.portmapper.router.cling.callback;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

import static java.util.Arrays.*;

public class GetPortMappingEntry extends SyncActionCallback<PortMapping> {

    public GetPortMappingEntry(final RemoteService service, final ControlPoint controlPoint, final int index) {
        super(service, controlPoint, "GetGenericPortMappingEntry", asList(arg("NewPortMappingIndex", index)));
    }

    @Override
    protected PortMapping convertResult(final ActionInvocation<?> invocation) {
        final Protocol protocol = null;
        final String remoteHost = null;
        final int externalPort = 0;
        final String internalClient = null;
        final int internalPort = 0;
        final String description = null;
        return new PortMapping(protocol, remoteHost, externalPort, internalClient, internalPort, description);
    }

}
