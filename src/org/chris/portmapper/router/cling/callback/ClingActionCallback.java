package org.chris.portmapper.router.cling.callback;

import org.teleal.cling.controlpoint.ActionCallback;
import org.teleal.cling.controlpoint.ControlPoint;
import org.teleal.cling.model.action.ActionInvocation;
import org.teleal.cling.model.message.UpnpResponse;
import org.teleal.cling.model.meta.Service;

/**
 * 
 */
abstract class ClingActionCallback extends ActionCallback {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected ClingActionCallback(final Service<?, ?> service, final ControlPoint controlPoint, final String action) {
        super(new ActionInvocation(service.getAction(action)));
        setControlPoint(controlPoint);
    }

    @Override
    public void failure(final ActionInvocation invocation, final UpnpResponse operation, final String defaultMsg) {
        throw new RuntimeException("Error executing " + invocation + ", response: " + operation + ", message: "
                + defaultMsg);
    }
}
