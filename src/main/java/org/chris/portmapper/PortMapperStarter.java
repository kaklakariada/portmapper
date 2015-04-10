package org.chris.portmapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortMapperStarter {

    private final static Logger LOG = LoggerFactory.getLogger(PortMapperStarter.class);

    public static void main(final String[] args) {
        final PortMapperCli cli = new PortMapperCli();
        try {
            cli.start(args);
        } catch (final Exception e) {
            LOG.error("PortMapper failed with exception " + e.getMessage(), e);
            System.exit(1);
        }
    }
}
