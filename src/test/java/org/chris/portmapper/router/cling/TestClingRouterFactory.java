package org.chris.portmapper.router.cling;

import java.util.List;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;

/**
 * Unit tests for {@link ClingRouterFactory}.
 */
public class TestClingRouterFactory {

	@Mock
	private PortMapperApp appMock;
	private ClingRouterFactory routerFactory;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		routerFactory = new ClingRouterFactory(appMock);
	}

	@Test
	public void testFindRoutersInternal() throws RouterException {
		final List<IRouter> routers = routerFactory.findRouters();
		assertEquals(1, routers.size());
	}
}
