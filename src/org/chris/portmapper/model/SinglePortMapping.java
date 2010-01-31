/**
 * 
 */
package org.chris.portmapper.model;

import java.io.Serializable;

/**
 * @author chris
 * @version $Id$
 */
public class SinglePortMapping implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3055383170284209747L;

	private int externalPort;
	private int internalPort;
	private Protocol protocol;

	/**
	 * @param protocol
	 * @param internalPort
	 * @param externalPort
	 */
	public SinglePortMapping(Protocol protocol, int internalPort,
			int externalPort) {
		super();
		this.protocol = protocol;
		this.internalPort = internalPort;
		this.externalPort = externalPort;
	}

	public int getExternalPort() {
		return externalPort;
	}

	public void setExternalPort(int externalPort) {
		this.externalPort = externalPort;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public void setProtocol(Protocol protocol) {
		this.protocol = protocol;
	}

	public int getInternalPort() {
		return internalPort;
	}

	public void setInternalPort(int internalPort) {
		this.internalPort = internalPort;
	}

	public Object clone() {
		return new SinglePortMapping(protocol, internalPort, externalPort);
	}
}
