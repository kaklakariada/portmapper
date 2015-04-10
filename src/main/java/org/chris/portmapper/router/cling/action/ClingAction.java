package org.chris.portmapper.router.cling.action;

import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.meta.RemoteService;

public interface ClingAction<T> {

    ActionInvocation<RemoteService> getActionInvocation();

    T convert(ActionInvocation<RemoteService> response);
}
