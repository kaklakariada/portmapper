/**
 * 
 */
package org.chris.portmapper.router;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author chris
 * 
 */
public abstract class AbstractRouter implements IRouter {

	private Log logger = LogFactory.getLog(this.getClass());

	public String getLocalHostAddress() throws RouterException {
		logger.debug("Get IP of localhost...");

		InetAddress localHostIP = null;
		try {

			// In order to use the Socked method to get the address, we have to
			// be connected to the router.

			int routerInternalPort = getInternalPort();
			logger.debug("Got internal router port " + routerInternalPort);

			// Check, if we got a correct port number
			if (routerInternalPort > 0) {
				logger.debug("Creating socket to router: "
						+ getInternalHostName() + ":" + routerInternalPort
						+ "...");
				Socket socket;
				try {
					socket = new Socket(getInternalHostName(),
							routerInternalPort);
				} catch (UnknownHostException e) {
					throw new RouterException("Could not create socked to "
							+ getInternalHostName() + ":" + routerInternalPort,
							e);
				}
				localHostIP = socket.getLocalAddress();

				logger.debug("Got address " + localHostIP + " from socket.");
			} else {
				logger.debug("Got invalid internal router port number "
						+ routerInternalPort);
			}

			// We are not connected to the router or got an invalid port number,
			// so we have to use the traditional method.
			if (localHostIP == null) {

				logger
						.debug("Not connected to router or got invalid port number, can not use socket to determine the address of the localhost. "
								+ "If no address is found, please connect to the router.");

				localHostIP = InetAddress.getLocalHost();

				logger.debug("Got address " + localHostIP
						+ " via InetAddress.getLocalHost().");
			}

		} catch (IOException e) {
			throw new RouterException("Could not get IP of localhost.", e);
		}

		// We do not want an address like 127.0.0.1
		if (localHostIP.getHostAddress().startsWith("127.")) {
			throw new RouterException(
					"Only found an address that begins with 127.");
		}

		return localHostIP.getHostAddress();

	}
}