/**
 * 
 */
package org.chris.portmapper.router.sbbi;

import org.chris.portmapper.router.AbstractRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * 
 */
public class SBBIRouterFactory implements IRouterFactory {

	@Override
	public AbstractRouter findRouter() throws RouterException {
		return SBBIRouter.findRouter();
	}

}
