package org.chris.portmapper.router.cling.callback;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public class GetNumberOfPortMappings extends SyncActionCallback<Integer> {

    public GetNumberOfPortMappings(final RemoteService service, final ControlPoint controlPoint) {
        super(service, controlPoint, "GetPortMappingNumberOfEntries");
    }

    @Override
    protected Integer convertResult(final ActionInvocation<?> invocation) {
        return Integer.parseInt((String) invocation.getOutput("PortMappingNumberOfEntries").getValue());
    }

}
