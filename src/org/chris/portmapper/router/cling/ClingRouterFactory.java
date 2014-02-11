package org.chris.portmapper.router.cling;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.EventObject;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.LogManager;

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

    private static final String JUL_LOGGING_PROPERTIES = "/jul-logging.properties";
    private static final long DISCOVERY_TIMEOUT_SECONDS = 5;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public ClingRouterFactory(final PortMapperApp app) {
        super(app, "Cling lib");
        loadJavaUtilLoggingConfiguration();
    }

    private void loadJavaUtilLoggingConfiguration() {
        try (final InputStream inputStream = getClass().getResourceAsStream(JUL_LOGGING_PROPERTIES)) {
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (final IOException e) {
            log.error("Error reading j.u.l configuration from {}", JUL_LOGGING_PROPERTIES);
        }
    }

    @Override
    protected List<IRouter> findRoutersInternal() throws RouterException {
        final UpnpServiceConfiguration config = new DefaultUpnpServiceConfiguration();
        final ClingRegistryListener clingRegistryListener = new ClingRegistryListener();
        final UpnpService upnpService = new UpnpServiceImpl(config, clingRegistryListener);
        shutdownServiceOnExit(upnpService);

        log.debug("Start searching using upnp service");
        upnpService.getControlPoint().search();
        final Service<?, ?> service = clingRegistryListener.waitForServiceFound(DISCOVERY_TIMEOUT_SECONDS,
                TimeUnit.SECONDS);

        if (service == null) {
            log.debug("Did not find a service after {} seconds", DISCOVERY_TIMEOUT_SECONDS);
            return Collections.emptyList();
        }

        log.debug("Found service {}", service);
        return Arrays.<IRouter> asList(new ClingRouter(service, upnpService.getRegistry(), upnpService
                .getControlPoint()));
    }

    private void shutdownServiceOnExit(final UpnpService upnpService) {
        app.addExitListener(new ExitListener() {
            @Override
            public void willExit(final EventObject event) {
                log.debug("Shutdown upnp service");
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
        throw new UnsupportedOperationException("Direct connection is not supported for Cling library.");
    }
}
