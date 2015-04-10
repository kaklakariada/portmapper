/**
 * 
 */
package org.chris.portmapper.router.dummy;

import java.util.LinkedList;
import java.util.List;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;

/**
 * Router factory for testing without a real router.
 */
public class DummyRouterFactory extends AbstractRouterFactory {

	public DummyRouterFactory(final PortMapperApp app) {
		super(app, "Dummy library");
	}

	@Override
	protected List<IRouter> findRoutersInternal() throws RouterException {
		final List<IRouter> routers = new LinkedList<>();
		routers.add(new DummyRouter("DummyRouter1"));
		routers.add(new DummyRouter("DummyRouter2"));
		return routers;
	}

	@Override
	protected IRouter connect(final String locationUrl) throws RouterException {
		return new DummyRouter("DummyRouter @ " + locationUrl);
	}
}
