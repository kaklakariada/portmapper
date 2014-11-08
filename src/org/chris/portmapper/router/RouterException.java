package org.chris.portmapper.router;

/**
 * @author chris
 */
public class RouterException extends Exception {

    /**
	 *
	 */
    private static final long serialVersionUID = 1L;

    public RouterException(final String arg0, final Throwable arg1) {
        super(arg0, arg1);
    }

    public RouterException(final String arg0) {
        super(arg0);
    }

}
