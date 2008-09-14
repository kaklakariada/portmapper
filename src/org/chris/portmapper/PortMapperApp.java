/**
 * 
 */
package org.chris.portmapper;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.EventObject;

import javax.swing.JTextArea;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
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
	 * The name of the system property which will be used as the directory where
	 * all configuration files will be stored.
	 */
	private static final String CONFIG_DIR_PROPERTY_NAME = "portmapper.config.dir";

	/**
	 * The file name for the settings file.
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

		setCustomConfigDir();

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

	/**
	 * Read the system property with name
	 * {@link PortMapperApp#CONFIG_DIR_PROPERTY_NAME} and change the local
	 * storage directory if the property is given and points to a writable
	 * directory. If there is a directory named <code>PortMapperConf</code> in
	 * the current directory, use this as the configuration directory.
	 */
	private void setCustomConfigDir() {
		String customConfigurationDir = System
				.getProperty(CONFIG_DIR_PROPERTY_NAME);
		File portableAppConfigDir = new File("PortMapperConf");

		// the property is set: check, if the given directory can be used
		if (customConfigurationDir != null) {
			File dir = new File(customConfigurationDir);
			if (!dir.isDirectory()) {
				logger.error("Custom configuration directory '"
						+ customConfigurationDir + "' is not a directory.");
				System.exit(1);
			}
			if (!dir.canRead() || !dir.canWrite()) {
				logger
						.error("Can not read or write to custom configuration directory '"
								+ customConfigurationDir + "'.");
				System.exit(1);
			}
			logger.info("Using custom configuration directory '"
					+ dir.getAbsolutePath() + "'.");
			getContext().getLocalStorage().setDirectory(dir);

			// check, if the portable app directory exists and use this one
		} else if (portableAppConfigDir.isDirectory()
				&& portableAppConfigDir.canRead()
				&& portableAppConfigDir.canWrite()) {
			logger.info("Found portable app configuration directory '"
					+ portableAppConfigDir.getAbsolutePath() + "'.");
			getContext().getLocalStorage().setDirectory(portableAppConfigDir);

			// use the default configuration directory
		} else {
			logger.info("Using default configuration directory '"
					+ getContext().getLocalStorage().getDirectory()
							.getAbsolutePath() + "'.");
		}
	}

	/**
	 * Load the application settings from file
	 * {@link PortMapperApp#SETTINGS_FILENAME} located in the configuration
	 * directory.
	 */
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
			this.setLogLevel(settings.getLogLevel());
		}
	}

	private void initTextAreaLogger() {
		WriterAppender writerAppender = (WriterAppender) Logger.getLogger(
				"org.chris.portmapper").getAppender("jtextarea");
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
		logger.info("Searching for router...");
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

	/**
	 * Get the IP address of the local host.
	 * 
	 * @return IP address of the local host or <code>null</code>, if the address
	 *         could not be determined.
	 */
	public String getLocalHostAddress() {
		logger.debug("Get IP of localhost...");

		InetAddress localHostIP = null;
		try {

			// In order to use the Socked method to get the address, we have to
			// be connected to the router.

			int routerInternalPort = -1;

			if (this.isConnected()) {
				routerInternalPort = getRouter().getInternalPort();
				logger.debug("Got internal router port " + routerInternalPort);
			}

			// Check, if we got a correct port number
			if (routerInternalPort > 0) {
				logger.debug("Creating socket to router: "
						+ getRouter().getInternalHostName() + ":"
						+ routerInternalPort + "...");

				Socket socket = new Socket(getRouter().getInternalHostName(),
						routerInternalPort);
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
			logger.error("Could not get IP of localhost", e);
		}

		// We do not want an address like 127.0.0.1
		if (localHostIP.getHostAddress().startsWith("127.")) {
			logger
					.warn("Could not determine the address of localhost. Please enter it manually.");
			return null;
		}

		return localHostIP.getHostAddress();

	}

	public void setLogLevel(Level level) {
		Logger.getLogger("org.chris.portmapper").setLevel(level);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		launch(PortMapperApp.class, args);
	}

}
