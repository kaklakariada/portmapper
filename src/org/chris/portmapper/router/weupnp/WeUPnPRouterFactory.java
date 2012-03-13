/**
 * 
 */
package org.chris.portmapper.router.weupnp;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.RouterException;
import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.GatewayDiscover;
import org.wetorrent.upnp.WeUPnPException;

/**
 * @author chris
 * @version $Id$
 */
public class WeUPnPRouterFactory extends AbstractRouterFactory {
	private final Log logger = LogFactory.getLog(this.getClass());

	private final GatewayDiscover discover = new GatewayDiscover();

	@Override
	protected List<IRouter> findRoutersInternal() throws RouterException {
		logger.debug("Searching for gateway devices...");
		final Map<InetAddress, GatewayDevice> devices;
		try {
			devices = discover.discover();
		} catch (final WeUPnPException e) {
			throw new RouterException(
					"Could not discover a valid gateway device: "
							+ e.getMessage(), e);
		}

		if (devices == null || devices.size() == 0) {
			return Collections.emptyList();
		}

		final List<IRouter> routers = new ArrayList<IRouter>(devices.size());
		for (final GatewayDevice device : devices.values()) {
			routers.add(new WeUPnPRouter(device));
		}
		return routers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouterFactory#getName()
	 */
	@Override
	public String getName() {
		return "weupnp lib";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouterFactory#connect(java.lang.String)
	 */
	@Override
	protected IRouter connect(final String locationUrl) throws RouterException {

		final GatewayDevice device = new GatewayDevice();
		device.setLocation(locationUrl);
		device.setSt("urn:schemas-upnp-org:device:InternetGatewayDevice:1");
		try {
			device.setLocalAddress(InetAddress.getLocalHost());
		} catch (final UnknownHostException e) {
			throw new RouterException("Could not get ip of localhost: "
					+ e.getMessage(), e);
		}
		try {
			device.loadDescription();
		} catch (final WeUPnPException e) {
			throw new RouterException(
					"Could not load description of device for location url "
							+ locationUrl + " : " + e.getMessage(), e);
		}
		return new WeUPnPRouter(device);
	}
}
