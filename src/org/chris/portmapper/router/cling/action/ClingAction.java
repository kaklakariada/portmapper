package org.chris.portmapper.router.cling.action;

import org.fourthline.cling.model.action.ActionInvocation;

/**
 *
 */
public interface ClingAction<T> {

    ActionInvocation<?> getActionInvocation();

    T convert(ActionInvocation<?> response);
}
