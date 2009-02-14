/**
 * 
 */
package org.wetorrent.upnp;

/**
 * @author chris
 * 
 */
@SuppressWarnings("serial")
public class WeUPnPException extends Exception {

	/**
	 * @param message
	 */
	public WeUPnPException(String message) {
		super(message);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WeUPnPException(String message, Throwable cause) {
		super(message, cause);
	}

}
