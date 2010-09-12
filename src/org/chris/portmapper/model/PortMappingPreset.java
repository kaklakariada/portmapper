package org.chris.portmapper.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.Settings;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.Application;

/**
 * This class stores a port mapping preset containing a description, internal
 * and remote host and a {@link List} of {@link SinglePortMapping}s.
 * 
 * @author chris
 * @version $Id$
 */
public class PortMappingPreset implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3749136884938395765L;

	private final static Log logger = LogFactory
			.getLog(PortMappingPreset.class);

	/**
	 * The description of this preset.
	 */
	private String description;

	/**
	 * The ip address of the internal client or <code>null</code> for localhost.
	 */
	private String internalClient;

	/**
	 * The host name of the remote host.
	 */
	private String remoteHost;

	/**
	 * The port mappings in this preset.
	 */
	private List<SinglePortMapping> ports;

	/**
	 * The rate in seconds in which this preset is applied or <code>null</code>
	 * to deactivate automatic refresh.
	 */
	private RefreshRate refreshRate = RefreshRate.DEACTIVATED;

	/**
	 * <code>true</code> if this preset has not been saved.
	 */
	private boolean isNew;

	private transient Timer autoRefreshTimer;

	/**
	 * Creates a new preset with the given default values.
	 */
	public PortMappingPreset(String remoteHost, String internalClient,
			String description) {
		super();
		this.remoteHost = remoteHost;
		this.internalClient = internalClient;
		this.description = description;
		this.ports = new LinkedList<SinglePortMapping>();

		this.isNew = false;
		setupTimer();
	}

	public void setupTimer() {
		if (this.autoRefreshTimer != null) {
			this.autoRefreshTimer.cancel();
			this.autoRefreshTimer = null;
		}
		if (this.refreshRate == RefreshRate.DEACTIVATED) {
			return;
		}
		logger.debug("Setting up timer for preset '" + description
				+ "' with refresh rate " + refreshRate);
		this.autoRefreshTimer = new Timer("Auto refresh timer for '"
				+ this.getDescription() + "'", true);
		final TimerTask task = new TimerTask() {
			@Override
			public void run() {
				logger.debug("Timer fired for preset '" + description + "'.");
				final PortMapperApp app = (PortMapperApp) Application
						.getInstance();
				// not connected to router: cancel timer.
				if (!app.isConnected()) {
					logger.debug("Not connected to router: cancel timer");
					autoRefreshTimer.cancel();
					return;
				}

				// get address of localhost that is required for applying the
				// preset
				final String localHostAddress = app.getLocalHostAddress();
				if (localHostAddress == null) {
					logger.error("Could not get address of localhost: cancel auto refresh.");
					autoRefreshTimer.cancel();
				}

				// apply the preset
				try {
					logger.info("Auto-applying preset '" + description + "'.");
					app.getRouter().addPortMappings(
							PortMappingPreset.this
									.getPortMappings(localHostAddress));
				} catch (RouterException e) {
					logger.error("Error when applying preset '" + description
							+ "': " + e.getMessage(), e);
					autoRefreshTimer.cancel();
				}

				// refresh the port mappings table
				app.getView().updatePortMappings();
			}
		};
		this.autoRefreshTimer.scheduleAtFixedRate(task, 0,
				1000 * refreshRate.getSeconds());
	}

	/**
	 * Creates a new empty preset.
	 */
	public PortMappingPreset() {
		ports = new LinkedList<SinglePortMapping>();
		this.isNew = true;
		this.autoRefreshTimer = null;
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

	/**
	 * @return the refreshRate
	 */
	public RefreshRate getRefreshRate() {
		return refreshRate;
	}

	/**
	 * @return the isNew
	 */
	public boolean isNew() {
		return isNew;
	}

	/**
	 * @param isNew
	 *            the isNew to set
	 */
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}

	/**
	 * @param refreshRate
	 *            the refreshRate to set
	 */
	public void setRefreshRate(RefreshRate refreshRate) {
		this.refreshRate = refreshRate;
		setupTimer();
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
