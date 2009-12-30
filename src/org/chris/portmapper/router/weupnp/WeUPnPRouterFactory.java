/**
 * 
 */
package org.chris.portmapper.router.weupnp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.router.AbstractRouter;
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
	private Log logger = LogFactory.getLog(this.getClass());

	public AbstractRouter findRouter() throws RouterException {
		GatewayDiscover discover = new GatewayDiscover();
		logger.debug("Searching for gateway devices...");
		GatewayDevice device = null;
		try {
			discover.discover();
			device = discover.getValidGateway();
		} catch (WeUPnPException e) {
			throw new RouterException(
					"Could not discover a valid gateway device: "
							+ e.getMessage(), e);
		}

		return new WeUPnPRouter(device);
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
}
