package org.chris.portmapper;

/**
 * @author chris
 */
public class PortMapperStarter {

    /**
     * @param args
     */
    public static void main(final String[] args) {
        final PortMapperCli cli = new PortMapperCli();
        cli.start(args);
    }
}
