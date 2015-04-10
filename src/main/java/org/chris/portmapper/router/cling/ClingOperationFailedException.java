package org.chris.portmapper.router.cling;

import org.fourthline.cling.model.message.control.IncomingActionResponseMessage;

public class ClingOperationFailedException extends ClingRouterException {

    private static final long serialVersionUID = 1L;
    private final IncomingActionResponseMessage response;

    public ClingOperationFailedException(final String message, final IncomingActionResponseMessage response) {
        super(message);
        assert response.getOperation().isFailed();
        this.response = response;
    }

    public IncomingActionResponseMessage getResponse() {
        return response;
    }
}
