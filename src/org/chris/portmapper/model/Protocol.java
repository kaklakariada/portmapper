/**
 * 
 */
package org.chris.portmapper.model;

import java.io.Serializable;

/**
 * This {@link Enum} represents the protocol of a {@link SinglePortMapping},
 * possible values are {@link #TCP} and {@link #UDP}.
 * 
 * @author chris
 * 
 * @version $Id$
 */
public enum Protocol implements Serializable {

	TCP("TCP"), UDP("UDP");

	private final String name;

	private Protocol(String name) {
		this.name = name;
	}

	public static Protocol getProtocol(String name) {
		if (name != null && name.equalsIgnoreCase("TCP")) {
			return TCP;
		}
		if (name != null && name.equalsIgnoreCase("UDP")) {
			return UDP;
		}
		throw new IllegalArgumentException("Invalid protocol name '" + name
				+ "'");
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
};
