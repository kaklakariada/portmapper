package org.chris.portmapper.router;

public class RouterException extends Exception {

    private static final long serialVersionUID = 1L;

    public RouterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RouterException(final String message) {
        super(message);
    }
}
