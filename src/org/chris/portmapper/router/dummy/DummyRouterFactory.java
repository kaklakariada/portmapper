/**
 * 
 */
package org.chris.portmapper.router.dummy;

import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * 
 */
public class DummyRouterFactory implements IRouterFactory {

	@Override
	public IRouter findRouter() throws RouterException {
		return new DummyRouter();
	}

}
