/**
 * 
 */
package org.chris.portmapper.router.sbbi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sbbi.upnp.impls.InternetGatewayDevice;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

/**
 * Router factory using the SBBI UPnP library.
 */
public class SBBIRouterFactory extends AbstractRouterFactory {

	/**
	 * The timeout in milliseconds for finding a router device.
	 */
	private final static int DISCOVERY_TIMEOUT = 5000;

	public SBBIRouterFactory(final PortMapperApp app) {
		super(app, "SBBI UPnP lib");
	}

	@Override
	protected List<IRouter> findRoutersInternal() throws RouterException {

		final InternetGatewayDevice[] devices;
		try {
			devices = InternetGatewayDevice.getDevices(DISCOVERY_TIMEOUT);
		} catch (final IOException e) {
			throw new RouterException("Could not find devices", e);
		}

		if (devices == null || devices.length == 0) {
			return Collections.emptyList();
		}

		final List<IRouter> routers = new ArrayList<>(devices.length);

		for (final InternetGatewayDevice device : devices) {
			routers.add(new SBBIRouter(app, device));
		}

		return routers;
	}

	@Override
	protected IRouter connect(final String locationUrl) throws RouterException {
		throw new UnsupportedOperationException(
				"Direct connection is not implemented for SBBI library.");
	}
}
