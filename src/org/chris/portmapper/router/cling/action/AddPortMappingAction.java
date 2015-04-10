package org.chris.portmapper.router.cling.action;

import java.util.HashMap;
import java.util.Map;

import org.chris.portmapper.model.PortMapping;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;
import org.fourthline.cling.model.types.UnsignedIntegerFourBytes;
import org.fourthline.cling.model.types.UnsignedIntegerTwoBytes;

public class AddPortMappingAction extends AbstractClingAction<Void> {

    private final PortMapping portMapping;

    public AddPortMappingAction(final Service<RemoteDevice, RemoteService> service, final PortMapping portMapping) {
        super(service, "AddPortMapping");
        this.portMapping = portMapping;
    }

    @Override
    public Map<String, Object> getArgumentValues() {
        final HashMap<String, Object> args = new HashMap<>();
        args.put("NewExternalPort", new UnsignedIntegerTwoBytes(portMapping.getExternalPort()));
        args.put("NewProtocol", portMapping.getProtocol());
        args.put("NewInternalClient", portMapping.getInternalClient());
        args.put("NewInternalPort", new UnsignedIntegerTwoBytes(portMapping.getInternalPort()));
        args.put("NewLeaseDuration", new UnsignedIntegerFourBytes(portMapping.getLeaseDuration()));
        args.put("NewEnabled", portMapping.isEnabled());
        args.put("NewRemoteHost", portMapping.getRemoteHost());
        args.put("NewPortMappingDescription", portMapping.getDescription());
        return args;
    }

    @Override
    public Void convert(final ActionInvocation<RemoteService> response) {
        return null;
    }
}
