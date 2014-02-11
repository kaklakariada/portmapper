package org.chris.portmapper.router.cling;

import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.chris.portmapper.PortMapperApp;
import org.chris.portmapper.router.AbstractRouterFactory;
import org.chris.portmapper.router.IRouter;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.Application.ExitListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.teleal.cling.DefaultUpnpServiceConfiguration;
import org.teleal.cling.UpnpService;
import org.teleal.cling.UpnpServiceConfiguration;
import org.teleal.cling.UpnpServiceImpl;
import org.teleal.cling.model.meta.Service;

/**
 * 
 */
public class ClingRouterFactory extends AbstractRouterFactory {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public ClingRouterFactory(final PortMapperApp app) {
		super(app, "Cling lib");
	}

	@Override
	protected List<IRouter> findRoutersInternal() throws RouterException {
		final UpnpServiceConfiguration config = new DefaultUpnpServiceConfiguration();
		final ClingRegistryListener clingRegistryListener = new ClingRegistryListener();
		final UpnpService upnpService = new UpnpServiceImpl(config,
				clingRegistryListener);
		shutdownServiceOnExit(upnpService);
		upnpService.getControlPoint().search();
		final Service service = clingRegistryListener.waitForServiceFound(5,
				TimeUnit.SECONDS);

		if (service == null) {
			return Collections.emptyList();
		}

		return Arrays.<IRouter> asList(new ClingRouter(upnpService
				.getRegistry(), service));
	}

	private void shutdownServiceOnExit(final UpnpService upnpService) {
		app.addExitListener(new ExitListener() {
			@Override
			public void willExit(final EventObject event) {
				upnpService.shutdown();
			}

			@Override
			public boolean canExit(final EventObject event) {
				return true;
			}
		});
	}

	@Override
	protected IRouter connect(final String locationUrl) throws RouterException {
		throw new UnsupportedOperationException(
				"Direct connection is not supported for Cling library.");
	}
}
