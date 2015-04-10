/**
 *
 */
package org.chris.portmapper.router.cling;

public class ClingRouterException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ClingRouterException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ClingRouterException(final String message) {
        super(message);
    }
}
