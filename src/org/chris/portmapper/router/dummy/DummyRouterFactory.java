/**
 * 
 */
package org.chris.portmapper.router.dummy;

import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * @version $Id$
 */
public class DummyRouterFactory implements IRouterFactory {

	public IRouter findRouter() throws RouterException {
		return new DummyRouter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Dummy lib";
	}
}
