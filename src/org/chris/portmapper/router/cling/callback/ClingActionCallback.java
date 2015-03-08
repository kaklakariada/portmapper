package org.chris.portmapper.router.cling.callback;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.chris.portmapper.router.cling.ClingRouterException;
import org.fourthline.cling.controlpoint.ActionCallback;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.UpnpResponse;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.ActionArgument;
import org.fourthline.cling.model.meta.ActionArgument.Direction;
import org.fourthline.cling.model.meta.RemoteService;

abstract class ClingActionCallback extends ActionCallback {

    protected ClingActionCallback(final RemoteService service, final ControlPoint controlPoint, final String action) {
        this(service, controlPoint, action, Collections.<ActionArgumentValue<RemoteService>> emptyList());
    }

    protected ClingActionCallback(final RemoteService service, final ControlPoint controlPoint, final String action,
            final List<ActionArgumentValue<RemoteService>> arguments) {
        super(getAction(service, action, arguments));
        setControlPoint(controlPoint);
    }

    @SuppressWarnings("unchecked")
    private static ActionInvocation<?> getAction(final RemoteService service, final String actionName,
            final List<ActionArgumentValue<RemoteService>> arguments) {
        final Action<RemoteService> action = service.getAction(actionName);
        if (action == null) {
            throw new ClingRouterException("No action found for name '" + actionName + "'. Available actions: "
                    + Arrays.toString(service.getActions()));
        }
        @SuppressWarnings("rawtypes")
        final ActionArgumentValue[] argumentArray = arguments.toArray(new ActionArgumentValue[arguments.size()]);
        return new ActionInvocation<RemoteService>(action, argumentArray);
    }

    protected static ActionArgumentValue<RemoteService> arg(final String name, final Object value) {
        return new ActionArgumentValue<>(new ActionArgument<RemoteService>(name, name, Direction.IN), value);
    }

    @Override
    public void failure(@SuppressWarnings("rawtypes") final ActionInvocation invocation, final UpnpResponse operation,
            final String defaultMsg) {
        throw new ClingRouterException("Error executing " + invocation + ", response: " + operation + ", message: "
                + defaultMsg);
    }
}
