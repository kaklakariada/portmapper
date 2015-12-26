/**
 *
 */
package org.chris.portmapper.model;

import java.io.Serializable;

/**
 * This class is used by {@link PortMapping} to store the information about a single port mapping, i.e. the protocol
 * (TCP or UDP) and internal and extern port.
 *
 * @author chris
 */
public class SinglePortMapping implements Cloneable, Serializable {

    private static final long serialVersionUID = 7458514232916039775L;
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
    public SinglePortMapping(final Protocol protocol, final int internalPort, final int externalPort) {
        super();
        this.protocol = protocol;
        this.internalPort = internalPort;
        this.externalPort = externalPort;
    }

    public int getExternalPort() {
        return externalPort;
    }

    public void setExternalPort(final int externalPort) {
        this.externalPort = externalPort;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(final Protocol protocol) {
        this.protocol = protocol;
    }

    public int getInternalPort() {
        return internalPort;
    }

    public void setInternalPort(final int internalPort) {
        this.internalPort = internalPort;
    }

    @Override
    public Object clone() {
        return new SinglePortMapping(protocol, internalPort, externalPort);
    }
}
