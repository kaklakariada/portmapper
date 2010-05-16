/**
 * 
 */
package org.chris.portmapper.router;

import java.util.Collection;
import java.util.List;

/**
 * @author chris
 * @version $Id$
 */
public interface IRouterFactory {

	/**
	 * Get the name of the router factory that can be displayed to the user.
	 * 
	 * @return the name of the router factory that can be displayed to the user.
	 */
	public String getName();

	/**
	 * Search for routers on the network.
	 * 
	 * @return the found router or an empty {@link Collection} if no router was
	 *         found.
	 * @throws RouterException
	 *             if something goes wrong during discovery.
	 */
	public List<IRouter> findRouters() throws RouterException;

}
