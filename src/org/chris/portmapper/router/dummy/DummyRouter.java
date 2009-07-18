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

	private final Log logger = LogFactory.getLog(this.getClass());

	private final Collection<PortMapping> mappings;

	public DummyRouter() {
		logger.debug("Created new DummyRouter");
		mappings = new LinkedList<PortMapping>();
	}

	public void addPortMapping(PortMapping mapping) {
		logger.debug("Adding mapping " + mapping);
		mappings.add(mapping);
	}

	public void addPortMappings(Collection<PortMapping> mappings) {
		logger.debug("Adding mappings " + mappings);
		this.mappings.addAll(mappings);
	}

	public void disconnect() {
		logger.debug("Disconnect");
	}

	public String getExternalIPAddress() {
		return "DummyExternalIP";
	}

	public String getInternalHostName() {
		return "DummyInternalHostName";
	}

	public int getInternalPort() {
		return 42;
	}

	public String getName() {
		return "DummyRouterName";
	}

	public Collection<PortMapping> getPortMappings() {
		return mappings;
	}

	public long getUpTime() {
		return 0;
	}

	public void logRouterInfo() {
		logger.info("DummyRouter");
	}

	public void removeMapping(PortMapping mapping) {
		mappings.remove(mapping);
	}

	public void removePortMapping(Protocol protocol, String remoteHost,
			int externalPort) {
		// TODO Auto-generated method stub
	}

	public String getLocalHostAddress() throws RouterException {
		return "DummyLocalhostAddress";
	}
}
