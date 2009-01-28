package org.chris.portmapper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.chris.portmapper.Settings;

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
		if (this.useLocalhostAsInternalClient()
				&& (localhost == null || localhost.length() == 0)) {
			throw new IllegalArgumentException(
					"Got invalid localhost and internal host is not given.");
		}

		List<PortMapping> allPortMappings = new ArrayList<PortMapping>(
				this.ports.size());
		int i = 0;
		for (SinglePortMapping port : this.ports) {
			i++;
			String internalClientName = this.useLocalhostAsInternalClient() ? localhost
					: this.internalClient;

			PortMapping newMapping = new PortMapping(port.getProtocol(),
					remoteHost, port.getExternalPort(), internalClientName,
					port.getInternalPort(), description);

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

	public boolean useLocalhostAsInternalClient() {
		return this.getInternalClient() == null
				|| this.getInternalClient().length() == 0;
	}

	/**
	 * @param settings
	 * 
	 */
	public void save(Settings settings) {
		if (this.isNew) {
			settings.addPreset(this);
		} else {
			settings.savePreset(this);
		}
		this.isNew = false;
	}
}
