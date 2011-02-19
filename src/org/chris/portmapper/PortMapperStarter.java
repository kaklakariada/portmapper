package org.chris.portmapper;

/**
 * @author chris
 * @version $Id$
 */
public class PortMapperStarter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (System.getProperty("os.name").startsWith("Mac")) {
			MacSetup.setupMac();
		}
		PortMapperCli cli = new PortMapperCli();
		cli.start(args);
	}
}
