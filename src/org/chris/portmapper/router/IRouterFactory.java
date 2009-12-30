/**
 * 
 */
package org.chris.portmapper.router;

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
	 * Search for a router on the network.
	 * 
	 * @return the router if it was found.
	 * @throws RouterException
	 *             if no router was found.
	 */
	public IRouter findRouter() throws RouterException;

}
