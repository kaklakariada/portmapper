package org.chris.portmapper.model;

import java.util.HashMap;
import java.util.Map;

import net.sbbi.upnp.messages.ActionResponse;

/**
 * This immutable class represents a port mapping / forwarding on a router.
 * 
 * @author chris
 * @version $Id$
 */
public class PortMapping implements Cloneable {

	private final int externalPort;
	private final Protocol protocol;
	private final int internalPort;
	private final String description;
	private final String internalClient;
	private final String remoteHost;
	private final boolean enabled;
	private final long leaseDuration;

	public PortMapping(Protocol protocol, String remoteHost, int externalPort,
			String internalClient, int internalPort, String description) {
		this(protocol, remoteHost, externalPort, internalClient, internalPort,
				description, true);
	}

	private PortMapping(Protocol protocol, String remoteHost, int externalPort,
			String internalClient, int internalPort, String description,
			boolean enabled) {
		super();
		this.protocol = protocol;
		this.remoteHost = remoteHost;
		this.externalPort = externalPort;
		this.internalClient = internalClient;
		this.internalPort = internalPort;
		this.description = description;
		this.enabled = enabled;
		this.leaseDuration = -1;
	}

	private PortMapping(ActionResponse response) {
		final Map<String, String> values = new HashMap<String, String>();

		for (Object argObj : response.getOutActionArgumentNames()) {
			final String argName = (String) argObj;
			values.put(argName, response.getOutActionArgumentValue(argName));
		}

		externalPort = Integer.parseInt(values.get("NewExternalPort"));
		internalPort = Integer.parseInt(values.get("NewInternalPort"));
		final String protocolString = values.get("NewProtocol");
		protocol = (protocolString.equalsIgnoreCase("TCP") ? Protocol.TCP
				: Protocol.UDP);
		description = values.get("NewPortMappingDescription");
		internalClient = values.get("NewInternalClient");
		remoteHost = values.get("NewRemoteHost");
		final String enabledString = values.get("NewEnabled");
		enabled = enabledString != null && enabledString.equals("1");
		leaseDuration = Long.parseLong(values.get("NewLeaseDuration"));
	}

	public static PortMapping create(ActionResponse response) {
		PortMapping mapping = new PortMapping(response);
		return mapping;
	}

	/**
	 * @return the leaseDuration
	 */
	public long getLeaseDuration() {
		return leaseDuration;
	}

	public int getExternalPort() {
		return externalPort;
	}

	public Protocol getProtocol() {
		return protocol;
	}

	public int getInternalPort() {
		return internalPort;
	}

	public String getDescription() {
		return description;
	}

	public String getInternalClient() {
		return internalClient;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public String getCompleteDescription() {
		StringBuilder b = new StringBuilder();
		b.append(protocol);
		b.append(" ");
		if (remoteHost != null) {
			b.append(remoteHost);
		}
		b.append(":");
		b.append(externalPort);
		b.append(" -> ");
		b.append(internalClient);
		b.append(":");
		b.append(internalPort);
		b.append(" ");
		b.append(enabled ? "enabled" : "not enabled");
		b.append(" ");
		b.append(description);
		return b.toString();
	}

	@Override
	public String toString() {
		return description;
	}

	public Object clone() {
		PortMapping clonedMapping = new PortMapping(protocol, remoteHost,
				externalPort, internalClient, internalPort, description,
				enabled);
		return clonedMapping;
	}
}
