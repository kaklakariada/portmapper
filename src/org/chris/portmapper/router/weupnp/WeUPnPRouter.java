/**
 * 
 */
package org.chris.portmapper.router.weupnp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.PortMappingEntry;
import org.wetorrent.upnp.WeUPnPException;

/**
 * This class is an implements an {@link IRouter} using the weupnp library's
 * {@link GatewayDevice}.
 * 
 * @author chris
 * @version $Id$
 */
public class WeUPnPRouter extends AbstractRouter {

	private final Log logger = LogFactory.getLog(this.getClass());
	private final GatewayDevice device;

	/**
	 * @param device
	 */
	WeUPnPRouter(GatewayDevice device) {
		super(device.getFriendlyName());
		this.device = device;
	}

	public void addPortMapping(PortMapping mapping) throws RouterException {
		try {
			device.addPortMapping(mapping.getExternalPort(),
					mapping.getInternalPort(), mapping.getInternalClient(),
					mapping.getProtocol().getName(), mapping.getDescription());
		} catch (WeUPnPException e) {
			throw new RouterException("Could not add portmapping", e);
		}
	}

	public void addPortMappings(Collection<PortMapping> mappings)
			throws RouterException {
		for (PortMapping mapping : mappings) {
			this.addPortMapping(mapping);
		}
	}

	public void disconnect() {
		// noting to do right now
	}

	public String getExternalIPAddress() throws RouterException {
		try {
			return device.getExternalIPAddress();
		} catch (WeUPnPException e) {
			throw new RouterException("Could not get external IP address", e);
		}
	}

	public String getInternalHostName() {
		final String url = device.getPresentationURL();
		if (url == null || url.trim().length() == 0) {
			return null;
		}
		try {
			return new URL(url).getHost();
		} catch (MalformedURLException e) {
			logger.warn("Could not get URL for internal host name '" + url
					+ "'", e);
			return url;
		}
	}

	public int getInternalPort() throws RouterException {
		try {
			return new URL(device.getPresentationURL()).getPort();
		} catch (MalformedURLException e) {
			throw new RouterException("Could not get internal port", e);
		}
	}

	public Collection<PortMapping> getPortMappings() throws RouterException {
		Collection<PortMapping> mappings = new LinkedList<PortMapping>();
		boolean morePortMappings = true;
		int index = 0;
		while (morePortMappings) {
			PortMappingEntry entry = null;
			try {
				logger.debug("Getting port mapping " + index + "...");
				entry = device.getGenericPortMappingEntry(index);
				logger.debug("Got port mapping " + index + ": " + entry);
			} catch (WeUPnPException e) {
				morePortMappings = false;
				// logger.trace("Got an exception for index " + index
				// + ", stop getting more mappings", e);
			}

			if (entry != null) {
				Protocol protocol = entry.getProtocol().equalsIgnoreCase("TCP") ? Protocol.TCP
						: Protocol.UDP;
				PortMapping m = new PortMapping(protocol,
						entry.getRemoteHost(), entry.getExternalPort(),
						entry.getInternalClient(), entry.getInternalPort(),
						entry.getPortMappingDescription());
				mappings.add(m);
			} else {
				logger.debug("Got null port mapping for index " + index);
			}
			index++;
		}
		return mappings;
	}

	public void logRouterInfo() throws RouterException {
		Map<String, String> info = new HashMap<String, String>();
		info.put("friendlyName", device.getFriendlyName());
		info.put("manufacturer", device.getManufacturer());
		info.put("modelDescription", device.getModelDescription());

		SortedSet<String> sortedKeys = new TreeSet<String>(info.keySet());

		for (String key : sortedKeys) {
			String value = info.get(key);
			logger.info("Router Info: " + key + " \t= " + value);
		}

		logger.info("def loc " + device.getLocation());
		logger.info("device type " + device.getDeviceType());
	}

	public void removeMapping(PortMapping mapping) throws RouterException {
		this.removePortMapping(mapping.getProtocol(), mapping.getRemoteHost(),
				mapping.getExternalPort());
	}

	public void removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) throws RouterException {
		try {
			device.deletePortMapping(externalPort, protocol.getName());
		} catch (WeUPnPException e) {
			throw new RouterException("Could not delete port mapping", e);
		}
	}
}
