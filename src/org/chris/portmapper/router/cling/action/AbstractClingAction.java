package org.chris.portmapper.router.cling.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.chris.portmapper.router.cling.ClingRouterException;
import org.fourthline.cling.model.action.ActionArgumentValue;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.Action;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.model.meta.Service;

abstract class AbstractClingAction<T> implements ClingAction<T> {

    private final Service<RemoteDevice, RemoteService> service;
    private final String actionName;

    public AbstractClingAction(final Service<RemoteDevice, RemoteService> service, final String actionName) {
        this.service = service;
        this.actionName = actionName;
    }

    public List<ActionArgumentValue<RemoteService>> getArguments() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ActionInvocation<?> getActionInvocation() {
        final Action<RemoteService> action = service.getAction(actionName);
        if (action == null) {
            throw new ClingRouterException("No action found for name '" + actionName + "'. Available actions: "
                    + Arrays.toString(service.getActions()));
        }
        final List<ActionArgumentValue<RemoteService>> arguments = getArguments();
        @SuppressWarnings("rawtypes")
        final ActionArgumentValue[] argumentArray = arguments.toArray(new ActionArgumentValue[arguments.size()]);
        return new ActionInvocation<RemoteService>(action, argumentArray);
    }
}
