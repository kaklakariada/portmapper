package org.chris.portmapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class PortMapperStarter {

    private final static Logger LOG = LoggerFactory.getLogger(PortMapperStarter.class);

    public static void main(final String[] args) {
        redirectJavaUtilLoggingToLogback();
        final PortMapperCli cli = new PortMapperCli();
        try {
            cli.start(args);
        } catch (final Exception e) {
            LOG.error("PortMapper failed with exception " + e.getMessage(), e);
            System.exit(1);
        }
    }

    private static void redirectJavaUtilLoggingToLogback() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }
}
