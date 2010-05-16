/**
 * 
 */
package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sbbi.upnp.impls.InternetGatewayDevice;

import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * @version $Id$
 */
public class SBBIRouterFactory implements IRouterFactory {

	/**
	 * The timeout in milliseconds for finding a router device.
	 */
	private final static int DISCOVERY_TIMEOUT = 5000;

	public List<IRouter> findRouters() throws RouterException {

		final InternetGatewayDevice[] devices;
		try {
			devices = InternetGatewayDevice.getDevices(DISCOVERY_TIMEOUT);
		} catch (IOException e) {
			throw new RouterException("Could not find devices", e);
		}

		if (devices == null || devices.length == 0) {
			return Collections.emptyList();
		}

		final List<IRouter> routers = new ArrayList<IRouter>(devices.length);

		for (InternetGatewayDevice device : devices) {
			routers.add(new SBBIRouter(device));
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
		return "SBBI UPnP lib";
	}
}
