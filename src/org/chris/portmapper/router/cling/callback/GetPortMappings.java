package org.chris.portmapper.router.cling.callback;

import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Service;

public class GetPortMappings extends ClingActionCallback {

    protected GetPortMappings(final Service<?, ?> service, final ControlPoint controlPoint) {
        super(service, controlPoint, "");
    }

    @Override
    public void success(final ActionInvocation invocation) {
        // TODO Auto-generated method stub
    }
}
