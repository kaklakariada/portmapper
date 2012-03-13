/**
 * 
 */
package org.chris.portmapper.router.dummy;

import java.util.LinkedList;
import java.util.List;

import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.RouterException;

/**
 * @author chris
 * @version $Id$
 */
public class DummyRouterFactory extends AbstractRouterFactory {

	@Override
	protected List<IRouter> findRoutersInternal() throws RouterException {
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
	@Override
	public String getName() {
		return "Dummy library";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.chris.portmapper.router.IRouterFactory#connect(java.lang.String)
	 */
	@Override
	protected IRouter connect(final String locationUrl) throws RouterException {
		return new DummyRouter("DummyRouter @ " + locationUrl);
	}
}
