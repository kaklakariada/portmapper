/**
 * 
 */
package org.chris.portmapper.router;

import java.io.Serializable;

/**
 * @author chris
 * 
 */
public class SinglePortMapping implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3055383170284209747L;

	private int externalPort;
	private Protocol protocol;
	private int internalPort;

	public SinglePortMapping() {
		super();
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
		SinglePortMapping port = new SinglePortMapping();
		port.externalPort = this.externalPort;
		port.internalPort = this.internalPort;
		port.protocol = this.protocol;
		return port;
	}

}
