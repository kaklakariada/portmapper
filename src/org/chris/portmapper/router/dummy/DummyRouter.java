package org.chris.portmapper.router.dummy;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

/**
 * 
 * @author chris
 * 
 */
public class DummyRouter implements IRouter {

	private Log logger = LogFactory.getLog(this.getClass());

	private Collection<PortMapping> mappings;

	public DummyRouter() {
		logger.debug("Created new DummyRouter");
		mappings = new LinkedList<PortMapping>();
	}

	@Override
	public void addPortMapping(PortMapping mapping) {
		logger.debug("Adding mapping " + mapping);
		mappings.add(mapping);
	}

	@Override
	public void addPortMappings(Collection<PortMapping> mappings) {
		logger.debug("Adding mappings " + mappings);
		this.mappings.addAll(mappings);
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
	public String getName() {
		return "DummyRouterName";
	}

	@Override
	public Collection<PortMapping> getPortMappings() {
		return mappings;
	}

	@Override
	public long getUpTime() {
		return 0;
	}

	@Override
	public void logRouterInfo() {
		logger.info("DummyRouter");
	}

	@Override
	public void removeMapping(PortMapping mapping) {
		mappings.remove(mapping);
	}

	@Override
	public void removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLocalHostAddress() throws RouterException {
		return "DummyLocalhostAddress";
	}

}
