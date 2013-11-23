package org.wetorrent.upnp;

/**
 * Exception caused by a weupnp action.
 */
public class WeUPnPException extends Exception {

	private static final long serialVersionUID = 1L;

	public WeUPnPException(final String message) {
		super(message);
	}

	public WeUPnPException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
