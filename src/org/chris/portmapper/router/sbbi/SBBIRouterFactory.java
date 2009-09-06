/**
 * 
 */
package org.chris.portmapper.router.sbbi;

import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * @version $Id$
 */
public class SBBIRouterFactory implements IRouterFactory {

	public AbstractRouter findRouter() throws RouterException {
		return SBBIRouter.findRouter();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SBBI UPnP lib";
	}
}
