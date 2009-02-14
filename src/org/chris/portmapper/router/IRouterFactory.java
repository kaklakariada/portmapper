/**
 * 
 */
package org.chris.portmapper.router;

/**
 * @author chris
 * 
 */
public interface IRouterFactory {
	public IRouter findRouter() throws RouterException;
}
