/**
 * 
 */
package org.chris.portmapper.model;

import java.beans.PersistenceDelegate;
import java.beans.XMLEncoder;
import java.io.Serializable;

/**
 * @author chris
 * 
 *         Note: it would be better to use a simple <code>enum</code>, but the
 *         {@link XMLEncoder} of JDK 5.0 does not know how to serialize enums.
 *         It would be possible to write a {@link PersistenceDelegate}, but I do
 *         not want to do this.
 * @see {@linkplain http 
 *      ://weblogs.java.net/blog/malenkov/archive/2006/08/how_to_encode_e.html}
 * 
 */
public class Protocol implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8825654573118875844L;

	public final static Protocol TCP = new Protocol("TCP");
	public final static Protocol UDP = new Protocol("UDP");

	private final String value;

	/**
	 * @param string
	 */
	private Protocol(String string) {
		this.value = string;
	}

	public String toString() {
		return value;
	}

	public String getValue() {
		return value;
	}
};
