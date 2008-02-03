package org.chris.portmapper.router;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.chris.portmapper.PortMapperApp;

public class PortMappingPreset implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7524672328836766162L;

	private String description;
	private String internalClient;
	private String remoteHost;
	private List<SinglePortMapping> ports;

	private boolean isNew;

	public PortMappingPreset(String remoteHost, String internalClient,
			String description) {
		super();
		this.remoteHost = remoteHost;
		this.internalClient = internalClient;
		this.description = description;
		this.ports = new LinkedList<SinglePortMapping>();

		this.isNew = false;
	}

	/**
	 * 
	 */
	public PortMappingPreset() {
		ports = new LinkedList<SinglePortMapping>();
		this.isNew = true;
	}

	@Override
	public String toString() {
		return description;
	}

	public List<PortMapping> getPortMappings(String localhost) {
		List<PortMapping> allPortMappings = new ArrayList<PortMapping>(
				this.ports.size());
		int i = 0;
		for (SinglePortMapping port : this.ports) {
			i++;
			String internalClientName = this.internalClient != null ? this.internalClient
					: localhost;
			String portDescription;
			if (ports.size() == 1) {
				portDescription = description;
			} else {
				portDescription = description + " (" + i + ")";
			}
			PortMapping newMapping = new PortMapping(port.getProtocol(),
					remoteHost, port.getExternalPort(), internalClientName,
					port.getInternalPort(), portDescription);

			allPortMappings.add(newMapping);
		}

		return allPortMappings;
	}

	public String getCompleteDescription() {
		StringBuffer b = new StringBuffer();

		b.append(" ");
		b.append(remoteHost);
		b.append(":");

		b.append(" -> ");
		b.append(internalClient);
		b.append(":");

		b.append(" ");

		b.append(" ");
		b.append(description);
		return b.toString();
	}

	public List<SinglePortMapping> getPorts() {
		return ports;
	}

	public void setPorts(List<SinglePortMapping> ports) {
		this.ports = ports;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public String getRemoteHost() {
		return remoteHost;
	}

	public void setInternalClient(String internalClient) {
		this.internalClient = internalClient;
	}

	public String getInternalClient() {
		return internalClient;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * 
	 */
	public void save() {
		if (this.isNew) {
			PortMapperApp.getInstance().getSettings().addPreset(this);
		} else {
			PortMapperApp.getInstance().getSettings().savePreset(this);
		}
		this.isNew = false;
	}
}
