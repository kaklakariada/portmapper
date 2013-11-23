/**
 * 
 */
package org.chris.portmapper.router;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.PortMapperApp;

/**
 * @author chris
 * @version $Id$
 */
public abstract class AbstractRouterFactory {

	private static final String LOCATION_URL_SYSTEM_PROPERTY = "portmapper.locationUrl";

	private final Log logger = LogFactory.getLog(this.getClass());

	private final PortMapperApp app;

	private final String name;

	protected AbstractRouterFactory(final PortMapperApp app, final String name) {
		this.app = app;
		this.name = name;
	}

	/**
	 * Get the name of the router factory that can be displayed to the user.
	 * 
	 * @return the name of the router factory that can be displayed to the user.
	 */
	public String getName() {
		return name;
	}

	public List<IRouter> findRouters() throws RouterException {
		final String locationUrl = System
				.getProperty(LOCATION_URL_SYSTEM_PROPERTY);
		if (locationUrl == null) {
			logger.debug("System property " + LOCATION_URL_SYSTEM_PROPERTY
					+ " not defined: discover routers automatically.");
			return findRoutersInternal();
		}
		logger.info("Trying to connect using location url " + locationUrl);
		return Arrays.asList(connect(locationUrl));
	}

	/**
	 * Search for routers on the network.
	 * 
	 * @return the found router or an empty {@link Collection} if no router was
	 *         found.
	 * @throws RouterException
	 *             if something goes wrong during discovery.
	 */
	protected abstract List<IRouter> findRoutersInternal()
			throws RouterException;

	/**
	 * Directly connect to a router using a location url like
	 * <code>http://192.168.179.1:49000/igddesc.xml</code>.
	 * 
	 * @param locationUrl
	 *            a location url
	 * @return a router if the connection was successful.
	 * @throws RouterException
	 *             if something goes wrong during connection.
	 */
	protected abstract IRouter connect(final String locationUrl)
			throws RouterException;

	@Override
	public String toString() {
		return name;
	}
}
