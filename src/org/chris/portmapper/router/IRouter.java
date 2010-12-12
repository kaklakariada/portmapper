/**
 * 
 */
package org.chris.portmapper.router;

import java.util.Collection;

import org.chris.portmapper.model.PortMapping;
import org.chris.portmapper.model.Protocol;

/**
 * @author chris
 * @version $Id$
 */
public interface IRouter {

	public abstract String getName();

	/**
	 * Get the IP address of the local host.
	 * 
	 * @return IP address of the local host or <code>null</code>, if the address
	 *         could not be determined.
	 * @throws RouterException
	 */
	public String getLocalHostAddress() throws RouterException;

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
	 * @throws RouterException
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

	/**
	 * Write information about the router to the log.
	 * 
	 * @throws RouterException
	 */
	public abstract void logRouterInfo() throws RouterException;

	/**
	 * Add the given port mappings to the router.
	 * 
	 * @param mappings
	 *            the port mappings to add.
	 * @throws RouterException
	 */
	public abstract void addPortMappings(Collection<PortMapping> mappings)
			throws RouterException;

	/**
	 * Add the given port mapping to the router.
	 * 
	 * @param mapping
	 *            the port mapping to add.
	 * @throws RouterException
	 */
	public abstract void addPortMapping(PortMapping mapping)
			throws RouterException;

	/**
	 * Remove the given port mapping from the router.
	 * 
	 * @param mapping
	 *            the port mapping to remove.
	 * @throws RouterException
	 */
	public abstract void removeMapping(PortMapping mapping)
			throws RouterException;

	/**
	 * Remove the port mapping with the given data from the router.
	 * 
	 * @param protocol
	 * @param remoteHost
	 * @param externalPort
	 * @throws RouterException
	 */
	public abstract void removePortMapping(Protocol protocol,
			String remoteHost, int externalPort) throws RouterException;

	/**
	 * Disconnect from the router.
	 */
	public abstract void disconnect();
}