package org.chris.portmapper.router.cling.action;

import java.util.Collections;
import java.util.Map;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class GetPortMappingEntryAction extends AbstractClingAction<PortMapping> {

    private final int index;

    public GetPortMappingEntryAction(final Service<RemoteDevice, RemoteService> service, final int index) {
        super(service, "GetGenericPortMappingEntry");
        this.index = index;
    }

    @Override
    public Map<String, Object> getArgumentValues() {
        return Collections.<String, Object> singletonMap("NewPortMappingIndex", new UnsignedIntegerTwoBytes(index));
    }

    @Override
    public PortMapping convert(final ActionInvocation<RemoteService> response) {
        final Protocol protocol = Protocol.getProtocol(getStringValue(response, "NewProtocol"));
        final String remoteHost = getStringValue(response, "NewRemoteHost");
        final int externalPort = getIntValue(response, "NewExternalPort");
        final String internalClient = getStringValue(response, "NewInternalClient");
        final int internalPort = getIntValue(response, "NewInternalPort");
        final String description = getStringValue(response, "NewPortMappingDescription");
        final boolean enabled = getBooleanValue(response, "NewEnabled");
        final long leaseDuration = getLongValue(response, "NewLeaseDuration");
        return new PortMapping(protocol, remoteHost, externalPort, internalClient, internalPort, description, enabled,
                leaseDuration);
    }

    private boolean getBooleanValue(final ActionInvocation<RemoteService> response, final String argumentName) {
        return (boolean) response.getOutput(argumentName).getValue();
    }

    protected int getIntValue(final ActionInvocation<?> response, final String argumentName) {
        return ((UnsignedIntegerTwoBytes) response.getOutput(argumentName).getValue()).getValue().intValue();
    }

    protected long getLongValue(final ActionInvocation<?> response, final String argumentName) {
        return ((UnsignedIntegerFourBytes) response.getOutput(argumentName).getValue()).getValue().longValue();
    }

    protected String getStringValue(final ActionInvocation<?> response, final String argumentName) {
        return (String) response.getOutput(argumentName).getValue();
    }
}
