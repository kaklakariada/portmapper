/**
 * 
 */
package org.chris.portmapper.router;

import java.util.Collection;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;

/**
 * @author chris
 * 
 */
public interface IRouter {

	public abstract String getName() throws RouterException;

	/**
	 * Get the external IP of the router.
	 * 
	 * @return the external IP of the router.
	 */
	public abstract String getExternalIPAddress() throws RouterException;

	/**
	 * Get the internal host name or IP of the router.
	 * 
	 * @return the internal host name or IP of the router.
	 */
	public abstract String getInternalHostName();

	/**
	 * Get the internal port of the router.
	 * 
	 * @return the internal port of the router.
	 * @throws RouterException
	 */
	public abstract int getInternalPort() throws RouterException;

	/**
	 * Get all port mappings from the router.
	 * 
	 * @return all port mappings from the router.
	 * @throws RouterException
	 *             if something went wrong when getting the port mappings.
	 */
	public abstract Collection<PortMapping> getPortMappings()
			throws RouterException;

	public abstract void logRouterInfo() throws RouterException;

	public abstract void addPortMappings(Collection<PortMapping> mappings)
			throws RouterException;

	public abstract void addPortMapping(PortMapping mapping)
			throws RouterException;

	public abstract void removeMapping(PortMapping mapping)
			throws RouterException;

	public abstract void removePortMapping(Protocol protocol,
			String remoteHost, int externalPort) throws RouterException;

	public abstract void disconnect();

	/**
	 * @return
	 * @throws RouterException
	 */
	public abstract long getUpTime() throws RouterException;

}