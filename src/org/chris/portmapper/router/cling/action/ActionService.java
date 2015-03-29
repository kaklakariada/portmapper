package org.chris.portmapper.router.cling.action;

import java.net.URL;

import org.chris.portmapper.router.cling.ClingRouterException;
import org.fourthline.cling.controlpoint.ControlPoint;
import org.fourthline.cling.model.action.ActionInvocation;
import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;
import org.fourthline.cling.model.meta.RemoteService;
import org.fourthline.cling.protocol.sync.SendingAction;

/**
 */
public class ActionService {
    private final RemoteService remoteService;
    private final ControlPoint controlPoint;

    public ActionService(final RemoteService remoteService, final ControlPoint controlPoint) {
        this.remoteService = remoteService;
        this.controlPoint = controlPoint;
    }

    public <T> T run(final ClingAction<T> action) {
        // Figure out the remote URL where we'd like to send the action request to
        final URL controLURL = remoteService.getDevice().normalizeURI(remoteService.getControlURI());

        // Do it
        final ActionInvocation<?> actionInvocation = action.getActionInvocation();
        final SendingAction prot = controlPoint.getProtocolFactory().createSendingAction(actionInvocation, controLURL);
        prot.run();

        final IncomingActionResponseMessage response = prot.getOutputMessage();
        if (response == null) {
            throw new ClingRouterException("Got null response");
        } else if (response.getOperation().isFailed()) {
            throw new ClingRouterException("Invocation " + actionInvocation + " failed with operation "
                    + response.getOperation());
        }
        return action.convert(actionInvocation);
    }
}
