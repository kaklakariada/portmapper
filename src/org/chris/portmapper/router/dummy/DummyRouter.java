package org.chris.portmapper.router.dummy;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.RouterException;

/**
 * 
 * @author chris
 * @version $Id$
 */
public class DummyRouter extends AbstractRouter {

	private final Log logger = LogFactory.getLog(this.getClass());

	private final Collection<PortMapping> mappings;

	public DummyRouter(final String name) {
		super(name);
		logger.debug("Created new DummyRouter");
		mappings = new LinkedList<>();
		mappings.add(new PortMapping(Protocol.TCP, "remoteHost1", 1,
				"internalClient1", 1, getName() + ": dummy port mapping 1"));
		mappings.add(new PortMapping(Protocol.UDP, null, 2, "internalClient2",
				2, getName() + ": dummy port mapping 2"));
		mappings.add(new PortMapping(Protocol.TCP, null, 3, "internalClient3",
				3, getName() + ": dummy port mapping 3"));
	}

	@Override
	public void addPortMapping(final PortMapping mapping) {
		logger.debug("Adding mapping " + mapping);
		mappings.add(mapping);
	}

	@Override
	public void addPortMappings(final Collection<PortMapping> mappingsToAdd) {
		logger.debug("Adding mappings " + mappingsToAdd);
		this.mappings.addAll(mappingsToAdd);
	}

	@Override
	public void disconnect() {
		logger.debug("Disconnect");
	}

	@Override
	public String getExternalIPAddress() {
		return "DummyExternalIP";
	}

	@Override
	public String getInternalHostName() {
		return "DummyInternalHostName";
	}

	@Override
	public int getInternalPort() {
		return 42;
	}

	@Override
	public Collection<PortMapping> getPortMappings() {
		try {
			Thread.sleep(3000);
		} catch (final InterruptedException e) {
			// ignore
		}
		return mappings;
	}

	@Override
	public void logRouterInfo() {
		logger.info("DummyRouter " + getName());
	}

	@Override
	public void removeMapping(final PortMapping mapping) {
		mappings.remove(mapping);
	}

	@Override
	public void removePortMapping(final Protocol protocol,
			final String remoteHost, final int externalPort) {
		// ignore
	}

	@Override
	public String getLocalHostAddress() throws RouterException {
		return "DummyLocalhostAddress";
	}
}
