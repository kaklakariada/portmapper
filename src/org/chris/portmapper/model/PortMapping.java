package org.chris.portmapper.model;

import java.util.HashMap;
import java.util.Map;

import net.sbbi.upnp.messages.ActionResponse;

/**
 * 
 * @author chris
 * @version $Id$
 */
public class PortMapping implements Cloneable {

	private final int externalPort;
	private final Protocol protocol;
	private final int internalPort;
	private final String description;
	private String internalClient;
	private final String remoteHost;
	private boolean enabled;
	private long leaseDuration;

	public PortMapping(Protocol protocol, String remoteHost, int externalPort,
			String internalClient, int internalPort, String description) {
		super();
		this.protocol = protocol;
		this.remoteHost = remoteHost;
		this.externalPort = externalPort;
		this.internalClient = internalClient;
		this.internalPort = internalPort;
		this.description = description;
		this.enabled = true;
	}

	private PortMapping(ActionResponse response) {
		Map<String, String> values = new HashMap<String, String>();

		for (Object argObj : response.getOutActionArgumentNames()) {
			String argName = (String) argObj;
			values.put(argName, response.getOutActionArgumentValue(argName));
		}

		externalPort = Integer.parseInt(values.get("NewExternalPort"));
		internalPort = Integer.parseInt(values.get("NewInternalPort"));
		String protocolString = values.get("NewProtocol");
		protocol = (protocolString.equalsIgnoreCase("TCP") ? Protocol.TCP
				: Protocol.UDP);
		description = values.get("NewPortMappingDescription");
		internalClient = values.get("NewInternalClient");
		remoteHost = values.get("NewRemoteHost");
		leaseDuration = Long.parseLong(values.get("NewLeaseDuration"));
		String enabledString = values.get("NewEnabled");
		enabled = enabledString != null && enabledString.equals("1");
	}

	public static PortMapping create(ActionResponse response) {
		PortMapping mapping = new PortMapping(response);
		return mapping;
	}

	@Override
	public String toString() {
		return description;
		// StringBuffer b = new StringBuffer();
		// b.append("PortMappping: ");
		// b.append(protocol);
		// b.append(" ");
		// b.append(externalPort);
		// b.append(" -> ");
		// b.append(internalClient);
		// b.append(":");
		// b.append(internalPort);
		// b.append(" ");
		// b.append(enabled ? "enabled" : "not enabled");
		// b.append(" ");
		// b.append(description);
		// return b.toString();
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

	public void setInternalClient(String internalClient) {
		this.internalClient = internalClient;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public long getLeaseDuration() {
		return leaseDuration;
	}

	public String getCompleteDescription() {
		StringBuffer b = new StringBuffer();
		b.append(protocol);
		b.append(" ");
		b.append(remoteHost);
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

	public Object clone() {
		PortMapping clonedMapping = new PortMapping(protocol, remoteHost,
				externalPort, internalClient, internalPort, description);
		clonedMapping.enabled = this.enabled;
		clonedMapping.leaseDuration = this.leaseDuration;
		return clonedMapping;
	}
}
