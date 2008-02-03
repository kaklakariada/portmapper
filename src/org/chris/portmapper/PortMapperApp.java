/**
 * 
 */
package org.chris.portmapper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;
import java.util.EventObject;
import java.util.LinkedList;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;
import org.chris.portmapper.gui.PortMapperView;
import org.chris.portmapper.logging.TextAreaWriter;
import org.chris.portmapper.router.Router;
import org.chris.portmapper.router.RouterException;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;

/**
 * @author chris
 * 
 */
public class PortMapperApp extends SingleFrameApplication {

	/**
	 * 
	 */
	private static final String SETTINGS_FILENAME = "settings.xml";

	private Log logger = LogFactory.getLog(this.getClass());

	private Router router;
	private Settings settings;
	private TextAreaWriter logWriter;

	/**
	 * @see org.jdesktop.application.Application#startup()
	 */
	@Override
	protected void startup() {
		initTextAreaLogger();

		loadSettings();

		PortMapperView view = new PortMapperView();
		addExitListener(new ExitListener() {
			public boolean canExit(EventObject arg0) {
				return true;
			}

			public void willExit(EventObject arg0) {
				disconnectRouter();
			}
		});

		show(view);
	}

	private void loadSettings() {
		logger.info("Loading settings from file " + SETTINGS_FILENAME);
		try {
			settings = (Settings) getContext().getLocalStorage().load(
					SETTINGS_FILENAME);
		} catch (IOException e) {
			logger.warn("Could not load settings from file", e);
		}

		if (settings == null) {
			logger
					.info("Settings were not loaded from file: create new settings");
			settings = new Settings();
		} else {
			logger.info("Got settings " + settings);
		}
	}

	private void initTextAreaLogger() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris").getAppender("jtextarea");
		logWriter = new TextAreaWriter();
		writerAppender.setWriter(logWriter);
	}

	public void setLoggingTextArea(JTextArea textArea) {
		this.logWriter.setTextArea(textArea);
	}

	@Override
	protected void shutdown() {
		super.shutdown();
		logger.info("Saving settings " + settings + " to file "
				+ SETTINGS_FILENAME);
		try {
			getContext().getLocalStorage().save(settings, SETTINGS_FILENAME);
		} catch (IOException e) {
			logger.warn("Could not save settings to file", e);
		}
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

	public Settings getSettings() {
		return settings;
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
			return localHostIP.getHostAddress();
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
