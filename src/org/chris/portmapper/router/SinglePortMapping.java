/**
 * 
 */
package org.chris.portmapper.router;

/**
 * @author chris
 * 
 */
public class SinglePortMapping implements Cloneable {

	public enum Protocol {
		TCP, UDP
	};

	private int externalPort;
	private Protocol protocol;
	private int internalPort;

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
