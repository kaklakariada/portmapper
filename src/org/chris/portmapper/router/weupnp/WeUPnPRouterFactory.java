/**
 * 
 */
package org.chris.portmapper.router.weupnp;

import java.net.InetAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;
import org.wetorrent.upnp.GatewayDevice;
import org.wetorrent.upnp.GatewayDiscover;
import org.wetorrent.upnp.WeUPnPException;

/**
 * @author chris
 * @version $Id$
 */
public class WeUPnPRouterFactory implements IRouterFactory {
	private final Log logger = LogFactory.getLog(this.getClass());

	private final GatewayDiscover discover = new GatewayDiscover();

	public Collection<IRouter> findRouters() throws RouterException {
		logger.debug("Searching for gateway devices...");
		final Map<InetAddress, GatewayDevice> devices;
		try {
			devices = discover.discover();
		} catch (WeUPnPException e) {
			throw new RouterException(
					"Could not discover a valid gateway device: "
							+ e.getMessage(), e);
		}

		if (devices == null || devices.size() == 0) {
			return Collections.emptyList();
		}

		final Collection<IRouter> routers = new LinkedList<IRouter>();
		for (GatewayDevice device : devices.values()) {
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
	public String getName() {
		return "weupnp lib";
	}
}
