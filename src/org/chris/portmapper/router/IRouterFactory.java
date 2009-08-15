/**
 * 
 */
package org.chris.portmapper.router;

/**
 * @author chris
 * @version $Id$
 */
public interface IRouterFactory {
	public IRouter findRouter() throws RouterException;
}
