/**
 * 
 */
package org.chris.portmapper.model;

import java.io.Serializable;

/**
 * This class is used by {@link PortMapping} to store the information about a
 * single port mapping, i.e. the protocol (TCP or UDP) and internal and extern
 * port.
 * 
 * @author chris
 * @version $Id$
 */
public class SinglePortMapping implements Cloneable, Serializable {

	private int externalPort;
	private int internalPort;
	private Protocol protocol;

	public SinglePortMapping() {
		this(Protocol.TCP, 1, 1);
	}

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
