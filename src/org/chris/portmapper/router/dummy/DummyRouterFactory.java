/**
 * 
 */
package org.chris.portmapper.router.dummy;

import java.util.LinkedList;
import java.util.List;

import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.IRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * @version $Id$
 */
public class DummyRouterFactory implements IRouterFactory {

	public List<IRouter> findRouters() throws RouterException {
		final List<IRouter> routers = new LinkedList<IRouter>();
		routers.add(new DummyRouter("DummyRouter1"));
		routers.add(new DummyRouter("DummyRouter2"));
		return routers;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouterFactory#getName()
	 */
	public String getName() {
		return "Dummy library";
	}
}
