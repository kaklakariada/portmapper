/**
 * 
 */
package org.chris.portmapper.model;

/**
 * This class represents the protocol ({@link #TCP} or {@link #UDP}) of a
 * {@link SinglePortMapping}.
 * 
 * 
 * @author chris
 * @version $Id$
 */
public enum Protocol {
	TCP("TCP"), UDP("UDP");

	private final String name;

	private Protocol(String name) {
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
};
