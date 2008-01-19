/**
 * 
 */
package org.chris.portmapper;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.chris.portmapper.gui.PortMapperView;
import org.chris.portmapper.router.Router;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 * @author chris
 * 
 */
public class PortMapperApp extends SingleFrameApplication {

	private Log logger = LogFactory.getLog(this.getClass());

	private Router router;

	/**
	 * @see org.jdesktop.application.Application#startup()
	 */
	@Override
	protected void startup() {
		PortMapperView view = new PortMapperView();

		addExitListener(new ExitListener() {
			public boolean canExit(EventObject arg0) {
				return disconnectRouter();
			}

			public void willExit(EventObject arg0) {
			}
		});
		show(view);
	}

	public static PortMapperApp getInstance() {
		return SingleFrameApplication.getInstance(PortMapperApp.class);
	}

	public static ResourceMap getResourceMap() {
		return PortMapperApp.getInstance().getContext().getResourceMap();
	}

	public PortMapperView getView() {
		return (PortMapperView) PortMapperApp.getInstance().getMainView();
	}

	public boolean connectRouter() throws RouterException {
		if (this.router != null) {
			logger
					.warn("Already connected to router. Cannot create a second connection.");
			return false;
		}
		this.router = Router.findRouter();
		logger.info("Connected to router " + router.getName());

		boolean isConnected = this.router != null;
		this.getView().fireConnectionStateChange();
		return isConnected;
	}

	/**
	 * @return
	 */
	public boolean disconnectRouter() {
		if (this.router == null) {
			logger.warn("Not connected to router. Can not disconnect.");
			return false;
		}

		this.router.disconnect();
		this.router = null;
		this.getView().fireConnectionStateChange();

		return true;
	}

	public Router getRouter() {
		return router;
	}

	public boolean isConnected() {
		return this.getRouter() != null;
	}

	public String getLocalHostAddress() {
		logger.info("Get IP of localhost");
		InetAddress localHostIP = null;
		try {
			localHostIP = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			logger.error("Could not get IP of localhost", e);
		}

		if (!localHostIP.getHostAddress().startsWith("127.")) {
			// return localHostIP.getHostAddress();
			return null;
		}

		Collection<InetAddress> localHostIPs = new LinkedList<InetAddress>();
		try {
			InetAddress localHost = InetAddress.getLocalHost();
			logger.info("Host name of localhost: " + localHost.getHostName()
					+ ", canonical host name: "
					+ localHost.getCanonicalHostName());
			localHostIPs.addAll(Arrays.asList(InetAddress
					.getAllByName(localHost.getCanonicalHostName())));
			localHostIPs.addAll(Arrays.asList(InetAddress
					.getAllByName(localHost.getHostAddress())));
			localHostIPs.addAll(Arrays.asList(InetAddress
					.getAllByName(localHost.getHostName())));
		} catch (UnknownHostException e) {
			logger.error("Could not get IP of localhost", e);
		}
		for (InetAddress address : localHostIPs) {
			logger.info("Got IP address " + address);
			if (!address.getHostAddress().startsWith("127.")) {
				return address.getHostAddress();
			}
		}

		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(PortMapperApp.class, args);
	}

}
